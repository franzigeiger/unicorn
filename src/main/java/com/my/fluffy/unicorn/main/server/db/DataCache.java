package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.client.data.*;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

class DataCache {
    private Map<Integer, Party> parties = new HashMap<>();
    private Map<Integer, District> districts = new HashMap<>();
    private Map<Integer, State> states = new HashMap<>();
    private Map<Integer, Candidate> candidates = new HashMap<>();
    private Map<Integer, Election> elections = new HashMap<>();
    private Map<Integer, StateList> stateLists = new HashMap<>();

    public Map<Integer, Party> getParties() {
        return withStatement("select * from parties", stmt -> {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                parties.put(id, Party.fullCreate(id, rs.getString(2)));
            }
            return parties;
        });
    }

    Party getPartyById(int id) {
        if (!parties.containsKey(id)) {
            parties.put(id, getParty(id));
        }
        return parties.get(id);
    }

    public Map<Integer, District> getDistricts() {
        districts.clear();
        return withStatement("select * from districts", stmt -> {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                State s = getStateById(rs.getInt(4));
                Election e = getElectionByYear(rs.getInt(3));
                districts.put(id,
                        District.fullCreate(id, rs.getInt(2), e, s, rs.getString(5), rs.getInt(6), rs.getInt(7), rs.getInt(8)));
            }
            stmt.close();
            rs.close();
            return districts;
        });
    }

    District getDistrictById(int id) {
        if (!districts.containsKey(id)) {
            districts.put(id, getDistrict(id));
        }
        return districts.get(id);
    }

    public Map<Integer, StateList> getStateLists() {
        return withStatement("select * from statelists", stmt -> {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                Party p = getPartyById(rs.getInt(1));
                Election e = getElection(rs.getInt(2));
                State s = getStateById(rs.getInt(3));
                stateLists.put(id,
                        StateList.fullCreate(id, p, e, s));
            }
            stmt.close();
            rs.close();
            return stateLists;
        });
    }

    StateList getStateListById(int id) {
        if (!stateLists.containsKey(id)) {
            stateLists.put(id, getStatelist(id));
        }
        return stateLists.get(id);
    }

    State getStateById(int id) {
        if (!states.containsKey(id)) {
            states.put(id, getState(id));
        }
        return states.get(id);
    }

    Candidate getCandidateById(int id) {
        if (!candidates.containsKey(id)) {
            candidates.put(id, getCandidate(id));
        }
        return candidates.get(id);
    }

    Election getElectionByYear(int year) {
        if (!elections.containsKey(year)) {
            elections.put(year, getElection(year));
        }
        return elections.get(year);
    }

    private interface SQLFunction<T, U> {
        U apply(T t) throws SQLException;
    }

    private <T> T withStatement(String query, SQLFunction<PreparedStatement, T> getter) {
        try (DatabaseConnection conn = DatabaseConnection.create()) {
            return getter.apply(conn.getConnection().prepareStatement(query));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Candidate getCandidate(int candidateId) {
        String query = "SELECT * FROM election.candidates WHERE id=?;";
        return withStatement(query, stmt -> {
            stmt.setInt(1, candidateId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return Candidate.fullCreate(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6),
                        rs.getString(7), rs.getString(8), rs.getInt(9));
            }
        });
    }

    private Party getParty(int id) {
        String query = "SELECT * FROM election.parties WHERE id=?;";
        return withStatement(query, stmt -> {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return Party.fullCreate(rs.getInt(1), rs.getString(2));
            }
        });
    }

    private State getState(int state) {
        String query = "SELECT * FROM election.states WHERE id=?;";
        return withStatement(query, stmt -> {
            stmt.setInt(1, state);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return State.fullCreate(rs.getInt(1), rs.getString(2), rs.getInt(3));
            }
        });
    }

    private District getDistrict(int id) {
        String query = "SELECT * FROM election.districts WHERE id=?;";
        return withStatement(query, stmt -> {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                State s = getStateById(rs.getInt(4));
                Election e = getElectionByYear(rs.getInt(3));
                if (s == null || e == null) return null;
                return District.fullCreate(rs.getInt(1), rs.getInt(2), e, s, rs.getString(5), rs.getInt(6), rs.getInt(7), rs.getInt(8));
            }
        });
    }

    private StateList getStatelist(int id) {
        String query = "SELECT * FROM election.statelists WHERE id=?;";
        return withStatement(query, stmt -> {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                Party p = getPartyById(rs.getInt(2));
                State s = getStateById(rs.getInt(4));
                Election e = getElectionByYear(rs.getInt(3));
                if (s == null || e == null || p == null) return null;
                return StateList.fullCreate(rs.getInt(1), p, e, s);
            }
        });
    }

    private Election getElection(int year) {
        String query = "SELECT * FROM election.elections WHERE year=?";
        return withStatement(query, stmt -> {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return Election.create(rs.getDate(2));
            }
        });
    }
}
