package com.my.fluffy.unicorn.main.server.db;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Arrays;
import java.util.stream.Collectors;

class ConnectionFactory {
    private static final String driver = "jdbc:postgresql://";
    private static final String host = "localhost:5432/";
    private static final String database = "unicorn";
    private static final String username = "postgres";
    private static final String password = "123";
    private static final String driverClass = "org.postgresql.Driver";

    private Connection connection;

    @NotNull
    public static Connection create() throws SQLException, ClassNotFoundException {
        return new ConnectionFactory().getConnection();
    }

    private ConnectionFactory() throws ClassNotFoundException, SQLException {
        loadDriver();
        this.connection = openOrCreate();
        ensureAllTablesExist();
    }

    private void ensureAllTablesExist() throws SQLException {
        createEnum("SEX", "M", "W");
        createTable("candidates",
                "ID INTEGER NOT NULL",
                "NAME TEXT NOT NULL",
                "FIRSTNAME TEXT NOT NULL",
                "TITLE TEXT",
                "SEX SEX NOT NULL",
                "HOMETOWN TEXT NOT NULL",
                "PROFESSION TEXT",
                "BIRTHYEAR INTEGER NOT NULL",
                "BIRTHPLACE TEXT NOT NULL");
        DatabaseMetaData metaData = this.connection.getMetaData();
        printResultSet(metaData.getTables(null, null, "%", new String[]{"TABLE"}));
        printResultSet(metaData.getColumns(null, null, "candidates", null));
        printResultSet(connection.prepareStatement("SELECT *" +
                "FROM        pg_type t \n" +
                "LEFT JOIN   pg_catalog.pg_namespace n ON n.oid = t.typnamespace \n" +
                "WHERE       (t.typrelid = 0 OR (SELECT c.relkind = 'c' FROM pg_catalog.pg_class c WHERE c.oid = t.typrelid)) \n" +
                "AND     NOT EXISTS(SELECT 1 FROM pg_catalog.pg_type el WHERE el.oid = t.typelem AND el.typarray = t.oid)\n" +
                "AND     n.nspname NOT IN ('pg_catalog', 'information_schema')\n").executeQuery());
    }

    private void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        for (int i = 1; i <= columnsNumber; i++) {
            System.out.print(rsmd.getColumnName(i) + " | ");
        }
        System.out.println();
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                System.out.print(rs.getString(i) + " | ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void createEnum(String enumName, String... constants) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TYPE ").append(enumName).append(" AS ENUM (");
        sb.append(Arrays.stream(constants).map(s -> '\'' + s + '\'').collect(Collectors.joining(",")));
        sb.append(");");
        PreparedStatement stmt = connection.prepareStatement(sb.toString());
        stmt.executeUpdate();
    }

    private void createTable(String tableName, String... params) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName).append(" (");
        sb.append(Arrays.stream(params).collect(Collectors.joining(",")));
        sb.append(");");
        PreparedStatement stmt = connection.prepareStatement(sb.toString());
        stmt.executeUpdate();
    }

    private static Connection openOrCreate() throws SQLException, ClassNotFoundException {
        try {
            return open(driver + host + database, username, password);
        } catch (SQLException e) {
            System.out.println("Creating database...");
            createDatabase(driver + host, database, username, password);
            return open(driver + host + database, username, password);
        }
    }

    @Contract(pure = true)
    private Connection getConnection() {
        return connection;
    }

    private static Connection open(@NotNull String url, @NotNull String user, @NotNull String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static void createDatabase(@NotNull String url, @NotNull String database, @NotNull String username, @NotNull String password) throws SQLException {
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            PreparedStatement stmt = c.prepareStatement("CREATE DATABASE " + database);
            stmt.executeUpdate();
        }
    }

    private static void loadDriver() throws ClassNotFoundException {
        Class.forName(driverClass);
    }
}
