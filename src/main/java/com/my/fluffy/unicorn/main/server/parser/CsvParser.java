package com.my.fluffy.unicorn.main.server.parser;

import com.my.fluffy.unicorn.main.server.parser.JsonParser;
import com.my.fluffy.unicorn.main.server.parser.data.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class CsvParser {

    JsonParser jsonParser;

    public ArrayList<Candidate> candidates2013;

    public CsvParser(String jsonPath, String path13){
        this.jsonParser = new JsonParser();
        jsonParser.parseAll(jsonPath);

        this.candidates2013 = parse2013Candidates(path13);
    }

    private ArrayList<Candidate> parse2013Candidates(String path13){
        ArrayList<Candidate> candidates = new ArrayList<>();
        ArrayList<String> parties = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader( new FileReader(path13) );
            ArrayList<String> lines = new ArrayList<String>();
            String newLine;
            //do not parse first line that contains information about file format
            br.readLine();
            while ((newLine = br.readLine()) != null) {
                String[] cols = newLine.split(";", -1);

                State state = null;
                ElectionDistrict district = null;
                Party party = jsonParser.getParty(cols[6]);
                if(cols[8].length() > 0) {
                    state = jsonParser.getState(cols[8]);
                }
                if(party == null){
                    party = jsonParser.getParty(43);
                }

                if(cols[7].length() > 0){
                    district = jsonParser.getDistrict(Integer.parseInt(cols[7]));
                }

                DirectCandidature candidature = null;
                if(district != null){
                    candidature = new DirectCandidature(district, party);
                }

                ListPlacement list = null;
                if(state != null && cols[9].length() > 0) {
                    StateList stateList = jsonParser.getList(state, party, 2013);
                    if(stateList == null){
                        stateList = new StateList(2013, party, state);
                        jsonParser.allStateLists.add(stateList);
                    }
                    list = new ListPlacement(Integer.parseInt(cols[9]), stateList);
                }

                Candidate newCandidate = new Candidate(Integer.parseInt(cols[1]), cols[3], cols[4], cols[2],
                        null, null, null,
                        2013, Integer.parseInt(cols[5]), null,
                        candidature, list);
                candidates.add(newCandidate);

                }
            br.close();
        } catch(IOException e){
            System.out.println("Could not parse file: " + e);
        }

        for(String p: parties){
            System.out.println(p);
        }

        return candidates;
    }

}
