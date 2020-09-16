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
        var out = "<div class='char'><table>" +
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
         out += "<button id='send' type='button' disabled>send</button></div>";
         out += "</div></div>";
         out += "<div class='logout'>logout</div>";
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
                    for (var i = 0; i < data.length; i++) {
                        $(".messages").append(data[i].msg).append("<br/>");
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
               $("#send").click(sendMessage);
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
                  url: '/dsa/chat/send?name='+inst.user.name+"&msg="+ encodeURIComponent(value),
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