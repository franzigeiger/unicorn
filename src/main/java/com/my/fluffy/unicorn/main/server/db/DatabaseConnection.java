package com.my.fluffy.unicorn.main.server.db;

import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class DatabaseConnection implements AutoCloseable {
    private final Connection connection;

    private DatabaseConnection(Connection connection) {
        this.connection = connection;
    }

    @NotNull
    public static DatabaseConnection create() throws SQLException, ClassNotFoundException {
        Connection c = ConnectionFactory.create();
        return new DatabaseConnection(c);
    }

    protected Connection getConnection() {
        return connection;
    }

    public DatabaseInserter getInserter() {
        return new DatabaseInserter(this);
    }

    public DatabaseQuery getQuery() {
        return new DatabaseQuery(this);
    }

    public void updateAggregates() {
        // TODO implement
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
