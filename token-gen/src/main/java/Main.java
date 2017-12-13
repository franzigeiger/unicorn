import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        int numOfTokens = Integer.parseInt(args[0]);
        int distinctPrefixCount = getPrefixCount(Integer.parseInt(args[1]));

        // distribute num of tokens on prefixes
        int[] count = new int[distinctPrefixCount];
        for (int i = 0; i < distinctPrefixCount; i++) {
            count[i] = numOfTokens / distinctPrefixCount;
            if (i < numOfTokens % distinctPrefixCount) {
                count[i]++;
            }
        }

        TokenToDatabase.storeTokensInDatabase(IntStream.range(0, distinctPrefixCount).boxed().flatMap(i ->
                distinctTokensWithPrefix(count[i], String.format("%02X", i))));
    }

    private static int getPrefixCount(int prefixLen) {
        return (int) Math.pow(16, prefixLen);
    }

    private static Stream<String> distinctTokensWithPrefix(int count, String prefix) {
        return Stream.generate(() -> Token.genToken(12 - prefix.length())).distinct().limit(count).map(t -> prefix + t);
    }
}
