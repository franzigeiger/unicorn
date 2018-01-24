package com.my.fluffy.unicorn.main.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.my.fluffy.unicorn.main.client.BaseView;
import com.my.fluffy.unicorn.main.client.data.District;
import com.my.fluffy.unicorn.main.client.mainService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ElectionView extends VerticalPanel{

    private Panel master;
    private List<District> districts;
    private ListBox districtBox;

    public ElectionView(Panel view){
        this.setSpacing(100);
        this.master=view;
        mainService.App.getInstance().getAllDistricts(2017, new AsyncCallback<List<District>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(List<District> data) {
                districts = data;
                initializeDistrictSelection();
            }
        });
    }

    private void initializeDistrictSelection() {
        Collections.sort(districts, new Comparator<District>() {
            @Override
            public int compare(final District object1, final District object2) {
                return object1.getName().compareTo(object2.getName());
            }
        } );

        districtBox = new ListBox();
       for(District district : districts){
           districtBox.addItem(district.getName());
       }

    this.add(new HTML("<h3>Choose District:</h3>"));
       add(districtBox);


        Button startInsertion = new Button("Start election" , new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
               DialogBox box =new ConfirmDialog("Confirm", "Do you really want to start a new election?","Start", true);
                box.center();
                box.show();
            }
        }
        );

        this.add(startInsertion);

    }


    private class ConfirmDialog extends DialogBox {

        public ConfirmDialog(String header, String text, String button, boolean ifConfirmation) {
            // Set the dialog box's caption.
            setText(header);

            // Enable animation.
            setAnimationEnabled(true);

            // Enable glass background.
            setGlassEnabled(true);

            // DialogBox is a SimplePanel, so you have to set its widget
            // property to whatever you want its contents to be.
            Button ok = new Button(button);
            ok.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    ConfirmDialog.this.hide();
                    if(ifConfirmation){
                        createElectionView();
                    }

                }
            });

            Label label = new Label(text);

            VerticalPanel panel = new VerticalPanel();
            panel.setHeight("100");
            panel.setWidth("300");
            panel.setSpacing(10);
            panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            panel.add(label);
            panel.add(ok);

            setWidget(panel);
        }
    }

    public void createElectionView(){

        String district =districtBox.getSelectedItemText();
        District electionDistrict = null;
        for(District d : districts){
            if(d.getName().equals(district)){
              electionDistrict = d;
            }
        }

        if(electionDistrict!= null){
            master.clear();
            master.add(new BallotView(electionDistrict));
        } else {
           DialogBox box= new ConfirmDialog("Error" ,"Please select a eletion district to start!", "OK", false);
            box.center();
            box.show();
        }


    }


}
