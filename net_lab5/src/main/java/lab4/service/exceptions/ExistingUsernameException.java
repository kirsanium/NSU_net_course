package lab4.service.exceptions;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ExistingUsernameException extends RESTServiceException {

    private final int ERROR_CODE = 401;

    public ExistingUsernameException(HttpServerExchange httpExchange) {
        super(httpExchange);
    }

    @Override
    public void sendError() {
        httpExchange.getResponseHeaders().add(new HttpString("WWW-Authenticate"), "Token realm='Username is already in use'");
        httpExchange.setStatusCode(ERROR_CODE);
        httpExchange.endExchange();
    }
}
