package com.my.fluffy.unicorn.main.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.my.fluffy.unicorn.main.client.data.*;

import java.util.List;
import java.util.Map;

public interface mainServiceAsync {

    void getParlamentSeats(int year, AsyncCallback<Map<Party, Integer>> async);

    void getParlamentMembers(int year, AsyncCallback<Map<Candidate, Party>> async);

    void getAllDistricts(int year, AsyncCallback<List<District>> async);

    void getDistrict(int districtId, int year, AsyncCallback<District> async);

    void getDistrictWinner(int districtID, int year, AsyncCallback<Candidate> async);

    void getAdditionalMandatsPerParty(int year, AsyncCallback<List<PartyStateInfos>> async);

    void getAdditionalMandatsPerstate(AsyncCallback<Map<State, Integer>> async);

    void getParties(AsyncCallback<List<Party>> async);

    void getTopTen(int parteiID, int year, AsyncCallback<List<Candidate>> async);

    void getPartyPercent(int year, AsyncCallback<Map<Party, Double>> async);
    void getDifferencesFirstSecondVotes(int year, AsyncCallback<Map<Party, DifferenceFirstSecondVotes>> async);
    void getFirstVotesTotal(int year, AsyncCallback<Map<Party, Double>> async);


}
