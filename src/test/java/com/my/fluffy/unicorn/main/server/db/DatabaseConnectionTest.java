package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.client.data.*;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class DatabaseConnectionTest {
    /*@Test
    public void testConnection() throws SQLException, ClassNotFoundException {
        try (DatabaseConnection c = DatabaseConnection.create()) {
            c.getInserter().insertCandidate(Candidate.minCreate("Tobias", "Beeh", -1));
            c.getInserter().insertElection(Election.create(new Date()));
            c.getInserter().insertParty(Party.create("Meine neue tolle Partei!"));
            c.getInserter().insertState(State.create("Bayern"));
            c.getInserter().insertDistrict(District.create(1, Election.create(new Date()), State.create("Bayern"), "Augsburg Uni", 2));

            c.getQuery().getCandidate(Candidate.minCreate("Tobias", "Beeh", -1));
            System.out.println(c.getQuery().getDistrict(District.minCreate(1, Election.create(new Date()), State.create("Bayern"), 2)));
            System.out.println(c.getQuery().getDistrict(District.minCreate(1, Election.create(new Date()), State.create("Bayern"), 2)));
        }
    }*/
}
