package com.my.fluffy.unicorn.main.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import com.my.fluffy.unicorn.main.client.views.*;

public class BaseView {

    Panel content;
    VerticalPanel contentPanel ;

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

        SelectableElectionView parent = new SelectableElectionView(2017, true);
        content.add(parent);
        contentPanel= new VerticalPanel();
        content.add(contentPanel);

        menu.addItem(new MenuItem("Parlament", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new ParlamentView(parent));
            }
        }));

        menu.addItem(new MenuItem("District", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new DistrictView(parent));
            }
        }));

        menu.addItem(new MenuItem("Winning Parties", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new WinningPartiesView(parent));
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
