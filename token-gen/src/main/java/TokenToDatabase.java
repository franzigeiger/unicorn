import org.apache.commons.dbcp2.BasicDataSource;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.postgresql.PGConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

class TokenToDatabase {
    private static final String driver = "jdbc:postgresql://";
    private static final String driverClass = "org.postgresql.Driver";
    private static final String host = "localhost:5432/";
    private static final String database = "unicorn";
    private static final String schema = "election";
    private static final String username = "postgres";
    private static final String password = "root";

    private static BasicDataSource dataSource = null;

    static void storeTokensInDatabase(Stream<String> tokens) throws SQLException, ClassNotFoundException, IOException {
        File f = writeToTmpFile(tokens);
        System.out.println("File written. Inserting...");
        insertTmpToDb(f);
        if (!f.delete()) System.out.println("Could not delete tmp file");
    }

    private static void insertTmpToDb(File f) throws SQLException, IOException, ClassNotFoundException {
        try (Connection conn = create()) {
            conn.prepareStatement("DROP TABLE IF EXISTS election.tokens;").execute();
            conn.prepareStatement("CREATE TABLE election.tokens (token text PRIMARY KEY);").execute();
            CopyManager copyManager = conn.unwrap(PGConnection.class).getCopyAPI();
            copyManager.copyIn("COPY election.tokens FROM STDIN", new FileReader(f));
        }
    }

    private static File writeToTmpFile(Stream<String> tokens) throws IOException {
        File f = File.createTempFile("copy-sql", ".csv");
        Writer writer = new FileWriter(f);
        tokens.map(TokenToDatabase::ballotToTmpfileString).forEach(s -> {
            try {
                writer.write(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.close();
        return f;
    }

    private static Connection create() throws SQLException, ClassNotFoundException {
        if (dataSource == null) createDataSource();
        return dataSource.getConnection();
    }

    private static void createDataSource() throws ClassNotFoundException {
        Class.forName(driverClass);

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setUrl(driver + host + database + "?currentSchema=" + schema);
    }

    private static String ballotToTmpfileString(String token) {
        return token + "\n";
    }
}
