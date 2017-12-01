package com.my.fluffy.unicorn.main.client;

import com.google.gwt.core.client.GWT;
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

    Map<Integer, String> getAllDistricts(int year);

    District getDistrict(int districtId, int year);

    List<Candidate> getDistrictWinners(int districtID);

    List<PartyStateInfos> getAdditionalMandatsPerParty(int year);

    List<Party> getParties();

    List<Candidate> getTopTen(int parteiID);

    Map<Party, DifferenceFirstSecondVotes> getDifferencesFirstSecondVotes(int year);
    Map<Party, Double> getFirstVotesTotal(int year);

    public static class App {
        private static mainServiceAsync ourInstance = GWT.create(mainService.class);

        public static synchronized mainServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
