function login() {
    var postAction = getParameter("postAction");
    var subParamData = getParameter("params");
    var dest = "AdminRedirect";
    if (postAction != null) {
        dest = postAction;
        if (subParamData != null) {
            var subParams = "?";
            var paramData = JSON.parse(subParamData);
            for (let key in paramData) {
                subParams += key + "=" + paramData[key][0] + "&";
            }
            if (Object.keys(paramData).length > 0) {
                subParams = subParams.slice(0, -1);
                dest += subParams;
            }
        }
    }
    hideAllToolTips();
    var un = document.getElementById("un").value.trim();
    var pw = document.getElementById("pw").value;
    var rem = document.getElementById("rem").checked;

    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                window.location = dest;
            } else if (resp == "un") {
                showToolTip("un-tt", "Invalid UserName");
            } else if (resp == "pw") {
                showToolTip("pw-tt", "Incorrect Password");
            } else if (resp == "cook") {
                showMsg("Your cookies are blocked !", "Please unblock your cookies to use MicroTek.");
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't log you in.");
            }
        }
    };
    req.open("POST", "AdminLogin", true);
    req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    req.send("un=" + un + "&pw=" + pw + "&rem=" + rem);
}

function goToSignUp() {
    var postAction = getParameter("postAction");
    var subParamData = getParameter("params");
    var dest = "signup.jsp";
    if (postAction != null) {
        dest += "?postAction=" + postAction;
        if (subParamData != null) {
            dest += "&params=" + subParamData;
        }
    }
    window.location = dest;
}

var emlField = document.getElementById("eml");
var btn = document.getElementById("send-otp-btn");
var sec = 10;
var i;
function reSendCountDown() {
    sec--;
    if (sec == 0) {
        btn.innerHTML = "<i class=\"fas fa-mail-bulk\"></i>Resend";
        btn.disabled = false;
        emlField.disabled = false;
        sec = 10;
        clearInterval(i);
    } else {
        btn.innerHTML = "<i class=\"fas fa-mail-bulk\"></i>Resend in " + sec + " sec.s";
    }

}

function sendOTP() {
    hideAllToolTips();

    var eml = emlField.value.trim();
    btn.disabled = true;
    emlField.disabled = true;
    btn.innerHTML = "<i class=\"fas fa-mail-bulk\"></i>Sending...";
    document.getElementById("otp-div").style.display = "none";
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp.substring(0, 2) == "ok") {
                var usr = resp.substring(3, resp.length);
                document.getElementById("usr-name").innerHTML = usr;
                document.getElementById("usr-name-rs").innerHTML = usr;
                document.getElementById("reset-btn").setAttribute("onclick", "resetPassword('" + usr + "');");
                btn.innerHTML = "<i class=\"fas fa-mail-bulk\"></i>Resend in 10 sec.s";
                i = setInterval(reSendCountDown, 1000);
                document.getElementById("otp-div").style.display = "initial";
            } else if (resp == "emp") {
                showToolTip("eml-tt", "Empty e-mail");
            } else if (resp == "inv") {
                showToolTip("eml-tt", "Invalid e-mail");
                showMsg("Invalid e-mail", "The email you've entered doesn't belong to anyone on MicroTek.");
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't send the OTP.");
            }
            if (resp.substring(0, 2) != "ok") {
                btn.innerHTML = "<i class=\"fas fa-mail-bulk\"></i>Send OTP";
                btn.disabled = false;
                emlField.disabled = false;
                document.getElementById("otp-div").style.display = "none";
            }
        }
    }
    req.open("POST", "SendOTP", true);
    req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    req.send("eml=" + eml);
}

function submitOTP() {
    hideAllToolTips();

    var otpField = document.getElementById("otp");
    var submitBtn = document.getElementById("submit-otp-btn");

    var otp = otpField.value.trim();
    submitBtn.disabled = true;
    otpField.disabled = true;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                closeModal("forgot-popup-bg");
                showModal("reset-popup-bg");
            } else if (resp == "emp") {
                showToolTip("otp-tt", "Empty OTP");
            } else if (resp == "inv") {
                showToolTip("otp-tt", "Invalid OTP");
                showMsg("Invalid OTP", "The OTP you've entered is either incorrect or invalid.");
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't Verify the OTP.");
            }
            if (resp != "ok") {
                submitBtn.disabled = false;
                otpField.disabled = false;
            }
        }
    }
    req.open("POST", "CheckOTP", true);
    req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    req.send("otp=" + otp);
}

function resetPassword(usr) {
    hideAllToolTips();
    
    var otp = document.getElementById("otp").value.trim();
    var pw = document.getElementById("new-pw").value;
    var pwCon = document.getElementById("pw-con").value;

    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                document.getElementById("otp").value = "";
                document.getElementById("new-pw").value = "";
                document.getElementById("pw-con").value = "";
                closeModal("reset-popup-bg");
                showMsg("Success !", "Password has been reset. Log in to continue.");
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't reset password.");
            } else {
                var msg = "Please Check the following:";
                var errList = JSON.parse(resp);
                for (var i = 0; i < errList.length; i++) {
                    var err = errList[i];
                    if (err == "otp") {
                        showMsg("Something went wrong !", "Coudn't verify the OTP. Try re-sending the OTP again.");
                    }
                    if (err == "inv-pw") {
                        showToolTip("new-pw-tt", "Empty Password");
                    }
                    if (err == "pwmis") {
                        showToolTip("pw-con-tt", "Passwords do not match");
                    }
                }
            }

        }
    };
    req.open("POST", "ResetPassword", true);
    req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    req.send("otp=" + otp + "&usr=" + usr + "&pw=" + pw + "&pwCon=" + pwCon);
}

document.getElementById("un").addEventListener("keydown", function (e) {
    if (e.keyCode == 13) {
        e.preventDefault();
        document.getElementById("pw").focus();
    }
});

document.getElementById("pw").addEventListener("keydown", function (e) {
    if (e.keyCode == 13) {
        e.preventDefault();
        login();
    }
});