package com.my.fluffy.unicorn.main.server.db;

import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

abstract class ConnectionFactory {
    private static final String driver = "jdbc:postgresql://";
    private static final String host = "localhost:5432/";
    private static final String database = "unicorn";
    private static final String username = "postgres";
    private static final String password = "123";

    @NotNull
    public static Connection create() throws SQLException, ClassNotFoundException {
        loadDriver();
        try {
            return create(driver + host + database, username, password);
        } catch (SQLException e) {
            System.out.println("Creating database...");
            createDatabase(driver + host, database, username, password);
            return create(driver + host + database, username, password);
        }
    }

    private static void createDatabase(String url, String database, String username, String password) throws SQLException {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            PreparedStatement stmt = c.prepareStatement("CREATE DATABASE " + database);
            stmt.executeUpdate();
        }
    }

    private static Connection create(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static void loadDriver() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
    }
}
