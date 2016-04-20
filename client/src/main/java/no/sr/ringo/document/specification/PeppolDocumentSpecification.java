package no.sr.ringo.document.specification;

import no.sr.ringo.common.NamespaceAware;
import no.sr.ringo.common.XmlSpecification;
import org.jdom.Namespace;

import java.util.ArrayList;
import java.util.List;

/**
 * User: andy
 * Date: 10/29/12
 * Time: 2:27 PM
 */
public abstract class PeppolDocumentSpecification<T> implements NamespaceAware, XmlSpecification<T> {

    List<Namespace> result = new ArrayList<Namespace>();

    /**
     * the name of the entity, this is used only for logging.
     *
     * @return
     */
    public String getName() {
        return this.getClass().getName();
    }


    public List<Namespace> getNamespaces() {

        result.clear();
        //default namespace
        result.add(Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"));
        result.add(Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
        result.add(Namespace.getNamespace("cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"));
        result.add(Namespace.getNamespace("cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"));
        result.add(Namespace.getNamespace("ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2"));
        return result;
    }
}
