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

    public static List<Party> getStates(int year) throws SQLException {
        System.out.println("Fetch all parties");
        return new ArrayList<>();
    }


    public Map<Party,Double> getPartyPercent(int year) throws SQLException {

        System.out.println("Fetch all parties");
        PreparedStatement stmt = db.getConnection().prepareStatement("select * from rawdistribution where election = ?");
        stmt.setInt(1   ,year);
        Map<Party, Double> parties = new HashMap<>();
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
                    db.getQuery().getElection(Election.minCreate(rs.getInt(3))),
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

    public Candidate getDirectWinner(District district) throws SQLException {
        Logger.getLogger("").log(Level.INFO, "Get district winner");
        PreparedStatement stmt = db.getConnection().prepareStatement("select * from election.directwinner where district = ?;");
        stmt.setInt(1, district.getId());
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return null;
        }
        int candidateId = rs.getInt("winner");
        return db.getQuery().getCandidateById(candidateId);
    }

    public List<Candidate> getTopTen(Party party, int year) throws SQLException {
        List<Candidate> retVal = new ArrayList<>(10);

        String maxvotes_per_district = "SELECT district,year,MAX(votes) AS maxvotes FROM election.direct_candidatures AS dc JOIN election.districts AS d ON d.id = dc.district GROUP BY year,district";
        String winners_per_district = "SELECT dc.*,maxvotes,year FROM election.direct_candidatures AS dc JOIN maxvotes_per_district AS mv ON mv.district = dc.district AND mv.maxvotes = dc.votes";
        String second_max_votes_per_district = "SELECT district,MAX(votes) AS secondvotes FROM election.direct_candidatures AS dc WHERE NOT EXISTS (SELECT * FROM winners_per_district WHERE id = dc.id) GROUP BY district";
        String second_candidate_per_district = "SELECT dc.*,secondvotes FROM election.direct_candidatures AS dc JOIN secondvotes_per_district AS sv ON sv.district = dc.district AND sv.secondvotes = dc.votes";
        String join_and_votediff_per_district = "SELECT winners.district,winners.year," +
                                                        "winners.votes - second.votes AS votediff," +
                                                        "winners.candidate AS winner,winners.party AS winnerparty,winners.votes AS winnervotes," +
                                                        "second.candidate AS second,second.party AS secondparty,second.votes AS secondvotes" +
                                                "FROM winners_per_district as winners JOIN second_per_district AS second ON winners.district = second.district";

        String query = "WITH maxvotes_per_district AS (" + maxvotes_per_district + ")," +
                        "winners_per_district AS (" + winners_per_district + ")," +
                        "secondvotes_per_district AS (" + second_max_votes_per_district + ")," +
                        "second_per_district AS (" + second_candidate_per_district + ")," +
                        "first_second_diff_per_district AS (" + join_and_votediff_per_district + ")" +
                "SELECT * FROM first_second_diff_per_district WHERE ?=? AND year=? ORDER BY votediff ASC LIMIT 10";

        try(PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            {
                stmt.setString(1, "winnerparty");
                stmt.setInt(2, party.getId());
                stmt.setInt(3, year);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    retVal.add(db.getQuery().getCandidateById(rs.getInt("winner")));
                }
            }

            if (retVal.size() < 10) {
                stmt.setString(1, "secondparty");
                stmt.setInt(2, party.getId());
                stmt.setInt(3, year);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    retVal.add(db.getQuery().getCandidateById(rs.getInt("winner")));
                }
            }
        }

        return retVal;
    }
}
