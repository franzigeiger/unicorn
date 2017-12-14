package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.client.data.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DatabaseQuery {
    private DatabaseConnection db;
    private static DataCache dc = new DataCache();

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

    public Candidate getCandidateById(int candidateId) {
        return dc.getCandidateById(candidateId);
    }

    public Election getElection(Election e) throws SQLException {
        String query = "SELECT * FROM election.elections WHERE day=?";
        try(PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setDate(1, new Date(e.getDate().getTime()));
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return Election.create(rs.getDate(2));
            }
        }
    }

    public List<Party> getAllParties() {
        return new ArrayList<>(dc.getParties().values());
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

    public Party getPartyById(int id) {
        return dc.getPartyById(id);
    }


    public State getState(State state) throws SQLException {
        String query = "SELECT * FROM election.states WHERE name=?;";
        try(PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, state.getName());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return State.fullCreate(rs.getInt(1), rs.getString(2), rs.getInt(3));
            }
        }
    }

    public State getStateById(int state) {
        return dc.getStateById(state);
    }

    public Map<Integer, District> getAllDistricts() {
        return dc.getDistricts();
    }

    public List<District> getDistrictsPerYear(int year) {
        return dc.getDistricts().values().stream().filter(d -> d.getElection().getYear() == year).collect(Collectors.toList());
    }

    public District getDistrict(int districtId, int year) throws SQLException {
        Logger.getLogger("").log(Level.INFO, "Fetch a district");
        return getDistrict(District.minCreate(districtId, Election.minCreate(year)));
    }

    public District getDistrict(District district) throws SQLException {
        Election e = getElection(district.getElection());

        String query = "SELECT * FROM election.districts WHERE number=? AND year=?;";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, district.getNumber());
            stmt.setInt(2, district.getElection().getYear());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                State s = getStateById(rs.getInt(4));
                if (s == null) return null;
                return District.fullCreate(rs.getInt(1), rs.getInt(2), e, s, rs.getString(5), rs.getInt(6), rs.getInt(7), rs.getInt(8));
            }
        }
    }

    public District getDistrictById(int id) {
        return dc.getDistrictById(id);
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

    public StateList getStateListById(int id) {
        return dc.getStateListById(id);
    }

    public DirectCandidature getDirectCandidatures(DirectCandidature directCandidature) throws SQLException {
        District d = getDistrict(directCandidature.getDistrict());
        Party p = getParty(directCandidature.getParty());
        Candidate c = getCandidate(directCandidature.getCandidate());
        if (d == null || p == null || c == null) {
            return null;
        }

        String query = "SELECT * FROM election.direct_candidatures WHERE party=? AND district=? AND candidate=?;";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, p.getId());
            stmt.setInt(2, d.getId());
            stmt.setInt(3, c.getId());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return DirectCandidature.fullCreate(rs.getInt(1), d, c, p, rs.getInt(5));
            }
        }
    }

    public ListCandidature getListCandidature(ListCandidature listCandidature) throws SQLException {
        StateList s = getStateList(listCandidature.getStateList());
        Candidate c = getCandidate(listCandidature.getCandidate());
        if (s == null || c == null) {
            return null;
        }

        String query = "SELECT * FROM election.list_candidatures WHERE statelist=? AND candidate=?;";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, s.getId());
            stmt.setInt(2, c.getId());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return ListCandidature.fullCreate(rs.getInt(1), c, s, rs.getInt(4));
            }
        }
    }

    public boolean checkToken(String token) throws SQLException {
        String query = "SELECT count(*) " +
                "FROM election.tokens " +
                "WHERE token = ?;";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return false;
            } else {
                deleteToken(token);
                return true;
            }
        }
    }

    public void deleteToken(String token) throws SQLException {
        String query = "DELETE FROM election.tokens " +
                "WHERE token = ?;";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, token);
            stmt.executeQuery();
        }
    }
}
