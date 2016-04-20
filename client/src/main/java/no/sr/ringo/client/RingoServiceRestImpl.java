package no.sr.ringo.client;

import no.sr.ringo.common.*;
import no.sr.ringo.document.ClientPeppolDocument;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.request.TestSocketFactory;
import no.sr.ringo.response.*;
import no.sr.ringo.response.exception.*;
import no.sr.ringo.smp.AcceptedDocumentTransfer;
import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Exposes the SendRegning access point to the RingoClient.
 * It uses the HttpClient library to make HTTP requests that conform to the SREST protocol.
 *
 * @author andy
 * @author thore
 */
public class RingoServiceRestImpl implements RingoService {

    static final Logger log = LoggerFactory.getLogger(RingoServiceRestImpl.class);

    protected final String userId;
    protected final String password;
    protected final RingoConfig config;

    protected final URI baseUri;
    protected final HttpHost targetHost;
    protected final HttpContext localContext;

    protected HttpClient httpClient;

    /**
     * Sets up the client service with the given configuration.
     *
     * @param config   the configuration to use for the client, allows customisation of timeouts etc.
     * @param userName the name to use when logging in.
     * @param password the password to use when logging in.
     */
    RingoServiceRestImpl(RingoConfig config, String userName, String password) {
        this.config = config;
        this.userId = userName;
        this.password = password;
        this.baseUri = config.getBaseUri();
        this.targetHost = new HttpHost(baseUri.getHost(), baseUri.getPort(), baseUri.getScheme());

        // Sets up preemptive basic authentication so that the authentication credentials are sent with the header.
        // This means that only one request will be made for each operation.
        this.localContext = new BasicHttpContext();
        this.localContext.setAttribute("preemptive-auth", new BasicScheme());
    }

    public void downloadMessage(MessageWithLocations message, OutputStream outputStream) throws IOException {
        //creates the get request to download the xml document
        HttpGet httpGet = new HttpGet(message.getXmlDocumentURI());

        int executionCount = 0;
        BufferedOutputStream out = null;
        //loops forever until either the response is successful or the retry handler says not to retry.
        for (; ; ) {
            try {
                executionCount++;
                HttpResponse httpResponse = getHttpClient().execute(targetHost, httpGet, localContext);
                out = new BufferedOutputStream(outputStream);
                httpResponse.getEntity().writeTo(out);
                return;
            } catch (AccessPointTemporarilyUnavailableException e) {
                if (!config.getRetryHandler().retryRequest(e, executionCount, e.getHttpContext())) {
                    throw new AccessPointUnavailableException(executionCount, e);
                }
            } catch (IOException e) {
                throw handleException(httpGet, e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }

    public Boolean sendErrorNotification(ErrorNotificationData errorNotificationData) {
        String resource = null;
        switch (errorNotificationData.getNotificationType()) {
            case DOWNLOAD:
                resource = "downloadError";
                break;
            case BATCH_UPLOAD:
                resource = "batchUploadError";
                break;
            default:
                throw new IllegalArgumentException("Not supported notification type " + errorNotificationData.getNotificationType().name());
        }

        HttpPost httpPost = createHttpPost("/notify/" + resource);

        try {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("errorMessage", errorNotificationData.getErrorMessage()));
            nvps.add(new BasicNameValuePair("commandLine", errorNotificationData.getCommandLine()));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Encoding Exception occurred when sending notification request");
        }

        return execute(httpPost, new NotificationRingoResponseHandler());
    }

    public boolean markAsRead(MessageWithLocations message) {
        final String uri = message.getSelfURI() + "/read";
        HttpPost httpPost = new HttpPost(uri);
        return execute(httpPost, new MarkAsReadRingoResponseHandler());
    }

    public Messages next(Navigation navigation, RingoResponseHandler<? extends Messages> ringoResponseHandler) {
        HttpGet httpGet = new HttpGet(navigation.getNext());
        return execute(httpGet, ringoResponseHandler);
    }

    public Integer count(MessageContainer messageContainer) {
        String messageResource = String.format("%s/%s/count", baseUri, messageContainer.getPath());
        URI countMessagesUri = createURI(messageResource);
        HttpGet httpGet = new HttpGet(countMessagesUri);
        return execute(httpGet, new CountRingoResponseHandler());
    }

    public Messages messages(MessageContainer messageContainer, RingoResponseHandler<? extends Messages> messagesResponseHandler) {
        String messageResource = String.format("%s/%s", baseUri, messageContainer.getPath());
        HttpGet httpGet = new HttpGet(createURI(messageResource));
        return execute(httpGet, messagesResponseHandler);
    }

    public Message sendDocument(ClientPeppolDocument peppolDocument, PeppolHeader peppolHeader, UploadMode uploadMode) {
        peppolHeader.validate();
        return sendAsMimeMultipartHttpPost(peppolDocument, peppolHeader, uploadMode);
    }

    public boolean isParticipantRegistered(PeppolParticipantId peppolParticipantId) {
        try {
            String s = baseUri.toString() + "/directory/" + URLEncoder.encode(peppolParticipantId.stringValue(), "UTF-8");
            URI directoryLookupUri = createURI(s);
            HttpGet httpGet = new HttpGet(directoryLookupUri);
            return execute(httpGet, new IsParticipantRegisteredRingoResponseHandler());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to check if participant is registered", e);
        }
    }

    public List<AcceptedDocumentTransfer> fetchAcceptedDocumentTransfers(PeppolParticipantId peppolParticipantId, LocalName localName) {
        try {
            HttpGet httpGet = createHttpGet("/directory/" + urlEncode(peppolParticipantId) + "/" + localName.toString());
            return execute(httpGet, new AcceptedDocumentTransfersRingoResponseHandler(this));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to fetch accepted document transfers", e);
        }
    }

    /**
     * Fetches the underlying http client being used to make the requests.
     * N.b. This is not a factory method, it causes the httpclient to be created if it has not already been
     * created.
     *
     * @return the reusable http client connection
     */
    public HttpClient getHttpClient() {
        //creates the client only once.
        if (httpClient != null) {
            return httpClient;
        }
        createDefaultHttpClient();
        return httpClient;
    }

    /**
     * Executes the http request and processes the response using a dynamically assigned response handler.
     * <p/>
     * The request will automatically be retried if it fails based on the {@link no.sr.ringo.request.RetryHandler}
     *
     * @param httpRequest the method being executed.
     * @param <T>         the expected return type.
     * @return the HTTP entity wrapped in the expected type.
     */
    protected <T> T execute(HttpRequestBase httpRequest, RingoResponseHandler<? extends T> responseHandler) {
        int executionCount = 0;
        //loops forever until either the response is successful or the retry handler says not to retry.
        for (; ; ) {
            try {
                executionCount++;
                return getHttpClient().execute(targetHost, httpRequest, responseHandler, localContext);
            } catch (AccessPointTemporarilyUnavailableException exception) {
                if (!config.getRetryHandler().retryRequest(exception, executionCount, exception.getHttpContext())) {
                    throw new AccessPointUnavailableException(executionCount, exception);
                }
            } catch (IOException e) {
                throw handleException(httpRequest, e);
            } finally {
                if (httpRequest != null) httpRequest.reset(); // 4.2.x Resets internal state of the request making it reusable.
            }
        }
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private void createDefaultHttpClient() {

        RingoLoggingStream ringoLoggingStream = config.getLogger();

        //creates the default client if one is not already configured.
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

        //if proxy settings are available, try to use it
//        if (config.getProxySettings() != null && config.getProxySettings().getAddress() != null && config.getProxySettings().getPort() != null) {
//            log.debug("Using proxy settings: [address: " + config.getProxySettings().getAddress() + ", port: " + config.getProxySettings().getPort() + "]");
//            HttpHost proxy = new HttpHost(config.getProxySettings().getAddress(), config.getProxySettings().getPort(), "http");
//            defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//        }

        //sets up the default connection timeout and the socket timeout
        //to be 10 seconds and 5 seconds.
        HttpParams httpParams = defaultHttpClient.getParams();

        HttpConnectionParams.setConnectionTimeout(httpParams, config.getConnectionTimeOut() * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, config.getSocketTimeOut() * 1000);

        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(userId, password);

        // Set up for basic authentication
        AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
        defaultHttpClient.getCredentialsProvider().setCredentials(authScope, usernamePasswordCredentials);

        // Interceptor the checks the response code for errors.
        defaultHttpClient.addResponseInterceptor(new ResponseCodeInterceptor());
        // Interceptor which checks that the client is up to date.
        // a message will be logged to system error if not up to date
        defaultHttpClient.addResponseInterceptor(new ClientVersionInterceptor(ringoLoggingStream));

        //what should we do if there are any problems
        defaultHttpClient.setHttpRequestRetryHandler(config.getRetryHandler());

        //sets up the user agent using the client version
        try {
            final Properties properties = FileHelper.fetchProperties("/client.properties");
            final String clientVersion = properties.getProperty("client.version");
            log.info(String.format("SendRegning access point client version: %s", clientVersion));

            HttpProtocolParams.setUserAgent(httpParams, String.format("%s (Version: %s)", RingoConstants.USER_AGENT, clientVersion));
            HttpProtocolParams.setContentCharset(httpParams, RingoConstants.DEFAULT_CHARACTER_SET);
        } catch (IOException e) {
            log.error("Unable to get the client version", e);
        }

        defaultHttpClient.addRequestInterceptor(new PreemptiveAuth(), 0);
        httpClient = defaultHttpClient;

        //allows overriding the validation of the ssl certificate
//        if (baseUri.getHost().equals("localhost") || baseUri.getHost().equals("195.1.61.172")) {
//            this.httpClient = WebClientDevWrapper.wrapClient((DefaultHttpClient) httpClient);
//        }

        try {
            setUpSchemeRegistry(baseUri);
        } catch (KeyManagementException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private Message sendAsMimeMultipartHttpPost(ClientPeppolDocument peppolDocument, PeppolHeader peppolHeader, UploadMode uploadMode) {
        HttpPost httpPost = createHttpPost("/outbox");
        ContentBody contentBody = peppolDocument.getContentBody();
        try {
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET));
            multipartEntity.addPart("file", contentBody);
            multipartEntity.addPart("RecipientID", new StringBody(peppolHeader.getReceiver().stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("SenderID", new StringBody(peppolHeader.getSender().stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("ChannelID", new StringBody(peppolHeader.getPeppolChannelId().stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("ProcessID", new StringBody(peppolHeader.getProfileId().toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("DocumentID", new StringBody(peppolHeader.getPeppolDocumentTypeId().stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("UploadMode", new StringBody(uploadMode.name()));
            httpPost.setEntity(multipartEntity);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to create mime multipart http entity", e);
        }
        return execute(httpPost, new UploadRingoResponseHandler(this));
    }

    private HttpPost createHttpPost(String resource) {
        String postUrlString = baseUri.toString() + resource;
        return new HttpPost(createURI(postUrlString));
    }

    private HttpGet createHttpGet(String resource) throws UnsupportedEncodingException {
        String s = baseUri.toString() + resource;
        URI directoryLookupUri = createURI(s);
        return new HttpGet(directoryLookupUri);
    }

    protected String urlEncode(PeppolParticipantId peppolParticipantId) {
        try {
            return URLEncoder.encode(peppolParticipantId.stringValue(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    private URI createURI(String uploadUrlString) {
        try {
            return new URI(uploadUrlString);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("Unable to create uri from string %s", uploadUrlString), e);
        }
    }

    private RuntimeException handleException(HttpRequestBase httpRequest, IOException exception) {
        if (exception instanceof BadCredentialsException) {
            throw new IllegalArgumentException("Invalid username and or password", exception);
        }
        if (exception instanceof UnexpectedResponseCodeException) {
            UnexpectedResponseCodeException unexpectedResponseCodeException = (UnexpectedResponseCodeException) exception;
            throw new UnexpectedRestResponse(httpRequest, unexpectedResponseCodeException.getResponse(), unexpectedResponseCodeException);
        }
        log.error("Exception occurred: " + exception.getMessage(), exception);
        throw new IllegalStateException("Unable to execute http request " + httpRequest.getURI().toString(), exception);
    }

    private void setUpSchemeRegistry(URI baseUri) throws KeyManagementException, NoSuchAlgorithmException {
        //if we are executing against localhost or agresso then accept certificate!
        if (baseUri.getHost().equals("localhost") || baseUri.getHost().equals("195.1.61.172")) {
            SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
            schemeRegistry.register(new Scheme("https", 443, new TestSocketFactory(baseUri)));
        }
    }

    static class PreemptiveAuth implements HttpRequestInterceptor {
        // Taken from : httpclient/src/examples/org/apache/http/examples/client/ClientPreemptiveBasicAuthentication.java
        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
            AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
            // If no auth scheme avaialble yet, try to initialize it preemptively
            if (authState.getAuthScheme() == null) {
                AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                if (authScheme != null) {
                    Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
                    if (creds == null) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }
                    authState.setAuthScheme(authScheme);
                    authState.setCredentials(creds);
                }
            }
        }
    }

}
