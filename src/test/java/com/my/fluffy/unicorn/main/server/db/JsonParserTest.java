package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.data.*;
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
        System.out.println(jsonParser.allElectionDistricts.size());
        System.out.println(jsonParser.allCandidates.size());
        System.out.println(jsonParser.allStateLists.size());
    }

    @Test
    public void testBallotCreator() {
        BallotCreator creator = new BallotCreator("complete.json");
        ArrayList<ElectionDistrict> allDistricts = creator.allElectionDistricts;

        for (ElectionDistrict allDistrict : allDistricts) {
            ArrayList<Ballot> ballots = creator.createBallots(allDistrict);
        }
    }
}
