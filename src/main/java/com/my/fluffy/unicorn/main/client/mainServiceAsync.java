package com.my.fluffy.unicorn.main.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface mainServiceAsync {
    void getMessage(String msg, AsyncCallback<String> async);
}
