package com.my.fluffy.unicorn.main.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.my.fluffy.unicorn.main.client.data.Candidate;
import com.my.fluffy.unicorn.main.client.data.District;
import com.my.fluffy.unicorn.main.client.data.Party;
import com.my.fluffy.unicorn.main.client.data.State;

import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath("mainService")
public interface mainService extends RemoteService {

    Map<Party, Integer> getParlamentSeats(int year);

    Map<Party, Double> getPartyPercent(int year);

    Map<Candidate, Party> getParlamentMembers(int year);

    List<District> getAllDistricts(int year);

    District getDistrict(int districtId, int year);

    Candidate getDistrictWinner(int districtID, int year);

    Map<Party, Map<State, Integer>> getAdditionalMandatsPerParty();

    Map<State, Integer>getAdditionalMandatsPerstate();

    List<Party> getParties();

    List<Candidate> getTopTen(int parteiID);

    class App {
        private static mainServiceAsync ourInstance = GWT.create(mainService.class);

        public static synchronized mainServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
