package de.nmo.dsa.roller.chat;

import de.nmo.dsa.roller.entity.SkillToUser;
import de.nmo.dsa.roller.entity.User;
import de.nmo.dsa.roller.services.SessionService;
import de.nmo.dsa.roller.services.SkillService;
import de.nmo.dsa.roller.services.SkillToUserService;
import de.nmo.dsa.roller.services.UserService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@ApplicationScoped
@ActivateRequestContext
public class Roller {

    @Inject
    private UserService userService;

    @Inject
    private SessionService sessionService;

    @Inject
    private SkillService skillService;

    @Inject
    private SkillToUserService skillToUserService;

    public String getMessage(String message, String username) {
        String roll = checkRoll(message, username);
        return roll;
    }

    private String checkRoll(String msg, String username) {
        if (msg == null) {
            return null;
        }
        if (msg.startsWith("==roll")) {
            System.out.println("das hier?");
            System.out.println(userService);
            User user = userService.getByName(username);
            System.out.println(user);
            String content = msg.split("_")[1];
            String[] parts = content.split("-");
            System.out.println(parts[0] + " - " + parts[1]);
            if (parts[0].equals("attr")) {
                String attr = parts[1];
                int rand20 = new Random().nextInt(20) + 1;
                long val = getAttrValue(user, attr);
                String retval = "Roll " + attr.toUpperCase() + "(" + val + ") with D20: " + rand20 + (val < rand20 ? " (failed)" : "");
                return "<span class='username'>" + username + "</span><span class='message dsa_roll_text" + (val < rand20 ? " failed" : "") +"'>" + retval + "</span>";
            } else if (parts[0].equals("skill")) {
                int id = Integer.parseInt(parts[1]);
                List<SkillToUser> sus = skillToUserService.allByUser(user);
                sus = sus.stream().filter(su -> su.getSkill() == id).collect(Collectors.toList()); ;
                if (sus.size() == 1) {
                    long qsLeft = sus.get(0).getValue();
                    String name = skillService.get(sus.get(0).getId()).getName();
                    String attr = skillService.get(sus.get(0).getId()).getAttributes();
                    String[] attrs = attr.split("/");
                    String retval = "Roll '" + name + "(" + qsLeft + ")': ";
                    int mod = 0;
                    for (int i=0; i < attrs.length; i++) {
                        long val = getAttrValue(user,attrs[i]);
                        int r = new Random().nextInt(20) + 1;
                        retval += attrs[i].toUpperCase() + "(" + r + "/" + val + (mod != 0 ? " [Mod:" + mod + "]": "") + "), ";
                        val = val+mod;
                        if (r > val) {
                            qsLeft -= (r-val);
                        }
                    }
                    int qs = getQS(qsLeft);
                    retval += qsLeft >= 0 ? (" -> FP: " + qsLeft + " = QS:" + qs) : " failed";
                    return "<span class='username'>" + username + "</span><span class='message dsa_roll_text" + (qsLeft >= 0 ? "" : " failed") +"'>" + retval + "</span>";
                }
            }
        } else {
            msg = msg.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            return "<span class='username'>" + username + "</span><span class='message'>" + msg + "</span>";
        }
        return null;
    }

    private long getAttrValue(User user, String attr) {
        long val = 0;
        System.out.println(user + "-" + attr + "-");
        if("mu".equalsIgnoreCase(attr)) {
            val = user.getAttr_mu();
        } else if("kl".equalsIgnoreCase(attr)) {
            val = user.getAttr_kl();
        } else if("ch".equalsIgnoreCase(attr)) {
            val = user.getAttr_ch();
        } else if("ff".equalsIgnoreCase(attr)) {
            val = user.getAttr_ff();
        } else if("ge".equalsIgnoreCase(attr)) {
            val = user.getAttr_ge();
        } else if("ko".equalsIgnoreCase(attr)) {
            val = user.getAttr_ko();
        } else if("in".equalsIgnoreCase(attr)) {
            val = user.getAttr_in();
        } else if("kk".equalsIgnoreCase(attr)) {
            val = user.getAttr_kk();
        }
        return val;
    }

    private int getQS(long qsLeft) {
        int qs = 0;
        if(qsLeft > -1) {
            qs = 1;
        }
        if(qsLeft > 3) {
            qs = 2;
        }
        if(qsLeft > 6) {
            qs = 3;
        }
        if(qsLeft > 9) {
            qs = 4;
        }
        if(qsLeft > 12) {
            qs = 5;
        }
        if(qsLeft > 15) {
            qs = 6;
        }
        return qs;
    }

}