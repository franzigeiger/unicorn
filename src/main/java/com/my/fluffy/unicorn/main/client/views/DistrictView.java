package com.my.fluffy.unicorn.main.client.views;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.my.fluffy.unicorn.main.client.data.*;
import com.my.fluffy.unicorn.main.client.mainService;
import java.util.List;
import java.util.Map;

public class DistrictView extends VerticalPanel {

    SelectableElectionView parent;

    List<District> districts2013;
    List<District> districts2017;

    District chosen2017;
    District chosen2013;

    List<DistrictResults> results;
    Candidate winner2017;

    HorizontalPanel chartPanel = new HorizontalPanel();
    PieChart chartPartyFirst;
    PieChart chartPartySecond;
    PieChart chartVoters;

    public DistrictView(SelectableElectionView parent) {
        this.parent = parent;

        if(parent.getElectionYear()== 2013){
            this.add(new Label("Unfortunately, there is no corresponding data for this year."));
        } else {

            mainService.App.getInstance().getAllDistricts(2013, new AsyncCallback<List<District>>() {
                @Override
                public void onFailure(Throwable throwable) {

                }

                public void onSuccess(List<District> d) {
                    districts2013 = d;
                }
            });

            mainService.App.getInstance().getAllDistricts(2017, new AsyncCallback<List<District>>() {
                @Override
                public void onFailure(Throwable throwable) {

                }

                public void onSuccess(List<District> d) {
                    districts2017 = d;
                    createDistrictOverview();
                }
            });
        }
    }

    private void createDistrictOverview(){
        final FlexTable table = new FlexTable();

        this.add(new HTML("<h3>Choose District: </h3>"));

        ListBox listBox = new ListBox();

        listBox.addItem("None");
        for(District district : districts2017){
            listBox.addItem(district.getName());
        }


        this.add(new Label("Set Filter: "));
        this.add(listBox);
        this.add(table);
        this.add(chartPanel);

        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                getDistrictData(table, listBox.getSelectedValue());

            }
        });

        getDistrictData(table, "None");
    }

    private void getDistrictData(FlexTable table, String distName){
        table.removeAllRows();
        chartPanel.clear();
        if(!distName.equals("None")) {
            chosen2017 = getChosen(2017, distName);
            chosen2013 = getChosen(2013, distName);

            // get district results for 2017
            mainService.App.getInstance().getDistrictResults(
                    chosen2013.getId(),chosen2017.getId(), new AsyncCallback<List<DistrictResults>>() {
                @Override
                public void onFailure(Throwable throwable) {

                }

                public void onSuccess(List<DistrictResults> districtResults) {
                    results = districtResults;
                }
            });

            // get direct winner 2017
            mainService.App.getInstance().getDistrictWinner(chosen2017.getId(),  new AsyncCallback<Candidate>() {
                @Override
                public void onFailure(Throwable throwable) {

                }

                public void onSuccess(Candidate winner) {
                    winner2017 = winner;
                    table.setText(0, 0,
                            "Direct Winner: " + winner2017.getLastName() + ", " + winner2017.getFirstName());
                    drawCharts();
                }
            });

        }
    }

    private void drawCharts(){
        try {
            ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
            chartLoader.loadApi(new Runnable() {

                @Override
                public void run() {
                    // Create and attach chart for voters/non-voters
                    chartVoters = new PieChart();
                    chartPanel.add(chartVoters);

                    //data for voter/non-voters of 2017
                    DataTable pieNewDataVoters = DataTable.create();
                    pieNewDataVoters.addColumn(ColumnType.STRING, "Voters");
                    pieNewDataVoters.addColumn(ColumnType.NUMBER, "Amount");
                    pieNewDataVoters.addRows(2);
                    //add values for voters/non-voters chart of 2017
                    int voters = getFirstSum(2017) + chosen2017.getInvalidFirstVotes();
                    pieNewDataVoters.addRow("Voters",
                            voters);
                    pieNewDataVoters.addRow("Non-Voters",
                            chosen2017.getEligibleVoters() - voters);

                    //data for voter/non-voters of 2013
                    DataTable pieOldDataVoters = DataTable.create();
                    pieOldDataVoters.addColumn(ColumnType.STRING, "Voters");
                    pieOldDataVoters.addColumn(ColumnType.NUMBER, "Amount");
                    pieOldDataVoters.addRows(2);
                    //add values for voters/non-voters chart of 2013
                    int votersOld = getFirstSum(2013) + chosen2013.getInvalidFirstVotes();
                    pieOldDataVoters.addRow("Voters",
                            votersOld);
                    pieOldDataVoters.addRow("Non-Voters",
                            chosen2013.getEligibleVoters() - votersOld);

                    //set title and draw chart
                    PieChartOptions optionsVoters = PieChartOptions.create();
                    optionsVoters.setTitle("Voters");
                    chartVoters.draw(chartVoters.computeDiff(pieOldDataVoters, pieNewDataVoters),
                            optionsVoters);


                    //create and attach chart for first votes
                    chartPartyFirst = new PieChart();
                    chartPanel.add(chartPartyFirst);

                    //create and attach chart for second votes
                    chartPartySecond = new PieChart();
                    chartPanel.add(chartPartySecond);

                    //data for first votes 2017
                    DataTable pieNewDataFirst = DataTable.create();
                    pieNewDataFirst.addColumn(ColumnType.STRING, "Party");
                    pieNewDataFirst.addColumn(ColumnType.NUMBER, "Amount - First");
                    pieNewDataFirst.addRows(results.size());

                    //data for for first votes 2013
                    DataTable pieOldDataFirst = DataTable.create();
                    pieOldDataFirst.addColumn(ColumnType.STRING, "Party");
                    pieOldDataFirst.addColumn(ColumnType.NUMBER, "Amount - First");
                    pieOldDataFirst.addRows(results.size());

                    //data for second votes 2017
                    DataTable pieNewDataSecond = DataTable.create();
                    pieNewDataSecond.addColumn(ColumnType.STRING, "Party");
                    pieNewDataSecond.addColumn(ColumnType.NUMBER, "Amount - Second");
                    pieNewDataSecond.addRows(results.size());

                    //data for second votes 2013
                    DataTable pieOldDataSecond = DataTable.create();
                    pieOldDataSecond.addColumn(ColumnType.STRING, "Party");
                    pieOldDataSecond.addColumn(ColumnType.NUMBER, "Amount - Second");
                    pieOldDataSecond.addRows(results.size());

                    //add values for each chart
                    for(DistrictResults dr: results){
                        pieNewDataFirst.addRow(dr.getPartyName(), dr.getFirst17());
                        pieOldDataFirst.addRow(dr.getPartyName(), dr.getFirst13());

                        pieNewDataSecond.addRow(dr.getPartyName(), dr.getSecond17());
                        pieOldDataSecond.addRow(dr.getPartyName(), dr.getSecond13());
                    }

                    // set title of first votes pie chart and draw chart
                    PieChartOptions optionsFirst = PieChartOptions.create();
                    optionsFirst.setTitle("First");
                    chartPartyFirst.draw(chartPartyFirst.computeDiff(pieOldDataFirst, pieNewDataFirst), optionsFirst);

                    // set title of second votes pie chart and draw chart
                    PieChartOptions optionsSecond = PieChartOptions.create();
                    optionsSecond.setTitle("Second");
                    //
                    chartPartySecond.draw(chartPartySecond.computeDiff(pieOldDataSecond, pieNewDataSecond),
                            optionsSecond);

                }
            });
        }catch(Exception e){

        }
    }

    private int getFirstSum(int year){
        int sum = 0;
        if(year == 2017){
            for(DistrictResults dr: results){
                sum += dr.getFirst17();
            }
        } else {
            for(DistrictResults dr: results){
                sum += dr.getFirst13();
            }
        }
        return sum;
    }

    private District getChosen(int year,String name){
        if(year == 2017) {
            for (District d : districts2017) {
                if (d.getName().equals(name)) {
                    return d;
                }
            }
        } else{
            for (District d : districts2013) {
                if (d.getName().equals(name)) {
                    return d;
                }
            }
        }
        return null;
    }
}
