package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.parser.CsvParser;
import org.junit.Test;


public class CsvParserTest {
    @Test
    public void testCsvParser() {
        CsvParser parser = new CsvParser(
                "complete.json",
                "candidates2013.csv");
                //"C:\\Users\\Ich\\Downloads\\2013parties.csv");
    }

/*    @Test
    public void testBallotCreator() {
        BallotCreator creator = new BallotCreator("complete.json");
        ArrayList<ElectionDistrict> allDistricts = creator.allElectionDistricts;

        for (ElectionDistrict allDistrict : allDistricts) {
            ArrayList<Ballot> ballots = creator.createBallots(allDistrict);
        }
    }*/
}
