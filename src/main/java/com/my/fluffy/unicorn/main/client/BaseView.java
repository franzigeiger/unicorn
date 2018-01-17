package com.my.fluffy.unicorn.main.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import com.my.fluffy.unicorn.main.client.views.*;

public class BaseView {

    Panel content;
    VerticalPanel contentPanel ;
    SelectableElectionView parent;

    public BaseView(Panel base){
        super();

        this.content=base;

        initialize();
    }

    public void initialize(){

        MenuBar menu = new MenuBar();
        menu.setAutoOpen(true);
        menu.setWidth("100%");
        menu.setAnimationEnabled(true);

        content.add(menu);
        content.setWidth("100%");
        parent = new SelectableElectionView(2017, true, this);
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

        menu.addItem(new MenuItem("New Election", new Command(){

            @Override
            public void execute() {
                content.remove(parent);
                contentPanel.clear();
                contentPanel.add(new ElectionView(content));
            }
        }));

        contentPanel.add(new ParlamentView(parent));

    }


    public void reload() {
       Widget content = contentPanel.getWidget(0);

       if(content instanceof ParlamentView ){
           contentPanel.clear();
           contentPanel.add(new ParlamentView(parent));
       }

        if(content instanceof DistrictView ){
            contentPanel.clear();
            contentPanel.add(new DistrictView(parent));
        }

        if(content instanceof WinningPartiesView ){
            contentPanel.clear();
            contentPanel.add(new WinningPartiesView(parent));
        }

        if(content instanceof FirstVotesTotalView ){
            contentPanel.clear();
            contentPanel.add(new FirstVotesTotalView(parent));
        }

        if(content instanceof WomenInParliamentView ){
            contentPanel.clear();
            contentPanel.add(new WomenInParliamentView(parent));
        }

        if(content instanceof ElectionView ){
            contentPanel.clear();
            contentPanel.add(new ElectionView(parent));
        }
    }
}
