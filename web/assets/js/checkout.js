var freeDelWithin = 0;
var expDelWithin = 0;
var freeDelSt = 0;

function loadFulfillmentDates() {
    var freeCell = document.getElementById("free-del-cell");
    var expCell = document.getElementById("exp-del-cell");

    freeDelWithin = parseInt(freeCell.getAttribute("within")) * getTimeUnitMultiplier(freeCell.getAttribute("timeUnit"));
    expDelWithin = parseInt(expCell.getAttribute("within")) * getTimeUnitMultiplier(expCell.getAttribute("timeUnit"));
    if (freeDelWithin > 7) {
        freeDelSt = freeDelWithin - 7;
    }
}

function getTimeUnitMultiplier(timeUnit) {
    switch (timeUnit) {
        case "Days":
            return 1;
        case "Weeks":
            return 7;
        case "Months":
            return 30;
    }
}

function observeCart() {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            document.getElementById("cart-popup").innerHTML = req.responseText;
            document.getElementById("cart-count").innerHTML = document.getElementById("crt-count").textContent;
            var mo = new MutationObserver(loadOrderItems);
            var moConfig = {attributes: true, childList: true, subtree: true};
            mo.observe(document.getElementById("cart-total"), moConfig);
        }
    };
    req.open("GET", "get_cart.jsp", true);
    req.send();
}

function loadAddresses(isAsync) {
    var async = true;
    if (isAsync != null) {
        async = isAsync;
    }
    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            document.getElementById("addr-cmb").innerHTML = req.responseText;
            loadBillingAddresses();
        }
    };
    req.open("GET", "get_addresses.jsp", async);
    req.send();
}

function loadAddressDetails() {
    var cmb = document.getElementById("addr-cmb");

    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var addrData = JSON.parse(req.responseText);
            document.getElementById("addr-det").innerHTML = addrData["province"] + " Province, " + addrData["district"] + ", " + addrData["city"] + " (Post Code - " + addrData["postCode"] + ")";
            document.getElementById("del-fee").innerHTML = addrData["fee"];
            document.getElementById("del-addr").innerHTML = addrData["name"] + "<br>" + addrData["city"] + " [" + addrData["postCode"] + "]" + "<br>" + addrData["district"] + "<br>" + addrData["province"] + " Province<br>";
            loadOrderItems();
        }
    };
    req.open("GET", "LoadAddrDetails?id=" + cmb.value, true);
    req.send();
}

function setBillingAddress() {
    var cmb = document.getElementById("bill-addr-cmb");

    if (cmb.value == 0) {
        document.getElementById("bill-addr").innerHTML = "Same As Shipping Address";
    } else {
        var req = new XMLHttpRequest();
        req.onreadystatechange = function () {
            if (req.readyState == 4 && req.status == 200) {
                var addrData = JSON.parse(req.responseText);
                document.getElementById("bill-addr").innerHTML = addrData["name"] + "<br>" + addrData["city"] + " [" + addrData["postCode"] + "]" + "<br>" + addrData["district"] + "<br>" + addrData["province"] + " Province<br>";
            }
        };
        req.open("GET", "LoadAddrDetails?id=" + cmb.value, true);
        req.send();
    }
}

function loadOrderItems() {
    var param = "";
    if (document.getElementById("exp-del").checked) {
        param = "?addrID=" + document.getElementById("addr-cmb").value;
    }
    var stockID = getParameter("stockID");
    if (stockID != null) {
        var qty = getParameter("qty");
        if (param == "") {
            param += "?stockID=" + stockID + "&qty=" + qty;
        } else {
            param += "&stockID=" + stockID + "&qty=" + qty;
        }
    }

    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            document.getElementById("order-items").innerHTML = req.responseText;
            document.getElementById("order-tot").innerHTML = "Rs. " + document.getElementById("net-tot").textContent;
        }
    };
    req.open("GET", "get_order_items.jsp" + param, true);
    req.send();
}

function getNth(d) {
    if (d > 3 && d < 21)
        return '<sup>th</sup>';
    switch (d % 10) {
        case 1:
            return '<sup>st</sup>';
        case 2:
            return '<sup>nd</sup>';
        case 3:
            return '<sup>rd</sup>';
        default:
            return '<sup>th</sup>';
    }
}

function loadDeliveryDates() {
    var d = new Date();

    if (document.getElementById("exp-del").checked) {
        document.getElementById("del-method").innerHTML = "Expedited Delivery";

        d.setDate(d.getDate() + expDelWithin);
        document.getElementById("del-method-desc").innerHTML = "Expect fulfillment before<br>" + d.toLocaleString('default', {weekday: 'long'}) + " - " + d.getDate() + getNth(d.getDate()) + " " + d.toLocaleString('default', {month: 'long'}) + " " + d.getFullYear();
    } else {
        document.getElementById("del-method").innerHTML = "Free Delivery";
        var d1 = new Date();
        var d2 = new Date();
        d1.setDate(d.getDate() + freeDelSt);
        d2.setDate(d.getDate() + freeDelWithin);
        document.getElementById("del-method-desc").innerHTML = "Expect fulfillment between<br>" + d1.toLocaleString('default', {weekday: 'long'}) + " - " + d1.getDate() + getNth(d1.getDate()) + " " + d1.toLocaleString('default', {month: 'long'}) + " " + d1.getFullYear() + " and<br>" + d2.toLocaleString('default', {weekday: 'long'}) + " - " + d2.getDate() + getNth(d2.getDate()) + " " + d2.toLocaleString('default', {month: 'long'}) + " " + d2.getFullYear();
    }
    loadOrderItems();
}

function loadBillingAddresses() {
    var billCmb = document.getElementById("bill-addr-cmb");
    var shipCmb = document.getElementById("addr-cmb");
    billCmb.innerHTML = '<option value="0" selected="">[ Same as Shipping Address ]</option>' + shipCmb.innerHTML;
    billCmb.remove(shipCmb.selectedIndex + 1);
    setBillingAddress();
}

function addAddress() {
    hideAllToolTips();
    var addr = document.getElementById("addr").value.trim();
    var provinceID = document.getElementById("province").value;
    var districtID = document.getElementById("district").value;
    var cityID = document.getElementById("city").value;

    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            resp = req.responseText;
            if (resp == "ok") {
                loadAddresses(false);
                loadAddressDetails();
                closeModal("addr-popup-bg");
                document.getElementById("addr").value = "";
            } else if (resp == "inv") {
                showToolTip("addr-tt", "Invalid Address");
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't add the address.");
            }
        }
    };
    req.open("GET", "AddNewAddress?addr=" + encodeURIComponent(addr) + "&provinceID=" + provinceID + "&districtID=" + districtID + "&cityID=" + cityID, true);
    req.send();
}

var invID;
payhere.onCompleted = function onCompleted(orderId) {
    window.location = "order.jsp?orderID=" + invID;
};

payhere.onDismissed = function onDismissed() {
    window.location = "order.jsp?orderID=" + invID;
};

payhere.onError = function onError(error) {
    window.location = "order.jsp?orderID=" + invID;
};


function checkOut() {
    var param = "?addrID=" + document.getElementById("addr-cmb").value + "&billingAddrID=" + document.getElementById("bill-addr-cmb").value;
    if (document.getElementById("exp-del").checked) {
        param += "&delMethod=exp";
    }
    var stockID = getParameter("stockID");
    if (stockID != null) {
        var qty = getParameter("qty");
        param += "&stockID=" + stockID + "&qty=" + qty;
    }

    var reqChk = new XMLHttpRequest();
    reqChk.onreadystatechange = function () {
        if (reqChk.readyState == 4 && reqChk.status == 200) {
            var resp = reqChk.responseText;
            if (resp == "ok") {
                var req = new XMLHttpRequest();
                req.onreadystatechange = function () {
                    if (req.readyState == 4 && req.status == 200) {
                        var payment = JSON.parse(req.responseText);
                        invID = payment["order_id"];
                        payhere.startPayment(payment);
                    }
                };
                req.open("GET", "CheckOut" + param, true);
                req.send();
            } else if (resp.substring(0, 3) == "del") {
                showMsg("Out of reach !", "Sorry, We don't deliver to " + resp.substring(3) + " yet. Please choose another district.");
            } else if (resp == "inv") {
                showMsg("Obsolete Order !", "This order has no active items.");
            } else if (resp == "err") {
                showMsg("An error Occured !", "Couldn't verify the order.");
            }
        }
    };
    reqChk.open("GET", "CheckOrder" + param, true);
    reqChk.send();
}

function resumeCheckout(orderID) {
    var reqChk = new XMLHttpRequest();
    reqChk.onreadystatechange = function () {
        if (reqChk.readyState == 4 && reqChk.status == 200) {
            var resp = reqChk.responseText;
            if (resp == "ok") {
                var req = new XMLHttpRequest();
                req.onreadystatechange = function () {
                    if (req.readyState == 4 && req.status == 200) {
                        var payment = JSON.parse(req.responseText);
                        invID = orderID;
                        payhere.startPayment(payment);
                    }
                };
                req.open("GET", "CheckOut?orderID=" + orderID, true);
                req.send();
            } else if (resp == "modok") {
                document.getElementById("msg-ok-btn").setAttribute("onclick", "goToURL('order.jsp?orderID=" + orderID + "')");
                document.getElementById("msg-close-btn").setAttribute("onclick", "goToURL('order.jsp?orderID=" + orderID + "')");
                showMsg("Unavailable items detected !", "Some item(s) in this order are unavailable right now.<br>Please review the available items before Checking out.");
            } else if (resp == "modinv" || resp == "inv") {
                document.getElementById("msg-ok-btn").setAttribute("onclick", "discardOrder(" + orderID + ");");
                document.getElementById("msg-close-btn").setAttribute("onclick", "discardOrder(" + orderID + ");");
                showMsg("Unavailable items detected !", "None of the item(s) in this order are available right now.<br>Since this order in now obsolete, it will be deleted.");
            } else if (resp.substring(0, 3) == "del") {
                document.getElementById("msg-ok-btn").setAttribute("onclick", "discardOrder(" + orderID + ");");
                document.getElementById("msg-close-btn").setAttribute("onclick", "discardOrder(" + orderID + ");");
                showMsg("Out of reach !", "Sorry, We don't deliver to " + resp.substring(3) + " now.<br>Since this order in now obsolete, it will be deleted..");
            } else if (resp == "err") {
                showMsg("An error Occured !", "Couldn't verify the order.");
            }
        }
    };
    reqChk.open("GET", "CheckOrder?orderID=" + orderID, true);
    reqChk.send();
}

function discardOrder(orderID) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            window.location = "pending_orders.jsp";
        }
    };
    req.open("GET", "DismissOrder?orderID=" + orderID, true);
    req.send();
}

function generateInvoice(orderID) {
    window.open("ViewInvoice?orderID=" + orderID, "_blank", "scrollbars=yes,resizable=yes,location=no,menubar=no");
}