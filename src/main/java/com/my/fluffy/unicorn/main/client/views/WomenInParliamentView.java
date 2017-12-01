package com.my.fluffy.unicorn.main.client.views;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.my.fluffy.unicorn.main.client.data.Candidate;
import com.my.fluffy.unicorn.main.client.data.Party;
import com.my.fluffy.unicorn.main.client.mainService;

import java.util.Map;

public class WomenInParliamentView extends HorizontalPanel {

    Map<String,Integer> parliamentMembers;
    SelectableElectionView parent;
    PieChart chart;

    public WomenInParliamentView(SelectableElectionView parent) {
        this.parent = parent;
        if(parent.getElectionYear() == 2013){
            this.add(new Label("Unfortunately, there is no corresponding data for the election of 2013."));
        } else {
            mainService.App.getInstance().getAmountPerGender(
                    new AsyncCallback<Map<String, Integer>>() {
                        @Override
                        public void onFailure(Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(Map<String, Integer> amountPerGender) {
                            parliamentMembers = amountPerGender;
                            ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
                            chartLoader.loadApi(new Runnable() {

                                @Override
                                public void run() {
                                    // Create and attach the chart
                                    chart = new PieChart();

                                    add(chart);
                                    draw();

                                    chart.setWidth("600px");
                                    chart.setHeight("600px");
                                }
                            });
                        }
                    });
        }
    }

    public void draw(){
        PieChartOptions options = PieChartOptions.create();

        options.setHeight(800);
        options.setWidth(800);

        DataTable pieNewData = DataTable.create();
        pieNewData.addColumn(ColumnType.STRING, "Gender");
        pieNewData.addColumn(ColumnType.NUMBER, "Amount");
        pieNewData.addRows(parliamentMembers.size());

        for(Map.Entry<String, Integer> entry : parliamentMembers.entrySet()){
            if(entry.getKey().equals("M")){
                pieNewData.addRow("Male", entry.getValue());
            } else if(entry.getKey().equals("W")){
                pieNewData.addRow("Female", entry.getValue());
            } else {
                pieNewData.addRow(entry.getKey(), entry.getValue());
            }
        }

        options.setTitle("First votes per party");

        // Draw the chart
        chart.draw(pieNewData, options);
    }
}
