package com.my.fluffy.unicorn.main.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.my.fluffy.unicorn.main.client.mainService;

public class mainServiceImpl extends RemoteServiceServlet implements mainService {
    // Implementation of sample interface method
    public String getMessage(String msg) {
        return "Client said: \"" + msg + "\"<br>Server answered: \"Hi!\"";
    }
}