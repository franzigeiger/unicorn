package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.data.Candidate;
import com.my.fluffy.unicorn.main.server.data.Party;
import com.my.fluffy.unicorn.main.server.parser.data.PartyResultsJson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DistributionCalculator {
    class PartyData{
        Party party;
        int seats;
        int additional;
        int compensation;
        public PartyData(Party party, int seats, int additional, int compensation){
            this.party=party;
            this.seats = seats;
            this.additional=additional;
            this.compensation = compensation;
        }
    }

    private final DatabaseConnection db;
    DatabaseQuery calculator;

    DistributionCalculator(DatabaseConnection connection) {
        this.db = connection;
        calculator = new DatabaseQuery(db);
    }

    String calcSeats = "with RECURSIVE allVoter(number) (\n" +
            "select count(*) \n" +
            "from ballots b join direct_candidatures d\n" +
            "on b.district = d.id\n" +
            "where d.year = 2017)\n" +
            "\n" +
            ", parteiVotes (party, votes, state, counter) as (select s.name, s.id, s.sate, count(*) \n" +
            "from ballots b join statelists s on b.secondvote = s.id\n" +
            "group by s.id, s.name, s.state\n" +
            "),\n" +
            "\n" +
            " parteiVotes ( party, votes) as (\n" +
            "values\n" +
            "( 1, 5200 ) ,\n" +
            "(2, 1700) ,\n" +
            "(3, 3100)),\n" +
            "\n" +
            "high (party, counter, divisor) as (\n" +
            "select p.party, (cast (p.votes as decimal (7,3))) / 0.5 , 1.5 from parteiVotes p\n" +
            "union all \n" +
            "select distinct h.party , cast ((select votes from parteiVotes p where p.party = h.party) as decimal (7,3))/h.divisor, h.divisor +1 \n" +
            "from high h where h.divisor < 598 \n" +
            "),\n" +
            "\n" +
            "seats(party, counter) as(\n" +
            "SELECT *\n" +
            "  FROM high\n" +
            " ORDER BY counter DESC\n" +
            " limit 598 \n" +
            "),\n" +
            "secondVotesPercent (party, percent, votes) as (\n" +
            "select party , cast (votes as decimal(3,2))/(select number from allVoters) as percent , votes \n" +
            "from parteiVotes where percent > 0.05; \n" +
            "),\n" +
            "\n" +
            "directWinner(district, winner, votes) as (\n" +
            "select district, firstvote,  max(reached) from\n" +
            "(select district, firstvote, count(*) as reached from ballots group by  district, firstvotes)\n" +
            "group by district, firstvote\n" +
            "),\n" +
            "directWinnerPartyAggregate(party, directWinners) as (\n" +
            "select  k.party, count(*)\n" +
            "from directWinner w join directKandidatures k on d.winner=k.id\n" +
            "group by k.party\n" +
            "),\n" +
            "seatsGrouped(party, groupedSeats) as(\n" +
            "select party, count(*) from seats\n" +
            "group by party\n" +
            "),\n" +
            "\n" +
            "distribution (party, second_votes, second_votes_percent, seats, direct mandats) as (\n" +
            "select p.party, p.percent, p.votes , s.groupedSeats, d.directWinners\n" +
            "from (secondVotesPercent p join seats s on p.party=s.party) join  directWinnerPartyAggregate d on p.party = d.party \n" +
            "),\n" +
            "\n" +
            "gainedSeats(party , seats , additionalMandats) as (\n" +
            "select d.party, (case when d.seats > d.direct_mandats then d.seats else d.direct_madats end) , \n" +
            "( case when d.seats < d.direct_mandats then d.direct_mandats - d.seats alse 0 end)\n" +
            "from distribution\n" +
            "),\n" +
            "\n" +
            "newSeatsNumber(counter)as (\n" +
            "select count(seats) from gainedSeats;\n" +
            "),\n" +
            "\n" +
            "select party, seats, additionMandats, (select counter from newSeats) from gainedSeats";


    String calcWithIncreasingNumberOfSeats = "with seatsCompensation as (select * from high order by counter desc\n" +
            "limit = ? )\n" +
            "select party, count(*) from seats\n" +
            "group by party";


    public void calculate() throws SQLException {
        createViews();
        List<PartyData> workData = calculateMinimumSeats();
        int size= calculateParlamentSize(workData);
        List<PartyData> endResult= calculateEndResult(workData, size);

    }

    private void pringData(List<PartyData> data ){
        for(PartyData d : data){
            System.out.println("Party: " +d.party.getName() + " Seats: "
                    + d.seats  + "containing additional mandats:"
                    + d.additional + " with compensation mandas: " + d.compensation );
        }
    }

    private int calculateParlamentSize( List<PartyData> workData) {
        int counter =0;
        for(PartyData data : workData){
            counter+=data.seats;
        }

        return counter;
    }

    public void createViews(){
        //To Do generate important things as views. Do this after testing of the functionality.
    }

    //calculates number of seats without overwhelming seats
    public List<PartyData> calculateMinimumSeats() throws SQLException {
        List<PartyData> distribution = new ArrayList<PartyData>();
        PreparedStatement stmt = db.getConnection().prepareStatement(calcSeats);

        ResultSet set =stmt.executeQuery();
        stmt.close();

        Party party ;
        int seats;
        int additional;

        while(set.next()){
            party = calculator.getPartyByID(set.getInt(1)) ;
            seats = set.getInt(2);
            additional = set.getInt(3);
            distribution.add(new PartyData(party, seats, additional, 0));
            System.out.println("Party: " + party.getName() + " Seats: "  + seats  + "containing additional mandats:" + additional);
        }

    return distribution;
    }

    public List<PartyData> calculateEndResult(List<PartyData> base, int numberOfSeats){
        boolean isCorrectDistributed= false;
        do{

        }while(!isCorrectDistributed);
    return null;
    }
}
