package com.my.fluffy.unicorn.main.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.DOM;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;


/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class main implements EntryPoint {



    /**
     * The name of the style theme used in showcase.
     */
    public static final String THEME = "clean";
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        Panel base = new VerticalPanel();

        BaseView view= new BaseView(base);

        mainService.App.getInstance().startupRESTService(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Void aVoid) {

            }
        });


        RootPanel.get().add(base);
    }




}
