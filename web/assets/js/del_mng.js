var delProfMargin = parseFloat(document.getElementById("del-prof-margin").value);

function loadDeliveryFees() {
    var odrStat = document.getElementById("odr-stat").value;
    var odrDel = document.getElementById("odr-del").value;
    var filterBy = document.getElementById("filter-by").value;
    var sel = document.getElementById("odr-by");
    var odrBy = sel.options[sel.selectedIndex].text;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            document.getElementById("odr-mng-tbody").innerHTML = req.responseText;
            document.getElementById("odr-count").innerHTML = document.getElementById("odr-mng-table").rows.length - 1;
        }
    }
    req.open("GET", "get_orders.jsp?odrStat=" + odrStat + "&odrDel=" + odrDel + "&odrBy=" + odrBy + "&filterBy=" + filterBy, true);
    req.send();
}

function enableDelFeeBtn(distID) {
    var btn = document.getElementById("del-fee-btn-" + distID);
    btn.disabled = false;
    btn.innerHTML = "<i class=\"fas fa-check-circle\"></i>Apply";
}

function calcDelFee(distID) {
    var cost = parseFloat(document.getElementById("del-cost-" + distID).value);
    if (isNaN(cost)) {
        cost = 0;
    }
    document.getElementById("del-fee-" + distID).value = cost / (1 - (delProfMargin / 100));
}

function calcDelCost(distID) {
    var fee = parseFloat(document.getElementById("del-fee-" + distID).value);
    if (isNaN(fee)) {
        fee = 0;
    }
    document.getElementById("del-cost-" + distID).value = fee - (fee * delProfMargin / 100);
}

function saveDelFee(distID) {
    var btn = document.getElementById("del-fee-btn-" + distID);
    btn.disabled = true;
    btn.innerHTML = "<i class=\"fas fa-check-circle\"></i>Saving...";
    var fee = document.getElementById("del-fee-" + distID).value;
    if (isNaN(fee) || fee == "" || fee < 0) {
        showMsg("Invalid Amount !", "Delivery fee is invalid.");
        enableDelFeeBtn(distID);
    } else {
        var deliver = document.getElementById("del-chk-" + distID).checked;
        var req = new XMLHttpRequest();
        req.onreadystatechange = function () {
            if (req.readyState == 4 && req.status == 200) {
                var resp = req.responseText;
                if (resp == "ok") {
                    btn.innerHTML = "<i class=\"fas fa-check-circle\"></i>Saved !";
                } else if (resp == "err") {
                    showMsg("Somthing went wrong !", "Couldn't update the Delivery Fee.");
                    enableDelFeeBtn(distID);
                }
            }
        }
        req.open("GET", "UpdateDeliveyFee?distID=" + distID + "&fee=" + fee + "&deliver=" + deliver, true);
        req.send();
    }
}

function enableDelProfitBtn() {
    var btn = document.getElementById("del-prof-margin-btn");
    btn.disabled = false;
    btn.innerHTML = "<i class=\"fas fa-check-circle\"></i>Apply";
}

function saveDelProfitMargin() {
    var btn = document.getElementById("del-prof-margin-btn");
    btn.disabled = true;
    btn.innerHTML = "<i class=\"fas fa-check-circle\"></i>Saving...";
    var profit = document.getElementById("del-prof-margin").value;
    if (isNaN(profit) || profit == "" || profit <= 0) {
        showMsg("Invalid Amount !", "Please Enter a value between 1 and 100.");
        enableDelProfitBtn();
    } else {
        var req = new XMLHttpRequest();
        req.onreadystatechange = function () {
            if (req.readyState == 4 && req.status == 200) {
                var resp = req.responseText;
                if (resp == "ok") {
                    window.location = "del_mng.jsp";
                } else if (resp == "err") {
                    showMsg("Somthing went wrong !", "Couldn't update the Delivery Profit Margin.");
                    enableDelProfitBtn();
                }
            }
        }
        req.open("GET", "UpdateDeliveyProfit?profit=" + profit, true);
        req.send();
    }
}

function enableDelFulBtn(prefix) {
    var btn = document.getElementById(prefix + "-btn");
    btn.disabled = false;
    btn.innerHTML = "<i class=\"fas fa-check-circle\"></i>Apply";
}

function saveDelFulfillment() {
    var btn1 = document.getElementById("free-time-btn");
    var btn2 = document.getElementById("exp-time-btn");
    btn1.disabled = true;
    btn1.innerHTML = "<i class=\"fas fa-check-circle\"></i>Saving...";
    btn2.disabled = true;
    btn2.innerHTML = "<i class=\"fas fa-check-circle\"></i>Saving...";
    var freeTimeUnit = document.getElementById("free-time-unit").value;
    var expTimeUnit = document.getElementById("exp-time-unit").value;
    var freeTime = document.getElementById("free-time").value;
    var expTime = document.getElementById("exp-time").value;
    if (isNaN(freeTime) || freeTime == "" || freeTime <= 0 || isNaN(expTime) || expTime == "" || expTime <= 0) {
        showMsg("Invalid Amount !", "Amount(s) you entered are invaild.");
        btn1.disabled = false;
        btn2.disabled = false;
        btn1.innerHTML = "<i class=\"fas fa-check-circle\"></i>Apply";
        btn2.innerHTML = "<i class=\"fas fa-check-circle\"></i>Apply";
    } else {
        freeTime = parseInt(freeTime);
        expTime = parseInt(expTime);
        document.getElementById("free-time").value = freeTime;
        document.getElementById("exp-time").value = expTime;
        var req = new XMLHttpRequest();
        req.onreadystatechange = function () {
            if (req.readyState == 4 && req.status == 200) {
                var resp = req.responseText;
                if (resp == "ok") {
                    btn1.innerHTML = "<i class=\"fas fa-check-circle\"></i>Saved !";
                    btn2.innerHTML = "<i class=\"fas fa-check-circle\"></i>Saved !";
                } else if (resp == "err") {
                    showMsg("Somthing went wrong !", "Couldn't update the Delivery Profit Margin.");
                    btn1.disabled = false;
                    btn2.disabled = false;
                    btn1.innerHTML = "<i class=\"fas fa-check-circle\"></i>Apply";
                    btn2.innerHTML = "<i class=\"fas fa-check-circle\"></i>Apply";
                }
            }
        }
        req.open("GET", "UpdateDeliveyTime?freeTime=" + freeTime + "&freeTimeUnit=" + freeTimeUnit + "&expTime=" + expTime + "&expTimeUnit=" + expTimeUnit, true);
        req.send();
    }
}