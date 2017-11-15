package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.data.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseInserter {
    private final DatabaseConnection db;

    DatabaseInserter(DatabaseConnection connection) {
        this.db = connection;
    }

    public void insertCandidate(Candidate candidate) throws SQLException {
        if (db.getQuery().getCandidate(candidate) != null) {
            System.out.println("Duplicate candidate " + candidate.getFirstName() + " " + candidate.getLastName());
            return;
        }

        String query = "INSERT INTO " +
                "election.candidates(title, firstname, lastname, profession, sex, hometown, birthtown, yearofbirth) " +
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
        if (db.getQuery().getElection(election) != null) {
            System.out.println("Duplicate election " + election.getYear());
            return;
        }
        String query = "INSERT INTO election.elections (year, day) VALUES (?,?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        stmt.setInt(1, election.getYear());
        stmt.setDate(2, Date.valueOf(election.getDate()));
        stmt.executeUpdate();
        stmt.close();
    }

    public void insertParty(Party party) throws SQLException {
        if (db.getQuery().getParty(party) != null) {
            System.out.println("Duplicate party " + party.getName());
            return;
        }
        String query = "INSERT INTO election.parties (name) VALUES (?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        stmt.setString(1, party.getName());
        stmt.executeUpdate();
        stmt.close();
    }

    public void insertState(State state) throws SQLException {
        if (db.getQuery().getState(state) != null) {
            System.out.println("Duplicate state " + state.getName() );
            return;
        }
        String query = "INSERT INTO election.states (name) VALUES (?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        stmt.setString(1, state.getName());
        stmt.executeUpdate();
        stmt.close();
    }

    public void insertDistrict(District district) throws SQLException {
        if (db.getQuery().getDistrict(district) != null) {
            System.out.println("Duplicate district " + district);
        }

        String query = "INSERT INTO election.districts (number, year, state, name, eligiblevoters, invalidfirstvotes, invalidsecondvotes) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);

        stmt.setInt(1, district.getNumber());
        stmt.setInt(2, district.getElection().getYear());
        stmt.setInt(3, db.getQuery().getState(district.getState()).getId());
        stmt.setString(4, district.getName());
        stmt.setInt(5, district.getEligibleVoters());
        // aggregate values not set but calculated later
        stmt.setInt(6, 0);
        stmt.setInt(7, 0);

        stmt.executeUpdate();
        stmt.close();
    }

    public void insertStateList(StateList stateList) throws SQLException {
        if (db.getQuery().getStateList(stateList) != null) {
            System.out.println("Duplicate state list " + stateList);
        }

        String query = "INSERT INTO election.statelists (party, election, state) VALUES (?,?,?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);

        stmt.setInt(1, db.getQuery().getParty(stateList.getParty()).getId());
        stmt.setInt(2, db.getQuery().getElection(stateList.getElection()).getYear());
        stmt.setInt(3, db.getQuery().getState(stateList.getState()).getId());

        stmt.executeUpdate();
        stmt.close();
    }

    public void insertDirectCandidature(DirectCandidature directCandidature) throws SQLException {
        if (db.getQuery().getDirectCandidatures(directCandidature) != null) {
            System.out.println("Duplicate direct candidature " + directCandidature);
        }

        String query = "INSERT INTO election.direct_candidatures (district, candidate, party, votes) VALUES (?,?,?,?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);

        stmt.setInt(1, db.getQuery().getDistrict(directCandidature.getDistrict()).getId());
        stmt.setInt(2, db.getQuery().getCandidate(directCandidature.getCandidate()).getId());
        stmt.setInt(3, db.getQuery().getParty(directCandidature.getParty()).getId());
        // aggregates are calculated later
        stmt.setInt(4, 0);

        stmt.executeUpdate();
        stmt.close();
    }

    public void insertListCandidature(ListCandidature listCandidature) throws SQLException {
        if (db.getQuery().getListCandidature(listCandidature) != null) {
            System.out.println("Duplicate list candidature " + listCandidature);
        }

        String query = "INSERT INTO election.list_candidatures (candidate, statelist, placement) VALUES (?,?,?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);

        stmt.setInt(1, db.getQuery().getCandidate(listCandidature.getCandidate()).getId());
        stmt.setInt(2, db.getQuery().getStateList(listCandidature.getStateList()).getId());
        stmt.setInt(3, listCandidature.getPlacement());

        stmt.executeUpdate();
        stmt.close();
    }

    public void insertBallot(Ballot ballot) throws SQLException {
        String query = "INSERT INTO election.ballots (firstvote, secondvote, district) VALUES (?,?,?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);

        stmt.setInt(1, db.getQuery().getDirectCandidatures(ballot.getDirectCandidature()).getId());
        stmt.setInt(2, db.getQuery().getStateList(ballot.getStateList()).getId());
        stmt.setInt(3, db.getQuery().getDistrict(ballot.getDistrict()).getId());

        stmt.executeUpdate();
        stmt.close();
    }
}
