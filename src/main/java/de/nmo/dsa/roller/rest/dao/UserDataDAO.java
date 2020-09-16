package de.nmo.dsa.roller.rest.dao;

import de.nmo.dsa.roller.entity.User;

public class UserDataDAO {

    private String name;

    private boolean admin;

    private long attr_mu;
    private long attr_kl;
    private long attr_in;
    private long attr_ch;
    private long attr_ff;
    private long attr_ge;
    private long attr_ko;
    private long attr_kk;

    public UserDataDAO() {
    }

    public UserDataDAO(User u) {
        name = u.getName();
        admin = u.isAdmin();
        attr_mu = u.getAttr_mu();
        attr_kl = u.getAttr_kl();
        attr_in = u.getAttr_in();
        attr_ch = u.getAttr_ch();
        attr_ff = u.getAttr_ff();
        attr_ge = u.getAttr_ge();
        attr_ko = u.getAttr_ko();
        attr_kk = u.getAttr_kk();
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return admin;
    }

    public long getAttr_mu() {
        return attr_mu;
    }

    public long getAttr_kl() {
        return attr_kl;
    }

    public long getAttr_in() {
        return attr_in;
    }

    public long getAttr_ch() {
        return attr_ch;
    }

    public long getAttr_ff() {
        return attr_ff;
    }

    public long getAttr_ge() {
        return attr_ge;
    }

    public long getAttr_ko() {
        return attr_ko;
    }

    public long getAttr_kk() {
        return attr_kk;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setAttr_mu(long attr_mu) {
        this.attr_mu = attr_mu;
    }

    public void setAttr_kl(long attr_kl) {
        this.attr_kl = attr_kl;
    }

    public void setAttr_in(long attr_in) {
        this.attr_in = attr_in;
    }

    public void setAttr_ch(long attr_ch) {
        this.attr_ch = attr_ch;
    }

    public void setAttr_ff(long attr_ff) {
        this.attr_ff = attr_ff;
    }

    public void setAttr_ge(long attr_ge) {
        this.attr_ge = attr_ge;
    }

    public void setAttr_ko(long attr_ko) {
        this.attr_ko = attr_ko;
    }

    public void setAttr_kk(long attr_kk) {
        this.attr_kk = attr_kk;
    }
}


