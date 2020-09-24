package de.nmo.dsa.roller.config;

import de.nmo.dsa.roller.entity.Skill;
import de.nmo.dsa.roller.entity.User;
import de.nmo.dsa.roller.error.GenericException;
import de.nmo.dsa.roller.rest.controller.InitEndpoint;
import de.nmo.dsa.roller.rest.controller.UserEndpoint;
import de.nmo.dsa.roller.services.SkillService;
import de.nmo.dsa.roller.services.UserService;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class StartUp {

    @Inject
    private SkillService skillService;

    @Inject
    private UserService userService;

    @Inject
    InitEndpoint initEndpoint;

    @Inject
    UserEndpoint userEndpoint;



    void onStart(@Observes StartupEvent ev) {
        try {
            List<Skill> skills = skillService.all();
            if(skills.size() == 0) {
                initEndpoint.init();
            }
            User meister = null;
            try {
                meister = userService.getByName("Meister");
            } catch (Exception e) {
                //swallow
            }
            if (meister == null) {
                userEndpoint.create("Meister", "dsa");
                meister = userService.getByName("Meister");
                meister.setAdmin(true);
                userService.update(meister.getId(), meister);
            }

        } catch (GenericException ge) {
            //swallow
        }

    }

}
