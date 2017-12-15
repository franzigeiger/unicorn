package com.my.fluffy.unicorn.main.client.views;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.my.fluffy.unicorn.main.client.data.*;
import com.my.fluffy.unicorn.main.client.mainService;
import gwtfullscreen.Fullscreen;
import gwtfullscreen.FullscreenChangeEvent;
import gwtfullscreen.FullscreenChangeEventBindery;


import com.google.gwt.query.client.Function;
import com.google.gwt.user.client.Event;
import gwtfullscreen.Fullscreen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.gwt.query.client.GQuery.$;


public class BallotView extends VerticalPanel  {

    private HandlerRegistration registration;

    District district ;

    Map<Integer, BallotLine> ballotData ;

    Map<DirectCandidature, CheckBox> directCandidates;
    Map<StateList, CheckBox> parties;

    public BallotView(District electionDistrict){
        this.district = electionDistrict;
        Fullscreen.requestFullscreen();
        initialize();
    }


    public void initialize(){
        this.setSpacing(150);
        HTML select =  new HTML("<h1>Add your election token:</h1>");
        select.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
        this.add(select);

        TextBox tokenBox = new TextBox();

        Button submit = new Button("Submit", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                mainService.App.getInstance().checkToken(tokenBox.getValue(), new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Window.alert("The token is not valid!");
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (ballotData == null) {
                            mainService.App.getInstance().getBallotLinesForDistrict(district.getId(), 2017, 2017, new AsyncCallback<Map<Integer, BallotLine>>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    Window.alert("Couln't get your ballot from sytsem!");
                                }

                                @Override
                                public void onSuccess(Map<Integer, BallotLine> integerBallotLineMap) {
                                    ballotData = integerBallotLineMap;
                                    createBallot();
                                }
                            });
                        }else {
                            createBallot();
                        }

                    }
                });
            }
        });
    }

    private void createBallot(){
        this.clear();
        directCandidates= new HashMap<DirectCandidature, CheckBox>();
        parties = new HashMap<StateList, CheckBox>();

        this.add(new HTML("<h1>Ballot</h1" ));
        HorizontalPanel ballotPanel = new HorizontalPanel();
        ballotPanel.setSpacing(30);
        this.add(ballotPanel);
        VerticalPanel directPanel = new VerticalPanel();
        directPanel.setSpacing(10);
        directPanel.add(new HTML("<h2>First Vote</h2>"));
        FlexTable direct = new FlexTable();
        direct.setHTML(0, 0, "<h3>Range</h3>");
        direct.setHTML(0, 1, "<h3>Candidate</h3>");
        direct.setHTML(0, 2, "<h3>Check</h3>");
        directPanel.add(direct);

        VerticalPanel partyPanel = new VerticalPanel();
        partyPanel.setSpacing(10);
        partyPanel.add(new HTML("<h2>Second Vote</h2>"));
        FlexTable party = new FlexTable();
        party.setHTML(0, 0, "<h3>Check</h3>");
        party.setHTML(0, 1, "<h3>Party</h3>");
        party.setHTML(0, 3, "<h3>Range</h3>");
        partyPanel.add(party);

        ballotPanel.add(directPanel);
        ballotPanel.add(partyPanel);

        int i=1;
        for(Map.Entry<Integer, BallotLine> line : ballotData.entrySet()){

            CheckBox dCheck = new CheckBox();
            dCheck.setValue(false);
            CheckBox pCheck = new CheckBox();
            pCheck.setValue(false);

            FlexTable directInfos = new FlexTable();
            Candidate cand =line.getValue().getDirectCandidate();
            if(cand!= null) {
                String name = cand.getTitle() + " " + cand.getFirstName() + " " + cand.getLastName();
                directInfos.setHTML(0, 0, "<h4>" + name + "</h4>");
                directInfos.setText(0, 1, cand.getProfession());
                directInfos.setHTML(1, 0, "<h4>" + line.getValue().getParty().getName() + "</h4>");

                directCandidates.put(line.getValue().getDirectCandidature(), dCheck);
            }

            FlexTable partyInfos = new FlexTable();
            List<ListCandidature> statelist= line.getValue().getListCandidates();
            if(statelist != null && statelist.size()>0 ) {
                partyInfos.setHTML(0, 0, "<h3>" + line.getValue().getParty().getName() + "</h3>");
                FlexTable stateList = new FlexTable();
                int j = 0;
                for (ListCandidature candid : statelist)
                    stateList.setText(0, j, candid.getCandidate().getFirstName() + " " + candid.getCandidate().getLastName());

                partyInfos.setWidget(0, 1, stateList);
                parties.put(statelist.get(0).getStateList(), pCheck);
            }
            direct.setHTML(0, i, "<h4>" + line.getKey() + "</h4>");
            direct.setWidget(1,i, directInfos);
            direct.setWidget(2, i, dCheck);

            party.setWidget(0, i, pCheck);
            party.setWidget(1, i, partyInfos);
            party.setHTML(2, i, "<h4>" + line.getKey() + "</h4>");

            i++;

        }

       ballotPanel.add(new Button("<h3>Submit Votes</h3>", new ClickHandler() {
           @Override
           public void onClick(ClickEvent clickEvent) {
               submitVote();
           }
       }));

    }

    public void submitVote(){
        DirectCandidature cand = null;
        int onlyOne=0;
       for(Map.Entry<DirectCandidature, CheckBox> candidature :directCandidates.entrySet()){
           if(candidature.getValue().getValue() == true){
               cand= candidature.getKey();
               onlyOne++;
           }
       }

       if(onlyOne>1 ){
           Window.alert("Please select only one candidate for your first vote!");
           return;
       }

       if(cand == null){
          boolean confirm= Window.confirm("Do you want to submit a ballot with empty first vote?");
            if(!confirm)return;
       }

        StateList list = null;
        onlyOne=0;
        for(Map.Entry<StateList, CheckBox> party :parties.entrySet()){
            if(party.getValue().getValue() == true){
                list= party.getKey();
                onlyOne++;
            }
        }

        if(onlyOne>1 ){
            Window.alert("Please select only one party for your second vote!");
            return;
        }

        if(list == null){
            boolean confirm= Window.confirm("Do you want to submit a ballot with empty second vote?");
            if(!confirm)return;
        }

        Ballot b= new Ballot(list, cand, district);

        mainService.App.getInstance().insertSingleBallot(b, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert("A problem at ballot persisting happened! Please try again!");
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                Window.alert("Ballot was put to pallot box!");
                renew();
            }
        });

    }

    public void renew(){
        this.clear();
        initialize();
    }
}
