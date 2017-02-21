package no.sr.ringo.message;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.sr.ringo.account.Account;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.resource.InvalidUserInputWebException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * User: Adam
 * Date: 4/17/13
 * Time: 11:07 AM
 */
public class PeppolMessageValidatorTest {

    PeppolMessage mockPeppolMessage;
    PeppolHeader mockPeppolHeader;
    Account mockRingoAccount;

    PeppolMessageValidator validator;
    ParticipantIdentifier participantId;

    @BeforeMethod
    public void setUp() throws Exception {
        mockPeppolMessage = createStrictMock(PeppolMessage.class);
        mockPeppolHeader = createStrictMock(PeppolHeader.class);
        mockRingoAccount = createStrictMock(Account.class);

        participantId =  ParticipantIdentifier.of("9908:976098897");

    }



    @Test
    public void testValidateWrongSender() throws Exception {
        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").senderId("InvalidSenderId").build();
        validator = new PeppolMessageValidator( mockPeppolMessage, params);
        expect(mockPeppolMessage.getPeppolHeader()).andStubReturn(mockPeppolHeader);
        expect(mockPeppolHeader.getReceiver()).andReturn(participantId);
        expect(mockPeppolHeader.getSender()).andReturn(null);

        replay(mockPeppolMessage, mockPeppolHeader);

        try{
            validator.validateHeader();
            fail();
        } catch (InvalidUserInputWebException e) {
            assertEquals(e.getMessage(), "Wrong senderId value: InvalidSenderId");
        }

        verify(mockPeppolMessage, mockPeppolHeader);

    }

    @Test
    public void testValidateDocumentId() throws Exception {
        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").senderId("9908:976098897").documentId("invalidDocumentId").build();
        validator = new PeppolMessageValidator( mockPeppolMessage, params);
        expect(mockPeppolMessage.getPeppolHeader()).andStubReturn(mockPeppolHeader);
        expect(mockPeppolHeader.getReceiver()).andReturn(participantId);
        expect(mockPeppolHeader.getSender()).andReturn(participantId);
        expect(mockPeppolHeader.getPeppolDocumentTypeId()).andReturn(null);

        replay(mockPeppolMessage, mockPeppolHeader);

        try{
            validator.validateHeader();
            fail();
        } catch (InvalidUserInputWebException e) {
            assertEquals(e.getMessage(), "Wrong documentId value: invalidDocumentId");
        }

        verify(mockPeppolMessage, mockPeppolHeader);

    }

    @Test
    public void testValidateProcessId() throws Exception {
        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").senderId("9908:976098897").documentId(PeppolDocumentTypeId.EHF_INVOICE.stringValue()).processId("invalidProcessId").build();
        validator = new PeppolMessageValidator(mockPeppolMessage, params);
        expect(mockPeppolMessage.getPeppolHeader()).andStubReturn(mockPeppolHeader);
        expect(mockPeppolHeader.getReceiver()).andReturn(participantId);
        expect(mockPeppolHeader.getSender()).andReturn(participantId);
        expect(mockPeppolHeader.getPeppolDocumentTypeId()).andReturn(PeppolDocumentTypeId.EHF_INVOICE.toVefa()).times(1);
        expect(mockPeppolHeader.getProcessIdentifier()).andReturn(null);

        replay(mockPeppolMessage, mockPeppolHeader);

        try{
            validator.validateHeader();
            fail();
        } catch (InvalidUserInputWebException e) {
            assertEquals(e.getMessage(), "Wrong processId value: invalidProcessId");
        }

        verify(mockPeppolMessage, mockPeppolHeader);

    }
    @Test
    public void testValidData() throws Exception {
        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").senderId("9908:976098897").documentId(PeppolDocumentTypeId.EHF_INVOICE.stringValue()).processId(ProfileId.Predefined.BII04_INVOICE_ONLY.stringValue()).build();
        validator = new PeppolMessageValidator(mockPeppolMessage, params);
        expect(mockPeppolMessage.getPeppolHeader()).andStubReturn(mockPeppolHeader);

        expect(mockPeppolHeader.getReceiver()).andReturn(participantId);
        expect(mockPeppolHeader.getSender()).andReturn(participantId);
        expect(mockPeppolHeader.getPeppolDocumentTypeId()).andReturn(PeppolDocumentTypeId.EHF_INVOICE.toVefa()).times(1);
        expect(mockPeppolHeader.getProcessIdentifier()).andReturn(ProfileId.Predefined.BII04_INVOICE_ONLY.toVefa());

        replay(mockPeppolMessage, mockPeppolHeader);

        validator.validateHeader();

        verify(mockPeppolMessage, mockPeppolHeader);

    }


    @Test
    public void testValidateDocument() throws Exception {
        String xmlString = "<xml>invalid</xml>";
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());

        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").senderId("9908:976098897").documentId(PeppolDocumentTypeId.EHF_INVOICE.stringValue()).processId(ProfileId.Predefined.BII04_INVOICE_ONLY.stringValue()).inputStream(inputStream).build();
        PeppolMessage message = new PeppolMessageCreator(mockRingoAccount, params).extractDocument();

        validator = new PeppolMessageValidator(message, params);
        replay(mockPeppolHeader);

        try {
            validator.validateDocument();
            fail();
        } catch (InvalidUserInputWebException e) {
            assertTrue(e.getMessage().contains("Unable to validate the XML document"));
        }

        verify(mockPeppolHeader);

    }
}
