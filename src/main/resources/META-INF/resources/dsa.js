$.fn.dsa = function() {

    var settings = {}

    var username;
    var token;

    var user;
    var skills;

    var attributes = ["MU", "KL", "IN", "CH", "FF", "GE", "KO", "KK"];

    var connected = false;
    var socket;


    var blink = {
        Vars:{
            OriginalTitle: document.title,
            Interval: null
        },
        On: function(notification, intervalSpeed){
            clearInterval(this.Vars.Interval);
            var _this = this;
            _this.Vars.Interval = setInterval(function(){
                 if (document.hasFocus()) {
                    blink.Off();
                    return;
                 }
                 document.title = (_this.Vars.OriginalTitle == document.title)
                                     ? notification
                                     : _this.Vars.OriginalTitle;
            }, (intervalSpeed) ? intervalSpeed : 1000);
        },
        Off: function(){
            clearInterval(this.Vars.Interval);
            document.title = this.Vars.OriginalTitle;
        }
    };

    inst = this;
    init = function() {
        inst.addClass("dsa");
        var token = window.sessionStorage.getItem('dsa_token');
        inst.empty();
        if (!token) {
            loginForm();
        } else {
            inst.token = token;
            start();
        }
    }

    loginForm = function() {
        inst.append("<div class='loginform'><div><div><div id='l_username'><input type=text /></div><div id='l_password'><input type=password /></div><button class='login'>Anmelden</button><button class='register'>Registrieren</button></div></div></div>");
        inst.find("BUTTON.login").click(function() {
            var username = inst.find("#l_username INPUT").val();
            var password = inst.find("#l_password INPUT").val();
            console.log(username + " - " + password);
            $.ajax({
                type: "POST",
                url: '/dsa/user/login?name='+username+"&password="+password,
                success: function(data) {
                    inst.token = data.token;
                    inst.username = data.username;
                    window.sessionStorage.setItem("dsa_token", inst.token);
                    init();
                },
                error: function(data) {
                    alert();
                }
            });
        });
        inst.find("BUTTON.register").click(function() {
            var username = inst.find("#l_username INPUT").val();
            var password = inst.find("#l_password INPUT").val();
            console.log(username + " - " + password);
            $.ajax({
                type: "POST",
                url: '/dsa/user/register?name='+username+"&password="+password,
                success: function(data) {
                    inst.find("BUTTON.login").click();
                },
                error: function(data) {
                    alert();
                }
            });
        });
    }

    start = function() {
        $.ajax({
            type: "GET",
            url: '/dsa/user/' + inst.token,
            success: function(data) {
                inst.user = data;
                $.ajax({
                    type: "GET",
                    url: '/dsa/user/' + inst.token +"/skills",
                    success: function(data) {
                        inst.skills = data;
                        fillHTML();
                        addListener();
                        connect();
                    },
                    error: function(data) {
                        errorBack();
                    }
                });
            },
            error: function(data) {
                errorBack();
            }
        });
    }

    fillHTML = function() {
        console.log(inst.user['attr_ff']);
        var out = "<div class='char'>";
        out += "<div class='userinfos'><span class='username'>" + inst.user.username + "</span><span class='button edit'></span><span class='button save'></span></div>";
        out += "<table>" +
                    "<tr class='dsa_color_val'>";
        for(var i = 0; i < attributes.length; i++) {
            out += "<td class='dsa_" + attributes[i].toLowerCase() + "'><span class='dsa_attr'>" + attributes[i] + "</span></td>";
        }
        out += "</tr>";
        out += "<tr class='dsa_attributes'>";
        for(var i = 0; i < attributes.length; i++) {
            out += "<td><input class='dsa_attr_input' type='number' id='dsa_" + attributes[i].toLowerCase() + "' value='" + inst.user['attr_' + attributes[i].toLowerCase()] + "'/>";
        }
        out+=   "</tr>";
        out += "</table>";
        out += "<table class='dsa_attributes'>";
        var categories = "<tr class='skill_headline'><td colspan=4>";
        var cats  = [];
        for(var i = 0; i < inst.skills.length; i++) {
            if (cats.indexOf(inst.skills[i].category) < 0) {
                cats.push(inst.skills[i].category);
            }
        }
        console.log(cats);
        for (var i = 0; i < cats.length; i++) {
            categories += "<span class='skill_subheadline' >" + cats[i] + "</span>";
        }

        categories += "</td></tr>";
         out += categories;

         for(var i = 0; i < inst.skills.length; i++) {
            var e = inst.skills[i];
            out += "<tr class='skill tree_" + e.category + "'><td>" + e.name + ":</td><td><input type='number' id='change_skill_"+e.id+"' value='" + e.value + "' /></td><td>" + e.attributes + "</td><td><img class='skill_dice' id='skill_dice_" + e.id + "' src='d20.png'/></td></tr>";
         }

         out += "</table></div>";
         out += "<div class='chat'><div>";
         out += "<div class='messages'></div>";
         out += "<div class='msg'>";
         out += "<span id='msg' contenteditable='true' placeholder='enter your message'></span>";
         out += "<span id='send' type='button'></span><span class='sendbutton d6'></span><span class='sendbutton d8'></span><span class='sendbutton d10'></span><span class='sendbutton d12'></span><span class='sendbutton d20'></span></div>";
         out += "</div></div>";
         out += "<div class='logout'>logout</div>";
         out += "<div class='users'><div class='userlisthead'>Spieler in diesem Raum</div><div class='userlist'></div></div>";
         inst.append(out);
    }

    addListener = function() {
        inst.find(".dsa_attr").click(function() {
            sendMessage1("==roll_attr-" + $(this).text());
        });
        inst.find(".skill_subheadline").click(function() {
            var name = $(this).text();
            inst.find("TR.skill").css("display", "none");
            inst.find("TR.skill.tree_" + name).css("display", "table-row");
            $(".skill_subheadline").removeClass("active");
            $(this).addClass("active");
            $(this).addClass("active");
        });
        inst.find(".skill_subheadline").first().click();
        inst.find(".skill_dice").click(function() {
            var id = $(this).attr("id").split("_")[2];
            sendMessage1("==roll_skill-" + id);
        });
        inst.find(".logout").click(function() {
            window.sessionStorage.setItem("dsa_token", "");
            window.location.reload();
         });
         inst.find(".skill INPUT").change(function() {
             var id = $(this).attr("id").split("_")[2];
             var val = $(this).val();
             updateSkill(id, val);
         });
         inst.find(".dsa_attr_input").change(function() {
              var id = $(this).attr("id").split("_")[1];
              var val = $(this).val();
              updateAttr(id, val);
          });

        inst.find("INPUT").attr("readonly", "true");
        inst.find(".char .userinfos .edit").click(function(e){
            inst.find("INPUT").removeAttr("readonly");
            inst.find(".char .userinfos .username").attr("contenteditable", "true");
            inst.addClass("editable");
        });
        inst.find(".char .userinfos .save").click(function(e){
            inst.find("INPUT").attr("readonly", "true");
            inst.find(".char .userinfos .username").removeAttr("contenteditable");
            inst.removeClass("editable");
            updateUser();
        });
        inst.find(".msg .sendbutton").click(function(e){
                    var val = 20;
                    val = $(this).attr("class").split(" d")[1];
                    rollDice(val);
                });
        refreshUserList();
        window.setInterval(function() {
            refreshUserList();
        }, 5000)

    }

    refreshUserList = function () {
        $.ajax({
          type: "GET",
          url: '/dsa/user/room/HavenaChronicles',
          success: function(data) {
            var lastuser = "";
            for (var i = 0; i < data.length; i++) {
                var user = data[i];
                var msg = "<span class='user' id='user_" + user.id +"'>" + user.username + "</span>";
                if(inst.find("#user_" + user.id).length == 0) {
                    inst.find(".users .userlist").append(msg);
                }
            }
            inst.find(".users .userlist .user").each(function(){
                var id = $(this).attr("id").split("_")[1];
                var found = false;
                for (var i = 0; i < data.length; i++) {
                    if (id == data[i].id) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    $(this).remove();
                }
            });
          }
       });
    }

    updateUser = function() {
        var username = inst.find(".char .userinfos .username").text();
        $.ajax({
              type: "POST",
              url: '/dsa/user/user/' + inst.token + "?username=" + username,
        });
    }

    rollDice = function(dice) {
            sendMessage1("==roll_d-" + dice);
    }

    updateSkill = function(id, val) {
        $.ajax({
              type: "POST",
              url: '/dsa/user/skill/' + inst.token + "?skill=" + id + "&value=" + val,
        });
    }

    updateAttr = function(id, val) {
        $.ajax({
              type: "POST",
              url: '/dsa/user/attr/' + inst.token + "?attr=" + id + "&value=" + val,
        });
    }

    errorBack = function() {
        window.sessionStorage.removeItem('dsa_token');
        init();
    }

    connect = function() {
       if (! connected) {
           var name = $("#name").val();
           console.log("Val: " + name);
           socket = new WebSocket("ws://" + location.host + "/chat/" + inst.user.name);
           socket.onopen = function() {
               connected = true;
               $.ajax({
                  type: "GET",
                  url: '/dsa/chat/room/HavenaChronicles',
                  success: function(data) {
                    var lastuser = "";
                    for (var i = 0; i < data.length; i++) {
                        var msg = data[i].msg;
                        if (msg && msg.includes("<span class='username'>" + lastuser + "</span>")) {
                            msg = msg.replace("<span class='username'>" + lastuser + "</span>", "");
                        }else {
                            lastuser = msg.split("</span>")[0].split(">")[1];
                        }
                        $(".messages").append(msg).append("<br/>");
                    }
                    scrollToBottom();
                  }
               });
               console.log("Connected to the web socket");
               $("#send").attr("disabled", false);
               $("#msg").keypress(function(event) {
                 if(event.keyCode == 13 || event.which == 13) {
                    sendMessage2();
                 }
               });
               $("#send").click(sendMessage2);
           };
           socket.onmessage =function(m) {
               console.log("Got message: " + m.data);
               var lastname = $(".chat .messages .username").last().text();
               $(".messages").append(m.data + "<br>");
               $(".chat .messages .username").text();
               var newname = $(".chat .messages .username").last().text();
               if (lastname === newname) {
                $(".chat .messages .username").last().remove();
               }
               scrollToBottom();
               blink.On("New Message");
           };
       }
   };

    scrollToBottom = function() {
        var d = inst.find(".chat .messages");
        d.scrollTop(d.prop("scrollHeight"));
    }
    sendMessage1 = function(value) {
          if (connected) {
              console.log("Sending " + value);
              $.ajax({
                  type: "POST",
                  url: '/dsa/chat/send?token='+inst.token+"&msg="+ encodeURIComponent(value),
              });
          }
      };

   sendMessage2 = function() {
          if (connected) {
              var value = $("#msg").text();
              sendMessage1(value);
              $("#msg").text("");
          }
      };

    init();
}