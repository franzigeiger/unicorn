package com.my.fluffy.unicorn.main.client.views;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.my.fluffy.unicorn.main.client.data.Candidate;
import com.my.fluffy.unicorn.main.client.data.Party;
import com.my.fluffy.unicorn.main.client.data.Top10Data;
import com.my.fluffy.unicorn.main.client.mainService;

import java.util.List;

public class Top10View extends VerticalPanel {


   List<Party> parties;
   SelectableElectionView parent;

    public Top10View(SelectableElectionView parent) {
        this.parent = parent;
        this.setSpacing(20);
        if(parent.getElectionYear() == 2013) {
            this.add(new Label("Unfortunately, there is no corresponding data for 2013."));
        } else {
            mainService.App.getInstance().getParties(new AsyncCallback<List<Party>>() {
                @Override
                public void onFailure(Throwable throwable) {

                }

                public void onSuccess(List<Party> p) {
                    parties = p;
                    System.out.println("Parties: " + p.size());
                    createPerPartyView();
                }
            });
        }

    }

    private void createPerPartyView() {
        final FlexTable table = new FlexTable();
        table.addStyleName("FlexTable");
        table.setCellPadding(0);
        table.setCellSpacing(0);

        this.add(new HTML("<h3>Choose Party: </h3>"));

        ListBox dropdownParties = new ListBox();
        dropdownParties.addItem("None");

        for(Party p: parties){
            dropdownParties.addItem(p.getName());
        }

        this.add(dropdownParties);
        this.add(table);

        dropdownParties.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                showTop10(table, dropdownParties.getSelectedValue());
            }
        });

        showTop10(table, "None");
    }

    public void showTop10(FlexTable table, String party){
        table.removeAllRows();
        table.insertRow(0);
        table.setHTML(0,0,"<h3>Name</h3>");
        table.setHTML(0,1,"<h3>Winner or Looser</h3>");
        table.setHTML(0,2,"<h3>Differences</h3>");
        for(Party p: parties){

            if(p.getName().equalsIgnoreCase(party) ) {

                mainService.App.getInstance().getTopTen(p.getId(), parent.getElectionYear(), new AsyncCallback<List<Top10Data>>() {
                    @Override
                    public void onFailure(Throwable throwable) {

                    }

                    public void onSuccess(List<Top10Data> topTen) {
                        if(topTen.size() == 0){
                            table.setText(1, 0, "No Candidates found!");
                        } else {
                            int i = 1;
                            for (Top10Data t: topTen) {
                                table.setText(i, 0,
                                        t.getCandidate().getLastName() + ", " + t.getCandidate().getFirstName());
                                if(t.getIsWinner()){
                                    table.setText(i, 1, "Winner");
                                } else {
                                    table.setText(i, 1, "Looser");
                                }
                                table.setText(i, 2, Integer.toString(t.getDifferenceInAbsoluteVotes()));
                                i++;
                            }
                        }

                    }
                });
            }
        }

    }
}
