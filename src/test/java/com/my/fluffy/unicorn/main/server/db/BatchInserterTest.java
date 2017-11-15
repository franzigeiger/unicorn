package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.data.Election;
import com.my.fluffy.unicorn.main.server.parser.BatchInserter;
import org.junit.Test;

import java.time.LocalDate;


public class BatchInserterTest {
    @Test
    public void testBatchInserter() {
        BatchInserter batchInserter = new BatchInserter(
                null,
                "complete.json",
                "candidates2013.csv");
    }
}
