package no.sr.ringo.persistence;

import org.testng.annotations.Test;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.net.URI;

/**
 * @author steinar
 *         Date: 19.02.2017
 *         Time: 15.58
 */
public class PayloadReferenceRewriteTest {


    @Test
    public void simpleRewrite() throws Exception {

        String azureUrl = "azure:http://hmaptestdata01.blob.core.windows.net/invoice-out/sample-invoice-doc.xml?sig=qGR3D%2FdDhim8sg4Eibdqxw23eRK8UvwZ3OakkFDRvqk%3D&se=2017-02-19T15%3A10%3A36Z&sv=2016-05-31&sp=r&sr=b";
        final URI azureUri = new URI(azureUrl);

        System.out.printf("Scheme       : %s\n" ,azureUri.getScheme());
        System.out.printf("Scheme specifid: %s\n", azureUri.getSchemeSpecificPart());
        System.out.printf("Authority    : %s\n", azureUri.getAuthority());
        System.out.printf("    user-info: %s @ %s:%d\n", azureUri.getUserInfo(), azureUri.getHost(), azureUri.getPort());
        System.out.printf("Path         : %s\n", azureUri.getPath());
        System.out.printf("Query        : %s\n", azureUri.getQuery());
        System.out.printf("Fragment     : %s\n", azureUri.getFragment());
        System.out.printf("Raw query    : %s\n", azureUri.getRawQuery());
        final URI uri = new URI("http", "localhost", "/vefa-srest/messages",null);
        System.out.println(uri );

        final File test = File.createTempFile("test", ".tst");
        System.out.println(test.toURI());


        final UriBuilder uriBuilder = UriBuilder.fromUri(azureUri);
    }



}
