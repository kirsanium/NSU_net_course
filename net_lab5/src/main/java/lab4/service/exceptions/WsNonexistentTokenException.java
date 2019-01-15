package lab4.service.exceptions;

import com.sun.net.httpserver.HttpExchange;
import io.undertow.server.HttpServerExchange;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.io.IOException;

public class WsNonexistentTokenException extends WsRESTServiceException {

    private final int ERROR_CODE = 403;

    public WsNonexistentTokenException(WebSocketHttpExchange httpExchange) {
        super(httpExchange);
    }

    @Override
    public void sendError() {
        httpExchange.endExchange();
    }
}
