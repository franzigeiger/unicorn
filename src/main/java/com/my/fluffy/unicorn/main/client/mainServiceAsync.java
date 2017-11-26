package com.my.fluffy.unicorn.main.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.my.fluffy.unicorn.main.client.data.*;

import java.util.List;
import java.util.Map;

public interface mainServiceAsync {

    void getParlamentSeats(int year, AsyncCallback<Map<Party, Integer>> async);

    void getParlamentMembers(int year, AsyncCallback<Map<Candidate, Party>> async);

    void getAllDistricts(int year, AsyncCallback<Map<Integer, String>> async);

    void getDistrict(int districtId, int year, AsyncCallback<District> async);

    void getDistrictWinners(int districtID, AsyncCallback<List<Candidate>> async);

    void getAdditionalMandatsPerParty(AsyncCallback<Map<Party, Map<State, Integer>>> async);

    void getAdditionalMandatsPerstate(AsyncCallback<Map<State, Integer>> async);

    void getParties(AsyncCallback<List<Party>> async);

    void getTopTen(int parteiID, AsyncCallback<List<Candidate>> async);

    void getPartyPercent(int year, AsyncCallback<Map<Party, Double>> async);

    void getDifferencesFirstSecondVotes(int year, AsyncCallback<Map<Party, DifferenceFirstSecondVotes>> async);

}
