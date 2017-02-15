package no.sr.ringo;

import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolDocumentTypeId;
import no.sr.ringo.account.Account;
import no.sr.ringo.account.AccountId;
import no.sr.ringo.account.CustomerId;
import no.sr.ringo.account.UserName;

import java.util.Date;

/**
 * Object which shall be used to create complex objects for testing.
 *
 * @author andy
 * @author adam
 * @author thore
 */
public class ObjectMother {

    public static Account getTestAccount(){
        return new Account(
                new CustomerId(1), "AndyAccount",
                new UserName("sr"), new Date(), getTestPassword(), new AccountId(1), false, true);
    }

    public static Account getAdamsAccount() {
        return new Account(
                new CustomerId(1), "AdamAccount",
                new UserName("adam"), new Date(), getTestPassword(), new AccountId(2), false, true);
    }

    public static Account getThoresAccount() {
        return new Account(
                new CustomerId(1), "ThoreAccount",
                new UserName("teedjay"), new Date(), getTestPassword(), new AccountId(3), false, true);
    }

    private static String getTestPassword() {
        return "ringo";
    }

    public static ParticipantId getTestParticipantIdForSMPLookup() {
        return new ParticipantId(RingoConstant.NORWEGIAN_PEPPOL_PARTICIPANT_PREFIX +RingoConstant.DIFI_ORG_NO);
    }

    public static ParticipantId getTestParticipantIdForConsumerReceiver() {
        return new ParticipantId("9999:01029400470");
    }

    public static ParticipantId getTestParticipantId() {
        return new ParticipantId(RingoConstant.NORWEGIAN_PEPPOL_PARTICIPANT_PREFIX +RingoConstant.DUMMY_ORG_NO);
    }

    public static ParticipantId getAdamsParticipantId() {
        return new ParticipantId(RingoConstant.NORWEGIAN_PEPPOL_PARTICIPANT_PREFIX +"988890081");
    }

    public static final PeppolDocumentTypeId getDocumentIdForBisInvoice() {
        return PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1");
    }

}
