package no.sr.ringo.resource;

import no.sr.ringo.common.RingoConstants;

import javax.ws.rs.core.MediaType;

/**
 * Wraps a Media type with the charset set.
 * User: andy
 * Date: 2/28/12
 * Time: 10:48 AM
 */
public class RingoMediaType {

    public static final String APPLICATION_XML = MediaType.APPLICATION_XML + ";charset=" + RingoConstants.DEFAULT_CHARACTER_SET;

    public static final String TEXT_PLAIN = MediaType.TEXT_PLAIN + ";charset=" + RingoConstants.DEFAULT_CHARACTER_SET;
}
