package de.nmo.dsa.roller.chat;

import de.nmo.dsa.roller.entity.Skill;
import de.nmo.dsa.roller.entity.SkillToUser;
import de.nmo.dsa.roller.entity.User;
import de.nmo.dsa.roller.error.GenericException;
import de.nmo.dsa.roller.rest.controller.UserEndpoint;
import de.nmo.dsa.roller.services.SessionService;
import de.nmo.dsa.roller.services.SkillService;
import de.nmo.dsa.roller.services.SkillToUserService;
import de.nmo.dsa.roller.services.UserService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Inject
    UserEndpoint userEndpoint;

    public String getMessage(String message, String token)  throws GenericException {
        String roll = checkRoll(message, token);
        return roll;
    }

    private String checkRoll(String msg, String token) throws GenericException {
        if (msg == null) {
            return null;
        }
        User user = userEndpoint.getUser(token);
        long mod = user.getMod();
        if (msg.startsWith("==roll")) {
            String content = msg.split("_")[1];
            String[] parts = content.split("-");
            if (parts[0].equals("attr")) {
                String attr = parts[1];
                int rand20 = new Random().nextInt(20) + 1;
                long val = getAttrValue(user, attr);
                String retval = "Roll " + attr.toUpperCase() + "(" + val + (mod != 0 ? " [Mod:" + mod : "]") + ") with D20: " + rand20 + ((val+mod) < rand20 ? " (failed)" : "");
                return "<span class='username'>" + user.getUsername() + "</span><span class='message dsa_roll_text" + ((val+mod) < rand20 ? " failed" : "") + "'>" + retval + "</span>"+ timeStamp();
            } else if (parts[0].equals("skill")) {
                int id = Integer.parseInt(parts[1]);
                List<SkillToUser> sus = skillToUserService.allByUser(user);
                sus = sus.stream().filter(su -> su.getSkill() == id).collect(Collectors.toList());
                ;
                if (sus.size() == 1) {
                    long qsLeft = sus.get(0).getValue();
                    Skill skill = skillService.get(sus.get(0).getSkill());
                    String name = skill.getName();
                    String attr = skill.getAttributes();
                    String[] attrs = attr.split("/");
                    String retval = "Roll '" + name + "(" + qsLeft + ")': ";
                    for (int i = 0; i < attrs.length; i++) {
                        long val = getAttrValue(user, attrs[i]);
                        int r = new Random().nextInt(20) + 1;
                        retval += attrs[i].toUpperCase() + "(" + r + "/" + val + (mod != 0 ? " [Mod:" + mod + "]" : "") + "), ";
                        val = val + mod;
                        if (r > val) {
                            qsLeft -= (r - val);
                        }
                    }
                    int qs = getQS(qsLeft);
                    retval += qsLeft >= 0 ? (" -> FP: " + qsLeft + " = QS:" + qs) : " failed";
                    return "<span class='username'>" + user.getUsername() + "</span><span class='message dsa_roll_text" + (qsLeft >= 0 ? "" : " failed") + "'>" + retval + "</span>"+ timeStamp();
                }
            } else if (parts[0].equals("d")) {
                int d = Integer.parseInt(parts[1]);
                int r = new Random().nextInt(d) + 1;
                String retval = "Roll D" + d + " : " + r;
                return "<span class='username'>" + user.getUsername() + "</span><span class='message dsa_roll_text fate d" + d + "'>" + retval + "</span>"+ timeStamp();
            }
        } else if(msg.startsWith("img::")) {
            msg = msg.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            String content = msg.split("::")[1];
            return "<span class='username'>" + user.getUsername() + "</span><span class='message'><img src='" + content + "' /></span>"+ timeStamp();
        } else if(msg.startsWith("==mod")) {
            String[] parts = msg.split("_");
            if(parts.length == 3) {
                try {
                    long userid = Long.parseLong(parts[1]);
                    long modi = Long.parseLong(parts[2]);
                    User u = userService.get(userid);
                    u.setMod(modi);
                    userService.update(u.getId(), u);
                    return "<span class='username'>" + user.getUsername() + "</span><span class='message system'>" + u.getUsername() + " erhält den Modifikator " + modi + "</span>" + timeStamp();
                } catch (Exception e) {
                    //swallow
                }
            }
        } else {
            msg = patchMsg(msg);
            return "<span class='username'>" + user.getUsername() + "</span><span class='message'>" + msg + "</span>" + timeStamp();
        }
        return null;
    }

    private String timeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy<br>HH:mm");

        String date = simpleDateFormat.format(new Date());
        String ret = "<span class='timestamp'>" + date + "</span>";
        return ret;
    }

    private String patchMsg(String msg) {
        String ret = msg.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        ret =  ret.replaceAll("\\[c=#([0-9A-F]*)\\]([^\\[\\/c\\]]*)\\[\\/c\\]", "<span style='color:#$1;'>$2</span>");
        ret =  ret.replaceAll("\\[a=#([^\\]]*)\\]([^\\[\\/a\\]]*)\\[\\/a\\]", "<a href='$1' target='_blank'>$2</a>");
        return ret;
    }

    private long getAttrValue(User user, String attr) {
        long val = 0;
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
