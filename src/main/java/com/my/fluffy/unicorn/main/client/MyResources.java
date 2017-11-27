package com.my.fluffy.unicorn.main.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface MyResources extends ClientBundle {
    public static final MyResources INSTANCE =  GWT.create(MyResources.class);

    @Source("Showcase.css")
    public CssResource css();
}