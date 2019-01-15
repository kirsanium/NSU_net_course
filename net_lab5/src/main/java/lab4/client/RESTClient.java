package lab4.client;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lab4.User;
import lab4.client.exceptions.LoginErrorException;
import lab4.client.exceptions.LogoutErrorException;
import lab4.client.exceptions.RESTClientException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.websocket.*;

import static java.lang.Thread.sleep;

public class RESTClient {

    private boolean isWebSocket;

    private final String host = "localhost";
    private boolean toQuit = false;
    private int port;

    private List<User> userList = new LinkedList<>();

    public RESTClient(int port, boolean isWebSocket) {
        this.port = port;
        this.isWebSocket = isWebSocket;
    }

    public void start() {
        try {
            Scanner scanner = new Scanner(System.in);
            String token = null;

            while (token == null) {
                System.out.println("Enter your username:");
                String username = scanner.nextLine();
                try {
                    token = login(username);
                } catch (IOException e) {
                    System.out.println("Connection error, try again");
                }
            }

            System.out.println("Welcome to the chat!");

            if (!isWebSocket) {
                Thread updater = new Thread(new Updater(port, token));
                updater.start();
            }
            else {
                try {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    String wsMessagesUri = "ws://" + host + ":" + port + "/wsmessages";
                    container.connectToServer(WsClientMessageProcesser.class, URI.create(wsMessagesUri));

                    Thread usersUpdater = new Thread(new UsersUpdater(port, token));
                    usersUpdater.start();
                } catch (DeploymentException | IOException e) {
                    e.printStackTrace();
                }
            }


            while (true) {
                try {
                    String userInput = scanner.nextLine();
                    switch (userInput) {
                        case "/logout":
                            logout(token);
                            break;

                        case "/list":
                            list(token);
                            break;
                        default:
                            sendMessage(userInput, token);
                            break;
                    }
                    if (userInput.equals("/logout"))
                        break;
                } catch (IOException e) {
                    System.out.println("Connection error, try again");
                }
            }
            toQuit = true;
        } catch (RESTClientException e) {
            e.printStackTrace();
        }
    }

    private String login(String username) throws IOException, LoginErrorException {
        HttpURLConnection con = connect("/login");
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(con.getOutputStream()));
        writer.beginObject();
        writer.name("username").value(username);
        writer.endObject();
        writer.close();
        if (con.getResponseCode() == 401) {
            System.out.println("Username is taken; please choose another one");
            return null;
        }

        if (con.getResponseCode() != 200)
            throw new LoginErrorException();

        JsonReader reader = new JsonReader(new InputStreamReader(con.getInputStream()));
        reader.beginObject();
        String token = null;
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("token")) {
                token = reader.nextString();
                break;
            }
            else {
                reader.skipValue();
            }
        }
        if (token == null)
            throw new LoginErrorException();

        userList.add(new User(username));

        return token;
    }

    private void logout(String token) throws IOException, LogoutErrorException {
        HttpURLConnection con = connect("/logout");
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Token " + token);
        con.setDoOutput(true);

        if (con.getResponseCode() != 200)
            throw new LogoutErrorException();
    }

    private void list(String token) throws IOException, RESTClientException {
        System.out.println("~~List of people online~~");
        for (User user: userList) {
            if (user.isOnline())
                System.out.println(user.getUsername());
        }
    }

    private void sendMessage(String message, String token) throws IOException, RESTClientException {
        HttpURLConnection con = connect("/messages");
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Token " + token);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        JsonWriter writer = new JsonWriter(new OutputStreamWriter(con.getOutputStream()));
        writer.beginObject();
        writer.name("message").value(message);
        writer.endObject();
        writer.close();

        if (con.getResponseCode() != 200)
            throw new RESTClientException();
    }

    private HttpURLConnection connect(String path) throws IOException {
        URL url = new URL("http://" + host+":" + port + path);
        return (HttpURLConnection) url.openConnection();
    }

    private class UsersUpdater implements Runnable {

        private int port;
        private String token;

        private int offset = 0;

        public UsersUpdater(int port, String token) {
            this.port = port;
            this.token = token;
        }

        @Override
        public void run() {
            try {
                boolean toPrint = false;
                while (!toQuit) {
                    sleep(1000);
                    updateUsers(toPrint, token);
                    toPrint = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RESTClientException e) {
                e.printStackTrace();
            }
        }
    }

    private class Updater implements Runnable {

        private int port;
        private String token;

        private int offset = 0;

        public Updater(int port, String token) {
            this.port = port;
            this.token = token;
        }

        @Override
        public void run() {
            try {
                boolean toPrint = false;
                while (!toQuit) {
                    sleep(1000);
                    updateUsers(toPrint, token);
                    toPrint = true;
                    updateMessages();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RESTClientException e) {
                e.printStackTrace();
            }
        }

        private void updateMessages() throws IOException, RESTClientException {
            boolean updateNeeded = true;
            while (updateNeeded) {
                updateNeeded = false;
                HttpURLConnection con = connect("/messages?offset="+offset+"&count=100");
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "Token " + token);

                if (con.getResponseCode() != 200)
                    if (!toQuit)
                        throw new RESTClientException();
                    else return;

                JsonReader reader = new JsonReader(new InputStreamReader(con.getInputStream()));
                reader.beginObject();
                if(!reader.nextName().equals("messages"))
                    throw new RESTClientException();
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();
                    Integer id = null;
                    String message = null;
                    String author = null;
                    while (reader.hasNext()) {
                        updateNeeded = true;
                        switch (reader.nextName()) {
                            case "id":
                                id = reader.nextInt();
                                break;

                            case "message":
                                message = reader.nextString();
                                break;

                            case "author":
                                author = reader.nextString();
                                break;
                        }
                    }
                    reader.endObject();
                    if (id != null)
                        offset = id+1;
                    System.out.println(author + ": " + message);
                }
                reader.endArray();
                reader.endObject();
                reader.close();
            }

        }
    }

    private void updateUsers(boolean toPrint, String token) throws IOException, RESTClientException {
        HttpURLConnection con = connect("/users");
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Token " + token);

        if (con.getResponseCode() != 200)
            if (!toQuit)
                throw new RESTClientException();
            else return;

        JsonReader reader = new JsonReader(new InputStreamReader(con.getInputStream()));
        reader.beginObject();
        if(!reader.nextName().equals("users"))
            throw new RESTClientException();
        reader.beginArray();

        for (User user: userList) {
            user.setOnline(false);
        }

        while (reader.hasNext()) {
            boolean toAdd = true;
            reader.beginObject();
            Integer id = null;
            String username = null;
            Boolean isOnline = null;
            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "id":
                        id = reader.nextInt();
                        break;

                    case "username":
                        username = reader.nextString();
                        break;

                    case "online":
                        isOnline = reader.nextBoolean();
                        break;
                }
            }
            reader.endObject();

            Iterator<User> userIterator = userList.iterator();

            if (isOnline == null || !isOnline) {
                while (userIterator.hasNext()) {
                    User user = userIterator.next();
                    if (user.getUsername().trim().equals(username)) {
                        userIterator.remove();
                        if (isOnline == null) {
                            if (toPrint) System.out.println(username + " disconnected (timeout)");
                        }
                        else {
                            if (toPrint) System.out.println(username + " disconnected");
                        }
                    }
                }
            }
            else {
                for (User user: userList) {
                    if (user.getUsername().trim().equals(username)) {
                        user.setOnline(true);
                        toAdd = false;
                        break;
                    }
                }
                if (toAdd) {
                    userList.add(new User(username));
                    if (toPrint) System.out.println(username + " connected");
                }
            }
        }
        Iterator<User> userIterator = userList.iterator();
        while (userIterator.hasNext()) {
            User user = userIterator.next();
            if (!user.isOnline()) {
                if (toPrint) System.out.println(user.getUsername() + " disconnected");
                userIterator.remove();
            }
        }

        reader.endArray();
        reader.endObject();
        reader.close();
    }
}
