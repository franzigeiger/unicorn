package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.parser.BatchInserter;
import org.junit.Test;

import java.sql.SQLException;


public class BatchInserterTest {
    @Test
    public void testBatchInserter() throws SQLException, ClassNotFoundException {
        BatchInserter batchInserter = new BatchInserter(
                DatabaseConnection.create(),
                "complete.json");

        batchInserter.insertAll();
    }
}
