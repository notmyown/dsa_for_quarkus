package de.nmo.dsa.roller.chat;

import de.nmo.dsa.roller.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;

@ServerEndpoint("/chat/{username}")         
@ApplicationScoped
public class ChatSocket {

    Map<String, Session> sessions = new ConcurrentHashMap<>();
    Map<Long, Long> pollinguser = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        broadcast("User " + username + " joined");
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        broadcast("User " + username + " left");
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        broadcast("User " + username + " left on error: " + throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        broadcast(message);
    }

    private void broadcast(String message) {
        if (message == null || message.length() == 0) {
            return;
        }
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

    public List<String> getSessionUserNames() {
        List<String> users = new ArrayList<>();
        users.addAll(sessions.keySet());
        return users;
    }



    public void addPollingUser(User user) {
        pollinguser.put(user.getId(), System.currentTimeMillis());
    }

    public void removePollingUser(User user) {
        pollinguser.remove(user.getId());
    }

    public Map<Long, Long> getPollinguser() {
        return pollinguser;
    }
}