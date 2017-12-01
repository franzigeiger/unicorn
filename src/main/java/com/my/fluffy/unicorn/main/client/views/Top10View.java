package com.my.fluffy.unicorn.main.client.views;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.my.fluffy.unicorn.main.client.data.Candidate;
import com.my.fluffy.unicorn.main.client.data.Party;
import com.my.fluffy.unicorn.main.client.mainService;

import java.util.List;

public class Top10View extends HorizontalPanel {


   List<Party> parties;
   SelectableElectionView parent;

    public Top10View(SelectableElectionView parent) {
        this.parent = parent;
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

    private void createPerPartyView() {
        final FlexTable table = new FlexTable();

        ListBox dropdownParties = new ListBox();
        dropdownParties.addItem("None");

        for(Party p: parties){
            dropdownParties.addItem(p.getName());
        }

        this.add(new Label("Select Party: "));
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
        for(Party p: parties){

            if(p.getName().equalsIgnoreCase(party) ) {

                mainService.App.getInstance().getTopTen(p.getId(), parent.getElectionYear(), new AsyncCallback<List<Candidate>>() {
                    @Override
                    public void onFailure(Throwable throwable) {

                    }

                    public void onSuccess(List<Candidate> topTen) {
                        if(topTen.size() == 0){
                            table.setText(0, 0, "No Candidates found!");
                        } else {
                            int i = 0;
                            for (Candidate c : topTen) {
                                table.setText(i, 0, c.getLastName() + ", " + c.getFirstName());
                                i++;
                            }
                        }

                    }
                });
            }
        }

    }
}
