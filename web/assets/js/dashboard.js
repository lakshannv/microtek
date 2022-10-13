function suspendApp() {
    if (document.getElementById("sus-time").value == "") {
        document.getElementById("sus-time").value = 1;
    } else {
        document.getElementById("sus-time").value = document.getElementById("sus-time").value.split(".")[0];
        if (document.getElementById("sus-time").value == 0) {
            document.getElementById("sus-time").value = 1;
        }
    }
    var time = document.getElementById("sus-time").value;
    var sel = document.getElementById("sus-sel");
    var multiplier = sel.value;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                if (multiplier == 0) {
                    showMsg("Suspended", "Application has been suspended indefinitely.");
                } else {
                    showMsg("Suspended", "Application has been suspended for " + time + " " + sel.options[sel.selectedIndex].text + ".");
                }
                document.getElementById("sus-res-btn").disabled = false;
            } else {
                showMsg("Error", "Couldn't Suspend the Application.");
            }
        }
    }
    req.open("GET", "SuspendApp?time=" + time + "&multiplier=" + multiplier, true);
    req.send();
}

function resumeApp() {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                document.getElementById("sus-res-btn").disabled = true;
                showMsg("Resumed !", "MicroTek is now up and running...");
            } else {
                showMsg("Error", "Couldn't Resume the Application.");
            }
        }
    }
    req.open("GET", "ResumeApp", true);
    req.send();
}

function checkSuspendDuration() {
    if (document.getElementById("sus-sel").value == 0) {
        document.getElementById("sus-time").disabled = true;
        document.getElementById("sus-time").value = 0;
    } else {
        document.getElementById("sus-time").disabled = false;
    }
}

function updateNotice() {
    var notice = document.getElementById("notice-text").value;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            var resp = req.responseText;
            var status = resp.substring(0, 2);
            if (status == "ok") {
                showMsg("Success !", "Notice Updated !");
            } else {
                showMsg("Error !", "Couldn't update the notice.");
            }
        }
    };
    req.open("POST", "UpdateNotice", true);
    req.setRequestHeader("Content-type", "application/x-www-form-urlencoded-");
    req.send("notice=" + notice);
}

function updateSMTPDetails() {
    var host = document.getElementById("host").value;
    var port = document.getElementById("port").value;
    var sender = document.getElementById("sender").value;
    var pw = document.getElementById("pw").value;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                showMsg("Success !", "SMTP details updated !");
            } else if (resp == "emp") {
                showMsg("Empty Fields Detected !", "Please fill all the smtp details.");
            } else if (resp == "eml") {
                showMsg("Invalid Email !", "Please Select a valid email.");
            } else {
                showMsg("Error !", "Couldn't update SMTP details.");
            }
        }
    };
    req.open("POST", "UpdateSMTPDetails", true);
    req.setRequestHeader("Content-type", "application/x-www-form-urlencoded-");
    req.send("host=" + host + "&port=" + port + "&sender=" + sender + "&pw=" + pw);
}

function updatePGDetails() {
    var merchant_id = document.getElementById("merchant_id").value;
    var merchant_secret = document.getElementById("merchant_secret").value;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                showMsg("Success !", "Payment Gateway details Updated !");
            } else if (resp == "emp") {
                showMsg("Empty Fields Detected !", "Please fill all the Payment Gateway details.");
            } else {
                showMsg("Error !", "Couldn't update Payment Gateway Details.");
            }
        }
    };
    req.open("POST", "UpdatePGDetails", true);
    req.setRequestHeader("Content-type", "application/x-www-form-urlencoded-");
    req.send("merchant_id=" + merchant_id + "&merchant_secret=" + merchant_secret);
}