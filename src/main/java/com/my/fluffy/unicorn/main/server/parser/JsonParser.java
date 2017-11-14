package com.my.fluffy.unicorn.main.server.parser;

import com.my.fluffy.unicorn.main.server.parser.data.*;
import org.json.*;

import java.io.*;
import java.util.ArrayList;

public class JsonParser {

    public JSONObject obj;

    public ArrayList<Party> allParties;
    public ArrayList<State> allStates;
    public ArrayList<ElectionDistrict> allElectionDistricts;

    public ArrayList<StateList> allStateLists = new ArrayList<>();

    public ArrayList<Candidate> allCandidates2017;

    public JsonParser(){
    }

    /**
     * Loads complete.json from path,
     * extracts all data from file
     */
    public void parseAll(String path){
        loadJsonFile(path);
        this.allParties = loadParties();
        this.allStates = loadStates();
        this.allElectionDistricts = loadElectionDistricts();
        this.allCandidates2017 = loadCandidates();
    }

    /**
     * Loads json file and saved it asJSONObject in obj
     * @param path path to json file
     */
    private void loadJsonFile(String path){
        String jsonText = "";
        try{
            jsonText = loadFileToString(path);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Could not load file!");
        }
        this.obj = new JSONObject(jsonText);
    }

    /**
     * Gets all parties from obj
     * @return all parties as ArrayList
     */
    private ArrayList<Party> loadParties(){

        ArrayList<Party> parties = new ArrayList<>();
        JSONObject partiesObj = obj.getJSONObject("parteien");

        for (int i = 1; i < partiesObj.length()+1; i++) {

            JSONObject currentParty = partiesObj.getJSONObject(Integer.toString(i));

            int id = currentParty.getInt("ID");
            String name = currentParty.getString("Name");

            parties.add(new Party(id, name));
        }
        return parties;
    }

    public Party getParty(int partyId){
        for(Party party: this.allParties){
            if(party.id == partyId){
                return party;
            }
        }
        return null;
    }

    public Party getParty(String partyName){
        for(Party party: this.allParties){
            if(party.name.equals(partyName)){
                return party;
            }
        }
        return null;
    }

    /**
     * Gets all states from obj
     * @return all states as ArrayList
     */
    private ArrayList<State> loadStates(){

        ArrayList<State> states = new ArrayList<>();
        JSONObject statesObj = obj.getJSONObject("bundeslaender");

        for (int i = 1; i < statesObj.length()+1; i++) {

            JSONObject currentState = statesObj.getJSONObject(Integer.toString(i));

            int id = currentState.getInt("ID");
            String name = currentState.getString("Name");

            int eligibleVoters_13 = currentState.getInt("Wahlberechtigte_13");
            int eligibleVoters_17 = currentState.getInt("Wahlberechtigte_17");

            int voters_13 = currentState.getInt("Waehler_13");
            int voters_17 = currentState.getInt("Waehler_17");

            int valid_13_first = currentState.getInt("Gueltige_13_Erst");
            int valid_17_first = currentState.getInt("Gueltige_17_Erst");

            int invalid_13_first = currentState.getInt("Ungueltige_13_Erst");
            int invalid_17_first = currentState.getInt("Ungueltige_17_Erst");

            int valid_13_second = currentState.getInt("Gueltige_13_Zweit");
            int valid_17_second = currentState.getInt("Gueltige_17_Zweit");

            int invalid_13_second = currentState.getInt("Ungueltige_13_Zweit");
            int invalid_17_second = currentState.getInt("Ungueltige_17_Zweit");

            JSONArray partyResultsArray = currentState.getJSONArray("ParteiErgebnisse");
            ArrayList<PartyResults> partyResults = loadPartyResults(partyResultsArray);

            states.add(new State(
                    id, name,
                    eligibleVoters_13, eligibleVoters_17,
                    voters_13, voters_17,
                    valid_13_first, valid_17_first,
                    invalid_13_first, invalid_17_first,
                    valid_13_second, valid_17_second,
                    invalid_13_second, invalid_17_second,
                    partyResults
            ));
        }
        return states;
    }

    public State getState(String stateName){
        for(State state: this.allStates){
            if(state.name.equals(stateName)){
                return state;
            }
        }
        return null;
    }

    public State getState(int stateId){
        for(State state: this.allStates){
            if(state.id == stateId){
                return state;
            }
        }
        return null;
    }

    /**
     * Gets all election districts from obj
     * @return all election districts as ArrayList
     */
    private ArrayList<ElectionDistrict> loadElectionDistricts(){

        ArrayList<ElectionDistrict> electionDistricts = new ArrayList<>();
        JSONObject electionDistrictsObj = obj.getJSONObject("wahlkreise");

        for(int i = 1; i < electionDistrictsObj.length()+1; i++){

            JSONObject currentElectionDistricts = electionDistrictsObj.getJSONObject(Integer.toString(i));

            String stateName = currentElectionDistricts.getString("Bundesland");
            State state = getState(stateName);
            int id = currentElectionDistricts.getInt("ID");
            String name = currentElectionDistricts.getString("Name");

            int eligibleVoters_13 = currentElectionDistricts.getInt("Wahlberechtigte_13");
            int eligibleVoters_17 = currentElectionDistricts.getInt("Wahlberechtigte_17");

            int voters_13 = currentElectionDistricts.getInt("Waehler_13");
            int voters_17 = currentElectionDistricts.getInt("Waehler_17");

            int valid_13_first = currentElectionDistricts.getInt("Gueltige_13_Erst");
            int valid_17_first = currentElectionDistricts.getInt("Gueltige_17_Erst");

            int invalid_13_first = currentElectionDistricts.getInt("Ungueltige_13_Erst");
            int invalid_17_first = currentElectionDistricts.getInt("Ungueltige_17_Erst");

            int valid_13_second = currentElectionDistricts.getInt("Gueltige_13_Zweit");
            int valid_17_second = currentElectionDistricts.getInt("Gueltige_17_Zweit");

            int invalid_13_second = currentElectionDistricts.getInt("Ungueltige_13_Zweit");
            int invalid_17_second = currentElectionDistricts.getInt("Ungueltige_17_Zweit");


            JSONArray partyResultsArray = currentElectionDistricts.getJSONArray("ParteiErgebnisse");
            ArrayList<PartyResults> partyResults = loadPartyResults(partyResultsArray);

            electionDistricts.add(new ElectionDistrict(
                    state,
                    id, name,
                    eligibleVoters_13, eligibleVoters_17,
                    voters_13, voters_17,
                    valid_13_first, valid_17_first,
                    invalid_13_first, invalid_17_first,
                    valid_13_second, valid_17_second,
                    invalid_13_second, invalid_17_second,
                    partyResults
            ));
        }
        return electionDistricts;
    }

    public ElectionDistrict getDistrict(int districtId){
        for(ElectionDistrict district: this.allElectionDistricts){
            if(district.id == districtId){
                return district;
            }
        }
        return null;
    }

    /**
     * Gets all candidates from obj
     * @return all candidates as ArrayList
     */
    private ArrayList<Candidate> loadCandidates(){

        ArrayList<Candidate> candidates = new ArrayList<>();
        JSONObject candidatesObj = obj.getJSONObject("kandidaten");

        for (int i = 1; i < candidatesObj.length()+1; i++) {

            JSONObject currentCandidate = candidatesObj.getJSONObject(Integer.toString(i));

            int id = currentCandidate.getInt("ID");
            String name = currentCandidate.getString("Name");
            String firstName = currentCandidate.getString("Vorname");
            String title = currentCandidate.getString("Titel");

            String gender = currentCandidate.getString("Geschlecht");
            String hometown = currentCandidate.getString("Wohnort");
            String profession = currentCandidate.getString("Beruf");

            int birthYear = currentCandidate.getInt("Geburtsjahr");
            String birthPlace = currentCandidate.getString("Geburtsort");

            // directCandidature == null if currentCandidate is list candidate
            DirectCandidature directCandidature;
            try{
                JSONObject directCandidatureObj = currentCandidate.getJSONObject("DirektKandidatur");
                int districtId = directCandidatureObj.getInt("Wahlkreis");
                int partyId = directCandidatureObj.getInt("Partei");
                directCandidature = new DirectCandidature(getDistrict(districtId), getParty(partyId));
            } catch(org.json.JSONException e) {
                directCandidature = null;
            }

            // listPlacement == null if currentCandidate is direct candidate
            ListPlacement listPlacement;
            try {
                JSONObject listPlacementObj = currentCandidate.getJSONObject("ListePlatz");
                int stateId = listPlacementObj.getInt("Land");
                int partyId = listPlacementObj.getInt("Partei");

                int place = listPlacementObj.getInt("Platz");

                Party party = getParty(partyId);
                State state = getState(stateId);
                StateList list = getList(state, party, 2017);

                if(list == null){
                    list = new StateList(2017, party,state);
                    this.allStateLists.add(list);
                }

                listPlacement = new ListPlacement(place, list);
            } catch(org.json.JSONException e){
                listPlacement = null;
            }

            candidates.add(new Candidate(
                    id, name, firstName, title,
                    gender, hometown, profession,
                    -1, birthYear, birthPlace,
                    directCandidature, listPlacement
                    ));
        }
        return candidates;
    }

    public StateList getList(State state, Party party, int year){
        for(StateList list: this.allStateLists){
            if(list.state.id == state.id && list.party.id == party.id && list.yearOfCandidature == year){
                return list;
            }
        }
        return null;
    }

    /**
     * Gets all party results for a state or election district
     * @param partyResultsArray party results of state or election district
     * @return party results as array list
     */
    public ArrayList<PartyResults> loadPartyResults(JSONArray partyResultsArray){
        ArrayList<PartyResults> partyResults = new ArrayList<>();

        for (int j = 0; j < partyResultsArray.length(); j++) {

            JSONObject current = partyResultsArray.getJSONObject(j);

            int first_13 = current.getInt("Erststimme_13");
            int first_17 = current.getInt("Erststimme_17");
            int second_13 = current.getInt("Zweitstimme_13");
            int second_17 = current.getInt("Zweitstimme_17");

            boolean electable_first_13 = current.getBoolean("Angetreten_Erststimme_13");
            boolean electable_first_17 = current.getBoolean("Angetreten_Erststimme_17");
            boolean electable_second_13 = current.getBoolean("Angetreten_Zweitstimme_13");
            boolean electable_second_17 = current.getBoolean("Angetreten_Zweitstimme_13");

            int partyId = current.getInt("Partei");
            Party party = getParty(partyId);

            partyResults.add(new PartyResults(first_13, first_17, second_13, second_17,
                    electable_first_13, electable_first_17, electable_second_13, electable_second_17, party));
        }
        return partyResults;
    }

    /**
     * Loads content of file
     * @param fileName path to file
     * @return string containing file-content
     * @throws IOException
     */
    private String loadFileToString(String fileName) throws IOException {
        File file = new File(this.getClass().getClassLoader().getResource(fileName).getFile());
        StringBuffer content = new StringBuffer();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String s;

            while ((s = reader.readLine()) != null) {
                content.append(s).append(System.getProperty("line.separator"));
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }
        return content.toString();
    }
}
