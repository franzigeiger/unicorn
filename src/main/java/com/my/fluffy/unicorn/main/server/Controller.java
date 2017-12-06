package com.my.fluffy.unicorn.main.server;

import com.my.fluffy.unicorn.main.client.data.*;
import com.my.fluffy.unicorn.main.server.db.DatabaseStatements;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controller {

    private static Controller instance;

    Map<Integer, Party> parties = null;
    Map<Integer, District> districts = null;
    Map<Integer, State> states = null;
    Map<Integer, Candidate> candidates = null;
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
            districts = statements.getDistricts();
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

    public District getDistrict(int index){

        return districts.get(index);
    }

    public Map<Integer, District> getDistrictMap(){
        return this.districts;
    }


    public Map<Party, DifferenceFirstSecondVotes> getDifferenceFirstSecond(int year){
        try {
            return statements.getDifferencesFirstSecondVotes(year);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<Party, Integer> getFirstVotesPerParty(int year){
        try {

            return statements.getFirstVotesPerParty(year);

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
            Candidate winner = statements.getDirectWinner(district);
            return winner;
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

    public List<Top10Data> getTopTen(int partyId, int year) {
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


    public Map<String, Integer> getAmountPerGender(){
        try {
            return statements.getAmountPerGender();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public ArrayList<DistrictResults> getDistrictResults(int districtIdOld, int districtIdNew){
        try {
            return statements.getDistrictResults(districtIdOld, districtIdNew);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void updateAggregates(){
        try {
           statements.updateAggregates();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<String> getWinningParties(int districtId){
        try {
            return statements.getWinnigParties(districtId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
