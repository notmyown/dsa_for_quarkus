package de.nmo.dsa.roller.rest.controller;

import de.nmo.dsa.roller.chat.ChatSocket;
import de.nmo.dsa.roller.chat.Roller;
import de.nmo.dsa.roller.entity.ChatMessage;
import de.nmo.dsa.roller.error.GenericException;
import de.nmo.dsa.roller.rest.dao.ChatMessageListDAO;
import de.nmo.dsa.roller.services.ChatMessageService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    @GET
    @Path("room/{room}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@PathParam("room") String room) throws GenericException {
        try {
            List<ChatMessage> msgs = chatmessageService.getByRoom(room);
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
    public Response send(@QueryParam("name") String name, @QueryParam("msg") String msg) throws GenericException {
        try {
            String message = roller.getMessage(msg, name);
            ChatMessage cm = new ChatMessage();
            cm.setMessage(message);
            cm.setRoom("HavenaChronicles");
            cm.setTime(System.currentTimeMillis());
            System.out.println(cm);
            chatmessageService.create(cm);
            chatSocket.onMessage(message, name);
            return Response.status(200)
                    .entity("").build();
        } catch (Exception e) {
            throw new GenericException("Error creating Meeting", e);
        }
    }

}
