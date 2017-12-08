package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.parser.BatchInserter;
import com.my.fluffy.unicorn.main.server.parser.data.CandidateJson;
import org.junit.Test;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;


public class BatchInserterTest {
    @Test
    public void testBatchInserter() throws SQLException, ClassNotFoundException {
        BatchInserter batchInserter = new BatchInserter(
                DatabaseConnection.create(),
                "complete.json");

        batchInserter.insertAll();
    }
}
