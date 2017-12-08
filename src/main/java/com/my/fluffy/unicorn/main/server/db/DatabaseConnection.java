package com.my.fluffy.unicorn.main.server.db;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class DatabaseConnection implements AutoCloseable {
    private final Connection connection;

    private DatabaseConnection(Connection connection) {
        this.connection = connection;
    }

    @NotNull
    public static DatabaseConnection create() {
        try {
            Connection c = ConnectionFactory.create();
            return new DatabaseConnection(c);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static String getQuery(String name) {
        try {
            Path path = new File(DatabaseConnection.class.getClassLoader().getResource("sql/" + name).getFile()).toPath();
            return Files.readAllLines(path).stream().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    Connection getConnection() {
        return connection;
    }

    public DatabaseInserter getInserter() {
        return new DatabaseInserter(this);
    }

    public DatabaseQuery getQuery() {
        return new DatabaseQuery(this);
    }

    public DatabaseStatements getStatements() {
        return new DatabaseStatements(this);
    }

    /**
     * WARNING: will take some time.
     */
    public void updateAggregates() throws SQLException {
        String query = DatabaseConnection.getQuery("re-aggregate.sql");
        PreparedStatement stmt = getConnection().prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();
    }

    public void close() {
        if (this.connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
