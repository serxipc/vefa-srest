package no.sr.ringo.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * User: Adam
 * Date: 7/24/12
 * Time: 2:24 PM
 */
public class MessageHelper {
    /**
     * Returns i18n from ApplicationResources
     * (throws exception if key not found)
     *
     * @param key
     * @return translated message
     */
    public static String getMessage(String key) {
        ResourceBundle messages = ResourceBundle.getBundle("ApplicationResources");
        return messages.getString(key);
    }

    /**
     * Returns i18n from ApplicationResources
     * (throws exception if key not found)
     *
     * @param key
     * @param params - params to replace placeholders
     * @return translated message
     */
    public static String getMessage(String key, String... params) {
        String message = getMessage(key);

        if(params == null || params.length == 0) {
            return message;
        }

        MessageFormat formatter = new MessageFormat(message);
        return formatter.format(params);

    }
}
