package com.my.fluffy.unicorn.main.server.db;

import com.my.fluffy.unicorn.main.server.data.Candidate;
import com.my.fluffy.unicorn.main.server.data.DirectCandidature;
import com.my.fluffy.unicorn.main.server.data.Election;
import com.my.fluffy.unicorn.main.server.parser.BatchInserter;
import com.my.fluffy.unicorn.main.server.parser.data.CandidateJson;
import org.junit.Test;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;


public class BatchInserterTest {
    @Test
    public void testBatchInserter() throws SQLException, ClassNotFoundException {
        BatchInserter batchInserter = new BatchInserter(
                DatabaseConnection.create(),
                "complete.json",
                "candidates2013.csv");

        ArrayList<Candidate> c = batchInserter.candidates;
        for(Candidate d: c){
            if(d.getLastName().equals("Nicolaisen") && d.getFirstName().equals("Petra")){
                System.out.println(d.getLastName());
                System.out.println(d.getFirstName());
                System.out.println(d.getYearOfBirth());
            }
        }

        ArrayList<DirectCandidature> d = batchInserter.directCandidatures;
        for(DirectCandidature e: d){
            if(e.getCandidate().getLastName().equals("Nicolaisen") && e.getCandidate().equals("Petra")){
                System.out.println("!!!");
                System.out.println(e.getCandidate().getLastName());
                System.out.println(e.getCandidate().getFirstName());
                System.out.println(e.getCandidate().getYearOfBirth());
            }
        }

        batchInserter.insertAll();
    }
}
