package de.nmo.dsa.roller.rest.controller;

import de.nmo.dsa.roller.chat.ChatSocket;
import de.nmo.dsa.roller.config.Configuration;
import de.nmo.dsa.roller.entity.Session;
import de.nmo.dsa.roller.entity.Skill;
import de.nmo.dsa.roller.entity.SkillToUser;
import de.nmo.dsa.roller.entity.User;
import de.nmo.dsa.roller.error.GenericException;
import de.nmo.dsa.roller.error.InvalidLoginException;
import de.nmo.dsa.roller.error.InvalidUserException;
import de.nmo.dsa.roller.rest.dao.SkillDataResponse;
import de.nmo.dsa.roller.rest.dao.UserDataDAO;
import de.nmo.dsa.roller.rest.dao.UserListDAO;
import de.nmo.dsa.roller.rest.dao.UserLoginDAO;
import de.nmo.dsa.roller.services.SessionService;
import de.nmo.dsa.roller.services.SkillService;
import de.nmo.dsa.roller.services.SkillToUserService;
import de.nmo.dsa.roller.services.UserService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Path(UserEndpoint.PATH)
public class UserEndpoint {
    static final String PATH = "/dsa/user";

	@Context
	UriInfo uri;

	@Inject
    private Configuration config;

    @Inject
    private UserService userService;

    @Inject
    private SessionService sessionService;

    @Inject
    private SkillService skillService;

    @Inject
    private SkillToUserService skillToUserService;

    @Inject
    private ChatSocket chatSocket;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() throws GenericException {
        try {
            List<User> users = userService.all();
            List<UserListDAO> responses = users.stream().map(UserListDAO::new)
                    .collect(Collectors.toList());
            return Response.status(200)
                    .entity(responses).build();
        } catch (Exception e) {
            throw new GenericException("Error getting User list", e);
        }

    }

    @GET
    @Path("room/{room}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listbyroom(@PathParam("room") String room) throws GenericException {
        try {
            List<String> usernames = chatSocket.getSessionUserNames();
            List<UserListDAO> users = new ArrayList<>();
            for (String name : usernames) {
                try {
                    User u = userService.getByName(name);
                    if (u != null) {
                        users.add(new UserListDAO(u));
                    }
                } catch (NoResultException nre) {
                    continue;
                }
            }
            List<Long> removeIds = new ArrayList<>();
            for (Map.Entry<Long, Long> entry : chatSocket.getPollinguser().entrySet()) {
                try {
                    if (System.currentTimeMillis() -entry.getValue() > 5000 ) {
                        removeIds.add(entry.getValue());
                        continue;
                    }
                    User u = userService.get(entry.getKey());
                    if (u != null) {
                        users.add(new UserListDAO(u));
                    }
                } catch (Exception e) {
                    removeIds.add(entry.getKey());
                }
            }


            return Response.status(200)
                    .entity(users).build();
        } catch (Exception e) {
            throw new GenericException("Error getting User list", e);
        }
    }

    @POST
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@QueryParam("name") String name, @QueryParam("password") String password) throws GenericException {
        try {
            final User u = new User();
            u.setName(name);
            u.setUsername(name);
            u.setAdmin(false);
            u.setPassword(password);
            userService.create(u);

            List<Skill> skills = skillService.all();
            skills.forEach(skill -> {
                if (!"Eigene".equals(skill.getCategory())) {
                    userService.updateSkill(u, skill.getId(), 0);
                }
            });


            return Response.status(200)
                    .entity(new UserListDAO(u)).build();
        } catch (Exception e) {
            throw new GenericException("Error creating Meeting", e);
        }
    }

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@QueryParam("name") String name, @QueryParam("password") String password) throws GenericException {
        try {
            System.out.println("$"+name+"$");
            User u = userService.getByName(name);
            System.out.println(u);
            System.out.println(userService);
            if (u != null && u.getPassword() != null && u.getPassword().equals(password)) {
                //success
                Session s = sessionService.getByUser(u);
                if (s == null) {
                    s = sessionService.create(u);
                }
                return Response.status(200)
                        .entity(new UserLoginDAO(s,u)).build();
            }
            throw new InvalidLoginException("Invalid Login");
        } catch (Exception e) {
            throw new GenericException("Error creating Meeting", e);
        }
    }

    @POST
    @Path("logout/{id}/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@PathParam("token") String token) throws GenericException {
        try {
            User u = getUser(token);
            Session s = sessionService.getByUser(u);
            if (s == null || s.getToken() == null) {
                throw new InvalidUserException("No actrive Session for user " + u.getName());
            }

            if (!s.getToken().equals(token)) {
                throw new InvalidUserException("Invalid Session for user " + u.getName());
            }

            sessionService.delete(s);

            return Response.status(200)
                    .entity("logout").build();
        } catch (Exception e) {
            throw new GenericException("Error creating Meeting", e);
        }
    }

    @GET
    @Path("{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("token") String token) throws GenericException {
        try {
            User u = getUser(token);


            return Response.status(200)
                    .entity(new UserDataDAO(u)).build();
        } catch (Exception e) {
            throw new GenericException("Error creating Meeting", e);
        }
    }

    @GET
    @Path("{token}/skills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkills(@PathParam("token") String token) throws GenericException {
        try {
            User u = getUser(token);

            List<SkillDataResponse> responses = new ArrayList<>();
            List<SkillToUser> userskills = skillToUserService.allByUser(u);
            List<Long> given = new ArrayList<>();
            userskills.forEach(su -> {
                Skill sk = skillService.get(su.getSkill());
                given.add(sk.getId());
                responses.add(new SkillDataResponse(su, sk));
            });

            List<Skill> allskills = skillService.all();
            allskills.forEach(skill -> {
                if (!given.contains(skill.getId())) {
                    System.out.println(skill.getName());
                    SkillToUser su = new SkillToUser();
                    su.setUser(u.getId());
                    su.setSkill(skill.getId());
                    su.setValue(0);
                    skillToUserService.create(su);
                    responses.add(new SkillDataResponse(su, skill));
                }
            });


            return Response.status(200)
                    .entity(responses).build();
        } catch (Exception e) {
            throw new GenericException("Error creating Meeting", e);
        }
    }

    @POST
    @Path("user/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("token") String token, @QueryParam("username") String username) throws GenericException {
        User u = getUser(token);
        u.setUsername(username);
        userService.update(u.getId(), u);
        return Response.status(200)
                .entity(new UserDataDAO(u)).build();
    }

    @POST
    @Path("skill/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSkill(@PathParam("token") String token, @QueryParam("skill") long skill, @QueryParam("value") long value) throws GenericException {
        User u = getUser(token);
        userService.updateSkill(u, skill, value);
        return Response.status(200)
                .entity(new UserDataDAO(u)).build();
    }

    @POST
    @Path("attr/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAttr(@PathParam("token") String token, @QueryParam("attr") String attr, @QueryParam("value") long value) throws GenericException {
        User u = getUser(token);
        String a = attr.toLowerCase();
        switch(a) {
            case "mu" : {
                u.setAttr_mu(value);
                break;
            }
            case "kl" : {
                u.setAttr_kl(value);
                break;
            }
            case "in" : {
                u.setAttr_in(value);
                break;
            }
            case "ch" : {
                u.setAttr_ch(value);
                break;
            }
            case "ff" : {
                u.setAttr_ff(value);
                break;
            }
            case "ge" : {
                u.setAttr_ge(value);
                break;
            }
            case "ko" : {
                u.setAttr_ko(value);
                break;
            }
            case "kk" : {
                u.setAttr_kk(value);
                break;
            }

        }
        userService.update(u.getId(), u);
        return Response.status(200)
                .entity(new UserDataDAO(u)).build();
    }


    @PATCH
    @Path("{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response edit(@PathParam("token") String token, UserDataDAO u) throws GenericException {
        User user = getUser(token);
        user.setAttr_ff(u.getAttr_ff());
        user.setAttr_ch(u.getAttr_ch());
        user.setAttr_in(u.getAttr_in());
        user.setAttr_ge(u.getAttr_ge());
        user.setAttr_kk(u.getAttr_kk());
        user.setAttr_kl(u.getAttr_kl());
        user.setAttr_ko(u.getAttr_ko());
        user.setAttr_mu(u.getAttr_mu());
        user.setName(u.getName());
        System.out.println(user);
        userService.update(user.getId(), user);
        return get(token);
    }

    @POST
    @Path("skill/new/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newSkill(@PathParam("token") String token, @QueryParam("name") String name, @QueryParam("value") long value, @QueryParam("dices") String dices) throws GenericException {
        User user = getUser(token);
        //Prüfen ob es den Skill nicht schon gibt
        List<Skill> skills = skillService.all();
        Skill skill = null;
        for(Skill s : skills) {
            if (name.equals(s.getName())) {
                skill = s;
                break;
            }
        }
        //skill anlegen
        if (skill == null) {
            skill = new Skill();
            skill.setCategory("Eigene");
            skill.setAttributes(dices);
            skill.setName(name);
            skillService.create(skill);
        }

        List<SkillToUser> sus = skillToUserService.allByUser(user);
        SkillToUser su = null;
        for (SkillToUser s : sus) {
            if (s.getSkill() == skill.getId()) {
                su = s;
            }
        }
        if (su == null) {
            su = new SkillToUser();
            su.setSkill(skill.getId());
            su.setUser(user.getId());
            su = skillToUserService.create(su);
        }
        System.err.println("--->" + value);
        su.setValue(value);
        skillToUserService.update(su.getId(), su);

        return Response.status(200)
                .entity(new SkillDataResponse(su, skill)).build();
    }

    public User getUser(String token) throws InvalidUserException {
        Session s = sessionService.getByToken(token);
        if (s == null || s.getUser() == 0) {
            throw new InvalidUserException("No actrive Session for token");
        }
        User user = userService.get(s.getUser());
        User u = userService.get(s.getUser());
        if (u == null) {
            throw new InvalidUserException("");
        }
        return u;
    }




}
