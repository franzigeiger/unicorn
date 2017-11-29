package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.Controller;
import com.my.fluffy.unicorn.main.client.data.Party;
import com.my.fluffy.unicorn.main.client.data.State;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * For execution please run script verteilung.sql in your database!
 *
 */

public class DistributionCalculator {
    private static final String calcSeats = "select * from election.parlamentdistribution2017";

    private final DatabaseConnection db;
    private final DatabaseQuery calculator;


    public DistributionCalculator(DatabaseConnection connection) {
        this.db = connection;
        calculator = new DatabaseQuery(db);
    }



    //calculates number of seats without overwhelming seats
    public void calculate() throws SQLException {

        PreparedStatement stmt = db.getConnection().prepareStatement(calcSeats);

        long time = System.currentTimeMillis();
        ResultSet set =stmt.executeQuery();
        long duration= System.currentTimeMillis() - time;

        System.out.println("Duration: " + duration + " ms");
        Party party;
        State state;

        int baseseats=0;
        int seatswithdirect =0;
        int endresult =0;
        System.out.println( "| Party \t | State \t | Baseseats \t | seatswithdirect \t | seatsFromLandlist \t | seatsFromDistrict \t | FinalSeats  \t |");
        while(set.next()){
            party = calculator.getPartyByID(set.getInt(1)) ;
            state = calculator.getStateByID(set.getInt(2));
            int base =set.getInt(3);
            baseseats +=  base;
            int  complete = set.getInt(4);
            seatswithdirect += complete;
            int seatsFromLandlist = set.getInt(5);
            int seatsFromDirect = set.getInt(6);
            int result = set.getInt(7);
            endresult += result;

            System.out.println("| " + party.getName() + "\t | " + state.getName() + "\t | " + base + "\t | " + complete + "\t | " + seatsFromLandlist + "\t | " + seatsFromDirect + "\t | " + result +" \t |");

        }

        stmt.close();
        set.close();
        System.out.println("Seats at beginning: " + baseseats + "\nseats with additionalmandats:" + seatswithdirect + "\nseats in the end: " + endresult);

    }

    public Map<Party, Integer> calculatePerParty()throws Exception {
        System.out.println("Distribution between Parties");

        Map<Party, Integer> dist = new HashMap<>();
        PreparedStatement stmt = db.getConnection().prepareStatement("select party, sum(finalseats) from parlamentdistribution2017 group by party");

        ResultSet set = stmt.executeQuery();
        Party party;
        System.out.println("| Party \t | Values | ");
        while (set.next()) {
            party = Controller.get().getParty(set.getInt(1));
            int base = set.getInt(2);
            dist.put(party, base);
            System.out.println("| " + party.getName() + "\t | " + base + "\t | ");

        }
        stmt.close();
        set.close();

        return dist;
    }

    public void calculatePerState()throws Exception{
        System.out.println("Distribution between States");
        PreparedStatement stmt = db.getConnection().prepareStatement("select state, sum(finalseats) from parlamentdistribution group by state");

        ResultSet set =stmt.executeQuery();
        State state;
        System.out.println( "| State \t | Values | ");
        while(set.next()) {
            state = calculator.getStateByID(set.getInt(1));
            int base = set.getInt(2);

            System.out.println("| " + state.getName() + "\t | " + base + "\t | ");

        }

        stmt.close();
        set.close();
    }
   public static void main(String []args){
       try {
           DistributionCalculator calc = new DistributionCalculator(DatabaseConnection.create());
            calc.calculate();
            calc.calculatePerParty();
            calc.calculatePerState();
       }catch(Exception e){
           e.printStackTrace();
           System.out.print("Error");
       }
   }

}
