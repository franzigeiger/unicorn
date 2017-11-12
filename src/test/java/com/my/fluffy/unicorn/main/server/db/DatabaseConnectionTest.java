package com.my.fluffy.unicorn.main.server.db;

import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseConnectionTest {
    /*@Test
    public void testConnection() throws SQLException, ClassNotFoundException {
        try (DatabaseConnection c = DatabaseConnection.create()) {
        }
    }*/

    @Test
    public void testParser() {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parseAll("C:\\Users\\Ich\\Downloads\\complete.json");

        System.out.println(jsonParser.allParties.size());
        System.out.println(jsonParser.allStates.size());
        System.out.println(jsonParser.allElectionDistricts.size());
        System.out.println(jsonParser.allCandidates.size());
    }

    @Test
    public void testBallotCreator() {
        BallotCreator creator = new BallotCreator("C:\\Users\\Ich\\Downloads\\complete.json");
        ArrayList<ElectionDistrict> allDistricts = creator.allElectionDistricts;

        for(int i = 0; i < allDistricts.size(); i++){
            try {
                ArrayList<Ballot> ballots = creator.createBallots(allDistricts.get(i));
            } catch(Exception e){
                throw e;
            }
        }
    }
}
