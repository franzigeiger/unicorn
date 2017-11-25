package com.my.fluffy.unicorn.main.server.parser;

import com.my.fluffy.unicorn.main.client.data.*;
import com.my.fluffy.unicorn.main.server.db.DatabaseConnection;
import com.my.fluffy.unicorn.main.server.parser.data.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class BatchInserter {
    private final DatabaseConnection connection;

    private ArrayList<CandidateJson> candidatesJson;
    private ArrayList<PartyJson> allPartiesJson;
    private ArrayList<ElectionDistrictJson> allElectionDistrictsJson;
    private ArrayList<StateJson> allStatesJson;
    private ArrayList<StateListJson> allStateListsJson;

    public Map<Integer, Election> elections;
    public Map<PartyJson, Party> parties;
    public Map<StateJson, State> states;
    public Map<CandidateJson, Candidate> candidates;
    public Map<Integer, Map<ElectionDistrictJson, District>> districts;
    public Map<CandidateJson, DirectCandidature> directCandidatures;
    public Map<StateListJson, StateList> stateLists;
    public Map<CandidateJson, ListCandidature> listCandidatures;

    private Map<Integer, Map<ElectionDistrictJson, Map<PartyJson, CandidateJson>>> electionDistrictPartyToCandidate = new HashMap<>();
    private Map<Integer, Map<StateJson, Map<PartyJson, StateListJson>>> electionStatePartyToList = new HashMap<>();

    public BatchInserter(DatabaseConnection connection, String jsonPath) {

        JsonParser jsonParser = new JsonParser();
        jsonParser.parseAll(jsonPath);

        this.candidatesJson = jsonParser.allCandidates2017;
        this.allPartiesJson = jsonParser.allParties;
        this.allElectionDistrictsJson = jsonParser.allElectionDistrictJsons;
        this.allStatesJson = jsonParser.allStates;
        this.allStateListsJson = jsonParser.allStateListJsons;

        this.elections = new HashMap<>(2);
        elections.put(2013, Election.create(new Date(2013, 9, 22)));
        elections.put(2017, Election.create(new Date(2017, 9,22)));
        electionStatePartyToList.put(2013, new HashMap<>());
        electionStatePartyToList.put(2017, new HashMap<>());
        electionDistrictPartyToCandidate.put(2013, new HashMap<>());
        electionDistrictPartyToCandidate.put(2017, new HashMap<>());

        this.candidates = convertCandidates();
        this.parties = convertParties();
        this.states = convertStates();
        this.districts = convertDistricts();
        this.directCandidatures = convertDirectCandidatures();
        this.stateLists = convertStateLists();
        this.listCandidatures = convertListCandidatures();

        this.connection = connection;
    }

    private Map<CandidateJson, Candidate> convertCandidates(){
        Map<CandidateJson, Candidate> candidates = new HashMap<>();
        for(CandidateJson c: this.candidatesJson){
            candidates.put(c, Candidate.create(c.title, c.firstName, c.name,
                    c.profession, c.gender.toUpperCase(), c.hometown, c.birthPlace, c.birthYear));
        }
        return candidates;
    }

    private Map<PartyJson, Party> convertParties(){
        Map<PartyJson, Party> convertedParties = new HashMap<>();
        for(PartyJson p: allPartiesJson){
            convertedParties.put(p, Party.create(p.name));
        }
        return convertedParties;
    }

    private Map<StateJson, State> convertStates(){
        Map<StateJson, State> convertedStates = new HashMap<>();
        for(StateJson s: allStatesJson){
            // init helper map
            electionStatePartyToList.get(2013).put(s, new HashMap<>());
            electionStatePartyToList.get(2017).put(s, new HashMap<>());

            convertedStates.put(s, State.create(s.name));
        }
        return convertedStates;
    }

    private Map<Integer, Map<ElectionDistrictJson, District>> convertDistricts(){
        Map<Integer, Map<ElectionDistrictJson, District>> convertedDistricts = new HashMap<>();
        convertedDistricts.put(2013, new HashMap<>());
        convertedDistricts.put(2017, new HashMap<>());

        for(ElectionDistrictJson d: allElectionDistrictsJson){
            // init helper map
            electionDistrictPartyToCandidate.get(2013).put(d, new HashMap<>());
            electionDistrictPartyToCandidate.get(2017).put(d, new HashMap<>());

            convertedDistricts.get(2017).put(d, District.create(
                    d.id,
                    findElection(2017),
                    findState(d.state),
                    d.name,
                    d.eligibleVoters_17
            ));

            convertedDistricts.get(2013).put(d, District.create(
                    d.id,
                    findElection(2013),
                    findState(d.state),
                    d.name,
                    d.eligibleVoters_13
            ));
        }
        return convertedDistricts;
    }

    private Map<CandidateJson, DirectCandidature> convertDirectCandidatures(){
        Map<CandidateJson, DirectCandidature> convertedDirectCandidatures = new HashMap<>();

        for(CandidateJson c: this.candidatesJson){
            if(c.directCandidatureJson != null){
                convertedDirectCandidatures.put(c, DirectCandidature.create(
                        findDistrict(c.directCandidatureJson.electionDistrictJson, 2017),
                        findCandidate(c),
                        findParty(c.directCandidatureJson.partyJson)
                ));

                electionDistrictPartyToCandidate.get(2017)
                        .get(c.directCandidatureJson.electionDistrictJson)
                        .put(c.directCandidatureJson.partyJson, c);
            }
        }

       for(ElectionDistrictJson d: this.allElectionDistrictsJson){
            for(PartyResultsJson pr: d.partyResultJsons){
                // create dummy direct candidature for every party
                // received more than 0 first votes in 2013
                if(pr.first_13 > 0){
                    // dummy candidate for 2013
                    CandidateJson dummyCandidate = addDummyCandidate(
                            new DirectCandidatureJson(d, pr.partyJson),
                            null);

                    convertedDirectCandidatures.put(dummyCandidate, DirectCandidature.create(
                            findDistrict(d, 2013),
                            findCandidate(dummyCandidate),
                            findParty(pr.partyJson)
                    ));

                    electionDistrictPartyToCandidate.get(2013)
                            .get(d)
                            .put(pr.partyJson, dummyCandidate);

                }
            }
        }
        return convertedDirectCandidatures;
    }

    private Map<StateListJson, StateList> convertStateLists(){
        Map<StateListJson, StateList> convertedStateLists = new HashMap<>();

        for(StateListJson stateListJson: this.allStateListsJson){
            electionStatePartyToList.get(stateListJson.yearOfCandidature)
                    .get(stateListJson.state)
                    .put(stateListJson.partyJson, stateListJson);
            convertedStateLists.put(stateListJson, StateList.create(
                    findParty(stateListJson.partyJson),
                    findElection(stateListJson.yearOfCandidature),
                    findState(stateListJson.state)
            ));
        }

        for(StateJson s: this.allStatesJson){
            for(PartyResultsJson pr: s.partyResultJsons){
                //if party received more than 1 vote in this state
                if(pr.second_13 > 0){
                    //create dummy list for party
                    StateListJson dummyList = new StateListJson(2013, pr.partyJson, s);
                    this.allStateListsJson.add(dummyList);

                    electionStatePartyToList.get(dummyList.yearOfCandidature)
                            .get(dummyList.state)
                            .put(dummyList.partyJson, dummyList);

                    convertedStateLists.put(dummyList, StateList.create(
                            findParty(dummyList.partyJson),
                            findElection(dummyList.yearOfCandidature),
                            findState(dummyList.state)
                    ));
                }
            }
        }

        return convertedStateLists;
    }

    private Map<CandidateJson, ListCandidature> convertListCandidatures(){
        Map<CandidateJson, ListCandidature> convertedListCandidatures = new HashMap<>();

        for(CandidateJson c: this.candidatesJson){
            if(c.listPlacementJson != null){
                convertedListCandidatures.put(c, ListCandidature.create(
                        findCandidate(c),
                        findStateList(c.listPlacementJson.stateListJson),
                        c.listPlacementJson.place
                ));
            }
        }

        //create dummy list candidature for every party that received more than 1 vote in a district
        for(StateJson s: this.allStatesJson){
            for(PartyResultsJson pr: s.partyResultJsons){
                if(pr.second_13 > 0){

                    CandidateJson dummyCandidate = addDummyCandidate(
                            null,
                            new ListPlacementJson(1, findDummyStateList(pr.partyJson.id, s.id)));

                    convertedListCandidatures.put(dummyCandidate, ListCandidature.create(
                            findCandidate(dummyCandidate),
                            findStateList(dummyCandidate.listPlacementJson.stateListJson),
                            dummyCandidate.listPlacementJson.place
                    ));
                }
            }
        }

        return convertedListCandidatures;
    }

    private List<Ballot> createBallots(District d, int count, Map<DirectCandidature, Integer> firstVotes, Map<StateList, Integer> secondVotes) {
        List<Ballot> ballots = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            DirectCandidature dc = getAndDecrement(firstVotes);
            StateList sl = getAndDecrement(secondVotes);
            ballots.add(new Ballot(sl, dc, d));
        }
        return ballots;
    }

    private <T> T getAndDecrement(Map<T,Integer> map) {
        if (map.keySet().isEmpty()) {
            return null;
        } else {
            T t = map.keySet().iterator().next();
            int rem = map.get(t) - 1;
            if (rem <= 0) {
                map.remove(t);
            } else {
                map.put(t, rem);
            }
            return t;
        }
    }

    private StateList findStateList(StateListJson stateListJson){
        if (stateLists.containsKey(stateListJson)) {
            return stateLists.get(stateListJson);
        } else {
            throw new NoSuchElementException("Could not find matching state list for " + stateListJson.toString());
        }
    }

    private StateListJson findDummyStateList(int partyId, int stateId){
        for(StateListJson l: this.allStateListsJson){
            if(l.yearOfCandidature == 2013 && l.partyJson.id == partyId && l.state.id == stateId){
                return l;
            }
        }
        throw new NoSuchElementException("Could not find matching dummy state list for party " + partyId + " in state " + stateId);
    }

    private Election findElection(int year){
        if (elections.containsKey(year)) {
            return elections.get(year);
        } else {
            throw new NoSuchElementException("Could not find matching election for " + year);
        }
    }

    private Candidate findCandidate(CandidateJson candidateJson){
        if (candidates.containsKey(candidateJson)) {
            return candidates.get(candidateJson);
        } else {
            throw new NoSuchElementException("Could not find matching candidate for " + candidateJson.toString());
        }
    }

    private State findState(StateJson stateJson){
        if (states.containsKey(stateJson)) {
            return states.get(stateJson);
        } else {
            throw new NoSuchElementException("Could not find matching state for " + stateJson.toString());
        }
    }

    private Party findParty(PartyJson partyJson){
        if (parties.containsKey(partyJson)) {
            return parties.get(partyJson);
        } else {
            throw new NoSuchElementException("Could not find matching party for " + partyJson.toString());
        }
    }

    private District findDistrict(ElectionDistrictJson districtJson, int year){
        if (districts.containsKey(year) && districts.get(year).containsKey(districtJson)) {
            return districts.get(year).get(districtJson);
        } else {
            throw new NoSuchElementException("Could not find matching district for " + districtJson.toString());
        }
    }

    private CandidateJson addDummyCandidate(DirectCandidatureJson dc, ListPlacementJson lp){
        CandidateJson dummyCandidate = new CandidateJson(-1, "LastName", "FirstName", "Title",
                null, null, null,
                2013,
                -1, null,
                dc,
                lp);
        this.candidatesJson.add(dummyCandidate);
        candidates.put(dummyCandidate, Candidate.create(
                dummyCandidate.title, dummyCandidate.firstName, dummyCandidate.name,
                dummyCandidate.profession, dummyCandidate.gender, dummyCandidate.hometown,
                dummyCandidate.birthPlace, dummyCandidate.birthYear
        ));
        return dummyCandidate;
    }

    public void insertAll() throws SQLException {
        System.out.println(LocalTime.now().toString() + ": Inserting elections...");
        for (Election e : elections.values()) {
            connection.getInserter().insertElection(e);
        }
        System.out.println(LocalTime.now().toString() + ": Inserting parties...");
        for (Party p : parties.values()) {
            connection.getInserter().insertParty(p);
        }
        System.out.println(LocalTime.now().toString() + ": Inserting states...");
        for (State s : states.values()) {
            connection.getInserter().insertState(s);
        }
        System.out.println(LocalTime.now().toString() + ": Inserting candidates...");
        for (Candidate c : candidates.values()) {
            connection.getInserter().insertCandidate(c);
        }
        System.out.println(LocalTime.now().toString() + ": Inserting districts (2013)...");
        for (District d : districts.get(2013).values()) {
            connection.getInserter().insertDistrict(d);
        }
        System.out.println(LocalTime.now().toString() + ": Inserting districts (2017)...");
        for (District d : districts.get(2017).values()) {
            connection.getInserter().insertDistrict(d);
        }
        System.out.println(LocalTime.now().toString() + ": Inserting direct candidatures...");
        for (DirectCandidature d : directCandidatures.values()) {
            connection.getInserter().insertDirectCandidature(d);
        }
        System.out.println(LocalTime.now().toString() + ": Inserting statelists...");
        for (StateList s : stateLists.values()) {
            connection.getInserter().insertStateList(s);
        }
        System.out.println(LocalTime.now().toString() + ": Inserting candidatures...");
        for (ListCandidature l : listCandidatures.values()) {
            connection.getInserter().insertListCandidature(l);
        }

        System.out.println(LocalTime.now().toString() + ": Generating Ballots...");
        generateBallotsIntoDatabase();
    }

    private void generateBallotsIntoDatabase() {
        int i = 0;
        ArrayList<Ballot> convertedBallotsforDistrict = new ArrayList<>();
        for(ElectionDistrictJson districtJson: this.allElectionDistrictsJson){
            System.out.println(LocalTime.now().toString() + ": Generating Ballots (" + ++i + "/" + allElectionDistrictsJson.size() + ")");

            District d = findDistrict(districtJson, 2013);
            Map<DirectCandidature, Integer> firstVotes = new HashMap<>();
            Map<StateList, Integer> secondVotes = new HashMap<>();
            for (PartyResultsJson json : districtJson.partyResultJsons) {
                CandidateJson dcjson = electionDistrictPartyToCandidate.get(2013).get(districtJson).get(json.partyJson);
                StateListJson sljson = electionStatePartyToList.get(2013).get(districtJson.state).get(json.partyJson);
                firstVotes.put(directCandidatures.get(dcjson), json.first_13);
                secondVotes.put(stateLists.get(sljson), json.second_13);
            }
            convertedBallotsforDistrict.addAll(createBallots(d, districtJson.voters_13, firstVotes, secondVotes));

            d = findDistrict(districtJson, 2017);
            firstVotes.clear();
            secondVotes.clear();
            for (PartyResultsJson json : districtJson.partyResultJsons) {
                CandidateJson dcjson = electionDistrictPartyToCandidate.get(2017).get(districtJson).get(json.partyJson);
                StateListJson sljson = electionStatePartyToList.get(2017).get(districtJson.state).get(json.partyJson);
                firstVotes.put(directCandidatures.get(dcjson), json.first_17);
                secondVotes.put(stateLists.get(sljson), json.second_17);
            }
            convertedBallotsforDistrict.addAll(createBallots(d, districtJson.voters_17, firstVotes, secondVotes));

            if (i % 2 == 0) {
                insertBallots(convertedBallotsforDistrict);
                convertedBallotsforDistrict.clear();
            }
        }
        insertBallots(convertedBallotsforDistrict);
        System.out.println(LocalTime.now().toString() + ": Aggregating...");
        try {
            connection.updateAggregates();
            System.out.println(LocalTime.now().toString() + ": Done.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertBallots(List<Ballot> ballots) {
        try {
            connection.getInserter().insertManyBallots(ballots);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
