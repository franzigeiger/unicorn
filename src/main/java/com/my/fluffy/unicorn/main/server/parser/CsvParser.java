package com.my.fluffy.unicorn.main.server.parser;
import com.my.fluffy.unicorn.main.server.parser.data.*;

import java.io.*;
import java.util.ArrayList;

public class CsvParser {

    JsonParser jsonParser;

    public ArrayList<CandidateJson> candidates2013;

    public CsvParser(String jsonPath, String path13){
        this.jsonParser = new JsonParser();
        jsonParser.parseAll(jsonPath);

        this.candidates2013 = parse2013Candidates(path13);
    }

    /**
     * Get candidates from csv at path13
     * @param path13 path to csv-file
     * @return array list containing al candidates from csv
     */
    private ArrayList<CandidateJson> parse2013Candidates(String path13){

        ArrayList<CandidateJson> candidateJsons = new ArrayList<>();
        try{
            File file = new File(this.getClass().getClassLoader().getResource(path13).getFile());
            BufferedReader br = new BufferedReader( new FileReader(file) );
            String newLine;
            //do not parse first line that contains information about file format
            br.readLine();
            while ((newLine = br.readLine()) != null) {
                //split line into parts
                String[] cols = newLine.split(";", -1);

                //find state for candidate
                StateJson state = null;
                if(cols[8].length() > 0) {
                    state = jsonParser.getState(cols[8]);
                }

                //find partyJson for candidate
                PartyJson partyJson = jsonParser.getParty(cols[6]);
                if(partyJson == null){
                    //if there is no corresponding partyJson for candidate:
                    //candidate gets default partyJson
                    partyJson = jsonParser.getParty(43);
                }

                //find election district for candidate
                ElectionDistrictJson district = null;
                if(cols[7].length() > 0){
                    district = jsonParser.getDistrict(Integer.parseInt(cols[7]));
                }

                //if candidate has district: candidate has a direct candidature
                DirectCandidatureJson candidature = null;
                if(district != null){
                    candidature = new DirectCandidatureJson(district, partyJson);
                }

                //if candidate has list place: candidate has list candidature
                ListPlacementJson list = null;
                if(state != null && cols[9].length() > 0) {
                    StateListJson stateListJson = jsonParser.getList(state, partyJson, 2013);
                    if(stateListJson == null){
                        stateListJson = new StateListJson(2013, partyJson, state);
                        jsonParser.allStateListJsons.add(stateListJson);
                    }
                    list = new ListPlacementJson(Integer.parseInt(cols[9]), stateListJson);
                }

                //create new candidate
                CandidateJson newCandidateJson = new CandidateJson(Integer.parseInt(cols[1]), cols[3], cols[4], cols[2],
                        null, null, null,
                        2013, Integer.parseInt(cols[5]), null,
                        candidature, list);
                candidateJsons.add(newCandidateJson);

                }
            br.close();
        } catch(IOException e){
            System.out.println("Could not parse file: " + e);
        }

        return candidateJsons;
    }

}
