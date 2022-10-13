$(window).scroll(function () {
    $('nav').toggleClass('scrolled', $(this).scrollTop() > 50);
});

$(".field-tooltip").click(function (event) {
    hideToolTip(event.target.id);
});

function closeModal(mbg) {
    document.getElementById(mbg).style.display = "none";
}

function closeModalBG(mbg) {
    var d = document.getElementById(mbg);
    if (event.target == d) {
        d.style.display = "none";
    }
}

function showModal(mbg) {
    hideAllToolTips();
    var d = document.getElementById(mbg);
    if (d.style.display == "none") {
        d.style.display = "flex";
    } else {
        d.style.display = "none";
    }
    closeNav();
}

function closeCart() {
    document.getElementById("cart-popup-bg").style.display = "none";
}

function closeWishlist() {
    document.getElementById("wish-popup-bg").style.display = "none";
}

function closeCartBG() {
    var d = document.getElementById("cart-popup-bg");
    if (event.target == d) {
        d.style.display = "none";
    }
}

function closeWishlistBG() {
    var d = document.getElementById("wish-popup-bg");
    if (event.target == d) {
        d.style.display = "none";
    }
}

function closeMsg() {
    document.getElementById("msg-popup-bg").style.display = "none";
}

function showCart() {
    closeWishlist();
    var d = document.getElementById("cart-popup-bg");
    if (d.style.display == "none") {
        d.style.display = "flex";
    } else {
        d.style.display = "none";
    }
    closeNav();
}

function showWishlist() {
    closeCart();
    var d = document.getElementById("wish-popup-bg");
    if (d.style.display == "none") {
        d.style.display = "flex";
    } else {
        d.style.display = "none";
    }
    closeNav();
}

function showMsg(title, content) {
    document.getElementById("msg-title").innerHTML = title;
    document.getElementById("msg-body").innerHTML = content;
    document.getElementById("msg-popup-bg").style.display = "flex";
}

function showToolTip(ttid, content) {
    var tt = document.getElementById(ttid);
    tt.innerHTML = content;
    tt.style.display = "initial";
}

function hideToolTip(ttid) {
    var tt = document.getElementById(ttid);
    if (tt != null) {
        tt.style.removeProperty('display');
    }
}

function hideAllToolTips() {
    var ttList = document.getElementsByClassName("field-tooltip");
    for (var i = 0; i < ttList.length; i++) {
        ttList[i].style.removeProperty('display');
    }
}

function closeNav() {
    var nav = document.getElementById("navcol-1");
    if (nav != null) {
        nav.className = "collapse navbar-collapse";
    }
}

function setRating(i) {
    var empty = "far fa-star";
    var full = "fas fa-star";
    switch (i) {
        case 0:
            document.getElementById("st1").className = empty;
            document.getElementById("st2").className = empty;
            document.getElementById("st3").className = empty;
            document.getElementById("st4").className = empty;
            document.getElementById("st5").className = empty;
            break;
        case 1:
            document.getElementById("st1").className = full;
            document.getElementById("st2").className = empty;
            document.getElementById("st3").className = empty;
            document.getElementById("st4").className = empty;
            document.getElementById("st5").className = empty;
            break;
        case 2:
            document.getElementById("st1").className = full;
            document.getElementById("st2").className = full;
            document.getElementById("st3").className = empty;
            document.getElementById("st4").className = empty;
            document.getElementById("st5").className = empty;
            break;
        case 3:
            document.getElementById("st1").className = full;
            document.getElementById("st2").className = full;
            document.getElementById("st3").className = full;
            document.getElementById("st4").className = empty;
            document.getElementById("st5").className = empty;
            break;
        case 4:
            document.getElementById("st1").className = full;
            document.getElementById("st2").className = full;
            document.getElementById("st3").className = full;
            document.getElementById("st4").className = full;
            document.getElementById("st5").className = empty;
            break;
        case 5:
            document.getElementById("st1").className = full;
            document.getElementById("st2").className = full;
            document.getElementById("st3").className = full;
            document.getElementById("st4").className = full;
            document.getElementById("st5").className = full;
            break;
    }
    setCustRating();
}

var custRating = 0;
function setCustRating() {
    custRating = 0;
    var full = "fas fa-star";
    if (document.getElementById("st5").className == full) {
        custRating = 5;
    } else if (document.getElementById("st4").className == full) {
        custRating = 4;
    } else if (document.getElementById("st3").className == full) {
        custRating = 3;
    } else if (document.getElementById("st2").className == full) {
        custRating = 2;
    } else if (document.getElementById("st1").className == full) {
        custRating = 1;
    } else {
        custRating = 0;
    }
}

function starHover(i) {
    var col = "rgba(255,255,255,0.7)";
    switch (i) {
        case 1:
            document.getElementById("st1").style.color = col;
            break;
        case 2:
            document.getElementById("st1").style.color = col;
            document.getElementById("st2").style.color = col;
            break;
        case 3:
            document.getElementById("st1").style.color = col;
            document.getElementById("st2").style.color = col;
            document.getElementById("st3").style.color = col;
            break;
        case 4:
            document.getElementById("st1").style.color = col;
            document.getElementById("st2").style.color = col;
            document.getElementById("st3").style.color = col;
            document.getElementById("st4").style.color = col;
            break;
        case 5:
            document.getElementById("st1").style.color = col;
            document.getElementById("st2").style.color = col;
            document.getElementById("st3").style.color = col;
            document.getElementById("st4").style.color = col;
            document.getElementById("st5").style.color = col;
            break;
    }

    var empty = "far fa-star";
    var full = "fas fa-star";
    switch (i) {
        case 0:
            document.getElementById("st1").className = empty;
            document.getElementById("st2").className = empty;
            document.getElementById("st3").className = empty;
            document.getElementById("st4").className = empty;
            document.getElementById("st5").className = empty;
            break;
        case 1:
            document.getElementById("st1").className = full;
            document.getElementById("st2").className = empty;
            document.getElementById("st3").className = empty;
            document.getElementById("st4").className = empty;
            document.getElementById("st5").className = empty;
            break;
        case 2:
            document.getElementById("st1").className = full;
            document.getElementById("st2").className = full;
            document.getElementById("st3").className = empty;
            document.getElementById("st4").className = empty;
            document.getElementById("st5").className = empty;
            break;
        case 3:
            document.getElementById("st1").className = full;
            document.getElementById("st2").className = full;
            document.getElementById("st3").className = full;
            document.getElementById("st4").className = empty;
            document.getElementById("st5").className = empty;
            break;
        case 4:
            document.getElementById("st1").className = full;
            document.getElementById("st2").className = full;
            document.getElementById("st3").className = full;
            document.getElementById("st4").className = full;
            document.getElementById("st5").className = empty;
            break;
        case 5:
            document.getElementById("st1").className = full;
            document.getElementById("st2").className = full;
            document.getElementById("st3").className = full;
            document.getElementById("st4").className = full;
            document.getElementById("st5").className = full;
            break;
    }
}

function starOut() {
    setRating(custRating);
    document.getElementById("st1").style.removeProperty('color');
    document.getElementById("st2").style.removeProperty('color');
    document.getElementById("st3").style.removeProperty('color');
    document.getElementById("st4").style.removeProperty('color');
    document.getElementById("st5").style.removeProperty('color');
}

function toggleResult(res) {
    var h = document.getElementById(res);
    if (h.style.borderRadius == "") {
        h.style.borderRadius = "0 25px";
        h.getElementsByTagName("i")[0].className = "fa fa-chevron-circle-down";
    } else {
        h.style.removeProperty('border-radius');
        h.getElementsByTagName("i")[0].className = "fa fa-chevron-circle-up";
    }
}

function openFC(fput) {
    document.getElementById(fput).click();
}

function viewIMG(fput, ig, rmv, defImg) {
    var fi = document.getElementById(fput);
    var img = document.getElementById(ig);
    if (fi.files.length == 0) {
        img.src = defImg;
        document.getElementById(rmv).style.display = "none";
    } else {
        var f = fi.files[0];
        var fr = new FileReader();
        fr.onload = function () {
            img.src = fr.result;
        };
        fr.readAsDataURL(f);
        document.getElementById(rmv).style.display = "initial";
    }
}

function removeIMG(fput, ig, rmv, defImg) {
    var img = document.getElementById(ig);
    img.src = defImg;
    document.getElementById(rmv).style.display = "none";
    var f = document.getElementById(fput);
    f.value = "";
}

function numberBoxNext(numID) {
    var nb = document.getElementById(numID);
    var max = parseInt(nb.getAttribute("max"));

    var val = document.getElementById("val-" + numID);
    var currentVal = parseInt(val.textContent);
    var nextVal = currentVal + 1;
    if (max < nextVal) {
        val.innerHTML = "1";
    } else {
        val.innerHTML = nextVal;
    }
}

function numberBoxPrev(numID) {
    var nb = document.getElementById(numID);
    var max = parseInt(nb.getAttribute("max"));

    var val = document.getElementById("val-" + numID);
    var currentVal = parseInt(val.textContent);
    var prevVal = currentVal - 1;
    if (prevVal == 0) {
        if (max == 0) {
            val.innerHTML = 1;
        } else {
            val.innerHTML = max;
        }
    } else {
        val.innerHTML = prevVal;
    }
}

function loadDistricts() {
    var provinceID = document.getElementById("province").value;

    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            document.getElementById("district").innerHTML = req.responseText;
            loadCities();
        }
    };
    req.open("GET", "get_districts.jsp?provinceID=" + provinceID, true);
    req.send();
}

function loadCities() {
    var distID = document.getElementById("district").value;

    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            document.getElementById("city").innerHTML = req.responseText;
        }
    };
    req.open("GET", "get_cities.jsp?distID=" + distID, true);
    req.send();
}

var tOutID;
function addToCart(stockID) {
    if (tmID != null) {
        clearTimeout(tmID);
    }
    hideToolTip("wish-tt");
    if (tOutID != null) {
        clearTimeout(tOutID);
    }
    hideToolTip("cart-tt");
    var qty = document.getElementById("val-num-" + stockID).textContent;

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

var tmID;
function addToWishlist(prodID) {
    if (tOutID != null) {
        clearTimeout(tOutID);
    }
    hideToolTip("cart-tt");
    if (tmID != null) {
        clearTimeout(tmID);
    }
    hideToolTip("wish-tt");

    var btns = document.getElementsByClassName("wish-p-" + prodID);

    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            var resp = req.responseText;
            var status = resp.substring(0, 2);
            if (status == "ok") {
                showToolTip("wish-tt", resp.substring(3));
                tmID = setTimeout(hideToolTip, 5000, "wish-tt");
                loadWishlist();
                var on = "fa fa-heart wish-btn btn wish-p-" + prodID;
                var off = "fa fa-heart-o wish-btn btn wish-p-" + prodID;
                for (var i = 0, max = btns.length; i < max; i++) {
                    if (btns[i].className == on) {
                        btns[i].className = off;
                    } else {
                        btns[i].className = on;
                    }
                }
            } else if (status == "er") {
                showMsg("Error !", "Couldn't update your wishlist.<br>Try again later.");
            }
        }
    };
    req.open("GET", "AddToWishlist?prodID=" + prodID, true);
    req.send();
}

function moveToCart(stockID, wishID, prodID) {
    if (tOutID != null) {
        clearTimeout(tOutID);
    }
    hideToolTip("cart-tt");

    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            var resp = req.responseText;
            var status = resp.substring(0, 2);
            if (status == "ok") {
                removeWishlistProduct(wishID, prodID);
                showToolTip("cart-tt", resp.substring(3));
                tOutID = setTimeout(hideToolTip, 5000, "cart-tt");
                loadCart();
            } else {
                showMsg("Error !", resp);
            }
        }
    };
    req.open("GET", "AddToCart?stockID=" + stockID + "&qty=1", true);
    req.send();
}

function buyNow(stockID) {
    var qty = document.getElementById("val-num-" + stockID).textContent;
    window.location = "checkout.jsp?stockID=" + stockID + "&qty=" + qty;
}

function loadCart() {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            document.getElementById("cart-popup").innerHTML = req.responseText;
            document.getElementById("cart-count").innerHTML = document.getElementById("crt-count").textContent;
        }
    };
    req.open("GET", "get_cart.jsp", true);
    req.send();
}

function loadWishlist() {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            var resp = req.responseText;
            if (resp.substring(0, 6) != "nocust") {
                document.getElementById("wish-popup").innerHTML = resp;
                document.getElementById("wish-count").innerHTML = document.getElementById("wsh-count").textContent;
            }
        }
    };
    req.open("GET", "get_wishlist.jsp", true);
    req.send();
}

function calculateCartTotal(stockID) {
    var qty = document.getElementById("val-cart-num-" + stockID).textContent;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            document.getElementById("cart-total").innerHTML = req.responseText;
        }
    };
    req.open("GET", "LoadCartTotal?stockID=" + stockID + "&qty=" + qty, true);
    req.send();
}

function removeCartProduct(stockID) {
    var prod = document.getElementById("cart-product-" + stockID);
    document.getElementById("cart-details").removeChild(prod);
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            document.getElementById("cart-total").innerHTML = req.responseText;
            var ctCount = document.getElementById("cart-count");
            var newCount = parseInt(ctCount.textContent) - 1;
            ctCount.innerHTML = newCount;
            document.getElementById("crt-count").innerHTML = newCount;
            if (newCount == 0) {
                loadCart();
            }
        }
    };
    req.open("GET", "DeleteCartProduct?stockID=" + stockID, true);
    req.send();
}

function removeWishlistProduct(wishID, prodID) {
    var prod = document.getElementById("wish-product-" + wishID);
    document.getElementById("wish-details").removeChild(prod);
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            var ctCount = document.getElementById("wish-count");
            var newCount = parseInt(ctCount.textContent) - 1;
            ctCount.innerHTML = newCount;
            document.getElementById("wsh-count").innerHTML = newCount;
            if (newCount == 0) {
                loadWishlist();
            }
            var on = "wish-p-" + prodID;
            var off = "fa fa-heart-o wish-btn btn wish-p-" + prodID;
            var btns = document.getElementsByClassName(on);
            for (var i = 0, max = btns.length; i < max; i++) {
                btns[i].className = off;
            }
        }
    };
    req.open("GET", "DeleteWishlistProduct?wishID=" + wishID, true);
    req.send();
}

function getParameter(name) {
    if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search))
        return decodeURIComponent(name[1]);
}

function goToURL(url) {
    window.location = url;
}

function goToSearch() {
    var keyWord = document.getElementById("nav-search-box").value;
    goToURL("search.jsp?keyWord=" + keyWord);
}

function loadSuggestions() {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var suggestions = JSON.parse(req.responseText);
            initAutoComplete(document.getElementById("nav-search-box"), suggestions, goToSearch);
        }
    };
    req.open("GET", "LoadSuggestions", true);
    req.send();
}

if (document.getElementById("nav-search-box") != null) {
    loadSuggestions();
}