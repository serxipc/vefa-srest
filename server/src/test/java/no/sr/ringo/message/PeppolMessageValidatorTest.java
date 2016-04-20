package no.sr.ringo.message;

import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.peppol.SchemeId;
import no.sr.ringo.resource.InvalidUserInputWebException;
import no.sr.ringo.smp.RingoSmpLookup;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * User: Adam
 * Date: 4/17/13
 * Time: 11:07 AM
 */
public class PeppolMessageValidatorTest {

    PeppolMessage mockPeppolMessage;
    PeppolHeader mockPeppolHeader;
    RingoSmpLookup mockRingoSmpLookup;
    RingoAccount mockRingoAccount;

    PeppolMessageValidator validator;
    PeppolParticipantId participantId;

    @BeforeMethod
    public void setUp() throws Exception {
        mockPeppolMessage = createStrictMock(PeppolMessage.class);
        mockPeppolHeader = createStrictMock(PeppolHeader.class);
        mockRingoSmpLookup = createStrictMock(RingoSmpLookup.class);
        mockRingoAccount = createStrictMock(RingoAccount.class);

        participantId = new PeppolParticipantId(SchemeId.NO_ORGNR, "976098897");

    }

    @Test
    public void testValidateWrongRecipient() throws Exception {
        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("invalidRecipientId").build();
        validator = new PeppolMessageValidator(mockRingoSmpLookup, mockPeppolMessage, params);
        expect(mockPeppolMessage.getPeppolHeader()).andStubReturn(mockPeppolHeader);
        expect(mockPeppolHeader.getReceiver()).andReturn(null);

        replay(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

        try{
            validator.validateHeader();
            fail();
        } catch (InvalidUserInputWebException e) {
            assertEquals(e.getMessage(), "Wrong recipientId value: invalidRecipientId");
        }

        verify(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

    }

    @Test
    public void testValidateRecipientNotInSmp() throws Exception {
        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").build();
        validator = new PeppolMessageValidator(mockRingoSmpLookup, mockPeppolMessage, params);
        expect(mockPeppolMessage.getPeppolHeader()).andStubReturn(mockPeppolHeader);
        expect(mockPeppolHeader.getReceiver()).andReturn(participantId);
        expect(mockRingoSmpLookup.isRegistered(participantId)).andReturn(false);

        replay(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

        try{
            validator.validateHeader();
            fail();
        } catch (InvalidUserInputWebException e) {
            assertEquals(e.getMessage(), "recipient PeppolParticipantId{id='9908:976098897', partyId=NO_ORGNR} is not registered in the SMP with an accesspoint for receiving INVOICE documents");
        }

        verify(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

    }

    @Test
    public void testValidateWrongSender() throws Exception {
        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").senderId("InvalidSenderId").build();
        validator = new PeppolMessageValidator(mockRingoSmpLookup, mockPeppolMessage, params);
        expect(mockPeppolMessage.getPeppolHeader()).andStubReturn(mockPeppolHeader);
        expect(mockPeppolHeader.getReceiver()).andReturn(participantId);
        expect(mockRingoSmpLookup.isRegistered(participantId)).andReturn(true);
        expect(mockPeppolHeader.getSender()).andReturn(null);

        replay(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

        try{
            validator.validateHeader();
            fail();
        } catch (InvalidUserInputWebException e) {
            assertEquals(e.getMessage(), "Wrong senderId value: InvalidSenderId");
        }

        verify(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

    }

    @Test
    public void testValidateDocumentId() throws Exception {
        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").senderId("9908:976098897").documentId("invalidDocumentId").build();
        validator = new PeppolMessageValidator(mockRingoSmpLookup, mockPeppolMessage, params);
        expect(mockPeppolMessage.getPeppolHeader()).andStubReturn(mockPeppolHeader);
        expect(mockPeppolHeader.getReceiver()).andReturn(participantId);
        expect(mockRingoSmpLookup.isRegistered(participantId)).andReturn(true);
        expect(mockPeppolHeader.getSender()).andReturn(participantId);
        expect(mockPeppolHeader.getPeppolDocumentTypeId()).andReturn(null);

        replay(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

        try{
            validator.validateHeader();
            fail();
        } catch (InvalidUserInputWebException e) {
            assertEquals(e.getMessage(), "Wrong documentId value: invalidDocumentId");
        }

        verify(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

    }

    @Test
    public void testValidateProcessId() throws Exception {
        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").senderId("9908:976098897").documentId(PeppolDocumentTypeId.EHF_INVOICE.stringValue()).processId("invalidProcessId").build();
        validator = new PeppolMessageValidator(mockRingoSmpLookup, mockPeppolMessage, params);
        expect(mockPeppolMessage.getPeppolHeader()).andStubReturn(mockPeppolHeader);
        expect(mockPeppolHeader.getReceiver()).andReturn(participantId);
        expect(mockRingoSmpLookup.isRegistered(participantId)).andReturn(true);
        expect(mockPeppolHeader.getSender()).andReturn(participantId);
        expect(mockPeppolHeader.getPeppolDocumentTypeId()).andReturn(PeppolDocumentTypeId.EHF_INVOICE);
        expect(mockPeppolHeader.getProfileId()).andReturn(null);

        replay(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

        try{
            validator.validateHeader();
            fail();
        } catch (InvalidUserInputWebException e) {
            assertEquals(e.getMessage(), "Wrong processId value: invalidProcessId");
        }

        verify(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

    }
    @Test
    public void testValidData() throws Exception {
        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").senderId("9908:976098897").documentId(PeppolDocumentTypeId.EHF_INVOICE.stringValue()).processId(ProfileId.Predefined.BII04_INVOICE_ONLY.stringValue()).build();
        validator = new PeppolMessageValidator(mockRingoSmpLookup, mockPeppolMessage, params);
        expect(mockPeppolMessage.getPeppolHeader()).andStubReturn(mockPeppolHeader);
        expect(mockPeppolHeader.getReceiver()).andReturn(participantId);
        expect(mockRingoSmpLookup.isRegistered(participantId)).andReturn(true);
        expect(mockPeppolHeader.getSender()).andReturn(participantId);
        expect(mockPeppolHeader.getPeppolDocumentTypeId()).andReturn(PeppolDocumentTypeId.EHF_INVOICE);
        expect(mockPeppolHeader.getProfileId()).andReturn(ProfileId.Predefined.BII04_INVOICE_ONLY);

        replay(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

        validator.validateHeader();

        verify(mockPeppolMessage, mockPeppolHeader, mockRingoSmpLookup);

    }


    @Test
    public void testValidateDocument() throws Exception {
        String xmlString = "<xml>invalid</xml>";
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());

        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").senderId("9908:976098897").documentId(PeppolDocumentTypeId.EHF_INVOICE.stringValue()).processId(ProfileId.Predefined.BII04_INVOICE_ONLY.stringValue()).inputStream(inputStream).build();
        PeppolMessage message = new PeppolMessageCreator(mockRingoSmpLookup, mockRingoAccount, params).extractDocument();

        validator = new PeppolMessageValidator(mockRingoSmpLookup, message, params);
        replay(mockPeppolHeader, mockRingoSmpLookup);

        try {
            validator.validateDocument();
            fail();
        } catch (InvalidUserInputWebException e) {
            assertTrue(e.getMessage().contains("Unable to validate the XML document"));
        }

        verify(mockPeppolHeader, mockRingoSmpLookup);

    }
}
