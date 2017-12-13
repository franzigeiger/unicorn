import java.security.SecureRandom;

public class Token {
    private static SecureRandom rand = new SecureRandom();
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String genToken(int len) {
        byte[] bytes = new byte[len];
        rand.nextBytes(bytes);
        return bytesToHex(bytes);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
