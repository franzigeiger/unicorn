package com.my.fluffy.unicorn.main.client.views;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.my.fluffy.unicorn.main.client.data.District;
import com.my.fluffy.unicorn.main.client.data.Party;
import com.my.fluffy.unicorn.main.client.data.Top10Data;
import com.my.fluffy.unicorn.main.client.mainService;

import java.util.List;
import java.util.Map;

public class WinningPartiesView extends HorizontalPanel {


   List<District> districts;
   SelectableElectionView parent;
   Map<District, List<String>> winningParties;

    public WinningPartiesView(SelectableElectionView parent) {
        this.parent = parent;
        this.setSpacing(20);
        mainService.App.getInstance().getAllDistricts(parent.getElectionYear(), new AsyncCallback<List<District>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            public void onSuccess(List<District> d) {
                districts = d;
            }
        });

        mainService.App.getInstance().getWinningParties(parent.getElectionYear(),
                new AsyncCallback<Map<District, List<String>>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            public void onSuccess(Map<District, List<String>> w) {
                winningParties = w;
                showWinningParties();
            }
        });
    }

    private void showWinningParties() {
        final FlexTable table = new FlexTable();
        table.addStyleName("FlexTable");
        table.setCellPadding(0);
        table.setCellSpacing(0);
        table.removeAllRows();
        table.insertRow(0);
        table.setHTML(0,0,"<h3>District</h3>");
        table.setHTML(0,1,"<h3>First</h3>");
        table.setHTML(0,2,"<h3>Second</h3>");
        int i = 1;
        for(Map.Entry<District, List<String>> result: winningParties.entrySet()){
            table.setText(i, 0, result.getKey().getName());
            table.setText(i, 1, result.getValue().get(0));
            table.setText(i, 2, result.getValue().get(1));
            i++;
        }
        add(table);
    }
}
