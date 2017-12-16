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
import org.intellij.lang.annotations.JdkConstants;

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
        this.add(tokenBox);
        Button submit = new Button("<h3>Submit Votes</h3>", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                mainService.App.getInstance().checkToken(tokenBox.getValue(), new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        DialogBox box = new ConfirmDialog("Attention", "The token is not valid!" , "OK");
                        box.center();
                        box.show();

                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if(!aBoolean.booleanValue()){
                            DialogBox box = new ConfirmDialog("Attention", "The token is not valid!" , "OK");
                            box.center();
                            box.show();
                            return;
                        }
                        if (ballotData == null) {
                            mainService.App.getInstance().getBallotLinesForDistrict(district.getId(), 2017, 2017, new AsyncCallback<Map<Integer, BallotLine>>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    DialogBox box = new ConfirmDialog("Attention", "Couln't get your ballot from sytsem!" , "OK");
                                    box.center();
                                    box.show();
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

        this.add(submit);
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

        directPanel.add(new HTML("<h2>First Vote</h2>"));

        FlexTable direct = new FlexTable();

        direct.setHTML(0, 0, "<h3>Range</h3>");
        direct.setHTML(0, 1, "<h3>Candidate</h3>");
        direct.setHTML(0, 2, "<h3>Check</h3>");
        direct.addStyleName("FlexTable");
        direct.setCellSpacing(0);
        direct.setCellPadding(0);
        directPanel.add(direct);
        directPanel.setSpacing(0);

        VerticalPanel partyPanel = new VerticalPanel();
        partyPanel.setSpacing(0);

        partyPanel.add(new HTML("<h2>Second Vote</h2>"));
        FlexTable party = new FlexTable();
        party.setHTML(0, 0, "<h3>Check</h3>");
        party.setHTML(0, 1, "<h3>Party</h3>");
        party.setHTML(0, 2, "<h3>Range</h3>");
        party.addStyleName("FlexTable");
        party.setCellSpacing(0);
        party.setCellPadding(0);

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
            directInfos.setStyleName("FlexTable-empty");
            directInfos.setCellSpacing(0);
            directInfos.setCellPadding(0);
            DirectCandidature dCandidate= line.getValue().getDirectCandidate();
            directInfos.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
            directInfos.getFlexCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_RIGHT);

            if(dCandidate!= null) {
                Candidate cand =dCandidate.getCandidate();
                String name = cand.getTitle() + " " + cand.getFirstName() + " " + cand.getLastName();
                directInfos.setHTML(0, 0, "<h4>" + name + "</h4>");
                directInfos.setText(0, 1, cand.getProfession());
                directInfos.setHTML(1, 1, "<h4>" + line.getValue().getParty().getName() + "</h4>");

                directCandidates.put(line.getValue().getDirectCandidate(), dCheck);
            }

            FlexTable partyInfos = new FlexTable();
            partyInfos.setStyleName("FlexTable-empty");
            partyInfos.setCellSpacing(0);
            partyInfos.setCellPadding(0);
            partyInfos.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
            List<ListCandidature> statelist= line.getValue().getListCandidates();

            if(statelist != null && statelist.size()>0 ) {
                partyInfos.setHTML(0, 0, "<h3>" + line.getValue().getParty().getName() + "</h3>");
                FlexTable stateList = new FlexTable();
                stateList.setCellSpacing(0);

                int j = 0;
                for (ListCandidature candid : statelist){
                    stateList.setText(j, 0, candid.getCandidate().getFirstName() + " " + candid.getCandidate().getLastName());
                    j++;
                }


                partyInfos.setWidget(0, 1, stateList);
                parties.put(statelist.get(0).getStateList(), pCheck);
            }
            direct.setHTML(i, 0, "<h4>" + line.getKey() + "</h4>");
            direct.setWidget(i,1, directInfos);
            direct.setWidget(i, 2, dCheck);

            party.setWidget(i, 0, pCheck);
            party.setWidget(i, 1, partyInfos);
            party.setHTML(i, 2, "<h4>" + line.getKey() + "</h4>");

            i++;

        }
        for(int j = 0; j< 3;j++){
            for(int k =0 ; k < ballotData.size(); k++){
                party.getFlexCellFormatter().setHeight(k, j,"100px");
                direct.getFlexCellFormatter().setHeight(k, j,"100px");
            }
        }


       this.add(new Button("<h3>Submit Votes</h3>", new ClickHandler() {
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
           DialogBox box = new ConfirmDialog("Attention", "Please select only one candidate for your first vote!" , "OK");
           box.center();
           box.show();
           return;
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
           DialogBox box = new ConfirmDialog("Attention", "Please select only one second vote!" , "OK");
            box.center();
            box.show();
            return;
        }

        if(cand == null && list == null){
            DialogBox box = new ConfirmDialog("Attention", "Please select at least one vote!" , "OK");
            box.center();
            box.show();
            return;
        }

        StateList finalList = list;
        DirectCandidature finalCand = cand;
        if(list == null || cand == null){
            DialogBox box = new YesNoDialog("Attention" , "Do you want to submit a ballot with one empty vote?") {
                @Override
                public void sayYes() {
                    Ballot b= new Ballot(finalList, finalCand, district);

                    mainService.App.getInstance().insertSingleBallot(b, new AsyncCallback<Boolean>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                           hide();
                            DialogBox box = new ConfirmDialog("Attention", "A problem at ballot persisting happened! Please try again!" , "OK");
                            box.center();
                            box.show();
                        }

                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            hide();
                            DialogBox box = new ConfirmDialog("OK", "Ballot was put to pallot box!" , "OK");
                            box.center();
                            box.show();
                            renew();
                        }
                    });
                }

                @Override
                public void sayNo() {
                    hide();
                }
            };

            box.center();
            box.show();

        } else {
            DialogBox box = new YesNoDialog("Attention" , "Do you want to sumbit your ballot right now?") {
                @Override
                public void sayYes() {
                    Ballot b= new Ballot(finalList, finalCand, district);

                    mainService.App.getInstance().insertSingleBallot(b, new AsyncCallback<Boolean>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            hide();
                            DialogBox box = new ConfirmDialog("Attention", "A problem at ballot persisting happened! Please try again!" , "OK");
                            box.center();
                            box.show();
                        }

                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            hide();
                            DialogBox box = new ConfirmDialog("OK", "Ballot was put to pallot box!" , "OK");
                            box.center();
                            box.show();
                            renew();
                        }
                    });
                }

                @Override
                public void sayNo() {
                    hide();
                }
            };

            box.center();
            box.show();
        }



    }

    public void renew(){
        this.clear();
        initialize();
    }

    private class ConfirmDialog extends DialogBox {

        public ConfirmDialog(String header, String text, String button) {
            // Set the dialog box's caption.
            setText(header);

            // Enable animation.
            setAnimationEnabled(true);

            // Enable glass background.
            setGlassEnabled(true);

            // DialogBox is a SimplePanel, so you have to set its widget
            // property to whatever you want its contents to be.
            Button ok = new Button(button);
            ok.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    hide();
                }
            });

            Label label = new Label(text);

            VerticalPanel panel = new VerticalPanel();
            panel.setHeight("100");
            panel.setWidth("300");
            panel.setSpacing(10);
            panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            panel.add(label);
            panel.add(ok);

            setWidget(panel);
        }
    }

    public void saveBallot(){

    }

    private abstract class YesNoDialog extends DialogBox {

        public YesNoDialog(String header, String text) {
            // Set the dialog box's caption.
            setText(header);

            // Enable animation.
            setAnimationEnabled(true);

            // Enable glass background.
            setGlassEnabled(true);

            // DialogBox is a SimplePanel, so you have to set its widget
            // property to whatever you want its contents to be.
            Button yes = new Button("Yes");
            yes.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    sayYes();
                    hide();
                }
            });

            Button no = new Button("No");
            no.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    sayNo();
                    hide();
                }
            });

            Label label = new Label(text);

            VerticalPanel panel = new VerticalPanel();
            panel.setHeight("100");
            panel.setWidth("300");
            panel.setSpacing(10);
            panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            panel.add(label);
            HorizontalPanel buttonPanel = new HorizontalPanel();
            buttonPanel.setSpacing(30);
            buttonPanel.add(yes);
            buttonPanel.add(no);
            panel.add(buttonPanel);

            setWidget(panel);
        }

        public abstract void sayYes();

        public abstract void sayNo();
    }
}
