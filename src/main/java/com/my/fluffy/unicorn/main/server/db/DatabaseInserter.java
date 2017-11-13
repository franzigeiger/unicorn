package com.my.fluffy.unicorn.main.server.db;

import java.sql.Connection;

public class DatabaseInserter {
    private final DatabaseConnection db;

    DatabaseInserter(DatabaseConnection connection) {
        this.db = connection;
    }
}
