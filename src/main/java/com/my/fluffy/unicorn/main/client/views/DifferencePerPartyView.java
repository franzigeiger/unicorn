package com.my.fluffy.unicorn.main.client.views;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.BarChart;
import com.googlecode.gwt.charts.client.corechart.BarChartOptions;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.options.Bar;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;
import com.my.fluffy.unicorn.main.client.data.DifferenceFirstSecondVotes;
import com.my.fluffy.unicorn.main.client.data.Party;
import com.my.fluffy.unicorn.main.client.mainService;

import java.util.Map;

public class DifferencePerPartyView extends HorizontalPanel {

    Map<Party, DifferenceFirstSecondVotes> differenceTotal;

    BarChart chart;

    public DifferencePerPartyView(int year) {

        mainService.App.getInstance().getDifferencesFirstSecondVotes(
                year, new AsyncCallback<Map<Party, DifferenceFirstSecondVotes>>() {
                @Override
                public void onFailure(Throwable throwable) {

                }

                @Override
                public void onSuccess(Map<Party, DifferenceFirstSecondVotes> diffMap) {
                    differenceTotal = diffMap;
                    ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
                    chartLoader.loadApi(new Runnable() {

                        @Override
                        public void run() {
                            // Create and attach the chart
                            chart = new BarChart();

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
        DataTable dataTable = DataTable.create();

        dataTable.addColumn(ColumnType.STRING, "Difference");
        dataTable.addColumn(ColumnType.NUMBER, "Party");
        int i = 0;
        for(Map.Entry<Party, DifferenceFirstSecondVotes> partyDiff : differenceTotal.entrySet()){
            String district = differenceTotal.get(partyDiff.getKey()).getDistrictName();
            String partyLabel = partyDiff.getKey().getName() + ",\n(" + district + "";
            if(differenceTotal.get(partyDiff.getKey()).getFirstVotes()
                    > differenceTotal.get(partyDiff.getKey()).getSecondVotes()){
                partyLabel += ",\n first > second)";
            } else {
                partyLabel += ",\n second > first)";
            }
            dataTable.addRow( partyLabel, partyDiff.getValue().getDiff());
            i++;
        }

        // Set options
        BarChartOptions options = BarChartOptions.create();
        options.setFontName("Tahoma");
        options.setTitle("Percentage Distribution");
        options.setHAxis(HAxis.create("Party"));
        options.setVAxis(VAxis.create("Difference"));
        Bar bar = Bar.create();
        bar.setGroupWidth("100px");
        options.setBar(bar);
        options.setHeight(1200);
        options.setWidth(1100);

        // Draw the chart
        chart.draw(dataTable, options);
    }
}
