package lab4.service.exceptions;

import com.sun.net.httpserver.HttpExchange;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;

public class IllegalMethodException extends RESTServiceException {

    private final int ERROR_CODE = 405;

    public IllegalMethodException(HttpServerExchange httpExchange) {
        super(httpExchange);
    }

    @Override
    public void sendError() {
        httpExchange.setStatusCode(ERROR_CODE);
        httpExchange.endExchange();
    }
}
