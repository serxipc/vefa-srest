package no.sr.ringo.peppol;

import no.sr.ringo.cenbiimeta.ProfileId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a PEPPOL Customization Identifier contained within a PEPPOL Document Identifier.
 *
 * <h1>The pattern for decoding BIS v1 customization identifiers</h1>
 * <pre>
 *     &lt;transactionId>:#&lt;extensionId>[#&lt;extensionId>]
 *
 *     Example :
 *     urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol3a:ver1.0
 *     transactionId    = urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0
 *     extensionId      = urn:www.peppol.eu:bis:peppol3a:ver1.0
 * </pre>
 * I.e. a string followed by ":#" followed by another string, followed by optional strings starting with "#".
 * <p>
 *     The first part represents the CEN/BII transaction identity (transaction data model),
 *     the second extension is the identity of the PEPPOL customization of that very transaction model,
 *     while the next optional extensions are the extension of the PEPPOL extension.
 * </p>
 *
 * <h1>The pattern for decoding BIS v2 customization identifiers</h1>
 * <pre>
 *     urn:www.cenbii.eu:transaction:&lt;bii_Transaction>:&lt;version>:(restrictive|extended|partly):&lt;customization>:<version>
 *
 *     Example :
 *     urn:www.cenbii.eu:transaction:biitrns001:ver2.0:extended:urn:www.peppol.eu:bis:peppol3a:ver2.0
 *     transactionId        = urn:www.cenbii.eu:transaction:biitrns001:ver2.0 (the transaction is biitrns001 and the version is 2.0)
 *     customizationType    = extended
 *     customization        = urn:www.peppol.eu:bis:peppol3a:ver2.0
 * </pre>
 * For BIS v2 one should be aware that the policy specify customizations to be treated atomically.
 * <p>
 * POLICY 11 PEPPOL Customization Identifiers :
 * The Customization Identifier is defined in the relevant PEPPOL BIS specification.
 * A PEPPOL Access Point MUST treat the identifier as an atomic string.
 * The definition of the customization identifier within the BIS specifications should be defined according to the CEN BII rules.
 * </p>
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 * @author Thore Johnsen thore@sendregning.no
 *
 * @see "PEPPOL Policy for use of identifiers v3.0" (2014-02-03)
 * @see "CWA16558-Annex-C-BII-Guideline-ConformanceAndCustomizations-V1_0_0" (2012-12-06)
 */
public class CustomizationIdentifier {

    /**
     * First group matches any number lazily (? == non greedy)
     * Second group matches the first occurrence of the named 4
     * Third group matches the remaining (greedy as can be)
     */
    private static Pattern customizationIdentifierPattern = Pattern.compile("(.*?)(:#|:restrictive:|:extended:|:partly:)(.*)");

    /**
     * Must start with first group
     * Second group matches lazily (? == non greedy)
     * Third group matches end of text or next extension type ($ == end of text)
     */
    private static Pattern customizationIdentifyFirstPattern = Pattern.compile("^(:#|:restrictive:|:extended:|:partly:)(.*?)($|#|:restrictive:|:extended:|:partly:)");

    private final TransactionIdentifier transactionIdentifier;
    private final String extensions;

    private CustomizationIdentifier(TransactionIdentifier transactionIdentifier, String extensions) {
        this.transactionIdentifier = transactionIdentifier;
        this.extensions = extensions;
    }

    /**
     * Parses the supplied string as a PEPPOL Customization identifier
     * @param s the string to be parsed
     * @return instantiated CustomizationIdentifier
     */
    public static CustomizationIdentifier valueOf(String s) {

        CustomizationIdentifier result;

        Matcher matcher = customizationIdentifierPattern.matcher(s);

        if (matcher.find() && matcher.groupCount() == 3){
            String transactionId = matcher.group(1);
            TransactionIdentifier transactionIdentifier = TransactionIdentifier.valueFor(transactionId);
            String customizationPart = matcher.group(2) + matcher.group(3);
            result = new CustomizationIdentifier(transactionIdentifier, customizationPart);
        } else {
            // allow for a single urn: - it's just a string ....
            if (!s.startsWith("urn:")) throw new IllegalArgumentException(s + " not recognized as customization identifier");
            result = new CustomizationIdentifier(TransactionIdentifier.valueFor(s), s);
        }

        return result;

    }

    /**
     * Returns the complete extensions string (the part after the transactionId).
     * Complete with :extends: (BIS v2 style) or :# (BIS v1 style)
     */
    public String getFullExtensionIdentifier() {
        return extensions;
    }

    /**
     * Returns the first extension string without the extension modifier,
     * ie without the :extends: (BIS v2 style) or :# (BIS v1 style)
     */
    public ProfileId getPeppolExtensionIdentifier() {
        String firstExtension;
        Matcher matcher = customizationIdentifyFirstPattern.matcher(extensions);
        if (!matcher.find() || matcher.groupCount() < 3) {
            firstExtension = extensions;
        } else {
            firstExtension = matcher.group(2);
        }
        return ProfileId.valueOf(firstExtension);
    }

    public boolean containsExtension(ProfileId profileId) {
        return extensions.contains(profileId.stringValue());
    }

    @Override
    public String toString() {
        // this is a workaround for customization id's that only have zero extensions
        if (transactionIdentifier.toString().equals(extensions)) return extensions;
        return transactionIdentifier + extensions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomizationIdentifier that = (CustomizationIdentifier) o;
        if (!extensions.equals(that.extensions)) return false;
        if (!transactionIdentifier.equals(that.transactionIdentifier)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = transactionIdentifier.hashCode();
        result = 31 * result + extensions.hashCode();
        return result;
    }

}
