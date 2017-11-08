package com.my.fluffy.unicorn.main.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("mainService")
public interface mainService extends RemoteService {
    // Sample interface method of remote interface
    String getMessage(String msg);

    /**
     * Utility/Convenience class.
     * Use mainService.App.getInstance() to access static instance of mainServiceAsync
     */
    public static class App {
        private static mainServiceAsync ourInstance = GWT.create(mainService.class);

        public static synchronized mainServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
