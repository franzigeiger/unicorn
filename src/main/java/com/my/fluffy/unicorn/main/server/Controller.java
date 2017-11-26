package com.my.fluffy.unicorn.main.server;

import com.my.fluffy.unicorn.main.client.data.DifferenceFirstSecondVotes;
import com.my.fluffy.unicorn.main.client.data.District;
import com.my.fluffy.unicorn.main.server.db.DatabaseStatements;
import com.my.fluffy.unicorn.main.client.data.Party;

import java.sql.SQLException;
import java.util.Map;

public class Controller {

    public static Controller instance;

    Map<Integer, Party> parties = null;
    Map<Integer, District> districts = null;
    Map<Party, DifferenceFirstSecondVotes> diffFirstSecond = null;

    public static Controller get(){
        if(instance ==  null){
            instance = new Controller();

        }

        return instance;
    }

    private Controller(){
        DatabaseStatements statements = new DatabaseStatements();
        try {
            //parties do not have a year
            parties = statements.getParties(2017);
            districts = statements.getDistricts();
            //all other basic infos for 2017 and 2013!
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Party getParty(int index){
       return parties.get(index);
    }

    public Map<Party, Double> getPartyPercent(int year){
        DatabaseStatements statements = new DatabaseStatements();
        try {

           return statements.getPartyPercent(year);

            //all other basic infos for 2017 and 2013!
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public District getDistrict(int index){

        return districts.get(index);
    }


    public Map<Party, DifferenceFirstSecondVotes> getDifferenceFirstSecond(int year){
        if(diffFirstSecond == null){
            DatabaseStatements statements = new DatabaseStatements();
            try {
                this.diffFirstSecond = statements.getDifferencesFirstSecondVotes(year);
                return diffFirstSecond;


                //all other basic infos for 2017 and 2013!
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } else{
            return diffFirstSecond;
        }
    }
}
