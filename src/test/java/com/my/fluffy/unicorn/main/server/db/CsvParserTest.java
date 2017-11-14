package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.parser.CsvParser;
import org.junit.Test;


public class CsvParserTest {
    @Test
    public void testCsvParser() {
        CsvParser parser = new CsvParser(
                "complete.json",
                "candidates2013.csv");
    }
}
