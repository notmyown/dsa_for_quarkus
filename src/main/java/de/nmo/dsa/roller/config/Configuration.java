package de.nmo.dsa.roller.config;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Configuration {

   // @ConfigProperty(name = "timebox.locale")
   // String locale;

    static Configuration instance;

   // public String getLocale() {
   //     instance = this;
   //     return locale;
   // }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }

       return instance;
    }
}
