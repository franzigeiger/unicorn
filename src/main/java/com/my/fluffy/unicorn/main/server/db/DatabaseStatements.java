package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.client.data.DifferenceFirstSecondVotes;
import com.my.fluffy.unicorn.main.client.data.Candidate;
import com.my.fluffy.unicorn.main.client.data.District;
import com.my.fluffy.unicorn.main.client.data.Party;
import com.my.fluffy.unicorn.main.client.data.PartyStateInfos;
import com.my.fluffy.unicorn.main.client.data.State;
import com.my.fluffy.unicorn.main.client.data.*;
import com.my.fluffy.unicorn.main.server.Controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;

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

    public Map<Candidate,Party> getParlamentMembers() throws SQLException {
        String query = "select * from election.parliamentMembers";

        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        //stmt.setInt(1   ,year);
        Map<Candidate, Party> members = new LinkedHashMap<Candidate, Party>();
        ResultSet rs =stmt.executeQuery();
        double others=0;
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

        Map<Integer, District> districts = new HashMap<Integer, District>();
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
        Map<Party, Double> parties = new LinkedHashMap<Party, Double>();
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
                "with firstVotesPercentage as(\n" +
                        "    select \n" +
                        "        dc.party, \n" +
                        "        sum(dc.votes) as votes, \n" +
                        "        d.year\n" +
                        "    from election.direct_candidatures dc join election.districts d on d.id = dc.district\n" +
                        "    group by dc.party, d.year\n" +
                        ")\n" +
                        "\n" +
                        "select p.id, votes, year \n" +
                        "from firstVotesPercentage f join election.parties p on p.id = f.party\n" +
                        "where year = ? ");

        stmt.setInt(1   ,year);
        Map<Party, Integer> firstVotesPerParty = new HashMap<Party, Integer>();
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

    public Candidate getDirectWinner(District district) throws SQLException {
        Logger.getLogger("").log(Level.INFO, "Get district winner");
        PreparedStatement stmt = db.getConnection().prepareStatement(
                "select c.id\n" +
                        "from election.directwinner dw join election.direct_candidatures dc\n" +
                        "on dw.winner = dc.id\n" +
                        "join election.candidates c on dc.candidate = c.id\n" +
                        "where dw.district = ?;");
        stmt.setInt(1, district.getId());
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return null;
        }
        return Controller.get().getCandidate(rs.getInt(1));
    }

    public List<Top10Data> getTopTen(Party party, int year) throws SQLException {
        List<Top10Data> retVal = new ArrayList<>(10);

        String maxvotes_per_district = "SELECT district,year,MAX(votes) AS maxvotes FROM election.direct_candidatures AS dc JOIN election.districts AS d ON d.id = dc.district GROUP BY year,district";
        String winners_per_district = "SELECT dc.*,maxvotes,year FROM election.direct_candidatures AS dc JOIN maxvotes_per_district AS mv ON (mv.district = dc.district AND mv.maxvotes = dc.votes)";
        String second_max_votes_per_district = "SELECT district,MAX(votes) AS secondvotes FROM election.direct_candidatures AS dc WHERE NOT EXISTS (SELECT * FROM winners_per_district WHERE id = dc.id) GROUP BY district";
        String second_candidate_per_district = "SELECT dc.*,secondvotes FROM election.direct_candidatures AS dc JOIN secondvotes_per_district AS sv ON sv.district = dc.district AND sv.secondvotes = dc.votes";
        String join_and_votediff_per_district = "SELECT winners.district,winners.year, " +
                "winners.votes - second.votes AS votediff, " +
                "winners.candidate AS winner,winners.party AS winnerparty,winners.votes AS winnervotes, " +
                "second.candidate AS second,second.party AS secondparty,second.votes AS secondvotes " +
                "FROM winners_per_district as winners JOIN second_per_district AS second ON winners.district = second.district";

        String withClause = "WITH maxvotes_per_district AS (" + maxvotes_per_district + ")," +
                "winners_per_district AS (" + winners_per_district + ")," +
                "secondvotes_per_district AS (" + second_max_votes_per_district + ")," +
                "second_per_district AS (" + second_candidate_per_district + ")," +
                "first_second_diff_per_district AS (" + join_and_votediff_per_district + ")";

        String query = withClause + "SELECT * FROM first_second_diff_per_district WHERE winnerparty=? AND year=? ORDER BY votediff ASC LIMIT 10";
        try(PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, party.getId());
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                retVal.add(Top10Data.create(db.getQuery().getCandidateById(rs.getInt("winner")), true, 0));
            }
        }
        if (retVal.size() < 10) {
            query = withClause + "SELECT * FROM first_second_diff_per_district WHERE secondparty=? AND year=? ORDER BY votediff ASC LIMIT 10";
            try(PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
                stmt.setInt(1, party.getId());
                stmt.setInt(2, year);
                ResultSet rs = stmt.executeQuery();

                while (rs.next() && retVal.size() < 10) {
                    retVal.add(Top10Data.create(db.getQuery().getCandidateById(rs.getInt("winner")), true, 0));
                }
            }
        }

        return retVal;
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
            while(rs.next()) {
                states.put(rs.getInt(1), State.fullCreate(rs.getInt(1), rs.getString(2), rs.getInt(3)));
//                System.out.println("Added state: "+ rs.getString(2));
            }

            return states;
    }

    public Map<Integer,Candidate> getCandidates() throws SQLException {
        System.out.println("Fetch all parties");
        PreparedStatement stmt = db.getConnection().prepareStatement("select * from candidates");

        Map<Integer, Candidate> candidates = new HashMap<Integer, Candidate>();
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

    public Map<String, Integer> getAmountPerGender() throws SQLException {
        String query = "select c.sex, count(*) as total\n" +
                "from election.parliamentmembers m join election.candidates c on m.id = c.id\n" +
                "group by c.sex";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);

        Map<String, Integer> amountPerGender = new HashMap<String, Integer>();
        ResultSet rs =stmt.executeQuery();
        while(rs.next()) {
            amountPerGender.put(rs.getString(1), rs.getInt(2));
        }
        stmt.close();
        rs.close();

        return amountPerGender;
    }

    public ArrayList<DistrictResults> getDistrictResults(int distOldId, int distNewId) throws SQLException {
        String query = "with pfirst2013(party, first2013) as(\n" +
                "    select dc.party, dc.votes as first2013\n" +
                "    from election.direct_candidatures dc\n" +
                "    where dc.district = ?\n" +
                "),\n" +
                "\n" +
                "psecond2013(party, second2013) as(\n" +
                "    select dc.party, dc.votes as second2013\n" +
                "    from election.secondvote_aggregates dc\n" +
                "    where dc.district = ?\n" +
                "),\n" +
                "\n" +
                "pfirst2017(party, first2017) as(\n" +
                "    select dc.party, dc.votes as first2017\n" +
                "    from election.direct_candidatures dc\n" +
                "    where dc.district = ?\n" +
                "),\n" +
                "\n" +
                "psecond2017(party, second2017) as(\n" +
                "    select dc.party, dc.votes as second2017\n" +
                "    from election.secondvote_aggregates dc\n" +
                "    where dc.district = ?\n" +
                ")\n" +
                "\n" +
                "select p.name, pf13.first2013, ps13.second2013, pf17.first2017, ps17.second2017\n" +
                "from election.parties p \n" +
                "left outer join pFirst2013 pf13 on p.id = pf13.party \n" +
                "left outer join pSecond2013 ps13 on p.id = ps13.party\n" +
                "left outer join pFirst2017 pf17 on p.id = pf17.party \n" +
                "left outer join pSecond2017 ps17 on p.id = ps17.party";

        PreparedStatement stmt = db.getConnection().prepareStatement(query);

        stmt.setInt(1   , distOldId);
        stmt.setInt(2   , distOldId);
        stmt.setInt(3   , distNewId);
        stmt.setInt(4   , distNewId);

        ArrayList<DistrictResults> districtResults = new ArrayList<DistrictResults>();
        ResultSet rs =stmt.executeQuery();

        while(rs.next()) {
            districtResults.add(
                    DistrictResults.create(rs.getString(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getInt(4),
                            rs.getInt(5)
                    ));
            System.out.println(districtResults.get(districtResults.size()-1));

        }
        stmt.close();
        rs.close();

        return districtResults;
    }

    public void updateAggregates() throws SQLException {
        String query = "DELETE FROM election.secondvote_aggregates";
        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();

        query = "INSERT INTO election.secondvote_aggregates(district,party,votes)" +
                "SELECT b.district,s.party,COUNT(*) AS votes " +
                "FROM election.ballots AS b, election.statelists AS s " +
                "WHERE b.secondvote = s.id " +
                "GROUP BY b.district,s.party";
        stmt = db.getConnection().prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();

        query = "WITH aggregated_first AS (SELECT b.firstvote,COUNT(*) AS votes " +
                "    FROM election.ballots AS b " +
                "    GROUP BY b.firstvote " +
                "    HAVING b.firstvote IS NOT NULL) " +
                "UPDATE election.direct_candidatures " +
                "    SET votes = (SELECT votes FROM aggregated_first WHERE firstvote = id) " +
                "    WHERE EXISTS (SELECT * FROM aggregated_first WHERE firstvote = id)";
        stmt = db.getConnection().prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();

        query = "WITH invalid_first AS (" +
                "       SELECT district,COUNT(*) AS votes FROM election.ballots WHERE firstvote  IS NULL GROUP BY district)," +
                "     invalid_second AS (" +
                "       SELECT district,COUNT(*) AS votes FROM election.ballots WHERE secondvote IS NULL GROUP BY district)" +
                "UPDATE election.districts" +
                "   SET invalidfirstvotes =  (SELECT votes FROM invalid_first  WHERE district = id)," +
                "       invalidsecondvotes = (SELECT votes FROM invalid_second WHERE district = id)";
        stmt = db.getConnection().prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();
    }

    public List<String> getWinnigParties(int districtId) throws SQLException{
        String query = "with firstWinner as(\n" +
                "    select max(dc.votes) as winner\n" +
                "    from election.direct_candidatures dc join election.districts d on d.id = dc.district\n" +
                "    where d.id = ?\n" +
                "),\n" +
                "\n" +
                "secondWinner as(\n" +
                "    select max(sa.votes) as winner\n" +
                "    from election.secondvote_aggregates sa join election.districts d on d.id = sa.district\n" +
                "    where d.id = ?\n" +
                ")\n" +
                "\n" +
                "select dc.party, sa.party\n" +
                "from \n" +
                "election.direct_candidatures dc join firstWinner fw on dc.votes = fw.winner,\n" +
                "election.secondvote_aggregates sa join secondWinner sw on sa.votes = sw.winner\n" +
                "where dc.district = ? and sa.district = ?";

        PreparedStatement stmt = db.getConnection().prepareStatement(query);
        stmt.setInt(1   , districtId);
        stmt.setInt(2   , districtId);
        stmt.setInt(3   , districtId);
        stmt.setInt(4   , districtId);
        List<String> winningParty = new ArrayList<String>();

        ResultSet rs =stmt.executeQuery();
        while(rs.next()) {
            winningParty.add(Controller.get().getParty(rs.getInt(1)).getName());
            winningParty.add(Controller.get().getParty(rs.getInt(2)).getName());
        }
        stmt.close();
        rs.close();

        return winningParty;
    }
}
