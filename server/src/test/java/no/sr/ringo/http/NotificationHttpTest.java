package no.sr.ringo.http;

import no.sr.ringo.guice.ServerTestModuleFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Guice(moduleFactory = ServerTestModuleFactory.class)
public class NotificationHttpTest extends AbstractHttpClientServerTest {

    static final Logger log = LoggerFactory.getLogger(NotificationHttpTest.class);

    @Test(groups = {"integration"})
    public void testDownloadErrorNotification() throws Exception {

        String urlString = PEPPOL_BASE_REST_URL + "/notify/downloadError";
        HttpPost httpPost = new HttpPost(new URI(urlString));

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("errorMessage", "testErrorMessage"));
        nvps.add(new BasicNameValuePair("commandLine", "testCommandLine"));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        final HttpResponse httpResponse = httpClient.execute(httpPost);
        log.info(EntityUtils.toString(httpResponse.getEntity()));
        assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
    }

    @Test(groups = {"integration"})
    public void testBatchUploadErrorNotification() throws Exception {

        String urlString = PEPPOL_BASE_REST_URL + "/notify/batchUploadError";
        HttpPost httpPost = new HttpPost(new URI(urlString));

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("errorMessage", "testErrorMessage"));
        nvps.add(new BasicNameValuePair("commandLine", "testCommandLine"));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        final HttpResponse httpResponse = httpClient.execute(httpPost);
        log.info(EntityUtils.toString(httpResponse.getEntity()));
        assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
    }
}
