package com.my.fluffy.unicorn.main.server;

import com.my.fluffy.unicorn.main.client.data.Candidate;
import com.my.fluffy.unicorn.main.client.data.State;
import com.my.fluffy.unicorn.main.client.data.Candidate;
import com.my.fluffy.unicorn.main.client.data.District;
import com.my.fluffy.unicorn.main.server.db.DatabaseStatements;
import com.my.fluffy.unicorn.main.client.data.Party;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controller {

    private static Controller instance;

    Map<Integer, State> states = null;
    Map<Integer, Candidate> candidates = null;
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
            parties = statements.getParties();
            states = statements.getStates();
            candidates = statements.getCandidates();
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

    public List<Party> getParties() {
        return new ArrayList<>(parties.values());
    }

    public List<Candidate> getTopTen(int partyId, int year) {
        try {
            return statements.getTopTen(getParty(partyId), year);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public State getState(int stateID) {
        System.out.println(states + "Looking for id: " + stateID);
       return  states.get(stateID);
    }

    public Candidate getCandidate(int candidateID){
        return candidates.get(candidateID);
    }
}
