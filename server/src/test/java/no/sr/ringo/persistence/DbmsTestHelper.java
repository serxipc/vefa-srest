package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.identifier.PeppolDocumentTypeId;
import eu.peppol.identifier.PeppolDocumentTypeIdAcronym;
import eu.peppol.identifier.PeppolProcessTypeId;
import eu.peppol.identifier.PeppolProcessTypeIdAcronym;
import eu.peppol.persistence.TransferDirection;
import eu.peppol.persistence.jdbc.util.DatabaseHelper;

import java.util.Date;

/**
 * @author steinar
 *         Date: 17.01.2017
 *         Time: 18.49
 */
public class DbmsTestHelper {

    private final DatabaseHelper databaseHelper;

    @Inject
    public DbmsTestHelper(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }


    public Long createMessage(Integer accountId, TransferDirection direction, String senderValue, String receiverValue, final String uuid, Date delivered) {
        PeppolDocumentTypeId invoiceDocumentType = PeppolDocumentTypeIdAcronym.EHF_INVOICE.getDocumentTypeIdentifier();
        PeppolProcessTypeId processTypeId = PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId();

        return databaseHelper.createMessage(invoiceDocumentType, processTypeId, "<test>\u00E5</test>", accountId, direction, senderValue, receiverValue, uuid, delivered, new Date());
    }


    public Long createDummyMessage(Integer accountId, TransferDirection direction, String senderValue, String receiverValue, final String uuid, Date delivered, Date received) {
        PeppolDocumentTypeId invoiceDocumentType = PeppolDocumentTypeIdAcronym.EHF_INVOICE.getDocumentTypeIdentifier();
        PeppolProcessTypeId processTypeId = PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId();
        return databaseHelper.createMessage(invoiceDocumentType, processTypeId, "<test>\u00E5</test>", accountId, direction, senderValue, receiverValue, uuid, delivered, received);
    }
}
