package com.my.fluffy.unicorn.main.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import com.my.fluffy.unicorn.main.client.views.DistrictView;
import com.my.fluffy.unicorn.main.client.views.ParlamentView;
import com.my.fluffy.unicorn.main.client.views.StateView;

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
        menu.setWidth("500px");
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


        menu.addItem(new MenuItem("States", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new StateView(parent));
            }
        }));

        menu.addItem(new MenuItem("District", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new DistrictView(parent));
            }
        }));

        menu.addItem(new MenuItem("Other", new Command(){

            @Override
            public void execute() {
                contentPanel.clear();
                contentPanel.add(new Label("Nothing to see here"));
            }
        }));

    }
}
