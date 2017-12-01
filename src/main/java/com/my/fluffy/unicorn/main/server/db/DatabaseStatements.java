package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.client.data.DifferenceFirstSecondVotes;
import com.my.fluffy.unicorn.main.client.data.District;
import com.my.fluffy.unicorn.main.client.data.Party;
import com.my.fluffy.unicorn.main.client.data.PartyStateInfos;
import com.my.fluffy.unicorn.main.client.data.State;
import com.my.fluffy.unicorn.main.server.Controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is thought for our database statements
 */
public class DatabaseStatements {

    private DatabaseConnection db ;

    public DatabaseStatements(){
        try {
            db = DatabaseConnection.create();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public  Map<Integer,Party> getParties(int year) throws SQLException {
            System.out.println("Fetch all parties");
            PreparedStatement stmt = db.getConnection().prepareStatement("select * from parties");

            Map<Integer, Party> parties = new HashMap<Integer, Party>();
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

        List<Party> parties = new ArrayList<Party>();



        return parties;
    }

    public Map<Integer, District> getDistricts() throws SQLException {
        System.out.println("Fetch all districts");
        PreparedStatement stmt = db.getConnection().prepareStatement("select * from districts");

        Map<Integer, District> districts = new HashMap<Integer, District>();
        ResultSet rs =stmt.executeQuery();

        while(rs.next()) {
            int id = rs.getInt(1);
            districts.put(id, District.fullCreate(
                    id,
                    rs.getInt(2),
                    null,
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
        PreparedStatement stmt = db.getConnection().prepareStatement("select * from rawdistribution where election = ?");
        stmt.setInt(1   ,year);
        Map<Party, Double> parties = new HashMap<Party, Double>();
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

    public Map<Party, DifferenceFirstSecondVotes> getDifferencesFirstSecondVotes(
            int year) throws SQLException {
        System.out.println("Fetch Differences for " + year);
        PreparedStatement stmt = db.getConnection().prepareStatement(
                "select party, diff, first, second, district, year " +
                        "from election.differencefirstsecondvotes where year = ? " +
                        "order by diff desc");
        stmt.setInt(1   ,year);
        Map<Party, DifferenceFirstSecondVotes> differenceTotal = new HashMap<Party, DifferenceFirstSecondVotes>();
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

    public Map<Party, Double> getFirstVotesPerParty(
            int year) throws SQLException {
        System.out.println("Fetch First Votes perParty for " + year);

        PreparedStatement stmt = db.getConnection().prepareStatement(
                "with allVotes as(select sum(votes) as total, d.year\n" +
                        "                 from election.direct_candidatures dc join election.districts d on d.id = dc.district\n" +
                        "                group by d.year),\n" +
                        "\n" +
                        "firstVotesPercentage as(\n" +
                        "    select \n" +
                        "        dc.party, \n" +
                        "        sum(dc.votes) as votes, \n" +
                        "        sum(dc.votes)::numeric(12,2) / (select total from allVotes where year = d.year)::numeric(12,2) as percentage,\n" +
                        "        d.year\n" +
                        "    from election.direct_candidatures dc join election.districts d on d.id = dc.district\n" +
                        "    group by dc.party, d.year\n" +
                        ")\n" +
                        "\n" +
                        "select p.id, votes, percentage, year \n" +
                        "from firstVotesPercentage f join election.parties p on p.id = f.party\n" +
                        "where year = ? ");

        stmt.setInt(1   ,year);
        Map<Party, Double> firstVotesPerParty = new HashMap<Party, Double>();
        ResultSet rs =stmt.executeQuery();

        while(rs.next()) {
            Party party = Controller.get().getParty(rs.getInt(1));
            firstVotesPerParty.put(party, rs.getDouble(3));
        }
        stmt.close();
        rs.close();

        return firstVotesPerParty;
    }
    
    public  List<PartyStateInfos> getAdditionalMandats(int year) throws SQLException {

        System.out.println("Fetch additional mandats");
        List<PartyStateInfos> infos = new ArrayList<PartyStateInfos>();
        PreparedStatement stmt = null;
        if(year == 2017) {
             stmt = db.getConnection().prepareStatement("select party, state ,\n" +
                    "  (case when seatswithdirect > baseseats\n" +
                    "  then seatswithdirect - baseseats else 0 end) as additionalMandats from   election.parlamentdistribution2017 order by additionalmandats desc;");
        }else {
            //todo 2013 statement
        }
        ResultSet rs =stmt.executeQuery();
        double others=0;
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
        Map<Integer, State> states = new HashMap<Integer, State>();
        PreparedStatement stmt = db.getConnection().prepareStatement(query);

            ResultSet rs = stmt.executeQuery();
            while(!rs.next()) {
                states.put(rs.getInt(1), State.fullCreate(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }

            return states;
    }
}
