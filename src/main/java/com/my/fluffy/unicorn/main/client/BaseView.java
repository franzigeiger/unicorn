package com.my.fluffy.unicorn.main.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import com.my.fluffy.unicorn.main.client.views.DifferencePerPartyView;
import com.my.fluffy.unicorn.main.client.views.DistrictView;
import com.my.fluffy.unicorn.main.client.views.ParlamentView;
import com.my.fluffy.unicorn.main.client.views.StateView;

public class BaseView {

    Panel content;

    public BaseView(Panel base){
        super();

        this.content=base;

        initialize();
    }

    public void initialize(){

        MenuBar menu = new MenuBar();
        menu.setAutoOpen(true);
        menu.setWidth("1000px");
        menu.setAnimationEnabled(true);

        content.add(menu);

        Panel contentPanel = new HorizontalPanel();
        content.add(contentPanel);

        menu.addItem(new MenuItem("Parlament", new Command(){

            @Override
            public void execute() {

                contentPanel.clear();
                contentPanel.add(new ParlamentView());
            }
        }));


        menu.addItem(new MenuItem("States", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new StateView());
            }
        }));

        menu.addItem(new MenuItem("District", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new DistrictView());
            }
        }));

        menu.addItem(new MenuItem("Other", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new Label("Nothing to see here"));
            }
        }));

        menu.addItem(new MenuItem("Top10", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new Top10View(parent));
            }
        }));


        menu.addItem(new MenuItem("Differences per Party", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new DifferencePerPartyView(parent));
            }
        }));

        menu.addItem(new MenuItem("First Votes", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new FirstVotesTotalView(parent));
            }
        }));

        menu.addItem(new MenuItem("Women in Parliament", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new WomenInParliamentView(parent));
            }
        }));

    }
}
