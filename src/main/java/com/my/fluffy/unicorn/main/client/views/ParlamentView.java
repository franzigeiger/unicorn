package com.my.fluffy.unicorn.main.client.views;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.BarChart;
import com.googlecode.gwt.charts.client.corechart.BarChartOptions;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.options.AxisTitlesPosition;
import com.googlecode.gwt.charts.client.options.Bar;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;
import com.my.fluffy.unicorn.main.client.data.Candidate;
import com.my.fluffy.unicorn.main.client.data.PartyStateInfos;
import com.my.fluffy.unicorn.main.client.mainService;
import com.my.fluffy.unicorn.main.client.data.Party;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParlamentView extends VerticalPanel{

    SelectableElectionView parent;
    Map<Party, Integer> distribution;
    HorizontalPanel chartPanel = new HorizontalPanel();
    Map<Party, Double> percent;
    List<PartyStateInfos> additionalMandats;
    Map<Candidate, Party> members;
    PieChart chart;
    BarChart chartPercent ;

    public ParlamentView(SelectableElectionView parent){
        this.parent = parent;
        add(chartPanel);
        if(parent.getElectionYear() == 2013){
            add(new Label("Unfortunately, there are no results for 2013 available yet. This will be fixed soon."));
        } else {
            mainService.App.getInstance().getParlamentSeats(parent.getElectionYear(), new AsyncCallback<Map<Party, Integer>>() {
                @Override
                public void onFailure(Throwable throwable) {

                }

                public void onSuccess(Map<Party, Integer> partyIntegerMap) {
                    distribution = partyIntegerMap;
                    if (percent != null) {
                        drawCharts();
                    }

                }
            });

            mainService.App.getInstance().getPartyPercent(parent.getElectionYear(), new AsyncCallback<Map<Party, Double>>() {
                @Override
                public void onFailure(Throwable throwable) {

                }

                @Override
                public void onSuccess(Map<Party, Double> percentMap) {
                    percent = percentMap;
                    if (distribution != null) {
                        drawCharts();
                    }

                }
            });
            mainService.App.getInstance().getAdditionalMandatsPerParty(parent.getElectionYear(), new AsyncCallback<List<PartyStateInfos>>() {

                @Override
                public void onFailure(Throwable throwable) {

                }

                @Override
                public void onSuccess(List<PartyStateInfos> partyStateInfos) {
                    additionalMandats = partyStateInfos;
                    createAdditionalMandatView();
                }
            });

            mainService.App.getInstance().getParlamentMembers(parent.getElectionYear(), new AsyncCallback<Map<Candidate, Party>>() {

                @Override
                public void onFailure(Throwable throwable) {

                }

                @Override
                public void onSuccess(Map<Candidate, Party> partyStateInfos) {
                    members = partyStateInfos;
                    createPartyMemberTable();
                }
            });
        }
    }

    private void createPartyMemberTable() {
        final FlexTable table = new FlexTable();

        this.add(new HTML("<h3>Parlament Members: </h3>"));
        this.add(table);

        table.removeAllRows();
        int i=0;
        for(Map.Entry<Candidate, Party> member : members.entrySet()){
            Candidate c = member.getKey();
            table.setText(i, 0, member.getValue().getName());
            table.setText(i, 1,c.getFirstName() + " " + c.getLastName() );
            table.setText(i, 2, c.getProfession());
            i++;
        }

    }

    private void createAdditionalMandatView() {
       final FlexTable table = new FlexTable();

       this.add(new HTML("<h3>AdditionalMandats: </h3>"));

       ListBox party = new ListBox();
       List<String> parties = new ArrayList<String>();

       party.addItem("None");
       for(PartyStateInfos infos : additionalMandats){
           if(infos.getAdditionalMandats()>0 ) {
               if (!parties.contains(infos.getParty().getName())) {
                   party.addItem(infos.getParty().getName());
                   parties.add(infos.getParty().getName());
               }
           }
       }


       this.add(new Label("Set Filter: "));
       this.add(party);
       this.add(table);

        party.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                drawMandatTable(table, party.getSelectedValue());
            }
        });

       drawMandatTable(table, "None");
    }

    public void drawMandatTable(FlexTable table, String party){
        table.removeAllRows();
        int i=0;
        for(PartyStateInfos infos : additionalMandats){

            if(party.equals("None")|| infos.getParty().getName().equalsIgnoreCase(party) )
            if(infos.getAdditionalMandats()>0) {
                table.setText(i, 0, infos.getParty().getName());
                table.setText(i, 1, infos.getState().getName());
                table.setText(i, 2, infos.getAdditionalMandats() + "");
                i++;
            }

        }

    }

    private void drawCharts() {
        try {
            ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
            chartLoader.loadApi(new Runnable() {

                @Override
                public void run() {
                    // Create and attach the chart
                    chart = new PieChart();

                    chartPanel.add(chart);
                    draw();
                    chart.setWidth("600px");
                    chart.setHeight("600px");
                    // Create and attach the chart
                    chartPercent = new BarChart();
                    chartPanel.add(chartPercent);
                    drawPercent();
                    chartPercent.setWidth("600px");
                    chartPercent.setHeight("600px");

                }
            });
        }catch(Exception e){

        }
    }

    public void draw(){
        /*
        DataTable pieOldData = DataTable.create();
        pieOldData.addColumn(ColumnType.STRING, "Major");
        pieOldData.addColumn(ColumnType.NUMBER, "Degrees");
        pieOldData.addRow("Business", 256070);
        pieOldData.addRow("Education", 108034);
        pieOldData.addRow("Social Sciences & History", 127101);
        pieOldData.addRow("Health", 81863);
        pieOldData.addRow("Psychology", 74194);*/
        // Set options
        PieChartOptions options = PieChartOptions.create();

        options.setHeight(600);
        options.setWidth(600);

        DataTable pieNewData = DataTable.create();
        pieNewData.addColumn(ColumnType.STRING, "Party");
        pieNewData.addColumn(ColumnType.NUMBER, "Seats");
        pieNewData.addRows(distribution.size());
        //int row=0;
        int gesamt=0;
        for(Map.Entry<Party, Integer> entry : distribution.entrySet()){
            pieNewData.addRow(entry.getKey().getName(), (int) entry.getValue());
        //    pieNewData.setValue(row,0, entry.getKey().getName());
        //    pieNewData.setValue(row,1,(int) entry.getValue());
         //   row++;
            gesamt += entry.getValue();
        }

        options.setTitle("Parlament Distribution");

        // Draw the chart
        chart.draw(pieNewData, options);

        this.add(new Label("Common: " + gesamt));

    }

    public void drawPercent(){
        DataTable dataTable = DataTable.create();

        dataTable.addColumn(ColumnType.STRING, "Party");
        dataTable.addColumn(ColumnType.NUMBER, "Percent");
        int i =0;
        for(Map.Entry<Party, Double> party : percent.entrySet()){
            dataTable.addRow( party.getKey().getName(), party.getValue().doubleValue());
            i++;
        }

        // Set options
        BarChartOptions options = BarChartOptions.create();
        options.setFontName("Tahoma");
        options.setTitle("Percentage Distribution");
        options.setHAxis(HAxis.create("Party"));
        options.setVAxis(VAxis.create("Percent"));
        Bar bar = Bar.create();
        bar.setGroupWidth("100px");
        options.setBar(bar);
        options.setHeight(600);
        options.setWidth(550);


        // Draw the chart
        chartPercent.draw(dataTable, options);
    }
}
