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


    public  Map<Integer,Party> getParties(int year) throws SQLException {
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
        return db.getQuery().getDistrict(District.minCreate(districtId, Election.create(new Date())));
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
}
