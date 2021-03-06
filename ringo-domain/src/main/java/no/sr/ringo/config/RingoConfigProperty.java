package no.sr.ringo.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the names of all known configuration property names.
 *
 * This class uses a nested enum in order to ensure that the name of the property
 * is only entered once, while allowing for use of the property names as string constants.
 *
 * The Google Guice @{@link com.google.inject.name.Named} annotation requires that the argument be a string constant,
 * i.e. not a method returning a String, hence this slightly over engineered implementation.
 *
 * @author steinar
 *         Date: 28.01.2017
 *         Time: 10.22
 */
public final class RingoConfigProperty {


    public static final String HOME_DIR_PATH = "ringo.home";
    public static final String PAYLOAD_BASE_PATH = "ringo.payload.basedir";
    public static final String PLUGIN_PATH = "ringo.plugin.path";
    public static final String REMOVE_SBDH = "ringo.payload.removesbdh";

    public static final String JDBC_CONNECTION_URI= "jdbc.connection.uri";
    public static final String JDBC_DRIVER_CLASS = "jdbc.driver.class.name";
    public static final String JDBC_CLASS_PATH = "jdbc.driver.class.path";
    public static final String JDBC_USER = "jdbc.user";
    public static final String JDBC_PASSWORD = "jdbc.password";
    public static final String JDBC_VALIDATION_QUERY = "jdbc.validation.query";

    public static final String BLOB_SERVICE_URI_REWRITER = "ringo.blob.uri.handler";

    private RingoConfigProperty() { // restricts instantiation
    }

    public static List<String> getPropertyNames() {


        Field[] declaredFields = RingoConfigProperty.class.getDeclaredFields();

        int publicStaticFinal = (Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC);

        List<String> result = new ArrayList<>();
        for (Field field : declaredFields) {
            int modifiers = field.getModifiers();

            // We are only interested in public static final String
            if ( (field.getModifiers() & publicStaticFinal) == publicStaticFinal
                && field.getType() == String.class) {

                try {
                    String propertyName = (String)field.get(null); // this is ok for static fields
                    result.add(propertyName);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("OOps! " + e.getMessage(), e);
                }
            }
        }
        return result;
    }

}
