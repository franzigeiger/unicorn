package com.my.fluffy.unicorn.main.server.parser;

import com.my.fluffy.unicorn.main.server.data.*;
import com.my.fluffy.unicorn.main.server.db.DatabaseConnection;
import com.my.fluffy.unicorn.main.server.parser.data.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class BatchInserter {
    private final DatabaseConnection connection;

    private ArrayList<CandidateJson> candidatesJson2013;
    private ArrayList<CandidateJson> candidatesJson2017;
    private ArrayList<PartyJson> allPartiesJson;
    private ArrayList<ElectionDistrictJson> allElectionDistrictsJson;
    private ArrayList<StateJson> allStatesJson;
    private ArrayList<StateListJson> allStateListsJson;

    private Map<Integer, Election> elections;
    private Map<PartyJson, Party> parties;
    private Map<StateJson, State> states;
    private Map<CandidateJson, Candidate> candidates;
    private Map<Integer, Map<ElectionDistrictJson, District>> districts;
    private Map<CandidateJson, DirectCandidature> directCandidatures;
    private Map<StateListJson, StateList> stateLists;
    private Map<CandidateJson, ListCandidature> listCandidatures;

    private Map<ElectionDistrictJson, Map<PartyJson, CandidateJson>> districtPartyToCandidate = new HashMap<>();
    private Map<StateJson, Map<PartyJson, StateListJson>> statePartyToList = new HashMap<>();

    public BatchInserter(DatabaseConnection connection, String jsonPath,
                         String csvCandidates2013, String csvResults2013) {

        CsvParser csvParser = new CsvParser(jsonPath, csvCandidates2013, csvResults2013);
        JsonParser jsonParser = csvParser.jsonParser;

        this.candidatesJson2013 = csvParser.candidates2013;
        this.candidatesJson2017 = jsonParser.allCandidates2017;
        this.allPartiesJson = jsonParser.allParties;
        this.allElectionDistrictsJson = jsonParser.allElectionDistrictJsons;
        this.allStatesJson = jsonParser.allStates;
        this.allStateListsJson = jsonParser.allStateListJsons;

        this.elections = new HashMap<>(2);
        elections.put(2013, Election.create(LocalDate.of(2013, 9, 22)));
        elections.put(2017, Election.create(LocalDate.of(2017, 9, 24)));

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
        for(CandidateJson c: this.candidatesJson2017){
            candidates.put(c, Candidate.create(c.title, c.firstName, c.name,
                    c.profession, c.gender.toUpperCase(), c.hometown, c.birthPlace, c.birthYear));
        }
        for(CandidateJson c: this.candidatesJson2013){
            candidates.put(c, Candidate.create(c.title, c.firstName, c.name,
                    c.profession, c.gender, c.hometown, c.birthPlace, c.birthYear));
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
            statePartyToList.put(s, new HashMap<>());

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
            districtPartyToCandidate.put(d, new HashMap<>());

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

        for(CandidateJson c: this.candidatesJson2017){
            if(c.directCandidatureJson != null){
                convertedDirectCandidatures.put(c, DirectCandidature.create(
                        findDistrict(c.directCandidatureJson.electionDistrictJson, 2017),
                        findCandidate(c),
                        findParty(c.directCandidatureJson.partyJson)
                ));

                districtPartyToCandidate.get(c.directCandidatureJson.electionDistrictJson)
                        .put(c.directCandidatureJson.partyJson, c);
            }
        }
        for(CandidateJson c: this.candidatesJson2013){
            if(c.directCandidatureJson != null){
                convertedDirectCandidatures.put(c, DirectCandidature.create(
                        findDistrict(c.directCandidatureJson.electionDistrictJson, 2013),
                        findCandidate(c),
                        findParty(c.directCandidatureJson.partyJson)
                ));

                districtPartyToCandidate.get(c.directCandidatureJson.electionDistrictJson)
                        .put(c.directCandidatureJson.partyJson, c);
            }
        }
        return convertedDirectCandidatures;
    }

    private Map<StateListJson, StateList> convertStateLists(){
        Map<StateListJson, StateList> convertedStateLists = new HashMap<>();

        for(StateListJson stateListJson: this.allStateListsJson){
            statePartyToList.get(stateListJson.state).put(stateListJson.partyJson, stateListJson);
            convertedStateLists.put(stateListJson, StateList.create(
                    findParty(stateListJson.partyJson),
                    findElection(stateListJson.yearOfCandidature),
                    findState(stateListJson.state)
            ));
        }
        return convertedStateLists;
    }

    private Map<CandidateJson, ListCandidature> convertListCandidatures(){
        Map<CandidateJson, ListCandidature> convertedListCandidatures = new HashMap<>();

        for(CandidateJson c: this.candidatesJson2017){
            if(c.listPlacementJson != null){
                convertedListCandidatures.put(c, ListCandidature.create(
                        findCandidate(c),
                        findStateList(c.listPlacementJson.stateListJson),
                        c.listPlacementJson.place
                ));
            }
        }

        for(CandidateJson c: this.candidatesJson2013){
            if(c.listPlacementJson != null){
                convertedListCandidatures.put(c, ListCandidature.create(
                        findCandidate(c),
                        findStateList(c.listPlacementJson.stateListJson),
                        c.listPlacementJson.place
                ));
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

    public void insertAll() throws SQLException {
        for (Election e : elections.values()) {
            connection.getInserter().insertElection(e);
        }
        for (Party p : parties.values()) {
            connection.getInserter().insertParty(p);
        }
        for (State s : states.values()) {
            connection.getInserter().insertState(s);
        }
        for (Candidate c : candidates.values()) {
            connection.getInserter().insertCandidate(c);
        }
        for (District d : districts.get(2013).values()) {
            connection.getInserter().insertDistrict(d);
        }
        for (District d : districts.get(2017).values()) {
            connection.getInserter().insertDistrict(d);
        }
        for (DirectCandidature d : directCandidatures.values()) {
            connection.getInserter().insertDirectCandidature(d);
        }
        for (StateList s : stateLists.values()) {
            connection.getInserter().insertStateList(s);
        }
        for (ListCandidature l : listCandidatures.values()) {
            connection.getInserter().insertListCandidature(l);
        }

        generateBallotsIntoDatabase();
    }

    private void generateBallotsIntoDatabase(){
        for(ElectionDistrictJson districtJson: this.allElectionDistrictsJson){
            ArrayList<Ballot> convertedBallotsforDistrict = new ArrayList<>();

            District d = findDistrict(districtJson, 2013);
            Map<DirectCandidature, Integer> firstVotes = new HashMap<>();
            Map<StateList, Integer> secondVotes = new HashMap<>();
            for (PartyResultsJson json : districtJson.partyResultJsons) {
                CandidateJson dcjson = districtPartyToCandidate.get(districtJson).get(json.partyJson);
                StateListJson sljson = statePartyToList.get(districtJson.state).get(json.partyJson);
                firstVotes.put(directCandidatures.get(dcjson), json.first_13);
                secondVotes.put(stateLists.get(sljson), json.second_13);
            }
            convertedBallotsforDistrict.addAll(createBallots(d, districtJson.eligibleVoters_13, firstVotes, secondVotes));

            d = findDistrict(districtJson, 2017);
            firstVotes.clear();
            secondVotes.clear();
            for (PartyResultsJson json : districtJson.partyResultJsons) {
                CandidateJson dcjson = districtPartyToCandidate.get(districtJson).get(json.partyJson);
                StateListJson sljson = statePartyToList.get(districtJson.state).get(json.partyJson);
                firstVotes.put(directCandidatures.get(dcjson), json.first_17);
                secondVotes.put(stateLists.get(sljson), json.second_17);
            }
            convertedBallotsforDistrict.addAll(createBallots(d, districtJson.eligibleVoters_17, firstVotes, secondVotes));

            try {
                connection.getInserter().insertManyBallots(convertedBallotsforDistrict);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
