package com.my.fluffy.unicorn.main.server.parser;

import com.my.fluffy.unicorn.main.server.db.DatabaseConnection;

public class BatchInserter {
    private final DatabaseConnection connection;

    public BatchInserter(DatabaseConnection connection) {
        this.connection = connection;
    }

    public void insertAll() {
        // TODO implement
    }
}
