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

    PieChart chart;

    public WomenInParliamentView(int year) {

        if(year == 2013){
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

/*        int total = 0;
        for(Map.Entry<String, Integer> entry : parliamentMembers.entrySet()){
            total += entry.getValue();
        }*/

        for(Map.Entry<String, Integer> entry : parliamentMembers.entrySet()){
            /*int rowCounter = 0;
            pieNewData.addRow(rowCounter);*/

            if(entry.getKey().equals("M")){
//                pieNewData.setValue(rowCounter,0, "Male");
                pieNewData.addRow("Male", entry.getValue());
            } else if(entry.getKey().equals("W")){
//                pieNewData.setValue(rowCounter,0, "Female");
                pieNewData.addRow("Female", entry.getValue());
            } else {
//                pieNewData.setValue(rowCounter,0, "Other");
                pieNewData.addRow(entry.getKey(), entry.getValue());
            }

/*            pieNewData.setValue(rowCounter, 1, entry.getValue());
            rowCounter++;*/
        }

        options.setTitle("First votes per party");

        // Draw the chart
        chart.draw(pieNewData, options);
    }
}
