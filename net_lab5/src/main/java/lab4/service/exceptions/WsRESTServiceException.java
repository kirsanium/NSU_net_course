package lab4.service.exceptions;

import io.undertow.websockets.spi.WebSocketHttpExchange;

public abstract class WsRESTServiceException extends Exception {

    protected WebSocketHttpExchange httpExchange;

    public WsRESTServiceException(WebSocketHttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    public WebSocketHttpExchange getHttpExchange() {
        return httpExchange;
    }

    abstract public void sendError();
}
