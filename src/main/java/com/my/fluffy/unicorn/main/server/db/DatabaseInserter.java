package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.data.Candidate;
import com.my.fluffy.unicorn.main.server.data.Election;
import com.my.fluffy.unicorn.main.server.data.Party;
import com.my.fluffy.unicorn.main.server.data.State;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseInserter {
    private final DatabaseConnection db;

    DatabaseInserter(DatabaseConnection connection) {
        this.db = connection;
    }

    public void insertCandidate(Candidate candidate) throws SQLException {
        String query = "INSERT INTO " +
                "candidates(title, firstname, lastname, profession, sex, hometown, birthtown, yearofbirth) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        stmt.setString(1, candidate.getTitle());
        stmt.setString(2, candidate.getFirstName());
        stmt.setString(3, candidate.getLastName());
        stmt.setString(4, candidate.getProfession());
        stmt.setString(5, candidate.getSex());
        stmt.setString(6, candidate.getHometown());
        stmt.setString(7, candidate.getBirthtown());
        stmt.setInt(8, candidate.getYearOfBirth());
        stmt.executeUpdate();
        stmt.close();
    }

    public void insertElection(Election election) throws SQLException {
        String query = "INSERT INTO elections (year, day) VALUES (?,?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        stmt.setInt(1, election.getYear());
        stmt.setDate(2, Date.valueOf(election.getDate()));
        stmt.executeUpdate();
        stmt.close();
    }

    public void insertParty(Party party) throws SQLException {
        String query = "INSERT INTO parties (name) VALUES (?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        stmt.setString(1, party.getName());
        stmt.executeUpdate();
        stmt.close();
    }

    public void insertState(State state) throws SQLException {
        String query = "INSERT INTO states (name) VALUES (?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        stmt.setString(1, state.getName());
        stmt.executeUpdate();
        stmt.close();
    }
}
