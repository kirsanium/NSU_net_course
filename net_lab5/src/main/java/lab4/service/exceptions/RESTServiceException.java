package lab4.service.exceptions;

import com.sun.net.httpserver.HttpExchange;
import io.undertow.server.HttpServerExchange;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public abstract class RESTServiceException extends Exception {

    protected HttpServerExchange httpExchange;

    public RESTServiceException(HttpServerExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    public HttpServerExchange getHttpExchange() {
        return httpExchange;
    }

    abstract public void sendError();
}
