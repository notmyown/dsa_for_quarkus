package de.nmo.dsa.roller.rest.controller;

import de.nmo.dsa.roller.chat.ChatSocket;
import de.nmo.dsa.roller.chat.Roller;
import de.nmo.dsa.roller.entity.ChatMessage;
import de.nmo.dsa.roller.entity.Session;
import de.nmo.dsa.roller.entity.User;
import de.nmo.dsa.roller.error.GenericException;
import de.nmo.dsa.roller.error.InvalidUserException;
import de.nmo.dsa.roller.rest.dao.ChatMessageListDAO;
import de.nmo.dsa.roller.rest.dao.UserListDAO;
import de.nmo.dsa.roller.services.ChatMessageService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Path(ChatEndpoint.PATH)
public class ChatEndpoint {

    static final String PATH = "/dsa/chat";

    @Inject
    Roller roller;

    @Inject
    ChatSocket chatSocket;

    @Inject
    ChatMessageService chatmessageService;

    @Inject
    UserEndpoint userEndpoint;

    @GET
    @Path("room/{room}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@PathParam("room") String room, @QueryParam("page") int page) throws GenericException {
        try {
            List<ChatMessage> msgs = chatmessageService.getByRoom(room, page);
            Collections.reverse(msgs);
            List<ChatMessageListDAO> responses = msgs.stream().map(ChatMessageListDAO::new)
                    .collect(Collectors.toList());
            return Response.status(200)
                    .entity(responses).build();
        } catch (Exception e) {
            throw new GenericException("Error getting User list", e);
        }
    }

    @GET
    @Path("room/{room}/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clear(@PathParam("room") String room, @QueryParam("token") String token) throws GenericException {
        try {
            User user = userEndpoint.getUser(token);
            if(user.isAdmin()) {
                List<ChatMessage> msgs = chatmessageService.getByRoom(room);
                msgs.forEach(cm -> {
                    chatmessageService.delete(cm);
                });
            }
            return Response.status(200)
                    .entity("").build();
        } catch (Exception e) {
            throw new GenericException("Error getting Chat list", e);
        }
    }

    @GET
    @Path("room/{room}/poll/{time}/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@PathParam("room") String room, @PathParam("time") long time, @PathParam("token") String token) throws GenericException {
        try {
            User user = userEndpoint.getUser(token);
            chatSocket.addPollingUser(user);
            List<ChatMessage> msgs = chatmessageService.getByRoomSince(room, time);
            List<ChatMessageListDAO> responses = msgs.stream().map(ChatMessageListDAO::new)
                    .collect(Collectors.toList());
            return Response.status(200)
                    .entity(responses).build();
        } catch (Exception e) {
            throw new GenericException("Error getting User list", e);
        }
    }

    @POST
    @Path("send")
    @Produces(MediaType.APPLICATION_JSON)
    public Response send(@QueryParam("token") String token, @QueryParam("msg") String msg) throws GenericException {
        try {
            if (msg != null && msg.trim().length() > 0) {
                User user = userEndpoint.getUser(token);
                String message = roller.getMessage(msg, token);
                if (message == null) {
                    throw new GenericException("Error getting ChatMessage");
                }
                ChatMessage cm = new ChatMessage();
                chatmessageService.create(cm);

                message = message.replaceFirst("class='message", "id='msg_" + cm.getId() + "' class='message");
                cm.setMessage(message);
                cm.setRoom("HavenaChronicles");
                cm.setTime(System.currentTimeMillis());
                chatmessageService.update(cm.getId(), cm);

                chatSocket.onMessage(message, user.getName());
            }
            return Response.status(200)
                    .entity("").build();
        } catch (Exception e) {
            throw new GenericException("Error sending: " + e.getMessage(), e);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@QueryParam("token") String token, @QueryParam("msg") long msg) throws GenericException {
        try {
            User user = userEndpoint.getUser(token);
            if (user.isAdmin()) {
                ChatMessage cm = chatmessageService.get(msg);
                if (cm != null) {
                    chatmessageService.delete(cm);
                }
            }
            return Response.status(200)
                    .entity("").build();
        } catch (Exception e) {
            throw new GenericException("Error sending: " + e.getMessage(), e);
        }
    }

}
