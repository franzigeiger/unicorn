package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.parser.CsvParser;
import com.my.fluffy.unicorn.main.server.parser.data.ElectionDistrictJson;
import com.my.fluffy.unicorn.main.server.parser.data.PartyJson;
import org.junit.Test;

import java.util.ArrayList;


public class CsvParserTest {
    @Test
    public void testCsvParser() {
        CsvParser parser = new CsvParser(
                "complete.json",
                "candidates2013.csv",
                "results2013.csv");
    }
}
