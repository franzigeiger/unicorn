package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.parser.BatchInserter;

import java.sql.SQLException;


public class BatchInserterMain {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        BatchInserter batchInserter = new BatchInserter(
                DatabaseConnection.create(),
                "complete.json");

        batchInserter.insertAll();
    }
}
