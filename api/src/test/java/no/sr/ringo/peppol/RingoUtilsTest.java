package no.sr.ringo.peppol;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.testng.Assert.*;

/**
 * User: andy
 * Date: 1/20/12
 * Time: 10:09 AM
 */
public class RingoUtilsTest {
    
    @Test
    public void testIsEmpty() throws Exception {
        String value = null;
        assertTrue(RingoUtils.isEmpty(value));

        value = "";
        assertTrue(RingoUtils.isEmpty(value));

        value = "   ";
        assertTrue(RingoUtils.isEmpty(value));

        value = "value";
        assertFalse(RingoUtils.isEmpty(value));
    }

    @Test
    public void testQuoteRegExp() throws Exception {
        String value ="/test";
        assertEquals(RingoUtils.quoteForRegularExpression(value), "\\/test");
    }


    @Test
    public void testEncodeXmlEntites() throws Exception {
    //    Ring
        final String encoded = RingoUtils.encodePredefinedXmlEntities("&\"'<>");
        assertEquals(encoded, "&amp;&quot;&apos;&lt;&gt;");
    }

    @Test
    public void testDecodeXmlEntites() throws Exception {
        //    Ring
        final String encoded = RingoUtils.decodePredefinedXmlEntities("&amp;&quot;&apos;&lt;&gt;");
        assertEquals(encoded, "&\"'<>");
    }

    @Test
    public void testURLThatCausedProblems() throws Exception {
        String url = "https://localhost:8443/messages?index=1&sent='%3E2012-02-19'";
        final String encoded = RingoUtils.encodePredefinedXmlEntities(url);
        final String decoded = RingoUtils.decodePredefinedXmlEntities(encoded);
        
        assertEquals(decoded, url);
    }

    @Test
    public void testToXmlURI() throws Exception {
        String url = "https://localhost:8443/messages?index=1&sent='%3E2012-02-19'";
        final String encoded = RingoUtils.toXml(new URI(url));
        final String decoded = RingoUtils.decodePredefinedXmlEntities(encoded);

        assertEquals(decoded, url);
    }

    @Test
    public void testToXmlParticipantIdentifier(){
        String expected = "9908:976098897";
        final String encoded = RingoUtils.toXml(ParticipantIdentifier.of(expected));
        final String decoded = RingoUtils.decodePredefinedXmlEntities(encoded);
        assertEquals(decoded, expected);
    }

    @Test
    public void testToXmlPeppolChannelId(){
        String expected = "åknow'&;";
        final String encoded = RingoUtils.toXml(new PeppolChannelId(expected));
        final String decoded = RingoUtils.decodePredefinedXmlEntities(encoded);
        assertEquals(decoded, expected);
    }

    @Test
    public void testToXmlPeppolDocumentId(){
        String expected = "INVOICE";
        final String encoded = RingoUtils.toXml(PeppolDocumentIdAcronym.INVOICE);
        final String decoded = RingoUtils.decodePredefinedXmlEntities(encoded);
        assertEquals(decoded, expected);
    }

    @Test
    public void testToXmlPeppolProcessId(){
        String expected = "INVOICE_ONLY";
        final String encoded = RingoUtils.toXml(PeppolProcessIdAcronym.INVOICE_ONLY);
        final String decoded = RingoUtils.decodePredefinedXmlEntities(encoded);
        assertEquals(decoded, expected);
    }

    @Test
    public void testParseDateTimeFromISO8601String() throws Exception {
        String dateTime = "2012-01-01T12:38:40.001+01:00";

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        
        cal.set(Calendar.YEAR,2012);
        cal.set(Calendar.MONTH,0);
        cal.set(Calendar.DAY_OF_MONTH,1);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,38);
        cal.set(Calendar.SECOND,40);
        cal.set(Calendar.MILLISECOND,1);
        Date expectedTime = cal.getTime();


        Date actualTime = RingoUtils.getDateTimeFromISO8601String(dateTime);
        assertEquals(actualTime.getTime(),expectedTime.getTime());
        assertEquals(actualTime,expectedTime);
    }


    @Test
    public void testFormatDateTimeAsISO8601String() throws Exception {
        String expectedDateTimeString = "2012-01-01T12:38:40.001+01:00";

        //RingoUtils uses default timezone when formatting so we need to make sure we
        //use the same timezone in the test
        Calendar cal = Calendar.getInstance(DateTimeZone.getDefault().toTimeZone());

        cal.set(Calendar.YEAR,2012);
        cal.set(Calendar.MONTH,0);
        cal.set(Calendar.DAY_OF_MONTH,1);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,38);
        cal.set(Calendar.SECOND,40);
        cal.set(Calendar.MILLISECOND,1);
        String actualDateTimeString = RingoUtils.formatDateTimeAsISO8601String(cal.getTime());

        assertEquals(actualDateTimeString,expectedDateTimeString);
    }


    @Test
    public void testDaylightSavingsFormatDateTimeAsISO8601String() throws Exception {
        String expectedDateTimeString = "2012-06-01T12:38:40.001+02:00";

        //RingoUtils uses default timezone when formatting so we need to make sure we
        //use the same timezone in the test
        Calendar cal = Calendar.getInstance(DateTimeZone.getDefault().toTimeZone());

        cal.set(Calendar.YEAR,2012);
        cal.set(Calendar.MONTH,5);
        cal.set(Calendar.DAY_OF_MONTH,1);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,38);
        cal.set(Calendar.SECOND,40);
        cal.set(Calendar.MILLISECOND,1);
        String actualDateTimeString = RingoUtils.formatDateTimeAsISO8601String(cal.getTime());

        assertEquals(actualDateTimeString,expectedDateTimeString);
    }

}
