import java.security.SecureRandom;

public class Token {
    private final static SecureRandom rand = new SecureRandom();
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private final static char[] transferArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String genAlphaNumToken(int len) {
        char[] retVal = new char[len];
        for (int i = 0; i < len; i++) {
            retVal[i] = intToChar(rand.nextInt());
        }
        return new String(retVal);
    }

    public static char intToChar(int i) {
        i = i % 36;
        if (i < 0) {
            i += 36;
        }
        return transferArray[i];
    }
}
