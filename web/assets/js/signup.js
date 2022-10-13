function setIMGHeight() {
    var w = document.getElementById("s").offsetWidth;
    document.getElementById("s").style.height = w + "px";
}

function signUp() {
    hideAllToolTips();
    var fn = document.getElementById("fn").value.trim();
    var ln = document.getElementById("ln").value.trim();
    var mob = document.getElementById("mob").value.trim();
    var eml = document.getElementById("eml").value.trim();
    var addr = document.getElementById("addr").value.trim();
    var provinceID = document.getElementById("province").value;
    var districtID = document.getElementById("district").value;
    var cityID = document.getElementById("city").value;
    var un = document.getElementById("un").value;
    var pw = document.getElementById("pw").value;
    var pwCon = document.getElementById("pw-con").value;

    var formData = new FormData();
    formData.append("fn", fn);
    formData.append("ln", ln);
    formData.append("mob", mob);
    formData.append("eml", eml);
    formData.append("addr", addr);
    formData.append("provinceID", provinceID);
    formData.append("districtID", districtID);
    formData.append("cityID", cityID);
    formData.append("un", un);
    formData.append("pw", pw);
    formData.append("pwCon", pwCon);

    var f = document.getElementById("f");
    if (f.files.length != 0) {
        formData.append("f", f.files[0]);
    }

    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                var postAction = getParameter("postAction");
                var subParamData = getParameter("params");
                var dest = "login.jsp";
                if (postAction != null) {
                    dest += "?postAction=" + postAction;
                    if (subParamData != null) {
                        dest += "&params=" + subParamData;
                    }
                }
                window.location = dest;
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't sign you up.");
            } else {
                var msg = "Please Check the following:";
                var errList = JSON.parse(resp);
                for (var i = 0; i < errList.length; i++) {
                    var err = errList[i];
                    if (err == "fn") {
                        showToolTip("fn-tt", "Invalid Name");
                        msg = msg + "<br><br> - The name you've set as Frist name is not vaild.";
                    }
                    if (err == "ln") {
                        showToolTip("ln-tt", "Invalid Name");
                        msg = msg + "<br><br> - The name you've set as Last name is not vaild.";
                    }
                    if (err == "mob") {
                        showToolTip("mob-tt", "Invalid Mobile");
                        msg = msg + "<br><br> - The mobile number you've set is not vaild.";
                    }
                    if (err == "eml") {
                        showToolTip("eml-tt", "Invalid e-mail");
                        msg = msg + "<br><br> - The e-mail number you've set is not vaild.";
                    }
                    if (err == "eml-dup") {
                        showToolTip("eml-tt", "Already Exists");
                        msg = msg + "<br><br> - The email you've set belongs to another account.";
                    }
                    if (err == "addr") {
                        showToolTip("addr-tt", "Invalid Address");
                        msg = msg + "<br><br> - The address you've set is not vaild.";
                    }
                    if (err == "un") {
                        showToolTip("un-tt", "Invalid UserName");
                        msg = msg + "<br><br> - The user name you've set is not vaild.";
                    }
                    if (err == "un-dup") {
                        showToolTip("un-tt", "Already exists");
                        msg = msg + "<br><br> - The UserName you've set already exists.<br>Try a different username.";
                    }
                    if (err == "pw") {
                        showToolTip("pw-tt", "Empty Password");
                        msg = msg + "<br><br> - The password cannot be empty.";
                    }
                    if (err == "pwcon") {
                        showToolTip("pw-con-tt", "Contirm Password");
                        msg = msg + "<br><br> - You have to confirm the password you've set.";
                    }
                    if (err == "pwmis") {
                        showToolTip("pw-con-tt", "Passwords do not match");
                        msg = msg + "<br><br> - Passwords do not match.";
                    }
                }
                showMsg("Check Again !", msg);
            }

        }
    };
    req.open("POST", "AddNewCustomer", true);
    req.send(formData);
}

function goToLogin() {
    var postAction = getParameter("postAction");
    var subParamData = getParameter("params");
    var dest = "login.jsp";
    if (postAction != null) {
        dest += "?postAction=" + postAction;
        if (subParamData != null) {
            dest += "&params=" + subParamData;
        }
    }
    window.location = dest;
}