package lab4.service.exceptions;

import com.sun.net.httpserver.HttpExchange;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;

public class UnknownIdException extends RESTServiceException {

    private final int ERROR_CODE = 404;

    public UnknownIdException(HttpServerExchange httpExchange) {
        super(httpExchange);
    }

    @Override
    public void sendError() {
        httpExchange.setStatusCode(ERROR_CODE);
        httpExchange.endExchange();
    }
}
