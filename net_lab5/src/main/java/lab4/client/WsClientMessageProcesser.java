package lab4.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lab4.service.Message;

import javax.websocket.*;
import java.lang.reflect.Type;
import java.util.List;

@ClientEndpoint
public class WsClientMessageProcesser {

    @OnOpen
    public void onOpen(Session session) {
    }

    @OnMessage
    public void processMessage(String message) {
        Type type = new TypeToken<List<Message>>() {}.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<Message> messages = gson.fromJson(message, type);
        if (messages != null) {
            for (Message message1 : messages) {
                System.out.println(message1.getAuthor() + ": " + message1.getMessage());
            }
        }
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

}
