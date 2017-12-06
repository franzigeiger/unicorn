package com.my.fluffy.unicorn.main.server.db;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

class ConnectionFactory {
    private static final String driver = "jdbc:postgresql://";
    private static final String driverClass = "org.postgresql.Driver";
    private static final String host = "localhost:5432/";
    private static final String database = "unicorn";
    private static final String schema = "election";
    private static final String username = "postgres";
    private static final String password = "sspw7c!";

    private Connection connection;

    static Connection create() throws SQLException, ClassNotFoundException {
        return new ConnectionFactory().getConnection();
    }

    private ConnectionFactory() throws ClassNotFoundException, SQLException {
        loadDriver();
        this.connection = open();
    }

    private static Connection open() throws SQLException, ClassNotFoundException {
        return open(driver + host + database + "?currentSchema=" + schema, username, password);
    }

    @Contract(pure = true)
    private Connection getConnection() {
        return connection;
    }

    private static Connection open(@NotNull String url, @NotNull String user, @NotNull String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static void loadDriver() throws ClassNotFoundException {
        Class.forName(driverClass);
    }
}
