package no.sr.ringo.peppol;

import org.easymock.EasyMock;
import org.slf4j.Logger;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.easymock.EasyMock.*;

/**
 * @author andy
 * @author thore
 */
public class PeppolParticipantIdTest {

    @Test
    public void testFoedselsnummerWhichCouldBeUsedByDigitalMultiKanal() {

        // multikanal uses fødselsnummer
        PeppolParticipantId p1 = PeppolParticipantId.valueFor("9999:42342342343");
        assertNotNull(p1);
        assertEquals(p1.schemeId, SchemeId.ZZ_);
        assertEquals(p1.getOrganisationId(), "42342342343");

        // multikanal uses orgnumbers
        assertNotNull(PeppolParticipantId.valueFor("9999:968218743"));

    }

    @Test
    public void testParseNorwegianOrgNoTax() throws Exception {
        //Tests that parsing norweigian org num will always parse scheme NO:ORGNR
        PeppolParticipantId no976098897MVA = PeppolParticipantId.valueFor("NO976098897MVA");
        final PeppolParticipantId expected = new PeppolParticipantId(SchemeId.NO_ORGNR,"976098897");
        assertEquals(no976098897MVA, expected);

        no976098897MVA = PeppolParticipantId.valueFor("976098897MVA");
        assertEquals(no976098897MVA, expected);

        no976098897MVA = PeppolParticipantId.valueFor("NO 976098897MVA");
        assertEquals(no976098897MVA, expected);

        no976098897MVA = PeppolParticipantId.valueFor("NO 976098897 MVA");
        assertEquals(no976098897MVA, expected);

        no976098897MVA = PeppolParticipantId.valueFor(" NO 976098897 MVA  ");
        assertEquals(no976098897MVA, expected);
    }

    @Test
    public void testParseNorwegianOrgNoWithoutTax() throws Exception {
        //TEST THAT parsing norweigian org num will always parse scheme NO:ORGNR
        PeppolParticipantId no976098897 = PeppolParticipantId.valueFor("NO976098897");
        final PeppolParticipantId expected = new PeppolParticipantId(SchemeId.NO_ORGNR,"976098897");
        assertEquals(no976098897, expected);

        no976098897 = PeppolParticipantId.valueFor("976098897");
        assertEquals(no976098897, expected);

        no976098897 = PeppolParticipantId.valueFor(" 976098897 ");
        assertEquals(no976098897, expected);

        no976098897 = PeppolParticipantId.valueFor("NO 976098897");
        assertEquals(no976098897, expected);

        no976098897 = PeppolParticipantId.valueFor("NO 976098897 ");
        assertEquals(no976098897, expected);

        no976098897 = PeppolParticipantId.valueFor(" NO 976098897");
        assertEquals(no976098897, expected);

        no976098897 = PeppolParticipantId.valueFor("NO 976098897");
        assertEquals(no976098897, expected);

    }

    @Test
    public void testParsePeppolParticpantId() throws Exception {

        PeppolParticipantId no976098897 = PeppolParticipantId.valueFor("9908:976098897");
        assertEquals(no976098897,new PeppolParticipantId(SchemeId.NO_ORGNR,"976098897"));

        no976098897 = PeppolParticipantId.valueFor("9908:976098897");
        assertEquals(no976098897,new PeppolParticipantId(SchemeId.NO_ORGNR,"976098897"));

        no976098897 = PeppolParticipantId.valueFor("9901:976098897");
        assertEquals(no976098897,new PeppolParticipantId(SchemeId.DK_CPR,"976098897"));

        //invalid iso code will not be parsed.
        no976098897 = PeppolParticipantId.valueFor("0001:976098897");
        
        assertNull(no976098897);

    }

    @Test
    public void testIsValid() {

        // a valid orgNo
        assertTrue(PeppolParticipantId.isValidNorwegianOrgNum("968218743"));

        // not valid
        assertFalse(PeppolParticipantId.isValidNorwegianOrgNum("123456789"));

        // null
        assertFalse(PeppolParticipantId.isValidNorwegianOrgNum((String) null));

        // empty String
        assertFalse(PeppolParticipantId.isValidNorwegianOrgNum(""));

        assertTrue(PeppolParticipantId.isValidNorwegianOrgNum("961329310"));


    }

    /**
     * Tests that when using value of we get null with invalid norwegian organisation numbers
     */
    @Test
    public void testIsValidValueOf() {

        // a valid orgNo
        assertNotNull(PeppolParticipantId.valueFor("968218743"));

        // not valid
        assertNull(PeppolParticipantId.valueFor("123456789"));
        assertNull(PeppolParticipantId.valueFor("986532933"));
        assertNull(PeppolParticipantId.valueFor("986532952"));
        assertNull(PeppolParticipantId.valueFor("986532954"));
        assertNull(PeppolParticipantId.valueFor("986532955"));

        assertNotNull(PeppolParticipantId.valueFor("968 218 743"));

        // null
        assertNull(PeppolParticipantId.valueFor((String) null));

        // empty String
        assertNull(PeppolParticipantId.valueFor(""));

        assertNotNull(PeppolParticipantId.valueFor("9908:968218743"));

        assertNotNull(PeppolParticipantId.valueFor("9908:NO976098897MVA"));

        assertNotNull(PeppolParticipantId.valueFor("9908:NO 976098897 MVA"));

        assertNotNull(PeppolParticipantId.valueFor("9908:976098897 MVA"));

        assertNotNull(PeppolParticipantId.valueFor("9908:976098897MVA"));

        assertNotNull(PeppolParticipantId.valueFor("988890081"));

    }

    @Test
    public void testOrganistaionId() throws Exception {
        PeppolParticipantId peppolParticipantId = PeppolParticipantId.valueFor("9908:968218743");
        assertEquals("968218743",peppolParticipantId.getOrganisationId());
    }

    @Test
    public void testOrgNumWithSpaces() throws Exception {
        PeppolParticipantId organisationNumber = PeppolParticipantId.valueFor("968 218 743");
        assertEquals("968218743", organisationNumber.organisationId);
        assertEquals(SchemeId.NO_ORGNR, organisationNumber.schemeId);

        organisationNumber = PeppolParticipantId.valueFor("99 08:9682 18743");
        assertEquals("968218743", organisationNumber.organisationId);
        assertEquals(SchemeId.NO_ORGNR, organisationNumber.schemeId);

        organisationNumber = PeppolParticipantId.valueFor("00 07:9682 18743");
        assertEquals("968218743", organisationNumber.organisationId);
        assertEquals(organisationNumber.schemeId,SchemeId.SE_ORGNR);
    }


    @Test
    public void testTooLongOrgNo() {
        try{
            PeppolParticipantId orgNo = new PeppolParticipantId(SchemeId.NO_VAT, "1234567890123456789012345678901234567890");
            fail();
        } catch (IllegalArgumentException e){
            assertEquals("Invalid organisation id. '1234567890123456789012345678901234567890' is longer than 35 characters", e.getMessage());
        }
    }

    @Test
    public void testSerialize() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        ObjectInputStream ois = null;
        try {
            final PeppolParticipantId expectedPeppolParticipantId = PeppolParticipantId.valueFor("9908:976098897");

            oos.writeObject(expectedPeppolParticipantId);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            ois = new ObjectInputStream(in);

            final PeppolParticipantId peppolParticipantId = (PeppolParticipantId) ois.readObject();
            assertEquals(peppolParticipantId, expectedPeppolParticipantId);
        }
        finally {
            oos.close();
            if (ois != null) {
                ois.close();
            }
        }


        assertTrue(out.toByteArray().length > 0);
    }


    @Test
    public void testLogMessage() {
        Logger mockLogger = EasyMock.createStrictMock(Logger.class);
        String message = "Organisation number '940791902' is not a valid norwegian organisation number";

        expect(mockLogger.isInfoEnabled()).andReturn(true);
        mockLogger.info(EasyMock.eq("Organisation number '940791902' is not a valid norwegian organisation number"), isA(IllegalArgumentException.class));
        expectLastCall();
        replay(mockLogger);

        PeppolParticipantId.logMessage(mockLogger, message, new IllegalArgumentException("test exception"));

        verify(mockLogger);
    }

    @Test(enabled = false)
    public void testSRO3079() throws Exception {

        PeppolParticipantId peppolParticipantId = PeppolParticipantId.valueFor("9147:91723");
        assertNotNull(peppolParticipantId);

        peppolParticipantId = PeppolParticipantId.valueFor("9957:61394");
        assertNotNull(peppolParticipantId);
    }

}
