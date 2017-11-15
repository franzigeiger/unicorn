package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.parser.data.*;
import com.my.fluffy.unicorn.main.server.parser.BallotCreator;
import com.my.fluffy.unicorn.main.server.parser.JsonParser;
import org.junit.Test;

import java.util.ArrayList;

public class JsonParserTest {
    @Test
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
        BallotCreator creator = new BallotCreator("complete.json");
        ArrayList<ElectionDistrictJson> allDistricts = creator.allElectionDistrictJsons;

        for (ElectionDistrictJson allDistrict : allDistricts) {
            ArrayList<BallotJson> ballotJsons = creator.createBallots(allDistrict);
        }
    }
}
