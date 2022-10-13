function searchProducts() {
    var keyWord = document.getElementById("search-box").value;
    goToURL("search.jsp?keyWord=" + keyWord);
}

var tOutID;
function addDisToCart(stockID) {
    if (tmID != null) {
        clearTimeout(tmID);
    }
    hideToolTip("wish-tt");
    if (tOutID != null) {
        clearTimeout(tOutID);
    }
    hideToolTip("cart-tt");
    var qty = document.getElementById("val-dis-num-" + stockID).textContent;

    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            var resp = req.responseText;
            var status = resp.substring(0, 2);
            if (status == "ok") {
                showToolTip("cart-tt", resp.substring(3));
                tOutID = setTimeout(hideToolTip, 5000, "cart-tt");
                loadCart();
            } else if (resp == "cook") {
                showMsg("Your cookies are blocked !", "Please unblock your cookies to use MicroTek.");
            } else {
                showMsg("Error !", resp);
            }
        }
    };
    req.open("GET", "AddToCart?stockID=" + stockID + "&qty=" + qty, true);
    req.send();
}

function buyDisNow(stockID) {
    var qty = document.getElementById("val-dis-num-" + stockID).textContent;
    window.location = "checkout.jsp?stockID=" + stockID + "&qty=" + qty;
}

function triggerSearch() {
    if (event.keyCode == 13) {
        searchProducts();
    }
}