package de.nmo.dsa.roller.rest.controller;

import de.nmo.dsa.roller.config.Configuration;
import de.nmo.dsa.roller.entity.Skill;
import de.nmo.dsa.roller.entity.User;
import de.nmo.dsa.roller.error.GenericException;
import de.nmo.dsa.roller.rest.dao.SkillListDAO;
import de.nmo.dsa.roller.rest.dao.UserPwdDAO;
import de.nmo.dsa.roller.services.SessionService;
import de.nmo.dsa.roller.services.SkillService;
import de.nmo.dsa.roller.services.SkillToUserService;
import de.nmo.dsa.roller.services.UserService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Path(InitEndpoint.PATH)
public class InitEndpoint {
    static final String PATH = "/dsa/init";

    static String[][] skills = {
            {"Fliegen", "Körper", "MU/IN/CH"},
            {"Gaukeleien", "Körper", "MU/CH/FF"},
            {"Klettern", "Körper", "MU/GE/KK"},
            {"Körperberrschung", "Körper", "GE/GE/KO"},
            {"Kraftakt", "Körper", "KO/KK/KK"},
            {"Reiten", "Körper", "CH/GE/KK"},
            {"Schwimmen", "Körper", "GE/KO/KK"},
            {"Selbstbeherrschung", "Körper", "MU/MU/KO"},
            {"Singen", "Körper", "KL/CH/KO"},
            {"Sinnesschärfe", "Körper", "KL/IN/IN"},
            {"Tanzen", "Körper", "KL/CH/GE"},
            {"Taschendiebstahl", "Körper", "MU/FF/GE"},
            {"Verbergen", "Körper", "MU/IN/GE"},
            {"Zechen", "Körper", "KL/KO/KK"},
            {"Bekehren & Überzeugen", "Gesellschaft","MU/KL/CH"},
            {"Betören", "Gesellschaft","MU/CH/CH"},
            {"Einschüchtern", "Gesellschaft","MU/IN/CH"},
            {"Etikette", "Gesellschaft","KL/IN/CH"},
            {"Gassenwissen", "Gesellschaft","KL/IN/CH"},
            {"Menschenkenntnis", "Gesellschaft","KL/IN/CH"},
            {"Überreden", "Gesellschaft","MU/IN/CH"},
            {"Verkleiden", "Gesellschaft","IN/CH/GE"},
            {"Willenskraft", "Gesellschaft","MU/IN/CH"},
            {"Fährtensuchen", "Natur","MU/IN/GE"},
            {"Fesseln", "Natur","KL/FF/KK"},
            {"Fischen & Angeln", "Natur","FF/GE/KO"},
            {"Orientierung", "Natur","KL/IN/IN"},
            {"Pflanzenkunde", "Natur","KL/FF/KO"},
            {"Tierkunde", "Natur","MU/MU/CH"},
            {"Wildnisleben", "Natur","MU/GE/KO"},
            {"Brett- & Glücksspiel", "Wissen","KL/KL/IN"},
            {"Geographie", "Wissen","KL/KL/IN"},
            {"Geschichtswissen", "Wissen","KL/KL/IN"},
            {"Götter & Kulte", "Wissen","KL/KL/IN"},
            {"Kriegskunst", "Wissen","MU/KL/IN"},
            {"Magiekunde", "Wissen","KL/KL/IN"},
            {"Mechanik", "Wissen","KL/KL/FF"},
            {"Rechnen", "Wissen","KL/KL/IN"},
            {"Rechtskunde", "Wissen","KL/KL/IN"},
            {"Sagen & Legenden", "Wissen","KL/KL/IN"},
            {"Sphärenkunde", "Wissen","KL/KL/IN"},
            {"Sternkunde", "Wissen","KL/KL/IN"},
            {"Alchimie", "Handwerk","MU/KL/FF"},
            {"Boote & Schiffe", "Handwerk","FF/GE/KK"},
            {"Fahrzeuge", "Handwerk","CH/FF/KO"},
            {"Handel", "Handwerk","KL/IN/CH"},
            {"Heilkunde Gift", "Handwerk","MU/KL/IN"},
            {"Heilkunde Krankheiten", "Handwerk","MU/IN/KO"},
            {"Heilkunde Seele", "Handwerk","IN/CH/KO"},
            {"Heilkunde Wunden", "Handwerk","KL/FF/FF"},
            {"Holzbearbeitung", "Handwerk","FF/GE/KK"},
            {"Lebensmittelbearbeitung", "Handwerk","IN/FF/FF"},
            {"Lederbearbeitung", "Handwerk","FF/GE/KO"},
            {"Malen & Zeichnen", "Handwerk","IN/FF/FF"},
            {"Metallbearbeitung", "Handwerk","FF/KO/KK"},
            {"Musizieren", "Handwerk","CH/FF/KO"},
            {"Schlösserknacken", "Handwerk","IN/FF/FF"},
            {"Steinbearbeitung", "Handwerk","FF/FF/KK"},
            {"Stoffbearbeitung", "Handwerk","KL/FF/FF"},
};

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response init() throws GenericException {
        for(int i = 0; i < skills.length; i++) {
            String[] sk = skills[i];
            Skill s = null;
            s = new Skill();
            s.setId(i+1);
            s.setName(sk[0]);
            s.setCategory(sk[1]);
            s.setAttributes(sk[2]);
            skillService.add(s);
        }

        return list();
    }

    @GET
    @Path("skills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() throws GenericException {
        try {
            List<Skill> users = skillService.all();
            List<SkillListDAO> responses = users.stream().map(SkillListDAO::new)
                    .collect(Collectors.toList());
            return Response.status(200)
                    .entity(responses).build();
        } catch (Exception e) {
            throw new GenericException("Error getting User list", e);
        }
    }
    @GET
    @Path("pwd")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pwd() throws GenericException {
        try {
            List<User> users = userService.all();
            List<UserPwdDAO> responses = users.stream().map(UserPwdDAO::new)
                    .collect(Collectors.toList());
            return Response.status(200)
                    .entity(responses).build();
        } catch (Exception e) {
            throw new GenericException("Error getting User list", e);
        }
    }






}
