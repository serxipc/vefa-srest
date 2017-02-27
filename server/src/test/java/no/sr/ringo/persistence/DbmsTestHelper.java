package no.sr.ringo.persistence;

import com.google.inject.Inject;
import no.difi.oxalis.test.identifier.PeppolDocumentTypeIdAcronym;
import no.difi.oxalis.test.identifier.PeppolProcessTypeIdAcronym;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
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

         DocumentTypeIdentifier invoiceDocumentType = PeppolDocumentTypeIdAcronym.EHF_INVOICE.toVefa();
        ProcessIdentifier processTypeId = PeppolProcessTypeIdAcronym.INVOICE_ONLY.toVefa();

        return databaseHelper.createSampleMessage(invoiceDocumentType, processTypeId, "<test>\u00E5</test>", accountId, direction, senderValue, receiverValue, receptionId, delivered, new Date());
    }

}
