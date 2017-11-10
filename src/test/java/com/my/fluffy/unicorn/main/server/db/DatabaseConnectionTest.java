package com.my.fluffy.unicorn.main.server.db;

import org.junit.Test;

import java.sql.SQLException;

public class DatabaseConnectionTest {
    @Test public void testConnection() throws SQLException, ClassNotFoundException {
        try(DatabaseConnection c = DatabaseConnection.create()){}
    }
}
