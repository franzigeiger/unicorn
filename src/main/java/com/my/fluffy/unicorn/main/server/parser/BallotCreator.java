package com.my.fluffy.unicorn.main.server.parser;

import com.my.fluffy.unicorn.main.server.parser.data.*;

import java.util.ArrayList;

public class BallotCreator {

    public ArrayList<PartyJson> allParties;
    public ArrayList<ElectionDistrictJson> allElectionDistrictJsons;
    public ArrayList<CandidateJson> allCandidates2017;

    public BallotCreator(ArrayList<PartyJson> allParties,
                         ArrayList<ElectionDistrictJson> allElectionDistrictJsons,
                         ArrayList<CandidateJson> allCandidates2017) {
        this.allParties = allParties;
        this.allElectionDistrictJsons = allElectionDistrictJsons;
        this.allCandidates2017 = allCandidates2017;
    }

    /**
     * Get ballots for every election district for 2017
     * @return created ballots as ArrayList
     */
/*    public ArrayList<BallotJson> createBallotsForAllDistricts(){
        ArrayList<BallotJson> allBallotJsons = new ArrayList<>();
        for(int i = 0; i < allElectionDistrictJsons.size(); i++){
            allBallotJsons.addAll(createBallots(allElectionDistrictJsons.get(i)));
        }
        return allBallotJsons;
    }*/

    /**
     * Get Id of direct candidate from a certain partyJson and district
     * @param electionDistrictId id of election district
     * @param partyJson PartyJson of direct candidate
     * @return id of candidate as integer
     */
    public CandidateJson getCandidate(int electionDistrictId, PartyJson partyJson){

        for(int i = 0; i < allCandidates2017.size(); i++){
            //only look at direct candidates
            if(allCandidates2017.get(i).directCandidatureJson != null){
                //find candidate with right district and partyJson
                if(allCandidates2017.get(i).directCandidatureJson.partyJson.id == partyJson.id &&
                        allCandidates2017.get(i).directCandidatureJson.electionDistrictJson.id == electionDistrictId){
                    //get candidate's id
                    return allCandidates2017.get(i);
                }
            }
        }
        //no matching candidate
        return null;
    }

    /**
     * Creates ballots for one election district
     * Valid votes are represented through id of partyJson or candidate
     * Invalid votes are represented as -1
     * @param electionDistrictJson
     * @return created ballots as ArrayList
     * @throws Error if amount of created ballots do not match number of valid votes
     */
    public ArrayList<BallotJson> createBallots(ElectionDistrictJson electionDistrictJson) throws Error{
        //all votes
        int voters = electionDistrictJson.voters_17;
        int districtId = electionDistrictJson.id;

        //valid votes for candidates added to ballotJsons
        int currentFirst = 0;
        //valid votes for parties added to ballotJsons
        int currentSecond = 0;

        //ballotJsons (only for people who voted)
        ArrayList<BallotJson> ballotJsons = new ArrayList<>();

        for(int i = 0; i < voters; i++){
            //-1 means invalid vote for a partyJson or candidate
            ballotJsons.add(new BallotJson(null, null, null, 2017));
        }

        //for all parties of this election district
        for(int i = 0; i < electionDistrictJson.partyResultJsons.size(); i++){

            PartyResultsJson currentResult = electionDistrictJson.partyResultJsons.get(i);

            //get id for candidateJson
            CandidateJson candidateJson = getCandidate(electionDistrictJson.id, currentResult.partyJson);
            //for all valid votes this candidateJson received
            for(int j = currentFirst; j < (currentFirst + currentResult.first_17); j++){
                //add one vote for this candidateJson to the ballotJsons of the election district
                ballotJsons.set(j, new BallotJson(candidateJson, ballotJsons.get(j).secondVote, electionDistrictJson, 2017));
            }
            currentFirst += currentResult.first_17;

            //for all valid votes this partyJson received
            for(int k = currentSecond; k < (currentSecond + currentResult.second_17); k++){
                //add one vote for this partyJson to the ballotJsons of the election district
                ballotJsons.set(k, new BallotJson(ballotJsons.get(k).firstVote, currentResult.partyJson, electionDistrictJson, 2017));
            }
            currentSecond += currentResult.second_17;
        }

        if(currentFirst != electionDistrictJson.valid_17_first){
            throw new Error("First Votes do not match: " + currentFirst + ", " + electionDistrictJson.valid_17_first);
        }
        if(currentSecond != electionDistrictJson.valid_17_second){
            throw new Error("Second Votes do not match: " + currentSecond + ", " + electionDistrictJson.valid_17_second);
        }

        return ballotJsons;
    }
}
