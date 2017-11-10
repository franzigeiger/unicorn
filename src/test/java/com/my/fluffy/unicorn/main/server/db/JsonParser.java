package com.my.fluffy.unicorn.main.server.db;

import org.json.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonParser {

    public JSONObject obj;
    public ArrayList<Party> allParties;
    public ArrayList<State> allStates;
    public ArrayList<ElectionDistrict> allElectionDistricts;
    public ArrayList<Candidate> allCandidates;

    public JsonParser(){
    }

    public void parseAll(){
        loadJsonFile("C:\\Users\\Ich\\Downloads\\complete.json");
        this.allParties = loadParties();
        System.out.println(allParties.size());
        this.allStates = loadStates();
        System.out.println(allStates.size());
        this.allElectionDistricts = loadElectionDistricts();
        System.out.println(allElectionDistricts.size());
        this.allCandidates = loadCandidates();
        System.out.println(allCandidates.size());
    }

    public void loadJsonFile(String path){
        //path = "C:\\Users\\Ich\\Downloads\\complete.json"
        String jsonText = "";
        try{
            jsonText = loadFileToString(path);
        } catch(Exception e){
            System.out.println("Could not load file...");
        }
        this.obj = new JSONObject(jsonText);
    }

    public ArrayList<Party> loadParties(){
        ArrayList<Party> parties = new ArrayList<>();
        JSONObject partiesObj = obj.getJSONObject("parteien");

        for (int i = 1; i < partiesObj.length(); i++) {
            JSONObject currentParty = partiesObj.getJSONObject(Integer.toString(i));
            int id = currentParty.getInt("ID");
            String name = currentParty.getString("Name");
            parties.add(new Party(id, name));
        }
        return parties;
    }

    public ArrayList<State> loadStates(){
        ArrayList<State> states = new ArrayList<>();
        JSONObject statesObj = obj.getJSONObject("bundeslaender");

        for (int i = 1; i < statesObj.length(); i++) {
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
            HashMap<Integer, PartyResults> partyResults = loadPartyResults(partyResultsArray);

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

    public ArrayList<ElectionDistrict> loadElectionDistricts(){
        ArrayList<ElectionDistrict> electionDistricts = new ArrayList<>();
        JSONObject electionDistrictsObj = obj.getJSONObject("wahlkreise");

        for(int i = 1; i < electionDistrictsObj.length(); i++){
            JSONObject currentElectionDistricts = electionDistrictsObj.getJSONObject(Integer.toString(i));
            String state = currentElectionDistricts.getString("Bundesland");

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
            HashMap<Integer, PartyResults> partyResults = loadPartyResults(partyResultsArray);

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

    public ArrayList<Candidate> loadCandidates(){
        ArrayList<Candidate> candidates = new ArrayList<>();
        JSONObject candidatesObj = obj.getJSONObject("kandidaten");

        for (int i = 1; i < candidatesObj.length(); i++) {
            JSONObject currentCandidate = candidatesObj.getJSONObject(Integer.toString(i));
            int id = currentCandidate.getInt("ID");
            String name = currentCandidate.getString("Name");
            String firstName = currentCandidate.getString("Vorname");
            String title = currentCandidate.getString("Titel");
            String gender = currentCandidate.getString("Geschlecht");
            String hometown = currentCandidate.getString("Wohnort");
            String profession = currentCandidate.getString("Beruf");
            boolean repeatedCandidature = currentCandidate.getBoolean("Wiederkandidatur");
            int birthYear = currentCandidate.getInt("Geburtsjahr");
            String birthPlace = currentCandidate.getString("Geburtsort");

            DirectCandidature directCandidature;
            try{
                JSONObject directCandidatureObj = currentCandidate.getJSONObject("DirektKandidatur");
                int electionDistrict = directCandidatureObj.getInt("Wahlkreis");
                int party = directCandidatureObj.getInt("Partei");
                directCandidature = new DirectCandidature(electionDistrict, party);
            } catch(org.json.JSONException e) {
                directCandidature = null;
            }

            ListPlacement listPlacement;
            try {
                JSONObject listPlacementObj = currentCandidate.getJSONObject("ListePlatz");
                int state = listPlacementObj.getInt("Land");
                int place = listPlacementObj.getInt("Platz");
                int party = listPlacementObj.getInt("Partei");
                listPlacement = new ListPlacement(state, place, party);
            } catch(org.json.JSONException e){
                listPlacement = null;
            }

            candidates.add(new Candidate(
                    id, name, firstName, title,
                    gender, hometown, profession,
                    repeatedCandidature, birthYear, birthPlace,
                    directCandidature, listPlacement
                    ));
        }
        return candidates;
    }

    public HashMap<Integer, PartyResults> loadPartyResults(JSONArray partyResultsArray){
        HashMap<Integer, PartyResults> partyResults = new HashMap<Integer, PartyResults>();
        for (int j = 1; j < partyResultsArray.length(); j++) {
            JSONObject current = partyResultsArray.getJSONObject(j);
            int first_13 = current.getInt("Erststimme_13");
            int first_17 = current.getInt("Erststimme_17");
            int second_13 = current.getInt("Zweitstimme_13");
            int second_17 = current.getInt("Zweitstimme_17");

            boolean electable_first_13 = current.getBoolean("Angetreten_Erststimme_13");
            boolean electable_first_17 = current.getBoolean("Angetreten_Erststimme_17");
            boolean electable_second_13 = current.getBoolean("Angetreten_Zweitstimme_13");
            boolean electable_second_17 = current.getBoolean("Angetreten_Zweitstimme_13");
            int party = current.getInt("Partei");
            partyResults.put(party, new PartyResults(first_13, first_17, second_13, second_17,
                    electable_first_13, electable_first_17, electable_second_13, electable_second_17));
        }
        return partyResults;
    }

    private String loadFileToString(String fileName) throws IOException {
        File file = new File(fileName);
        StringBuffer content = new StringBuffer();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String s = null;

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
