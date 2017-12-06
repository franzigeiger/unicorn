package com.my.fluffy.unicorn.main.server.rest;


import com.my.fluffy.unicorn.main.server.mainServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/benchmark")
public class serviceQ{

    com.my.fluffy.unicorn.main.server.mainServiceImpl mainServiceImpl = new mainServiceImpl();
    //URL:http://localhost:8081/rest/benchmark/q1/2017 oder http://localhost:8081/rest/benchmark/q1/2013
    @GET
    @Path("q1/{year}")
    public void q1(@PathParam("year")int year) {

        System.out.println("Statement 1 execution");
        mainServiceImpl.getParlamentSeats(year);
    }

    //http://localhost:8081/rest/benchmark/q2/2017 oder http://localhost:8081/rest/benchmark/q2/2013
    @GET
    @Path("q2/{year}")
    public void q2(@PathParam("year")int year) {
        System.out.println("Statement 2 execution");
        mainServiceImpl.getParlamentMembers(year);
    }

    //http://localhost:8081/rest/benchmark/q3/{id]/{id}
    @GET
    @Path("q3/{districtIdOld}/{districtIdNew}")
    public void q3(@PathParam("districtIdOld")int districtIdOld, @PathParam("districtIdNew") int districtIdNew) {
        System.out.println("Statement 3 execution");
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

    //usw...
    @GET
    @Path("q4/{year}")
    public void q4(@PathParam("year")int year) {
        System.out.println("Statement 4 execution");
        // in DistrictView, winning parties are fetched for both 2013 and 2017 and displayed at once
        // here, we only get winning parties for one year, since this already satisfies Q2
        mainServiceImpl.getWinningParties(year);
    }

    @GET
    @Path("q5/{year}")
    public void q5(@PathParam("year")int year) {
        System.out.println("Statement 5 execution");
        mainServiceImpl.getAdditionalMandatsPerParty(year);

    }
    @GET
    @Path("q6/{year}")
    public void q6(@PathParam("year")int partyId, @PathParam("year")int year) {
        System.out.println("Statement 6 execution");
        mainServiceImpl.getTopTen(partyId, year);
    }

}