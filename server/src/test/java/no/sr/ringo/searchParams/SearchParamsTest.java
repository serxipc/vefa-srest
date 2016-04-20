package no.sr.ringo.searchParams;

import no.sr.ringo.message.SearchParams;
import no.sr.ringo.resource.InvalidUserInputWebException;
import org.testng.annotations.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.testng.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: adam
 * Date: 1/24/12
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class SearchParamsTest {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void testFormatter() throws ParseException {

        String stringDate = "2011-01-12";
        String regex = "[0-9]{4,4}-[0-2][0-9]-[0-3][0-9]";
        assertTrue(stringDate.matches(regex));

        stringDate = "2011-12-42";
        assertFalse(stringDate.matches(regex));

        stringDate = "2011-31-32";
        assertFalse(stringDate.matches(regex));

        stringDate = "20-01-2012";
        assertFalse(stringDate.matches(regex));

        stringDate = "2110-1-2012";
        assertFalse(stringDate.matches(regex));

    }
    
    public void testProperDate() throws Exception {
        String sent = "'<2012-11-21'";

        Date date = formatter.parse("2012-11-21");
        SearchParams params = new SearchParams(null, null, null, sent, null);

        assertEquals(SearchParams.DateCondition.LESS, params.getDateCondition());
        assertEquals(date, params.getSent());

            sent = "%22>2012-11-21%22";
        params = new SearchParams(null, null, null, sent, null);
        assertEquals(SearchParams.DateCondition.GREATER, params.getDateCondition());
        assertEquals(date, params.getSent());

        sent = ">=2012-11-21";
        params = new SearchParams(null, null, null, sent, null);
        assertEquals(SearchParams.DateCondition.GREATER_EQUAL, params.getDateCondition());
        assertEquals(date, params.getSent());

        sent = "<=2012-11-21";
        params = new SearchParams(null, null, null, sent, null);
        assertEquals(SearchParams.DateCondition.LESS_EQUAL, params.getDateCondition());
        assertEquals(date, params.getSent());

        sent = "=2012-11-21";
        params = new SearchParams(null, null, null, sent, null);
        assertEquals(SearchParams.DateCondition.EQUAL, params.getDateCondition());
        assertEquals(date, params.getSent());
    }

    @Test(expectedExceptions = InvalidUserInputWebException.class)
    public void testDateWithoutCondition(){

        String sent = "2012-11-21";
        SearchParams params = new SearchParams(null, null, null, sent, null);

    }

    @Test(expectedExceptions = InvalidUserInputWebException.class)
    public void testWrongDateCondition(){

        String sent = "==2012-11-21";
        SearchParams params = new SearchParams(null, null, null, sent, null);

    }

    @Test(expectedExceptions = InvalidUserInputWebException.class)
    public void testWrongDateFormat(){

        String sent = "<23-11-2012";
        SearchParams params = new SearchParams(null, null, null, sent, null);

    }


    @Test
    public void testCopyingDateSearchParmsToNewURI() throws Exception {

        String sent = "'<2012-11-21'";
        SearchParams params = new SearchParams(null, null, null, sent, null);

        final UriBuilder uriBuilder = UriBuilder.fromUri(new URI("http://test"));
        params.appendTo(uriBuilder);

        final URI build = uriBuilder.build();
        assertEquals(build.toString(),"http://test?sent='%3C2012-11-21'");
    }

    @Test
    public void testCopyingDirectionSearchParmsToNewURI() throws Exception {

        String direction = "IN";
        SearchParams params = new SearchParams(direction, null, null, null, null);

        final UriBuilder uriBuilder = UriBuilder.fromUri(new URI("http://test"));
        params.appendTo(uriBuilder);

        final URI build = uriBuilder.build();
        assertEquals(build.toString(),"http://test?direction=IN");
    }

    @Test
    public void testCopyingReceiverSearchParmsToNewURI() throws Exception {

        String receiver = "9908:123456789";
        SearchParams params = new SearchParams(null, null, receiver, null, null);

        final UriBuilder uriBuilder = UriBuilder.fromUri(new URI("http://test"));
        params.appendTo(uriBuilder);

        final URI build = uriBuilder.build();
        assertEquals(build.toString(),"http://test?receiver=9908:123456789");
    }

    @Test
    public void testCopyingSenderSearchParmsToNewURI() throws Exception {

        String sender = "9908:123456789";
        SearchParams params = new SearchParams(null, sender, null, null, null);

        final UriBuilder uriBuilder = UriBuilder.fromUri(new URI("http://test"));
        params.appendTo(uriBuilder);

        final URI build = uriBuilder.build();
        assertEquals(build.toString(),"http://test?sender=9908:123456789");
    }


    @Test
    public void testAllParamsExceptIndexToNewURI() throws Exception {
        String direction = "IN";
        String sender = "9908:123456789";
        String receiver = "9908:123456789";
        String sent = "'<2012-11-21'";
        SearchParams params = new SearchParams(direction, sender, receiver, sent, "1");

        final UriBuilder uriBuilder = UriBuilder.fromUri(new URI("http://test"));
        params.appendTo(uriBuilder);

        final URI build = uriBuilder.build();
        assertEquals(build.toString(),"http://test?sender=9908:123456789&receiver=9908:123456789&sent='%3C2012-11-21'&direction=IN");
    }
}
