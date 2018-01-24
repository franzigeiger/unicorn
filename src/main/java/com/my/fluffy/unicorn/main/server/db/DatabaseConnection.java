package com.my.fluffy.unicorn.main.server.db;

import org.jetbrains.annotations.NotNull;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class DatabaseConnection implements AutoCloseable {

    private final Connection connection;

    @javax.ws.rs.core.Context
    static ServletContext context;

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
            String result = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("sql/" + name), StandardCharsets.UTF_8);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
           throw new RuntimeException(e);
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
