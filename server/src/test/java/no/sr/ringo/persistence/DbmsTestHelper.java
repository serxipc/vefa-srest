package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.identifier.PeppolDocumentTypeId;
import eu.peppol.identifier.PeppolDocumentTypeIdAcronym;
import eu.peppol.identifier.PeppolProcessTypeId;
import eu.peppol.identifier.PeppolProcessTypeIdAcronym;
import no.sr.ringo.message.ReceptionId;
import no.sr.ringo.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.transport.TransferDirection;

import java.util.Date;

/**
 * @author steinar
 *         Date: 17.01.2017
 *         Time: 18.49
 *
 *         TODO: create method which will create new message without the Reception parameter
 */
public class DbmsTestHelper {

    private final DatabaseHelper databaseHelper;

    @Inject
    public DbmsTestHelper(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }


    public Long createSampleMessage(Integer accountId, TransferDirection direction, String senderValue, String receiverValue, final ReceptionId receptionId, Date delivered) {

        if (receptionId == null) {
            throw new IllegalArgumentException("ReceptionId is required argument");
        }

        PeppolDocumentTypeId invoiceDocumentType = PeppolDocumentTypeIdAcronym.EHF_INVOICE.getDocumentTypeIdentifier();
        PeppolProcessTypeId processTypeId = PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId();

        return databaseHelper.createSampleMessage(invoiceDocumentType, processTypeId, "<test>\u00E5</test>", accountId, direction, senderValue, receiverValue, receptionId, delivered, new Date());
    }

}
