package com.my.fluffy.unicorn.main.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.my.fluffy.unicorn.main.client.mainService;
import com.my.fluffy.unicorn.main.server.db.DatabaseConnection;
import com.my.fluffy.unicorn.main.server.db.DistributionCalculator;
import com.my.fluffy.unicorn.main.client.data.Candidate;
import com.my.fluffy.unicorn.main.client.data.District;
import com.my.fluffy.unicorn.main.client.data.Party;
import com.my.fluffy.unicorn.main.client.data.State;

import java.util.List;
import java.util.Map;

public class mainServiceImpl extends RemoteServiceServlet implements mainService {
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
        DatabaseStatements statements = new DatabaseStatements();
        try {

            return statements.getParlamentMembers();
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
        return Controller.get().getDistrict(districtId,year);
    }

    @Override
    public Candidate getDistrictWinner(int districtID, int year) {
        District district = getDistrict(districtID, year);
        return Controller.get().getDistrictWinner(district);
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
        return Controller.get().getParties();
    }

    @Override
    public List<Candidate> getTopTen(int parteiID, int year) {
        return Controller.get().getTopTen(parteiID, year);
    }
}