import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        int numOfTokens = Integer.parseInt(args[0]);
        int prefixLen = Integer.parseInt(args[1]);
        int tokenLen = Integer.parseInt(args[2]);
        int distinctPrefixCount = getPrefixCount(prefixLen);

        // distribute num of tokens on prefixes
        int[] count = new int[distinctPrefixCount];
        for (int i = 0; i < distinctPrefixCount; i++) {
            count[i] = numOfTokens / distinctPrefixCount;
            if (i < numOfTokens % distinctPrefixCount) {
                count[i]++;
            }
        }

        TokenToDatabase.storeTokensInDatabase(
                IntStream.range(0, distinctPrefixCount)
                        .boxed()
                        .flatMap(i ->
                                distinctTokensWithPrefix(count[i], tokenLen, getPrefix(i, prefixLen)).peek(System.out::println)));
    }

    private static int getPrefixCount(int prefixLen) {
        return (int) Math.pow(36, prefixLen);
    }

    private static Stream<String> distinctTokensWithPrefix(int count, int length, String prefix) {
        return Stream.generate(() -> Token.genAlphaNumToken(length - prefix.length())).distinct().limit(count).map(t -> prefix + t);
    }

    private static String getPrefix(long num, int len) {
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            long val = Math.round(Math.pow(36, i));
            int c = (int) (num / val) % 36;
            chars[i] = Token.intToChar(c);
        }
        return new String(chars);
    }
}
