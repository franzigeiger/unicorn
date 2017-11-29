package com.my.fluffy.unicorn.main.server;

import com.my.fluffy.unicorn.main.client.data.Candidate;
import com.my.fluffy.unicorn.main.client.data.District;
import com.my.fluffy.unicorn.main.server.db.DatabaseStatements;
import com.my.fluffy.unicorn.main.client.data.Party;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Controller {

    private static Controller instance;

    private Map<Integer, Party> parties = null;
    private final DatabaseStatements statements;

    public static Controller get(){
        if(instance ==  null){
            instance = new Controller();
        }

        return instance;
    }

    private Controller(){
        try {
            statements = new DatabaseStatements();
            //parties do not have a year
            parties = statements.getParties(2017);
            //all other basic infos for 2017 and 2013!
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
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

    public District getDistrict(int districtId, int year) {
        try {
            return statements.getDistrict(districtId,year);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Candidate getDistrictWinner(District district) {
        try {
            return statements.getDirectWinner(district);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<District> getDistricts(int year) {
        try {
            return statements.getDistricts(year);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
