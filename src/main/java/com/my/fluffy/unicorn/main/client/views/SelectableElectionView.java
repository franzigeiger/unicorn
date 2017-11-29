package com.my.fluffy.unicorn.main.client.views;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SelectableElectionView extends VerticalPanel {
    int year;
    ;

    CheckBox use2013;
    CheckBox use2017 ;
    CheckBox aggregated;

    public SelectableElectionView(int year, boolean useAggregated){
        this.year = year;
        generateWidgets(useAggregated);


    }

    public void generateWidgets(boolean useAggregated){
         use2013= new CheckBox("2013");
         use2017 = new CheckBox("2017");

         use2017.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
             @Override
             public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                 if(valueChangeEvent.getValue() == true){
                     use2013.setValue(false);
                 } else {
                     use2013.setValue(true);
                 }
             }
         });

        use2013.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                if(valueChangeEvent.getValue() == true){
                    use2017.setValue(false);
                } else {
                    use2017.setValue(true);
                }
            }
        });

         if(year == 2017){
             use2017.setValue(true);
         }
        aggregated = new CheckBox("Use aggregated Data: ");
        aggregated.setValue(useAggregated);
        this.add(aggregated);

        this.add(new Label("Select the year for election information:"));

        HorizontalPanel panel = new HorizontalPanel();
        panel.add(use2013);
        panel.add(use2017);

        this.add(panel);



    }


    public int getElectionYear(){
        return use2017.getValue()==true ? 2017 : 2013 ;
    }

    public boolean getAggregated(){
        return aggregated.getValue();
    }

}
