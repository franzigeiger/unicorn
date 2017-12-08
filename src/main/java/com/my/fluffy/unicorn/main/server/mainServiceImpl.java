package com.my.fluffy.unicorn.main.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.my.fluffy.unicorn.main.client.data.*;
import com.my.fluffy.unicorn.main.client.mainService;
import com.my.fluffy.unicorn.main.server.db.DatabaseStatements;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class mainServiceImpl extends RemoteServiceServlet implements mainService {
    @Override
    public Map<Party, Integer> getParlamentSeats(int year) {
        DatabaseStatements statements = new DatabaseStatements();
        try {
            return statements.calculatePerParty();
            //all other basic infos for 2017 and 2013!
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<Party, Double> getPartyPercent(int year) {
        return Controller.get().getPartyPercent(year);
    }

    @Override
    public Map<Candidate, Party> getParlamentMembers(int year) {
        DatabaseStatements statements = new DatabaseStatements();
        try {

            return statements.getParlamentMembers(year);
            //all other basic infos for 2017 and 2013!
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<District> getAllDistricts(int year) {
        return Controller.get().getDistricts(year);
    }

    @Override
    public District getDistrict(int districtId, int year) {
        return Controller.get().getDistrict(districtId, year);
    }

    @Override
    public Candidate getDistrictWinner(int districtId) {
        return Controller.get().getDistrictWinner(districtId);
    }

    @Override
    public List<PartyStateInfos> getAdditionalMandatsPerParty(int year) {
        DatabaseStatements statements = new DatabaseStatements();
        try {

            return statements.getAdditionalMandats(year);
            //all other basic infos for 2017 and 2013!
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<State, Integer> getAdditionalMandatsPerstate() {
        return null;
    }

    @Override
    public List<Party> getParties() {
        return Controller.get().getParties();
    }

    @Override
    public List<Top10Data> getTopTen(int parteiID, int year) {
        return Controller.get().getTopTen(parteiID, year);
    }

    @Override
    public Map<Party, DifferenceFirstSecondVotes> getDifferencesFirstSecondVotes(int year) {
        return Controller.get().getDifferenceFirstSecond(year);
    }

    @Override
    public Map<Party, Integer> getFirstVotesTotal(int year) {
        return Controller.get().getFirstVotesPerParty(year);
    }

    @Override
    public Map<String, Integer> getAmountPerGender() {
        return Controller.get().getAmountPerGender();
    }

    @Override
    public List<DistrictResults> getDistrictResults(int districtIdOld, int districtIdNew) {
        return Controller.get().getDistrictResults(districtIdOld, districtIdNew);
    }

    @Override
    public Map<Integer, District> getDistrictMap() {
        return Controller.get().getDistrictMap();
    }

    @Override
    public Map<District, List<String>> getWinningParties(int year) {
        return Controller.get().getWinningParties(year);
    }

    @Override
    public int updateAggregates() {
        Controller.get().updateAggregates();
        return 0;
    }

}