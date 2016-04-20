package no.sr.ringo.common;

import org.jdom.Element;

/**
 * Implementations are responsible for extracting a given entity from an XML element object.
 *
 * @author andy
 *
 * @param <T>
 */
public interface XmlSpecification<T> {
	
	/**
	 * the name of the entity, this is used only for logging.
	 * @return
	 */
	String getName();

	/**
	 * The xpath expression used to select node/nodes
	 * @return
	 */
	String getXPath();

	/**
	 * Implementations will try and extract the object
	 * from the provided XML element.
	 * 
	 * The exception will be caught by the helper and logged.
	 * 
	 * @param element
	 * @return
	 * @throws Exception if a problem occurs during parsing etc..
	 */
	T extractEntity(final Element element) throws Exception;

}
