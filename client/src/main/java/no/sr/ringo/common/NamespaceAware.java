package no.sr.ringo.common;

import org.jdom.Namespace;

import java.util.List;

/**
 * Interface for XmlSpecifications that need to be aware of the XmlDocuments Namespace.
 *
 * User: andy
 * Date: 1/31/12
 * Time: 12:42 PM
 */
public interface NamespaceAware {

    public List<Namespace> getNamespaces();
}
