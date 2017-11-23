package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.data.*;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DatabaseInserter {
    private final DatabaseConnection db;

    private final Map<Class<?>, Boolean> reportDups = new HashMap<>();

    DatabaseInserter(DatabaseConnection connection) {
        this.db = connection;
    }

    public void insertCandidate(Candidate candidate) throws SQLException {
        if (db.getQuery().getCandidate(candidate) != null) {
            if (reportDups.containsKey(Candidate.class)) {
                reportDups.put(Candidate.class, false);
                System.out.println("Duplicate candidate " + candidate.getFirstName() + " " + candidate.getLastName());
            }
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
            if (reportDups.containsKey(Election.class)) {
                reportDups.put(Election.class, false);
                System.out.println("Duplicate election " + election.getYear());
            }
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
            if (reportDups.containsKey(Party.class)) {
                reportDups.put(Party.class, false);
                System.out.println("Duplicate party " + party.getName());
            }
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
            if (reportDups.containsKey(State.class)) {
                reportDups.put(State.class, false);
                System.out.println("Duplicate state " + state.getName());
            }
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
            if (reportDups.containsKey(District.class)) {
                reportDups.put(District.class, false);
                System.out.println("Duplicate district " + district);
            }
            return;
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
            if (reportDups.containsKey(StateList.class)) {
                reportDups.put(StateList.class, false);
                System.out.println("Duplicate state list " + stateList);
            }
            return;
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
            if (reportDups.containsKey(DirectCandidature.class)) {
                reportDups.put(DirectCandidature.class, false);
                System.out.println("Duplicate direct candidature " + directCandidature);
            }
            return;
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
            if (reportDups.containsKey(ListCandidature.class)) {
                reportDups.put(ListCandidature.class, false);
                System.out.println("Duplicate list candidature " + listCandidature);
            }
            return;
        }

        String query = "INSERT INTO election.list_candidatures (candidate, statelist, placement) VALUES (?,?,?)";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);

        stmt.setInt(1, db.getQuery().getCandidate(listCandidature.getCandidate()).getId());
        stmt.setInt(2, db.getQuery().getStateList(listCandidature.getStateList()).getId());
        stmt.setInt(3, listCandidature.getPlacement());

        stmt.executeUpdate();
        stmt.close();
    }

    public void insertBallotsFromTmpFile(Collection<Ballot> ballots) throws IOException, SQLException {
        Map<DirectCandidature,DirectCandidature> candidate = new HashMap<>();
        Map<StateList,StateList> statelist = new HashMap<>();
        Map<District,District> district = new HashMap<>();

        for (Ballot b : ballots) {
            if (b.getDirectCandidature() != null && !candidate.containsKey(b.getDirectCandidature())) {
                candidate.put(b.getDirectCandidature(), db.getQuery().getDirectCandidatures(b.getDirectCandidature()));
            }
            if (b.getStateList() != null && !statelist.containsKey(b.getStateList())) {
                statelist.put(b.getStateList(), db.getQuery().getStateList(b.getStateList()));
            }
            if (!district.containsKey(b.getDistrict())) {
                district.put(b.getDistrict(), db.getQuery().getDistrict(b.getDistrict()));
            }
        }

        File f = File.createTempFile("copy-sql", "");
        f.deleteOnExit();
        Writer writer = new FileWriter(f);
        for (Ballot b : ballots) {
            writer.write(b.getDirectCandidature() == null ? "\\N" : candidate.get(b.getDirectCandidature()).getId().toString());
            writer.write("\t");
            writer.write(b.getStateList() == null ? "\\N" : statelist.get(b.getStateList()).getId().toString());
            writer.write("\t");
            writer.write(district.get(b.getDistrict()).getId().toString() + '\n');
        }
        writer.close();

        System.out.println("    Wrote to tmp file");

        db.getConnection().prepareStatement("ALTER TABLE election.ballots DISABLE TRIGGER ALL;").execute();
        CopyManager copyManager = new CopyManager((BaseConnection) db.getConnection());
        copyManager.copyIn("COPY election.ballots FROM STDIN", new FileReader(f));
        db.getConnection().prepareStatement("ALTER TABLE election.ballots ENABLE TRIGGER ALL;").execute();

        if (!f.delete()) System.out.println("Could not delete tmp file");
    }

    public void insertBallotsFromTmpFile(Stream<Ballot> ballots) throws IOException, SQLException {
        Map<DirectCandidature,DirectCandidature> candidate = new HashMap<>();
        Map<StateList,StateList> statelist = new HashMap<>();
        Map<District,District> district = new HashMap<>();

        File f = File.createTempFile("copy-sql", ".csv");
        f.deleteOnExit();
        Writer writer = new FileWriter(f);

        ballots.forEach(b -> {
            try {
                if (b.getDirectCandidature() != null && !candidate.containsKey(b.getDirectCandidature())) {
                    candidate.put(b.getDirectCandidature(), db.getQuery().getDirectCandidatures(b.getDirectCandidature()));
                }
                if (b.getStateList() != null && !statelist.containsKey(b.getStateList())) {
                    statelist.put(b.getStateList(), db.getQuery().getStateList(b.getStateList()));
                }
                if (!district.containsKey(b.getDistrict())) {
                    district.put(b.getDistrict(), db.getQuery().getDistrict(b.getDistrict()));
                }

                writer.write(b.getDirectCandidature() == null ? "\\N" : candidate.get(b.getDirectCandidature()).getId().toString());
                writer.write("\t");
                writer.write(b.getStateList() == null ? "\\N" : statelist.get(b.getStateList()).getId().toString());
                writer.write("\t");
                writer.write(district.get(b.getDistrict()).getId().toString() + '\n');
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });
        writer.close();

        CopyManager copyManager = new CopyManager((BaseConnection) db.getConnection());
        copyManager.copyIn("COPY election.ballots FROM STDIN", new FileReader(f));

        if (!f.delete()) System.out.println("Could not delete tmp file");
    }
}
