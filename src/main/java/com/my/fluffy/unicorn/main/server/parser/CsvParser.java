package com.my.fluffy.unicorn.main.server.parser;
import com.my.fluffy.unicorn.main.server.parser.data.*;

import java.io.*;
import java.util.ArrayList;

public class CsvParser {

    private JsonParser jsonParser;

    private ArrayList<CandidateJson> candidates2013;

    public CsvParser(String jsonPath, String path13, String result13){
        this.jsonParser = new JsonParser();
        jsonParser.parseAll(jsonPath);
        //addMissingParties();
        //addMissingPartyResults(result13);

        this.candidates2013 = parse2013Candidates(path13);
    }

    /**
     * Reads party results for a party that only ran for office in 2013, but not in 2017, from csv
     * and adds them as PartyResultJson to corresponding district from ArrayList districts
     * @param results13 path to csv file containing results of election of 2013 for all districts
     * @param party party that only ran for office in 2013, but not in 2017
     * @param districts ArrayList containing all districts
     */
    private void parseMissingPartyResults(String results13,
                                         PartyJson party,
                                         ArrayList<ElectionDistrictJson> districts){
        try{
            File file = new File(this.getClass().getClassLoader().getResource(results13).getFile());
            BufferedReader br = new BufferedReader( new FileReader(file) );
            String newLine;
            int rowOfMissingResult = getRowForParty(party);

            for(int i = 0; i < 8; i++){
                br.readLine();
            }

            while((newLine = br.readLine()) != null){
                String[] cols = newLine.split(";", -1);

                //line is only relevant if cols[0] contains id of district
                if(cols[0].length() > 0){

                    //cols[0] is id of district if it is between 1 and 299
                    int districtId = Integer.parseInt(cols[0]);
                    if(districtId >= 1 && districtId <= 299){

                        String firstVotes = cols[rowOfMissingResult];
                        String secondVotes = cols[rowOfMissingResult + 2];

                        //party has received any vote in district
                        if(firstVotes.length() > 0 || secondVotes.length() > 0){
                            //amount of votes received
                            int first = 0; boolean electableFirst = false;
                            if(firstVotes.length() > 0){
                                first = Integer.parseInt(firstVotes);
                                electableFirst = true;
                            }
                            int second = 0; boolean electableSecond = false;
                            if(secondVotes.length() >0){
                                second = Integer.parseInt(secondVotes);
                                electableSecond = true;
                            }

                            //party result for party in district with districtId
                            PartyResultsJson newResult = new PartyResultsJson(
                                    first, 0,
                                    second, 0,
                                    electableFirst, false,
                                    electableSecond, false,
                                    party);
                            //add party result to party results of district with districtId
                            int districtPosition = findDistrictPosition(districts, districtId);
                            districts.get(districtPosition).partyResultJsons.add(newResult);

                            /*System.out.println("Added party result of party " + party.name
                                    + " to district " + districtId + ": " + first + ", " + second)*/
                        }
                    }
                }
            }

        } catch(Exception e){
            System.out.println("Could not read file!");
        }

    }

    /**
     * Get first row containing amount of votes for party
     * @param party party that ran for office in 2013, but not in 2017
     * @return number of row as int
     */
    private int getRowForParty(PartyJson party){
        switch (party.name){
            case "BIG":
                return 107;
            case "RENTNER":
                return 71;
            case "REP":
                return 55;
            case "Bündnis21/RRP":
                return 67;
            case "pro Deutschland":
                return 111;
            case "PSG":
                return 99;
            case "NEIN!":
                return 151;
            case "PBC":
                return 79;
            case "Nichtwähler":
                return 127;
            case "BGD":
                return 143;
            default:
                System.out.println(party.name);
                return -1;
        }
    }

    /**
     * Finds position of district corresponding to id
     * @param districts ArrayList containing districts
     * @param id id of district
     * @return position of district corresponding to district id as int
     */
    private int findDistrictPosition(ArrayList<ElectionDistrictJson> districts, int id){
        for(int i = 0; i < districts.size(); i++){
            if(districts.get(i).id == id){
                return i;
            }
        }
        return -1;
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
                    System.out.println("Could not find party " + cols[6]);
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
