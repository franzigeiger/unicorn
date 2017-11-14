package com.my.fluffy.unicorn.main.server.parser;

import com.my.fluffy.unicorn.main.server.parser.data.*;

import java.util.ArrayList;

public class BallotCreator {

    public ArrayList<Party> allParties;
    public ArrayList<ElectionDistrict> allElectionDistricts;
    public ArrayList<Candidate> allCandidates2017;

    public BallotCreator(String path) {
        JsonParser parser = new JsonParser();
        parser.parseAll(path);
        this.allParties = parser.allParties;
        this.allElectionDistricts = parser.allElectionDistricts;
        this.allCandidates2017 = parser.allCandidates2017;
    }

    /**
     *
     * @return
     */
    public ArrayList<Ballot> createBallotsForAllDistricts(){
        ArrayList<Ballot> allBallots = new ArrayList<>();
        for(int i = 0; i < allElectionDistricts.size(); i++){
            allBallots.addAll(createBallots(allElectionDistricts.get(i)));
        }
        return allBallots;
    }

    /**
     * Get Id of direct candidate from a certain party and district
     * @param electionDistrictId id of election district
     * @param party Party of direct candidate
     * @return id of candidate as integer
     */
    public Candidate getCandidate(int electionDistrictId, Party party){

        for(int i = 0; i < allCandidates2017.size(); i++){
            //only look at direct candidates
            if(allCandidates2017.get(i).directCandidature != null){
                //find candidate with right district and party
                if(allCandidates2017.get(i).directCandidature.party.id == party.id &&
                        allCandidates2017.get(i).directCandidature.electionDistrict.id == electionDistrictId){
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
     * Valid votes are represented through id of party or candidate
     * Invalid votes are represented as -1
     * @param electionDistrict
     * @return created ballots as ArrayList
     * @throws Error if amount of created ballots do not match number of valid votes
     */
    public ArrayList<Ballot> createBallots(ElectionDistrict electionDistrict) throws Error{
        //all votes
        int voters = electionDistrict.voters_17;
        int districtId = electionDistrict.id;

        //valid votes for candidates added to ballots
        int currentFirst = 0;
        //valid votes for parties added to ballots
        int currentSecond = 0;

        //ballots (only for people who voted)
        ArrayList<Ballot> ballots = new ArrayList<>();

        for(int i = 0; i < voters; i++){
            //-1 means invalid vote for a party or candidate
            ballots.add(new Ballot(null, null, null, 2017));
        }

        //for all parties of this election district
        for(int i = 0; i < electionDistrict.partyResults.size(); i++){

            PartyResults currentResult = electionDistrict.partyResults.get(i);

            //get id for candidate
            Candidate candidate = getCandidate(electionDistrict.id, currentResult.party);
            //for all valid votes this candidate received
            for(int j = currentFirst; j < (currentFirst + currentResult.first_17); j++){
                //add one vote for this candidate to the ballots of the election district
                ballots.set(j, new Ballot(candidate, ballots.get(j).secondVote, electionDistrict, 2017));
            }
            currentFirst += currentResult.first_17;

            //for all valid votes this party received
            for(int k = currentSecond; k < (currentSecond + currentResult.second_17); k++){
                //add one vote for this party to the ballots of the election district
                ballots.set(k, new Ballot(ballots.get(k).firstVote, currentResult.party, electionDistrict, 2017));
            }
            currentSecond += currentResult.second_17;
        }

        if(currentFirst != electionDistrict.valid_17_first){
            throw new Error("First Votes do not match: " + currentFirst + ", " + electionDistrict.valid_17_first);
        }
        if(currentSecond != electionDistrict.valid_17_second){
            throw new Error("Second Votes do not match: " + currentSecond + ", " + electionDistrict.valid_17_second);
        }

        return ballots;
    }
}
