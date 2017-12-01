package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.client.data.Candidate;
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
        String query = "with directCandidates as(\n" +
                "select c.id , d.party from (directwinner w join direct_candidatures d on  w.winner = d.id) join candidates c on d.candidate=c.id where w.year =2017\n" +
                "),\n" +
                "\n" +
                "directFreeCandidates as(\n" +
                "select c.id, s.party, s.state, l.placement from (statelists s join list_candidatures l on s.id=l.statelist) join candidates c on c.id= l.candidate where s.election=2017 and \n" +
                "    c.id not in(select id from directCandidates)\n" +
                "),\n" +
                "\n" +
                "landlist as (\n" +
                "select x.id, x.state, x.party \n" +
                "    from (select ROW_NUMBER() over(partition by party, state order by placement) as r , t.* from directFreeCandidates t) x \n" +
                "    where x.r <=  (select seatsfromlandlist from parlamentdistribution2017 p where p.party =x.party and p.state=x.state)\n" +
                ")\n" +
                "\n" +
                "select * from  directCandidates\n" +
                "union\n" +
                "select id, party from landlist;";

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


    public Map<Party,Double> getPartyPercent(int year) throws SQLException {

        System.out.println("Fetch all parties");
        PreparedStatement stmt = db.getConnection().prepareStatement("select * from rawdistribution where election = ?");
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
                System.out.println("Added state: "+ rs.getString(2));
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
}
