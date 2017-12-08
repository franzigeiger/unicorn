package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.client.data.*;
import com.my.fluffy.unicorn.main.server.Controller;

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

    private DatabaseConnection db ;

    public DatabaseStatements(){
        try {
            db = DatabaseConnection.create();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public  Map<Integer,Party> getParties() throws SQLException {
            System.out.println("Fetch all parties");
            PreparedStatement stmt = db.getConnection().prepareStatement("select * from parties");

            Map<Integer, Party> parties = new HashMap<>();
            ResultSet rs =stmt.executeQuery();

            while(rs.next()) {
                int id = rs.getInt(1);
                parties.put(id, Party.fullCreate(id, rs.getString(2)));
            }

            stmt.close();
            rs.close();

            return parties;
    }

    public Map<Candidate,Party> getParlamentMembers(int year) throws SQLException {
        String query = null;
        if(year == 2017){
            query = DatabaseConnection.getQuery("get-parliament-members-2017.sql");
        } else {
            query = DatabaseConnection.getQuery("get-parliament-members-2013.sql");
        }


        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        Map<Candidate, Party> members = new LinkedHashMap<>();
        ResultSet rs =stmt.executeQuery();
        while(rs.next()) {
            Candidate candidate = Controller.get().getCandidate(rs.getInt(1));
            Party party = Controller.get().getParty(rs.getInt(2));
            members.put(candidate, party);
        }

        stmt.close();
        rs.close();
        return members;
    }

    public Map<Integer, District> getDistricts() throws SQLException {
        System.out.println("Fetch all districts");
        PreparedStatement stmt = db.getConnection().prepareStatement("select * from districts");

        Map<Integer, District> districts = new HashMap<>();
        ResultSet rs =stmt.executeQuery();

        while(rs.next()) {
            int id = rs.getInt(1);
            districts.put(id, District.fullCreate(
                    id,
                    rs.getInt(2),
                    Election.minCreate(rs.getInt(3)),
                    null,
                    rs.getString(5),
                    rs.getInt(6),
                    rs.getInt(7),
                    rs.getInt(8)
                ));
        }

        stmt.close();
        rs.close();

        return districts;
    }

    public Map<Party,Double> getPartyPercent(int year) throws SQLException {

        System.out.println("Fetch all parties");
        PreparedStatement stmt = db.getConnection().prepareStatement(
                "select * from rawdistribution where election = ?");
        stmt.setInt(1   ,year);
        Map<Party, Double> parties = new LinkedHashMap<>();
        ResultSet rs =stmt.executeQuery();
        double others=0;
        while(rs.next()) {
            double percent = rs.getDouble(4);
            Party party = Controller.get().getParty(rs.getInt(1));
            if (percent < 5.0) {
                others += percent;
            } else {
                parties.put( party, percent);
            }
        }

        parties.put(new Party("Others"), others);

        stmt.close();
        rs.close();

        return parties;
    }

    public Map<Party, Integer> getFirstVotesPerParty(
            int year) throws SQLException {
        System.out.println("Fetch First Votes perParty for " + year);

        PreparedStatement stmt = db.getConnection().prepareStatement(
                DatabaseConnection.getQuery("get-first-votes-per-party.sql"));

        stmt.setInt(1   ,year);
        Map<Party, Integer> firstVotesPerParty = new HashMap<>();
        ResultSet rs =stmt.executeQuery();

        while(rs.next()) {
            Party party = Controller.get().getParty(rs.getInt(1));
            firstVotesPerParty.put(party, rs.getInt(2));
        }
        stmt.close();
        rs.close();

        return firstVotesPerParty;
    }
    
    public District getDistrict(int districtId, int year) throws SQLException {
        Logger.getLogger("").log(Level.INFO, "Fetch a district");
        return db.getQuery().getDistrict(District.minCreate(districtId, Election.minCreate(year)));
    }

    public List<District> getDistricts(int year) throws SQLException {
        Logger.getLogger("").log(Level.INFO, "Fetch all districts");
        PreparedStatement stmt = db.getConnection().prepareStatement("select * from election.districts where year = ?;");
        stmt.setInt(1, year);
        ResultSet rs = stmt.executeQuery();

        List<District> districts = new ArrayList<>();
        while(rs.next()) {
            districts.add(District.fullCreate(
                    rs.getInt(1),
                    rs.getInt(2),
                    Election.minCreate(rs.getInt(3)),
                    db.getQuery().getStateByID(rs.getInt(4)),
                    rs.getString(5),
                    rs.getInt(6),
                    rs.getInt(7),
                    rs.getInt(8)));
        }
        stmt.close();
        rs.close();
        return districts;
    }

    public Candidate getDirectWinner(int districtId) throws SQLException {
        Logger.getLogger("").log(Level.INFO, "Get district winner");
        PreparedStatement stmt = db.getConnection().prepareStatement(
                DatabaseConnection.getQuery("get-direct-winner.sql"));
        stmt.setInt(1, districtId);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return null;
        }
        return Controller.get().getCandidate(rs.getInt(1));
    }

    public List<Top10Data> getTopTen(Party party, int year) throws SQLException {
        List<Top10Data> retVal = new ArrayList<>(10);
        String query = DatabaseConnection.getQuery("get-top10.sql");

        try(PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, party.getId());
            stmt.setInt(2, party.getId());
            stmt.setInt(3, year);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                boolean isWinner = party.getId() == rs.getInt("winnerparty");
                Candidate c = db.getQuery().getCandidateById(rs.getInt(isWinner? "winner" : "second"));
                int diff = rs.getInt("votediff");
                retVal.add(Top10Data.create(c, isWinner, diff));
            }
        }

        return retVal;
    }

    public List<PartyStateInfos> getAdditionalMandats(int year) throws SQLException {

        System.out.println("Fetch additional mandats");
        List<PartyStateInfos> infos = new ArrayList<>();
        PreparedStatement stmt = null;
        if(year == 2017) {
             stmt = db.getConnection().prepareStatement(DatabaseConnection.getQuery("get-additional-mandats-2017.sql"));
        } else {
            stmt = db.getConnection().prepareStatement(DatabaseConnection.getQuery("get-additional-mandats-2013.sql"));
        }
        ResultSet rs =stmt.executeQuery();
        while(rs.next()) {
            int partyID =rs.getInt(1);
            int stateID = rs.getInt(2);
            int additional = rs.getInt(3);
            Party party = Controller.get().getParty(partyID);
            State state = Controller.get().getState(stateID);

            infos.add(new PartyStateInfos(party, state, additional));
        }

        stmt.close();
        rs.close();

        return infos;
    }

    public Map<Integer, State> getStates() throws SQLException {
        String query = "SELECT * FROM election.states";
        Map<Integer, State> states = new HashMap<>();
        PreparedStatement stmt = db.getConnection().prepareStatement(query);

        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
            states.put(rs.getInt(1), State.fullCreate(rs.getInt(1), rs.getString(2), rs.getInt(3)));
        }

        return states;
    }

    public Map<Integer,Candidate> getCandidates() throws SQLException {
        System.out.println("Fetch all parties");
        PreparedStatement stmt = db.getConnection().prepareStatement("select * from candidates");

        Map<Integer, Candidate> candidates = new HashMap<>();
        ResultSet rs =stmt.executeQuery();

        while(rs.next()) {
            int id = rs.getInt(1);
            candidates.put(id, Candidate.fullCreate(rs.getInt(1),
                                                rs.getString(2),
                                                rs.getString(3),
                                                rs.getString(4),
                                                rs.getString(5),
                                                rs.getString(6),
                                                rs.getString(7),
                                                rs.getString(8),
                                                rs.getInt(9)
            ));
        }

        stmt.close();
        rs.close();

        return candidates;
    }


    public Map<Party, DifferenceFirstSecondVotes> getDifferencesFirstSecondVotes(
        int year) throws SQLException {
            System.out.println("Fetch Differences for " + year);
            PreparedStatement stmt = db.getConnection().prepareStatement(
                    DatabaseConnection.getQuery("get-difference-first-second.sql"));
            stmt.setInt(1   ,year);
            Map<Party, DifferenceFirstSecondVotes> differenceTotal = new HashMap<>();
            ResultSet rs =stmt.executeQuery();

            while(rs.next()) {
                Party party = Controller.get().getParty(rs.getInt(1));
                DifferenceFirstSecondVotes diff =  DifferenceFirstSecondVotes.create(
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        Controller.get().getDistrict(rs.getInt(5)).getName(),
                        rs.getInt(6)
                );
                differenceTotal.put(party, diff);
            }
            stmt.close();
            rs.close();

            return differenceTotal;
        }

    public Map<String, Integer> getAmountPerGender() throws SQLException {
        String query = DatabaseConnection.getQuery("get-amount-per-gender.sql");
        PreparedStatement stmt = db.getConnection().prepareStatement(query);

        Map<String, Integer> amountPerGender = new HashMap<>();
        ResultSet rs =stmt.executeQuery();
        while(rs.next()) {
            amountPerGender.put(rs.getString(1), rs.getInt(2));
        }
        stmt.close();
        rs.close();

        return amountPerGender;
    }

    public ArrayList<DistrictResults> getDistrictResults(int distOldId, int distNewId) throws SQLException {
        String query = DatabaseConnection.getQuery("get-district-results.sql");

        PreparedStatement stmt = db.getConnection().prepareStatement(query);

        stmt.setInt(1   , distOldId);
        stmt.setInt(2   , distOldId);
        stmt.setInt(3   , distNewId);
        stmt.setInt(4   , distNewId);

        ArrayList<DistrictResults> districtResults = new ArrayList<>();
        ResultSet rs =stmt.executeQuery();

        while(rs.next()) {
            districtResults.add(
                    DistrictResults.create(rs.getString(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getInt(4),
                            rs.getInt(5)
                    ));

        }
        stmt.close();
        rs.close();

        return districtResults;
    }

    public void updateAggregates() throws SQLException {
        db.updateAggregates();
    }

    public Map<District,List<String>> getWinnigParties(int year) throws SQLException{
        String query = DatabaseConnection.getQuery("get-winning-parties.sql");
        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        stmt.setInt(1, year);
        stmt.setInt(2, year);

        Map<District, List<String>> results = new HashMap<>();

        ResultSet rs =stmt.executeQuery();
        while(rs.next()) {
            List<String> winningParty = new ArrayList<>();
            winningParty.add(Controller.get().getParty(rs.getInt(2)).getName());
            winningParty.add(Controller.get().getParty(rs.getInt(3)).getName());
            results.put(Controller.get().getDistrict(rs.getInt(1)), winningParty);
        }
        stmt.close();
        rs.close();

        return results;
    }

    public Map<Party, Integer> calculatePerParty(int year) throws SQLException {
        System.out.println("Distribution between Parties");

        Map<Party, Integer> dist = new HashMap<>();
        PreparedStatement stmt = null;
         if(year == 2017){
            stmt = db.getConnection().prepareStatement("SELECT party, sum(finalseats) FROM election.parlamentdistribution2017 GROUP BY party");
         } else {
            stmt = db.getConnection().prepareStatement("SELECT party, sum(finalseats) FROM election.parlamentdistribution2013 GROUP BY party");
         }


        ResultSet set = stmt.executeQuery();
        Party party;



        while (set.next()) {
            party = Controller.get().getParty(set.getInt(1));
            int base = set.getInt(2);
            dist.put(party, base);



        }
        stmt.close();
        set.close();

        return dist;
    }
}
