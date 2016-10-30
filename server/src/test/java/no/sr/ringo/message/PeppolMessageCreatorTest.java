package no.sr.ringo.message;

import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.peppol.SchemeId;
import no.sr.ringo.smp.RingoSmpLookup;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * User: Adam
 * Date: 4/17/13
 * Time: 12:54 PM
 */
public class PeppolMessageCreatorTest {


    RingoSmpLookup mockRingoSmpLookup;
    Account mockRingoAccount;

    PeppolParticipantId participantId;

    @BeforeMethod
    public void setUp() throws Exception {
        mockRingoSmpLookup = createStrictMock(RingoSmpLookup.class);
        mockRingoAccount = createStrictMock(Account.class);

        participantId = new PeppolParticipantId(SchemeId.NO_ORGNR, "976098897");

    }

    @Test
    public void testCreatePeppolMessage() throws Exception {

        InputStream is = PeppolMessageCreatorTest.class.getClassLoader().getResourceAsStream("ehf-test-SendRegning-HelseVest2.xml");
        OutboundPostParams params = new OutboundPostParams.Builder().recipientId("9908:976098897").senderId("9908:976098897").documentId(PeppolDocumentTypeId.EHF_INVOICE.stringValue()).processId(ProfileId.Predefined.BII04_INVOICE_ONLY.stringValue()).inputStream(is).build();
        PeppolMessageCreator creator = new PeppolMessageCreator(mockRingoSmpLookup, mockRingoAccount, params);

        replay(mockRingoAccount, mockRingoSmpLookup);

        creator.extractHeader();

        PeppolMessage message = creator.extractDocument();

        assertNotNull(message);
        assertEquals(PeppolDocumentTypeId.EHF_INVOICE, message.getPeppolHeader().getPeppolDocumentTypeId());
        assertEquals(ProfileId.Predefined.BII04_INVOICE_ONLY, message.getPeppolHeader().getProfileId());
        assertEquals(participantId, message.getPeppolHeader().getSender());
        assertEquals(participantId, message.getPeppolHeader().getReceiver());
        assertNotNull(message.getXmlMessage());

        verify(mockRingoAccount, mockRingoSmpLookup);

    }

}
