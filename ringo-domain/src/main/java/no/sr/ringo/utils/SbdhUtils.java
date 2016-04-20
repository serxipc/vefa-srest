package no.sr.ringo.utils;

/**
 * Retrieves the payload from SBDH document the fastest way possible using simple string operations.
 *
 * @author thore
 */
public class SbdhUtils {

    /**
     * Returns the main XML without the SBDH envelope.
     * Between : </StandardBusinessDocumentHeader> and </StandardBusinessDocument>
     * @param xmlWithSbdh
     * @return
     */
    public static String removeSbdhEnvelope(String xmlWithSbdh) {

        if (xmlWithSbdh == null) return xmlWithSbdh;

        int begin = xmlWithSbdh.indexOf("</StandardBusinessDocumentHeader>"); // 33 chars
        int end = xmlWithSbdh.indexOf("</StandardBusinessDocument>"); // 27 chars

        // if we did not find the SBDH just return the input
        if ((begin == -1) || (end == -1)) {
            return xmlWithSbdh;
        }

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xmlWithSbdh.substring(begin + 33, end).trim(); // adjust for length of tag

    }


}
