package com.my.fluffy.unicorn.main.server;


public class serviceQ{

    mainServiceImpl mainServiceImpl = new mainServiceImpl();

    public void q1(int year) {
        mainServiceImpl.getParlamentSeats(year);
    }

    public void q2(int year) {
        mainServiceImpl.getParlamentMembers(year);
    }

    public void q3(int districtIdOld, int districtIdNew) {
        //when loading the page
        /*mainServiceImpl.getAllDistricts(2017);
        mainServiceImpl.getAllDistricts(2013);*/

        //after choosing a certain district
        // (only districts from 2017 can be chosen;
        // corresponding district from 2013 is then found in DistrictView)
        mainServiceImpl.getDistrictResults(districtIdOld, districtIdNew);
        // only for 2017, we don't have candidates for 2013
        mainServiceImpl.getDistrictWinner(districtIdNew);
    }

    public void q4(int districtId) {
        // in DistrictView, winning parties are fetched for both 2013 and 2017 and displayed at once
        // here, we only get winning parties for one year, since this already satisfies Q2
        mainServiceImpl.getWinningParties(districtId);
    }

    public void q5(int year) {
        mainServiceImpl.getAdditionalMandatsPerParty(year);

    }

    public void q6(int partyId, int year) {
        mainServiceImpl.getTopTen(partyId, year);
    }

}