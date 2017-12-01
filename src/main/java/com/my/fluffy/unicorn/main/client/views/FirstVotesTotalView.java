package com.my.fluffy.unicorn.main.client.views;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.BarChart;
import com.googlecode.gwt.charts.client.corechart.BarChartOptions;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.options.Bar;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;
import com.my.fluffy.unicorn.main.client.data.DifferenceFirstSecondVotes;
import com.my.fluffy.unicorn.main.client.data.Party;
import com.my.fluffy.unicorn.main.client.mainService;

import java.util.Map;

public class FirstVotesTotalView extends HorizontalPanel {

    Map<Party, Double> firstVotesPerParty;
    SelectableElectionView parent;
    PieChart chart;

    public FirstVotesTotalView(SelectableElectionView parent) {
        this.parent = parent;
        mainService.App.getInstance().getFirstVotesTotal(
                parent.getElectionYear(), new AsyncCallback<Map<Party, Double>>() {
                @Override
                public void onFailure(Throwable throwable) {

                }

                @Override
                public void onSuccess(Map<Party, Double> firstVotesParty) {
                    firstVotesPerParty = firstVotesParty;
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

    public void draw(){
        PieChartOptions options = PieChartOptions.create();

        options.setHeight(800);
        options.setWidth(800);

        DataTable pieNewData = DataTable.create();
        pieNewData.addColumn(ColumnType.STRING, "Party");
        pieNewData.addColumn(ColumnType.NUMBER, "First (%)");
        pieNewData.addRows(firstVotesPerParty.size());
        int counter = 0;
        for(Map.Entry<Party, Double> entry : firstVotesPerParty.entrySet()){
            System.out.println(counter++);
            pieNewData.addRow(entry.getKey().getName(), entry.getValue());
        }

        options.setTitle("First votes per party");

        // Draw the chart
        chart.draw(pieNewData, options);
    }
}
