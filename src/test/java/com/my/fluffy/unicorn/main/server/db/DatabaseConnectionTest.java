package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.data.Candidate;
import com.my.fluffy.unicorn.main.server.data.Election;
import com.my.fluffy.unicorn.main.server.data.Party;
import com.my.fluffy.unicorn.main.server.data.State;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;

public class DatabaseConnectionTest {
    @Test
    public void testConnection() throws SQLException, ClassNotFoundException {
        try (DatabaseConnection c = DatabaseConnection.create()) {
            c.getInserter().insertCandidate(new Candidate(null, "Tobias", "Beeh", null, null, null, null, -1));
            c.getInserter().insertElection(new Election(LocalDate.now()));
            c.getInserter().insertParty(new Party("Meine neue tolle Partei!"));
            c.getInserter().insertState(new State("Bayern"));
        }
    }
}
