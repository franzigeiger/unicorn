package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.client.data.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is thought for our database statements
 */
public class DatabaseStatements {
    private DatabaseConnection db;

    DatabaseStatements(DatabaseConnection databaseConnection) {
        this.db = databaseConnection;
    }

    public Map<Candidate, Party> getParlamentMembers(int year) throws SQLException {
        String query = DatabaseConnection.getQuery(year == 2017 ? "get-parliament-members-2017.sql" : "get-parliament-members-2013.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            Map<Candidate, Party> members = new LinkedHashMap<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Candidate candidate = db.getQuery().getCandidateById(rs.getInt(1));
                Party party = db.getQuery().getPartyById(rs.getInt(2));
                members.put(candidate, party);
            }
            return members;
        }
    }

    public Map<Party, Double> getPartyPercent(int year) throws SQLException {
        System.out.println("Fetch all parties");
        String query = "SELECT * FROM rawdistribution WHERE election = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            Map<Party, Double> parties = new LinkedHashMap<>();
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            double others = 0;
            while (rs.next()) {
                double percent = rs.getDouble(4);
                if (percent < 5.0) {
                    others += percent;
                } else {
                    parties.put(db.getQuery().getPartyById(rs.getInt(1)), percent);
                }
            }
            parties.put(new Party("Others"), others);
            return parties;
        }
    }

    public Map<Party, Integer> getFirstVotesPerParty(int year) throws SQLException {
        System.out.println("Fetch First Votes perParty for " + year);
        String query = DatabaseConnection.getQuery("get-first-votes-per-party.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, year);
            Map<Party, Integer> firstVotesPerParty = new HashMap<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                firstVotesPerParty.put(db.getQuery().getPartyById(rs.getInt(1)), rs.getInt(2));
            }
            return firstVotesPerParty;
        }
    }

    public Candidate getDirectWinner(int districtId) throws SQLException {
        Logger.getLogger("").log(Level.INFO, "Get district winner");
        String query = DatabaseConnection.getQuery("get-direct-winner.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, districtId);
            ResultSet rs = stmt.executeQuery();
            return !rs.next() ? null : db.getQuery().getCandidateById(rs.getInt(1));
        }
    }

    public List<Top10Data> getTopTen(Party party, int year) throws SQLException {
        String query = DatabaseConnection.getQuery("get-top10.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            List<Top10Data> retVal = new ArrayList<>(10);
            stmt.setInt(1, party.getId());
            stmt.setInt(2, party.getId());
            stmt.setInt(3, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                boolean isWinner = party.getId() == rs.getInt("winnerparty");
                Candidate c = db.getQuery().getCandidateById(rs.getInt(isWinner ? "winner" : "second"));
                int diff = rs.getInt("votediff");
                retVal.add(Top10Data.create(c, isWinner, diff));
            }
            return retVal;
        }
    }

    public List<PartyStateInfos> getAdditionalMandats(int year) throws SQLException {
        System.out.println("Fetch additional mandats");
        String query = DatabaseConnection.getQuery(year == 2017 ? "get-additional-mandats-2017.sql" : "get-additional-mandats-2013.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            List<PartyStateInfos> infos = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Party party = db.getQuery().getPartyById(rs.getInt(1));
                State state = db.getQuery().getStateById(rs.getInt(2));
                infos.add(new PartyStateInfos(party, state, rs.getInt(3)));
            }
            return infos;
        }
    }

    public Map<Party, DifferenceFirstSecondVotes> getDifferencesFirstSecondVotes(int year) throws SQLException {
        System.out.println("Fetch Differences for " + year);
        String query = DatabaseConnection.getQuery("get-difference-first-second.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, year);
            Map<Party, DifferenceFirstSecondVotes> differenceTotal = new HashMap<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DifferenceFirstSecondVotes diff = DifferenceFirstSecondVotes.create(
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        db.getQuery().getDistrictById(rs.getInt(5)).getName(),
                        rs.getInt(6)
                );
                differenceTotal.put(db.getQuery().getPartyById(rs.getInt(1)), diff);
            }
            return differenceTotal;
        }
    }

    public Map<String, Integer> getAmountPerGender() throws SQLException {
        String query = DatabaseConnection.getQuery("get-amount-per-gender.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            Map<String, Integer> amountPerGender = new HashMap<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                amountPerGender.put(rs.getString(1), rs.getInt(2));
            }
            return amountPerGender;
        }
    }

    public Map<Party, Integer> getStateListsWithRow(int prevYear, int state, int currentYear) throws SQLException {
        String query = DatabaseConnection.getQuery("get-ordered-state-lists-for-ballot.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            Map<Party, Integer> stateListsWithRows = new HashMap<>();
            stmt.setInt(1, prevYear);
            stmt.setInt(2, state);
            stmt.setInt(3, state);
            stmt.setInt(4, currentYear);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                    stateListsWithRows.put(
                            db.getQuery().getPartyById(rs.getInt(1)),
                            rs.getInt(2));
            }
            return stateListsWithRows;
        }
    }

    public Map<Party, Candidate> getDirectCandidatesForDistrict(int districtId) throws SQLException {
        String query = DatabaseConnection.getQuery("get-direct-candidate-for-ballot.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            Map<Party, Candidate> directCandidates = new HashMap<>();
            stmt.setInt(1, districtId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                directCandidates.put(
                        db.getQuery().getPartyById(rs.getInt(2)),
                        db.getQuery().getCandidateById(rs.getInt(1))
                        );
            }
            return directCandidates;
        }
    }

    public List<ListCandidature> getListCandidatures(int districtId, int year, int partyId) throws SQLException {
        String query = DatabaseConnection.getQuery("get-list-candidate-for-ballot.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            List<ListCandidature> listCandidates = new ArrayList<>();
            Party party = db.getQuery().getPartyById(partyId);
            stmt.setInt(1, districtId);
            stmt.setInt(2, year);
            stmt.setInt(3, partyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                listCandidates.add(
                        ListCandidature.create(
                                db.getQuery().getCandidateById(rs.getInt(2)),
                                db.getQuery().getStateListById(rs.getInt(3)),
                                rs.getInt(4)
                        ));
            }
            return listCandidates;
        }
    }

    public Map<Integer, BallotLine> getBallotLinesForDistrict(int districtId, int currentYear, int prevYear) throws SQLException {
        District d = db.getQuery().getDistrictById(districtId);
        State s = d.getState();
        Map<Party, Integer> stateLists = getStateListsWithRow(prevYear, s.getId(), currentYear);
        Map<Party, Candidate> directCandidates = getDirectCandidatesForDistrict(districtId);
        Map<Integer, BallotLine> ballotLines = new HashMap<>();
        for(Map.Entry<Party, Integer> stateList : stateLists.entrySet()){
            Party party = stateList.getKey();
            int placement = stateList.getValue();
            List<ListCandidature> listCandidates = getListCandidatures(districtId, currentYear, party.getId());
            BallotLine newLine = BallotLine.create(
                    party,
                    directCandidates.get(party),
                    listCandidates,
                    placement);
            ballotLines.put(placement, newLine);
        }

        return ballotLines;
    }

    public ArrayList<DistrictResults> getDistrictResults(int distOldId, int distNewId) throws SQLException {
        String query = DatabaseConnection.getQuery("get-district-results.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            ArrayList<DistrictResults> districtResults = new ArrayList<>();
            stmt.setInt(1, distOldId);
            stmt.setInt(2, distOldId);
            stmt.setInt(3, distNewId);
            stmt.setInt(4, distNewId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                districtResults.add(
                        DistrictResults.create(rs.getString(1),
                                rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)));
            }
            return districtResults;
        }
    }

    public Map<District, List<String>> getWinnigParties(int year) throws SQLException {
        String query = DatabaseConnection.getQuery("get-winning-parties.sql");
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            Map<District, List<String>> results = new HashMap<>();
            stmt.setInt(1, year);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                List<String> winningParty = new ArrayList<>();
                winningParty.add(db.getQuery().getPartyById(rs.getInt(2)).getName());
                winningParty.add(db.getQuery().getPartyById(rs.getInt(3)).getName());
                results.put(db.getQuery().getDistrictById(rs.getInt(1)), winningParty);
            }
            return results;
        }
    }

    public Map<Party, Integer> calculatePerParty(int year) throws SQLException {
        System.out.println("Distribution between Parties");
        String query = year == 2017 ?
                "SELECT party, sum(finalseats) FROM election.parlamentdistribution2017 GROUP BY party"
                : "SELECT party, sum(finalseats) FROM election.parlamentdistribution2013 GROUP BY party";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            Map<Party, Integer> dist = new HashMap<>();
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                dist.put(db.getQuery().getPartyById(set.getInt(1)), set.getInt(2));
            }
            return dist;
        }
    }


}
