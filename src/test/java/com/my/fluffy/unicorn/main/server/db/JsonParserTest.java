package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.parser.data.*;
import com.my.fluffy.unicorn.main.server.parser.BallotCreator;
import com.my.fluffy.unicorn.main.server.parser.JsonParser;
import org.junit.Test;

import java.util.ArrayList;

public class JsonParserTest {
    /*@Test
    public void testParser() {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseAll("complete.json");

        System.out.println(jsonParser.allParties.size());
        System.out.println(jsonParser.allStates.size());
        System.out.println(jsonParser.allElectionDistrictJsons.size());
        System.out.println(jsonParser.allCandidates2017.size());
        System.out.println(jsonParser.allStateListJsons.size());
    }

    @Test
    public void testBallotCreator() {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseAll("complete.json");
        BallotCreator creator = new BallotCreator(jsonParser.allParties,
                jsonParser.allElectionDistrictJsons,
                jsonParser.allCandidates2017);
        ArrayList<ElectionDistrictJson> allDistricts = creator.allElectionDistrictJsons;

        int counter = 0;
        for (ElectionDistrictJson allDistrict : allDistricts) {
            ArrayList<BallotJson> ballotJsons = creator.createBallots2017(allDistrict);
            System.out.println(ballotJsons.size());
            counter += ballotJsons.size();
        }
        System.out.println(counter);

    }*/
}
