function loadOrders() {
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

function setHeading() {
    var h = document.getElementById("odr-cat");
    var sel = document.getElementById("odr-stat");
    var delSel = document.getElementById("odr-del");
    var del = "";
    if (delSel.value != "") {
        del = " " + delSel.options[delSel.selectedIndex].text;
    }
    h.innerHTML = sel.options[sel.selectedIndex].text + del;
}

function enableBtn(invID) {
    var btn = document.getElementById("btn-" + invID);
    btn.disabled = false;
    btn.innerHTML = "<i class=\"fas fa-envelope\"></i>Save & Send E-mail";
}

function saveStatus(invID) {
    var btn = document.getElementById("btn-" + invID);
    btn.disabled = true;
    btn.innerHTML = "<i class=\"fas fa-envelope\"></i>Saving & Sending...";
    var odrStat = document.getElementById("cmb-" + invID).value;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                btn.innerHTML = "<i class=\"fa fa-check-circle\"></i>Saved & Sent !";
            } else if (resp == "err") {
                showMsg("Somthing went wrong !", "Couldn't update the order status.");
                enableBtn(invID);
            }
        }
    }
    req.open("GET", "UpdateOrderStatus?invID=" + invID + "&odrStat=" + odrStat, true);
    req.send();
}