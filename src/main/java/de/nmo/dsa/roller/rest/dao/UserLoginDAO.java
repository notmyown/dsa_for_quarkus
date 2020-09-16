package de.nmo.dsa.roller.rest.dao;

import de.nmo.dsa.roller.entity.Session;
import de.nmo.dsa.roller.entity.User;

public class UserLoginDAO {

    private final String token;
    private final String username;

    public UserLoginDAO() {
        this.token = "";
        this.username = "";
    }

    public UserLoginDAO(Session s, User u) {
        this.token = s.getToken();
        this.username = u.getName();
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}
