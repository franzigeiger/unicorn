package com.my.fluffy.unicorn.main.server.parser;

import com.my.fluffy.unicorn.main.server.data.*;
import com.my.fluffy.unicorn.main.server.db.DatabaseConnection;
import com.my.fluffy.unicorn.main.server.parser.data.*;

import java.sql.SQLException;
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


    public ArrayList<Election> elections;
    public ArrayList<Party> parties;
    public ArrayList<State> states;
    public ArrayList<Candidate> candidates;
    public ArrayList<District> districts;
//    public ArrayList<SecondVoteAggregates> secondVoteAggregates;
    public ArrayList<DirectCandidature> directCandidatures;
    public ArrayList<StateList> stateLists;
    public ArrayList<ListCandidature> listCandidatures;


    public BatchInserter(DatabaseConnection connection, String jsonPath,
                         String csvCandidates2013, String csvResults2013) {

        this.csvParser = new CsvParser(jsonPath, csvCandidates2013, csvResults2013);
        this.jsonParser = csvParser.jsonParser;
        this.ballotCreator = new BallotCreator(jsonParser.allParties,
                jsonParser.allElectionDistrictJsons,
                jsonParser.allCandidates2017);

        this.candidatesJson2013 = csvParser.candidates2013;
        this.candidatesJson2017 = jsonParser.allCandidates2017;
        this.allPartiesJson = jsonParser.allParties;
        this.allElectionDistrictsJson = jsonParser.allElectionDistrictJsons;
        this.allStatesJson = this.jsonParser.allStates;
        this.allStateListsJson = jsonParser.allStateListJsons;

        this.elections = new ArrayList<>();
        elections.add(new Election(LocalDate.of(2013, 9, 22)));
        elections.add(new Election(LocalDate.of(2017, 9, 24)));

        this.candidates = convertCandidates();

        this.parties = convertParties();
        this.states = convertStates();
        this.districts = convertDistricts();
//        this.secondVoteAggregates = convertSecondVoteAggregates();
        this.directCandidatures = convertDirectCandidatures();
        this.stateLists = convertStateLists();
        this.listCandidatures = convertListCandidatures();

//        convertBallots();

        this.connection = connection;
    }

    public ArrayList<Candidate> convertCandidates(){

        ArrayList<Candidate> candidates = new ArrayList<>();

        for(CandidateJson c: this.candidatesJson2017){
            candidates.add(new Candidate(c.title, c.firstName, c.name,
                    c.profession, c.gender.toUpperCase(), c.hometown, c.birthPlace, c.birthYear));
        }
        for(CandidateJson c: this.candidatesJson2013){
            candidates.add(new Candidate(c.title, c.firstName, c.name,
                    c.profession, c.gender, c.hometown, c.birthPlace, c.birthYear));
        }
        return candidates;
    }

    public ArrayList<Party> convertParties(){

        ArrayList<Party> convertedParties = new ArrayList<>();

        for(PartyJson p: allPartiesJson){
            convertedParties.add(new Party(p.name));
        }
        return convertedParties;
    }

    public ArrayList<State> convertStates(){
        ArrayList<State> convertedStates = new ArrayList<>();

        for(StateJson s: allStatesJson){
            convertedStates.add(new State(s.name));
        }
        return convertedStates;
    }

    public ArrayList<District> convertDistricts(){
        ArrayList<District> convertedDistricts = new ArrayList<>();

        for(ElectionDistrictJson d: allElectionDistrictsJson){
            convertedDistricts.add(new District(
                    d.id,
                    findElection(2017),
                    d.eligibleVoters_17,
                    findState(d.state)
            ));
        }
        for(ElectionDistrictJson d: allElectionDistrictsJson){
            convertedDistricts.add(new District(
                    d.id,
                    findElection(2013),
                    d.eligibleVoters_13,
                    findState(d.state)
            ));
        }
        return convertedDistricts;
    }

    /*public ArrayList<SecondVoteAggregates> convertSecondVoteAggregates(){

        ArrayList<SecondVoteAggregates> convertedSecondVoteAggregates = new ArrayList<>();

        for(ElectionDistrictJson d: this.allElectionDistrictsJson){
            for(PartyResultsJson p: d.partyResultJsons){

                convertedSecondVoteAggregates.add(new SecondVoteAggregates(findDistrict(d), findParty(p.partyJson), p.second_17));
            }
        }
        return convertedSecondVoteAggregates;
    }*/

    public ArrayList<DirectCandidature> convertDirectCandidatures(){
        ArrayList<DirectCandidature> convertedDirectCandidatures = new ArrayList<>();

        for(CandidateJson c: this.candidatesJson2017){
            if(c.directCandidatureJson != null){
                convertedDirectCandidatures.add( new DirectCandidature(
                        findDistrict(c.directCandidatureJson.electionDistrictJson, 2017),
                        findCandidate(c),
                        findParty(c.directCandidatureJson.partyJson)
                ));
            }
        }
        for(CandidateJson c: this.candidatesJson2013){
            if(c.directCandidatureJson != null){
                convertedDirectCandidatures.add( new DirectCandidature(
                        findDistrict(c.directCandidatureJson.electionDistrictJson, 2013),
                        findCandidate(c),
                        findParty(c.directCandidatureJson.partyJson)
                ));
            }
        }
        return convertedDirectCandidatures;
    }

    public ArrayList<StateList> convertStateLists(){
        ArrayList<StateList> convertedStateLists = new ArrayList<>();

        for(StateListJson stateListJson: this.allStateListsJson){
            convertedStateLists.add(new StateList(
                    findParty(stateListJson.partyJson),
                    findElection(stateListJson.yearOfCandidature),
                    findState(stateListJson.state)
            ));
        }
        return convertedStateLists;
    }

    public ArrayList<ListCandidature> convertListCandidatures(){
        ArrayList<ListCandidature> convertedListCandidatures = new ArrayList<>();

        for(CandidateJson c: this.candidatesJson2017){
            if(c.listPlacementJson != null){
                convertedListCandidatures.add(new ListCandidature(
                        findCandidate(c),
                        findStateList(c.listPlacementJson.stateListJson),
                        c.listPlacementJson.place
                ));
            }
        }

        for(CandidateJson c: this.candidatesJson2013){
            if(c.listPlacementJson != null){
                convertedListCandidatures.add(new ListCandidature(
                        findCandidate(c),
                        findStateList(c.listPlacementJson.stateListJson),
                        c.listPlacementJson.place
                ));
            }
        }

        return convertedListCandidatures;
    }

    public void convertBallots(){
        ArrayList<Ballot> convertedBallotsforDistrict = new ArrayList<>();

        int i = 0; int j = 0;
        for(ElectionDistrictJson districtJson: this.allElectionDistrictsJson){
            ArrayList<BallotJson> districtBallots = ballotCreator.createBallots2017(districtJson);
            for(BallotJson ballotJson: districtBallots){
                convertedBallotsforDistrict.add(new Ballot(
                        findStateListForBallot(ballotJson.secondVote, ballotJson.electionDistrictJson),
                        findDirectCandidatureForBallot(ballotJson.secondVote, ballotJson.firstVote, findDistrict(ballotJson.electionDistrictJson, 2017)),
                        findDistrict(ballotJson.electionDistrictJson, 2017)
                ));
            }
            j += districtBallots.size();
            System.out.println(++i + " - " + j);
        }
    }

    private DirectCandidature findDirectCandidatureForBallot(PartyJson party, CandidateJson x, District district){
        for(DirectCandidature d: this.directCandidatures){
            if(d.getDistrict().getNumber() == district.getNumber()
                    && d.getDistrict().getElection().getYear() == district.getNumber()

                    && d.getCandidate().getFirstName().equals(x.firstName)
                    && d.getCandidate().getLastName().equals(x.name)
                    && d.getCandidate().getYearOfBirth() == x.birthYear

                    && d.getParty().getName().equals(party.name)){
                return d;
            }
        }
        /*System.out.println("Could not find matching Direct Candidature for ballot with party "
                + party.name + ", candidate: " + candidate.name + ", district: " + district.getNumber());*/
        if(!x.name.equals("Nicolaisen")){
            System.out.println(x.name);
            System.out.println(x.name.startsWith("N"));
            System.out.println(x.name.hashCode());
            System.out.println("Nicolaisen");
            System.out.println("Nicolaisen".hashCode());
            System.out.println("Could not find matching Direct Candidature for ballot with party "
                    + party.name + ", candidate: " + x.name + ", district: " + district.getNumber());
        }
        return null;
    }

    private StateList findStateListForBallot(PartyJson partyJson, ElectionDistrictJson districtJson){
        for(StateList l: this.stateLists){
            if(l.getElection().getYear() == 2017
                    && l.getState().getName().equals(districtJson.state.name)
                    && l.getParty().getName().equals(partyJson.name)){
                return l;
            }
        }
        System.out.println("Could not find matching state list for ballot of party "
                + partyJson.toString() + " in district " + districtJson.toString());
        return null;
    }

    private StateList findStateList(StateListJson stateListJson){
        for(StateList l: this.stateLists){
            if(l.getParty().getName().equals(stateListJson.partyJson.name)
                    && l.getState().getName().equals(stateListJson.state.name)
                    && l.getElection().getYear() == stateListJson.yearOfCandidature){
                return l;
            }
        }
        System.out.println("Could not find matching state list for " + stateListJson.toString());
        return null;
    }

    private Election findElection(int year){
        for(Election e: elections){
            if(e.getYear() == year){
                return e;
            }
        }
        System.out.println("Could not find matching election for " + year);
        return null;
    }

    private Candidate findCandidate(CandidateJson candidateJson){
        for(Candidate c: candidates){
            if(c.getLastName().equals(candidateJson.name)
                    && c.getFirstName().equals(candidateJson.firstName)
                    && c.getYearOfBirth() == c.getYearOfBirth()){
                return c;
            }
        }
        System.out.println("Could not find matching candidate for " + candidateJson.toString());
        return null;
    }

    private State findState(StateJson stateJson){
        for(State s: states){
            if(s.getName().equals(stateJson.name)){
                return s;
            }
        }
        System.out.println("Could not find matching state for " + stateJson.toString());
        return null;
    }

    private Party findParty(PartyJson partyJson){
        for(Party p: parties){
            if(p.getName().equals(partyJson.name)){
                return p;
            }
        }
        System.out.println("Could not find matching party for " + partyJson.toString());
        return null;
    }

    private District findDistrict(ElectionDistrictJson districtJson, int year){
        for(District d: districts){
            if(d.getNumber() == districtJson.id
                    && d.getState().getName().equals(districtJson.state.name)
                    && d.getElection().getYear() == year){
                return d;
            }
        }
        System.out.println("Could not find matching district for " + districtJson.toString());
        return null;
    }


    public void insertAll() throws SQLException {
        for (Election e : elections) {
            connection.getInserter().insertElection(e);
        }
        for (Party p : parties) {
            connection.getInserter().insertParty(p);
        }
        for (State s : states) {
            connection.getInserter().insertState(s);
        }
        for (Candidate c : candidates) {
            connection.getInserter().insertCandidate(c);
        }
        for (District d : districts) {
            connection.getInserter().insertDistrict(d);
        }
        for (DirectCandidature d : directCandidatures) {
            connection.getInserter().insertDirectCandidature(d);
        }
        for (StateList s : stateLists ) {
            connection.getInserter().insertStateList(s);
        }
        for (ListCandidature l : listCandidatures) {
            connection.getInserter().insertListCandidature(l);
        }
    }
}
