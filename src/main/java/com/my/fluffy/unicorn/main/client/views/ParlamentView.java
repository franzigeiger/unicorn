package com.my.fluffy.unicorn.main.client.views;

import com.google.gwt.dom.client.Style;
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
import com.my.fluffy.unicorn.main.client.mainService;
import com.my.fluffy.unicorn.main.client.data.Party;

import java.util.Map;

public class ParlamentView extends HorizontalPanel {

    Map<Party, Integer> distribution;
    Map<Party, Double> percent;
    PieChart chart;
    BarChart chartPercent ;

    public ParlamentView(){
        mainService.App.getInstance().getParlamentSeats(2017, new AsyncCallback<Map<Party, Integer>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Map<Party, Integer> partyIntegerMap) {
                distribution = partyIntegerMap;
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

        mainService.App.getInstance().getPartyPercent(2017, new AsyncCallback<Map<Party, Double>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Map<Party, Double> percentMap) {
               percent=percentMap;
                ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
                chartLoader.loadApi(new Runnable() {

                    @Override
                    public void run() {
                        // Create and attach the chart
                        chartPercent = new BarChart();

                       add(chartPercent);
                        drawPercent();
                        chartPercent.setWidth("600px");
                        chartPercent.setHeight("600px");
                    }
                });
            }
        });

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