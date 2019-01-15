package lab4.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.io.UndertowInputStream;
import io.undertow.io.UndertowOutputStream;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.HttpString;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import lab4.User;
import lab4.service.exceptions.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static io.undertow.Handlers.websocket;
import static java.lang.Thread.sleep;

public class RESTService {

    private final int TOKEN_START = "Token ".length();

    private File messagesFile = null;
    private int port;

    private List<TimeoutInfo> timeoutList = new LinkedList<>();
    private Map<String, Integer> tokensMap = new ConcurrentHashMap<>();
    private Map<Integer, User> usersMap = new ConcurrentHashMap<>();
    private Map<WebSocketChannel, Integer> channelMap = new ConcurrentHashMap<>();
    private AtomicInteger currentUserId = new AtomicInteger(0);
    private AtomicInteger currentMessageId = new AtomicInteger(0);

    public RESTService(int port) {
        this.port = port;
    }

    public void start() {
        try {

            messagesFile = createMessagesFile();

            Undertow server = Undertow.builder()
                    .addHttpListener(port, "localhost")
                    .setHandler(Handlers.routing()
                        .post("/login", new BlockingHandler(new LoginHandler()))
                        .post("/logout", new BlockingHandler(new LogoutHandler()))
                        .get("/users", new BlockingHandler(new UsersHandler()))
                        .get("/users/{userID}", new BlockingHandler(new UsersIDHandler()))
                        .get("/messages", new BlockingHandler(new GetMessagesHandler()))
                        .post("/messages", new BlockingHandler(new PostMessagesHandler()))
                        .get("/wsmessages", websocket((exchange, channel) -> {
                            channelMap.put(channel,0);
                            channel.resumeReceives();
                            sendNewMessages();
                        }))
                        .setInvalidMethodHandler(new BlockingHandler(new IllegalMethodHandler()))
                    ).build();
            server.start();

            Thread timeoutHandler = new Thread(new TimeoutHandler());
            timeoutHandler.start();

        } catch (IOException e) {
            System.out.println("There was an error starting up the server");
        }
    }

    private static AbstractReceiveListener getListener() {
        return new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel,
                                             BufferedTextMessage message) {
                String messageData = message.getData();
                for (WebSocketChannel session : channel.getPeerConnections()) {
                    WebSockets.sendText(messageData, session, null);
                }
            }
        };
    }

    private File createMessagesFile() throws IOException {
        File messagesDir = new File("messages".trim());
        if (!messagesDir.exists()) {
            messagesDir.mkdir();
        }
        File newMessagesFile = new File(("messages" + File.separator + "messages.json").trim());
        newMessagesFile.delete();
        if (!newMessagesFile.exists()) {
            newMessagesFile.createNewFile();
        }

        JsonWriter fileWriter = new JsonWriter(new FileWriter(newMessagesFile, true));
        fileWriter.beginArray();
        fileWriter.endArray();
        fileWriter.close();

        return newMessagesFile;
    }

    private class LoginHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange httpExchange) {
            System.out.flush();
            try (JsonReader reader = new JsonReader(new InputStreamReader(new UndertowInputStream(httpExchange)))) {
                if (!httpExchange.getRequestHeaders().getFirst("Content-Type").equals("application/json")) {
                    throw new IllegalRequestFormatException(httpExchange);
                }

                String username = readUsername(reader, httpExchange);
                for (User user: usersMap.values()) {
                    if (user.getUsername().equals(username)) {
                        throw new ExistingUsernameException(httpExchange);
                    }
                }

                String token = UUID.randomUUID().toString();
                int id = currentUserId.getAndIncrement();
                tokensMap.put(token, id);
                usersMap.put(id, new User(username));

                httpExchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
                httpExchange.setStatusCode(200);

                JsonWriter writer = new JsonWriter(new OutputStreamWriter(new UndertowOutputStream(httpExchange)));
                writer.beginObject();
                writer.name("id").value(id);
                writer.name("username").value(username);
                writer.name("online").value(true);
                writer.name("token").value(token);
                writer.endObject();
                writer.close();

                httpExchange.endExchange();

                timeoutList.add(new TimeoutInfo(token));
            } catch (RESTServiceException e) {
                e.sendError();
            } catch (Exception e) {
                sendUnknownError(httpExchange);
            }
        }

        private String readUsername(JsonReader reader, HttpServerExchange httpExchange) throws IOException, IllegalRequestFormatException {
            reader.beginObject();
            String username;
            String name = reader.nextName();
            if (name.equals("username")) {
                username = reader.nextString();
            }
            else {
                System.out.println("check");
                throw new IllegalRequestFormatException(httpExchange);
            }
            reader.endObject();
            return username;
        }
    }

    private class LogoutHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange httpExchange) {
            try {
                String token = checkAndGetToken(httpExchange);

                httpExchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
                httpExchange.setStatusCode(200);

                int id = tokensMap.get(token);
                usersMap.remove(id);
                tokensMap.remove(token);

                JsonWriter writer = new JsonWriter(new OutputStreamWriter(new UndertowOutputStream(httpExchange)));
                writer.beginObject();
                writer.name("message").value("bye!");
                writer.endObject();
                writer.close();

            } catch (RESTServiceException e) {
                e.sendError();
            } catch (Exception e) {
                sendUnknownError(httpExchange);
            }
        }
    }

    private class UsersIDHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange httpExchange) {
            try {
                checkAndGetToken(httpExchange);

                httpExchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");

                int userID = Integer.parseInt(httpExchange.getQueryParameters().get("userID").getFirst());
                User user = usersMap.get(userID);
                if (user == null)
                    throw new UnknownIdException(httpExchange);
                httpExchange.setStatusCode(200);
                JsonWriter writer = new JsonWriter(new OutputStreamWriter(new UndertowOutputStream(httpExchange)));
                writeUser(writer, userID, user);
                writer.close();
            } catch (RESTServiceException e) {
                e.sendError();
            } catch (Exception e) {
                sendUnknownError(httpExchange);
            }
        }
    }

    private class UsersHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange httpExchange) {
            try {
                checkAndGetToken(httpExchange);

                httpExchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
                httpExchange.setStatusCode(200);

                JsonWriter writer = new JsonWriter(new OutputStreamWriter(new UndertowOutputStream(httpExchange)));
                writer.beginObject();
                writer.name("users").beginArray();
                for (int id : usersMap.keySet()) {
                    writeUser(writer, id, usersMap.get(id));
                }
                writer.endArray();
                writer.endObject();
                writer.close();
            } catch (RESTServiceException e) {
                e.sendError();
            } catch (Exception e) {
                sendUnknownError(httpExchange);
            }
        }

    }

    private class GetMessagesHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange httpExchange) {
            try {
                int offset = 0;
                int count = 10;

                String query = httpExchange.getQueryString();
                System.out.println("Query: " + query);

                if (!query.equals("")) {
                    String[] offsetAndCount = query.split("&");

                    if (offsetAndCount.length > 2) {
                        throw new IllegalRequestFormatException(httpExchange);
                    }


                    for (String anOffsetAndCount : offsetAndCount) {
                        String[] queryArray = anOffsetAndCount.split("=");
                        switch (queryArray[0]) {
                            case "offset":
                                offset = Integer.parseInt(queryArray[1]);
                                break;
                            case "count":
                                count = Integer.parseInt(queryArray[1]);
                                if (count > 100)
                                    throw new IllegalRequestFormatException(httpExchange);
                                break;
                            default:
                                throw new IllegalRequestFormatException(httpExchange);
                        }
                    }
                }
                System.out.println("Offset: " + offset);
                System.out.println("Count: " + count);

                checkAndGetToken(httpExchange);

                List<Message> messageList = new LinkedList<>();
                JsonReader fileReader = new JsonReader(new FileReader(messagesFile));
                fileReader.beginArray();
                for (int i = 0; i < offset && fileReader.hasNext(); ++i) {
                    fileReader.skipValue();
                }
                for (int i = 0; i < count && fileReader.hasNext(); ++i) {
                    fileReader.beginObject();
                    Integer id = null;
                    String message = null;
                    String author = null;
                    while (fileReader.hasNext()) {
                        switch (fileReader.nextName()) {
                            case "messageID":
                                id = fileReader.nextInt();
                                break;

                            case "message":
                                message = fileReader.nextString();
                                break;

                            case "author":
                                author = fileReader.nextString();
                                break;
                        }
                    }
                    messageList.add(new Message(id, message, author));
                    fileReader.endObject();
                }
                fileReader.close();

                httpExchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
                httpExchange.setStatusCode(200);

                JsonWriter writer = new JsonWriter(new OutputStreamWriter(new UndertowOutputStream(httpExchange)));
                writer.beginObject();
                writer.name("messages").beginArray();
                for (Message message : messageList) {
                    writeMessage(writer, message);
                }
                writer.endArray();
                writer.endObject();
                writer.close();

            } catch (RESTServiceException e) {
                e.sendError();
            } catch (Exception e) {
                e.printStackTrace();
                sendUnknownError(httpExchange);
            }
        }
    }

    private class PostMessagesHandler implements HttpHandler {

        @Override
        public void handleRequest(HttpServerExchange httpExchange) {
            try (JsonReader reader = new JsonReader(new InputStreamReader(new UndertowInputStream(httpExchange)))) {
                if (!httpExchange.getRequestHeaders().getFirst("Content-Type").equals("application/json")) {
                    throw new IllegalRequestFormatException(httpExchange);
                }

                String token = checkAndGetToken(httpExchange);
                String message = readMessage(reader, httpExchange);

                int messageID = currentMessageId.getAndIncrement();

                Type type = new TypeToken<List<Message>>() {}.getType();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonReader fileReader = new JsonReader(new FileReader(messagesFile));
                List<Message> data = gson.fromJson(fileReader, type);
                data.add(new Message(messageID, message, usersMap.get(tokensMap.get(token)).getUsername()));
                try (FileWriter fileWriter = new FileWriter(messagesFile)) {
                    gson.toJson(data, type, fileWriter);
                }

                httpExchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
                httpExchange.setStatusCode(200);

                JsonWriter writer = new JsonWriter(new OutputStreamWriter(new UndertowOutputStream(httpExchange)));
                writer.beginObject();
                writer.name("id").value(messageID);
                writer.name("message").value(message);
                writer.endObject();
                writer.close();

                sendNewMessages();

            } catch (RESTServiceException e) {
                e.sendError();
            } catch (Exception e) {
                sendUnknownError(httpExchange);
            }
        }
    }

    private void writeMessage(JsonWriter writer, Message message) throws IOException {
        writer.beginObject();
        writer.name("id").value(message.getMessageID());
        writer.name("message").value(message.getMessage());
        writer.name("author").value(message.getAuthor());
        writer.endObject();
    }

    private String readMessage(JsonReader reader, HttpServerExchange httpExchange) throws IOException, IllegalRequestFormatException {
        reader.beginObject();
        String message;
        String name = reader.nextName();
        if (name.equals("message")) {
            message = reader.nextString();
        }
        else
            throw new IllegalRequestFormatException(httpExchange);
        reader.endObject();
        return message;
    }

    private String checkAndGetToken(HttpServerExchange httpExchange) throws IllegalRequestFormatException, NoTokenException, NonexistentTokenException {
        String token = httpExchange.getRequestHeaders().getFirst("Authorization");
        if (!token.startsWith("Token "))
            throw new IllegalRequestFormatException(httpExchange);
        token = token.substring(TOKEN_START, token.length());
//        System.out.println(token); //TODO: remove me
//        System.out.println(tokensMap.keySet().toString());
        if (token.trim().equals(""))
            throw new NoTokenException(httpExchange);
        if (!tokensMap.containsKey(token))
            throw new NonexistentTokenException(httpExchange);

        for (TimeoutInfo timeoutInfo: timeoutList) {
            if (timeoutInfo.getToken().equals(token)) {
                timeoutInfo.resetTimeout();
            }
        }

        return token;
    }

    private String checkAndGetToken(WebSocketHttpExchange httpExchange) throws WsIllegalRequestFormatException, WsNoTokenException, WsNonexistentTokenException {
        System.out.println("ASDASDASDASDASDASDASd");
        String token = httpExchange.getRequestHeader("Authorization");
        if (!token.startsWith("Token "))
            throw new WsIllegalRequestFormatException(httpExchange);
        token = token.substring(TOKEN_START, token.length());
        System.out.println(token); //TODO: remove me
        System.out.println(tokensMap.keySet().toString());
        if (token.trim().equals(""))
            throw new WsNoTokenException(httpExchange);
        if (!tokensMap.containsKey(token))
            throw new WsNonexistentTokenException(httpExchange);

        for (TimeoutInfo timeoutInfo: timeoutList) {
            if (timeoutInfo.getToken().equals(token)) {
                timeoutInfo.resetTimeout();
            }
        }

        return token;
    }

    private void writeUser(JsonWriter writer, int id, User user) throws IOException {
        writer.beginObject();
        writer.name("id").value(id);
        writer.name("username").value(user.getUsername());
        writer.name("online").value(user.isOnline());
        writer.endObject();
    }

    private void sendUnknownError(HttpServerExchange httpExchange) {
        httpExchange.setStatusCode(500);
        httpExchange.endExchange();
    }

    private class IllegalMethodHandler implements HttpHandler {

        private final int ERROR_CODE = 405;

        @Override
        public void handleRequest(HttpServerExchange httpExchange) {
            httpExchange.setStatusCode(ERROR_CODE);
            httpExchange.endExchange();
        }
    }

    private class TimeoutHandler implements Runnable {

        public final int TIMEOUT = 1000;

        @Override
        public void run() {
            try {
                while (true) {
                    sleep(1000);
                    for (TimeoutInfo timeoutInfo: timeoutList) {
                        timeoutInfo.incrementTimeout();
                        if (timeoutInfo.getTimeout() > TIMEOUT) {
                            String token = timeoutInfo.getToken();
                            int userid = tokensMap.get(token);
                            usersMap.get(userid).setOnline((Boolean)null);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendNewMessages() {
        for (WebSocketChannel channel: channelMap.keySet()) {
            WebSockets.sendText(buildJsonMessages(channel), channel, null);
        }
    }

    private String buildJsonMessages(WebSocketChannel channel) {
        int offset = channelMap.get(channel);
        String jsonMessage = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(baos));
            writer.beginArray();
            //writer.name("messages").beginArray();

            JsonReader fileReader = new JsonReader(new FileReader(messagesFile));
            fileReader.beginArray();
            for (int i = 0; i < offset && fileReader.hasNext(); ++i) {
                fileReader.skipValue();
            }
            System.out.println("offset " + offset);
            for (int i = 0; fileReader.hasNext(); ++i, ++offset) {
                System.out.println("offset " + offset);
                fileReader.beginObject();
                Integer id = null;
                String message = null;
                String author = null;
                while (fileReader.hasNext()) {
                    switch (fileReader.nextName()) {
                        case "messageID":
                            id = fileReader.nextInt();
                            break;

                        case "message":
                            message = fileReader.nextString();
                            break;

                        case "author":
                            author = fileReader.nextString();
                            break;
                    }
                }
                fileReader.endObject();
                writer.beginObject();
                writer.name("id").value(id);
                writer.name("message").value(message);
                writer.name("author").value(author);
                writer.endObject();
            }
            fileReader.close();
            writer.endArray();
//                writer.endObject();
            writer.flush();
            jsonMessage = baos.toString("UTF-8");
            writer.close();
            channelMap.put(channel, offset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(jsonMessage == null ? "null" : jsonMessage);
        return jsonMessage;
    }
}
