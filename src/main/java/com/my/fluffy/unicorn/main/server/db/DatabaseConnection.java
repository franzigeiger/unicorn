package com.my.fluffy.unicorn.main.server.db;

import java.sql.*;

public class DatabaseConnection implements AutoCloseable {
    private static final String driver = "jdbc:postgresql://";
    private static final String host = "localhost:5432/";
    private static final String database = "unicorn";
    private static final String username = "postgres";
    private static final String password = "123";
    private final Connection connection;

    private DatabaseConnection(Connection connection) {
        this.connection = connection;
    }

    @org.jetbrains.annotations.NotNull
    public static DatabaseConnection create() throws SQLException, ClassNotFoundException {
        Connection c = ConnectionFactory.create();
        return new DatabaseConnection(c);
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
