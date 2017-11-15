package com.my.fluffy.unicorn.main.server.parser;

import com.my.fluffy.unicorn.main.server.data.*;
import com.my.fluffy.unicorn.main.server.db.DatabaseConnection;
import com.my.fluffy.unicorn.main.server.parser.data.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class BatchInserter {

    private final DatabaseConnection connection;

    public JsonParser jsonParser;
    public CsvParser csvParser;
    public BallotCreator ballotCreator;

    public ArrayList<CandidateJson> candidatesJson2013;
    public ArrayList<CandidateJson> candidatesJson2017;

    public ArrayList<PartyJson> allPartiesJson;

    public ArrayList<ElectionDistrictJson> allElectionDistrictsJson;
    public ArrayList<StateJson> allStatesJson;

    public ArrayList<StateListJson> allStateListsJson;

    public ArrayList<Election> allElections;

    public BatchInserter(DatabaseConnection connection, String jsonPath, String csvPath) {

        this.csvParser = new CsvParser(jsonPath, csvPath);
        this.jsonParser = csvParser.jsonParser;

        this.candidatesJson2013 = csvParser.candidates2013;
        this.candidatesJson2017 = jsonParser.allCandidates2017;

        this.allPartiesJson = jsonParser.allParties;
        this.allElectionDistrictsJson = jsonParser.allElectionDistrictJsons;
        this.allStatesJson = this.jsonParser.allStates;

        this.allStateListsJson = jsonParser.allStateListJsons;

        this.allElections = new ArrayList<>();
        allElections.add(new Election(LocalDate.of(2013, 9, 22)));
        allElections.add(new Election(LocalDate.of(2017, 9, 24)));

        this.connection = connection;
    }

    public ArrayList<Candidate> convertCandidates(){

        ArrayList<Candidate> candidates = new ArrayList<>();

        for(CandidateJson c: this.candidatesJson2017){
            candidates.add(new Candidate(c.title, c.firstName, c.name,
                    c.profession, c.gender, c.hometown, c.birthPlace, c.birthYear));
        }
        for(CandidateJson c: this.candidatesJson2013){
            candidates.add(new Candidate(c.title, c.firstName, c.name,
                    c.profession, c.gender, c.hometown, c.birthPlace, c.birthYear));
        }
        return candidates;
    }

    public ArrayList<Party> convertParties(){

        ArrayList<Party> parties = new ArrayList<>();

        for(PartyJson p: allPartiesJson){
            parties.add(new Party(p.name));
        }
        return parties;
    }

    public ArrayList<State> convertStates(){
        ArrayList<State> states = new ArrayList<>();

        for(StateJson s: allStatesJson){
            states.add(new State(s.name));
        }
        return states;
    }

    public ArrayList<District> convertDistricts(){
        ArrayList<District> districts = new ArrayList<>();

        for(ElectionDistrictJson d: allElectionDistrictsJson){

            districts.add(new District(
                    d.id,
                    allElections.get(1),
                    d.eligibleVoters_17,
                    findState(d.state)
            ));
        }
        return districts;
    }

/*    public ArrayList<SecondVoteAggregates> convertSecondVoteAggregates(){
        ArrayList<Party> parties = convertParties();
        ArrayList<District> districts = convertDistricts();



    }*/

    private State findState(StateJson stateJson){
        ArrayList<State> states = convertStates();
        for(State s: states){
            if(s.getName().equals(stateJson.name)){
                return s;
            }
        }
        return null;
    }

    public void insertAll() {
        // TODO implement
    }
}
