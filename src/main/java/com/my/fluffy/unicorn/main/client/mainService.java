package com.my.fluffy.unicorn.main.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.my.fluffy.unicorn.main.client.data.*;

import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath("mainService")
public interface mainService extends RemoteService {

    Map<Party, Integer> getParlamentSeats(int year);

    Map<Party, Double> getPartyPercent(int year);

    Map<Candidate, Party> getParlamentMembers(int year);

    List<District> getAllDistricts(int year);

    District getDistrict(int districtId, int year);

//    Candidate getDistrictWinner(int districtID, int year);
    Candidate getDistrictWinner(District district, int year);

    List<PartyStateInfos> getAdditionalMandatsPerParty(int year);

    Map<State, Integer>getAdditionalMandatsPerstate();

    List<Party> getParties();

    List<Candidate> getTopTen(int parteiID, int year);

    Map<Party, DifferenceFirstSecondVotes> getDifferencesFirstSecondVotes(int year);
    Map<Party, Integer> getFirstVotesTotal(int year);

    Map<String, Integer> getAmountPerGender();
    List<DistrictResults> getDistrictResults(int districtIdOld, int districtIdNew);
    Map<Integer, District> getDistrictMap();

    List<String> getWinningParties(int districtId);

    int updateAggregates();

    void startupRESTService();

    class App {


        private static mainServiceAsync ourInstance = GWT.create(mainService.class);

        public static synchronized mainServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
