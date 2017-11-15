package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.data.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseQuery {
    private DatabaseConnection db;

    DatabaseQuery(DatabaseConnection connection) {
        this.db = connection;
    }

    public Candidate getCandidate(Candidate candidate) throws SQLException {
        String query = "SELECT * FROM election.candidates WHERE firstname=? AND lastname=? and yearofbirth=?;";
        try(PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, candidate.getFirstName());
            stmt.setString(2, candidate.getLastName());
            stmt.setInt(3, candidate.getYearOfBirth());

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return Candidate.fullCreate(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6),
                        rs.getString(7), rs.getString(8), rs.getInt(9));
            }
        }
    }

    public Election getElection(Election e) throws SQLException {
        String query = "SELECT * FROM election.elections WHERE day=?";
        try(PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(e.getDate()));
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return Election.create(rs.getDate(2).toLocalDate());
            }
        }
    }

    public Party getParty(Party party) throws SQLException {
        String query = "SELECT * FROM election.parties WHERE name=?;";
        try(PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, party.getName());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return Party.fullCreate(rs.getInt(1), rs.getString(2));
            }
        }
    }

    public State getState(State state) throws SQLException {
        String query = "SELECT * FROM election.states WHERE name=?;";
        try(PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, state.getName());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return State.fullCreate(rs.getInt(1), rs.getString(2));
            }
        }
    }

    public District getDistrict(District district) throws SQLException {
        State s = getState(district.getState());
        Election e = getElection(district.getElection());
        if (s == null) return null;

        String query = "SELECT * FROM election.districts WHERE number=? AND year=? AND state=?;";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, district.getNumber());
            stmt.setInt(2, district.getElection().getYear());
            stmt.setInt(3, s.getId());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return District.fullCreate(rs.getInt(1), rs.getInt(2), e, s, rs.getString(5), rs.getInt(6), rs.getInt(7), rs.getInt(8));
            }
        }
    }

    public StateList getStateList(StateList stateList) throws SQLException {
        Election e = getElection(stateList.getElection());
        Party p = getParty(stateList.getParty());
        State s = getState(stateList.getState());
        if (e == null || p == null || s == null) {
            return null;
        }

        String query = "SELECT * FROM election.statelists WHERE party=? AND election=? AND state=?;";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, p.getId());
            stmt.setInt(2, e.getYear());
            stmt.setInt(3, s.getId());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return StateList.fullCreate(rs.getInt(1), p, e, s);
            }
        }
    }
}