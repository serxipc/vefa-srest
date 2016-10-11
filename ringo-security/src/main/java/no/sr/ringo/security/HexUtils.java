package no.sr.ringo.security;

/**
 * @author steinar
 *         Date: 11.10.2016
 *         Time: 12.14
 */
public class HexUtils {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static byte[] fromHexString(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static char[] toHexChars(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for (int byteCounter = 0; byteCounter < bytes.length; byteCounter++) {
            int v = bytes[byteCounter] & 0xFF;

            // Shift right zero fill by 4
            hexChars[byteCounter * 2] = hexArray[v >>> 4];
            hexChars[byteCounter * 2 + 1] = hexArray[v & 0x0F];
        }
        return hexChars;
    }

    public static String toHexString(byte[] bytes) {
        return new String(toHexChars(bytes));
    }
}
