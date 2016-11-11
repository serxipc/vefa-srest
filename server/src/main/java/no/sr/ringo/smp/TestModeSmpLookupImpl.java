package no.sr.ringo.smp;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolDocumentTypeId;

import java.util.ArrayList;
import java.util.List;

/**
 * SMP lookup for test server, just returns a bunch of document types.
 *
 * @author andy
 * @author thore
 */
public class TestModeSmpLookupImpl implements RingoSmpLookup {


    @Override
    public boolean isRegistered(ParticipantId participantId) {
        return true;
    }

    @Override
    public SmpLookupResult fetchSmpMetaData(ParticipantId peppolParticipantId, LocalName localName) {

        List<PeppolDocumentTypeId> documentTypeIds = new ArrayList<PeppolDocumentTypeId>();

        // Adding all document types available in ELMA as of 2014-08-29 : https://smp.difi.no/documenttype

        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biitrns001:ver2.0:extended:urn:www.peppol.eu:bis:peppol28a:ver2.0:extended:urn:www.difi.no:ehf:ordre:ver1.0::2.1"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2::Catalogue##urn:www.cenbii.eu:transaction:biitrns019:ver2.0:extended:urn:www.peppol.eu:bis:peppol1a:ver2.0:extended:urn:www.difi.no:ehf:katalog:ver1.0::2.1"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2::OrderResponse##urn:www.cenbii.eu:transaction:biitrns076:ver2.0:extended:urn:www.peppol.eu::bis:peppol28a:ver1.0:extended:urn:www.difi.no:ehf:ordrebekreftelse:ver1.0::2.1"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.cenbii.eu:profile:biixx:ver2.0:extended:urn:www.difi.no:ehf:kreditnota:ver2.0::2.1"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0::2.1"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:www.cenbii.eu:transaction:biicoretrdm057:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:www.cenbii.eu:transaction:biicoretrdm058:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2::Catalogue##urn:www.cenbii.eu:transaction:biicoretrdm019:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol3a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:OrderResponseSimple-2::OrderResponseSimple##urn:www.cenbii.eu:transaction:biicoretrdm002:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:OrderResponseSimple-2::OrderResponseSimple##urn:www.cenbii.eu:transaction:biicoretrdm003:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse::ApplicationResponse##urn:www.cenbii.eu:transaction:biitrns058:ver2.0:extended:urn:www.difi.no:ehf:katalogbekreftelse:ver1.0::2.1"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Reminder-2::Reminder##urn:www.cenbii.eu:transaction:biicoretrdm017:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:purring:ver1::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0"));
        documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0"));
        //documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:www.cenbii.eu:transaction:biitrns071:ver2.0:extended:urn:www.peppol.eu:bis:peppol36a:ver1.0"));
        //documentTypeIds.add(PeppolDocumentTypeId.valueOf("urn:www.cenbii.eu:transaction:biitrns071:ver2.0:extended:urn:www.peppol.eu:bis:peppol36a:ver1.0"));

        if (!documentTypeIds.isEmpty()) {
            documentTypeIds = RingoSmpLookupImpl.extractDocumentTypesForLocalName(documentTypeIds, localName);
        }

        return new SmpLookupResult(documentTypeIds);
    }

    @Override
    public boolean isAcceptable(ParticipantId participantId, PeppolDocumentTypeId peppolDocumentTypeId) {
        return true;
    }

}
