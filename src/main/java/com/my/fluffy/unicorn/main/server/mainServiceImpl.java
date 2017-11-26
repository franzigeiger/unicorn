package com.my.fluffy.unicorn.main.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.my.fluffy.unicorn.main.client.data.*;
import com.my.fluffy.unicorn.main.client.mainService;
import com.my.fluffy.unicorn.main.server.db.DatabaseConnection;
import com.my.fluffy.unicorn.main.server.db.DatabaseStatements;
import com.my.fluffy.unicorn.main.server.db.DistributionCalculator;

import java.util.List;
import java.util.Map;

public class mainServiceImpl extends RemoteServiceServlet implements mainService {
    // Implementation of sample interface method
    public String getMessage(String msg) {
        return "Client said: \"" + msg + "\"<br>Server answered: \"Hi!\"";
    }

    @Override
    public Map<Party, Integer> getParlamentSeats(int year) {
        try {
        DistributionCalculator calc = new DistributionCalculator(DatabaseConnection.create());
            return calc.calculatePerParty();
        } catch (Exception e) {
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
        return null;
    }

    @Override
    public Map<Integer, String> getAllDistricts(int year) {
        return null;
    }

    @Override
    public District getDistrict(int districtId, int year) {
        return null;
    }

    @Override
    public List<Candidate> getDistrictWinners(int districtID) {
        return null;
    }

    @Override
    public Map<Party, Map<State, Integer>> getAdditionalMandatsPerParty() {
        return null;
    }

    @Override
    public Map<State, Integer> getAdditionalMandatsPerstate() {
        return null;
    }

    @Override
    public List<Party> getParties() {
        return null;
    }

    @Override
    public List<Candidate> getTopTen(int parteiID) {
        return null;
    }

    @Override
    public Map<Party, DifferenceFirstSecondVotes> getDifferencesFirstSecondVotes(int year) {
        return Controller.get().getDifferenceFirstSecond(year);
    }
}