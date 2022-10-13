function setPageLimit(pLim) {
    if (pLim != null) {
        document.getElementById("page-limit").value = pLim;
    }
}

function loadResults(p, jsData, advJsData) {
    var catName = document.getElementById("cat-combo-box").value;
    var brandName = document.getElementById("brand-combo-box").value;
    var searchText = encodeURIComponent(document.getElementById("search-box").value.trim());
    var fromPrice = document.getElementById("price-from").value;
    var toPrice = document.getElementById("price-to").value;
    var sortBy = document.getElementById("sort-by").value;
    var pageLimit = document.getElementById("page-limit").value;

    var catParam = "";
    var brandParam = "";
    var jsonParam = "";
    var advJsonParam = "";
    if (catName != "Any") {
        catParam = "&catName=" + encodeURIComponent(catName);
    }
    if (brandName != "Any") {
        brandParam = "&brandName=" + encodeURIComponent(brandName);
    }
    if (jsData != null) {
        jsonParam = "&jsonData=" + encodeURIComponent(JSON.stringify(jsData));
    }
    if (advJsData != null) {
        if (Object.keys(advJsData).length != 0) {
            advJsonParam = "&advJsData=" + encodeURIComponent(JSON.stringify(advJsData));
        }
    }
    if (p == null) {
        p = 0;
    }

    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            document.getElementById("product-list-div").innerHTML = req.responseText;
            document.getElementById("res-count").innerHTML = document.getElementsByClassName("pagination")[0].getAttribute("resultCount");

            var sw = new Swiper('.blog-slider', {
                spaceBetween: 30
                , effect: 'fade'
                , loop: true
//                , autoplay: {
//                    delay: 5000
//                    , }
                , mousewheel: {
                    invert: false
                    , }
                , // autoHeight: true,
                pagination: {
                    el: '.blog-slider__pagination'
                    , clickable: true
                    , }
            });
        }
    };
    req.open("GET", "SearchProduct?p=" + p + "&pageLimit=" + pageLimit + "&searchText=" + searchText + catParam + brandParam + jsonParam + advJsonParam + "&fromPrice=" + fromPrice + "&toPrice=" + toPrice + "&sortBy=" + sortBy, true);
    req.send();
}

function loadBrandComboBox() {
    document.getElementById("brand-combo-box").value = "Any";
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            document.getElementById("brand-combo-box").innerHTML = req.responseText;
        }
    }
    req.open("GET", "get_brands combo.jsp?catName=" + encodeURIComponent(document.getElementById("cat-combo-box").value), true);
    req.send();
}

function loadSearchFilters() {
    var catName = document.getElementById("cat-combo-box").value;
    var brandName = brandName = document.getElementById("brand-combo-box").value;
    var searchText = encodeURIComponent(document.getElementById("search-box").value.trim());
    var fromPrice = document.getElementById("price-from").value;
    var toPrice = document.getElementById("price-to").value;

    var catParam = "";
    var brandParam = "";
    if (catName != "Any") {
        catParam = "&catName=" + encodeURIComponent(catName);
    }
    if (brandName != "Any") {
        brandParam = "&brandName=" + encodeURIComponent(brandName);
    }
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            var resData = JSON.parse(resp);
            var catfi = "";
            for (let c in resData["catMap"]) {
                catfi += '<div class="d-inline-flex flex-row check-box-div cat-sf-chk"><label class="chklabel">' + c + '<input onchange="applysearchFilters(\'cat\');" type="checkbox"><span class="checkmark"></span></label><span class="d-flex d-sm-flex d-md-flex d-lg-flex d-xl-flex justify-content-center align-items-center align-self-center justify-content-sm-center align-items-sm-center justify-content-md-center align-items-md-center justify-content-lg-center align-items-lg-center align-items-xl-center res-count">' + resData["catMap"][c] + '</span></div>';
            }
            document.getElementById("cat-filter").innerHTML = catfi;

            var bfi = "";
            for (let b in resData["brandMap"]) {
                bfi += '<div class="d-inline-flex flex-row check-box-div brand-sf-chk"><label class="chklabel">' + b + '<input onchange="applysearchFilters(\'brand\');" type="checkbox"><span class="checkmark"></span></label><span class="d-flex d-sm-flex d-md-flex d-lg-flex d-xl-flex justify-content-center align-items-center align-self-center justify-content-sm-center align-items-sm-center justify-content-md-center align-items-md-center justify-content-lg-center align-items-lg-center align-items-xl-center res-count">' + resData["brandMap"][b] + '</span></div>';

            }
            document.getElementById("brand-filter").innerHTML = bfi;

            if (resData["resCount"] == 0) {
                document.getElementById("advanced-filter-div").innerHTML = "";
            } else {
                if (Object.entries(resData["catMap"]).length == 1) {
                    catChkBox = document.getElementsByClassName("cat-sf-chk")[0].getElementsByTagName("input")[0];
                    catChkBox.checked = true;
                    catChkBox.disabled = true;

                    var fillReq = new XMLHttpRequest();
                    fillReq.onreadystatechange = function () {
                        if (fillReq.readyState == 4 && fillReq.status == 200) {
                            document.getElementById("advanced-filter-div").innerHTML = fillReq.responseText;
                        }
                    }

                    brandListParam = "&brandList=" + encodeURIComponent(JSON.stringify(Object.keys(resData["brandMap"])));
                    fillReq.open("GET", "get_advanced_filters.jsp?catName=" + Object.keys(resData["catMap"])[0] + brandListParam, true);
                    fillReq.send();
                } else {
                    document.getElementById("advanced-filter-div").innerHTML = "";
                    if (document.getElementsByClassName("cat-sf-chk").length != 0) {
                        catChkBox = document.getElementsByClassName("cat-sf-chk")[0].getElementsByTagName("input")[0];
                        catChkBox.checked = false;
                        catChkBox.disabled = false;
                    }
                }
                if (Object.entries(resData["brandMap"]).length == 1) {
                    brChkBox = document.getElementsByClassName("brand-sf-chk")[0].getElementsByTagName("input")[0];
                    brChkBox.checked = true;
                    brChkBox.disabled = true;
                } else if (Object.entries(resData["brandMap"]).length > 1) {
                    brChkBox = document.getElementsByClassName("brand-sf-chk")[0].getElementsByTagName("input")[0];
                    brChkBox.checked = false;
                    brChkBox.disabled = false;
                }
            }
        }
    }
    ;
    req.open("GET", "LoadSearchFilters?searchText=" + searchText + catParam + brandParam + "&fromPrice=" + fromPrice + "&toPrice=" + toPrice, true);
    req.send();
}

function applysearchFilters(filterType) {
    var jsData = {};

    var catFields = [];
    var catChks = document.getElementsByClassName("cat-sf-chk");
    for (var i = 0; i < catChks.length; i++) {
        if (catChks[i].getElementsByTagName("input")[0].checked == true && catChks[i].getElementsByTagName("input")[0].disabled == false) {
            catFields.push(catChks[i].getElementsByTagName("label")[0].textContent);
        }
    }

    var brandFields = [];
    var brChks = document.getElementsByClassName("brand-sf-chk");
    for (var i = 0; i < brChks.length; i++) {
        if (brChks[i].getElementsByTagName("input")[0].checked == true && brChks[i].getElementsByTagName("input")[0].disabled == false) {
            brandFields.push(brChks[i].getElementsByTagName("label")[0].textContent);
        }
    }

    var advFields = {};
    var advSFs = document.getElementsByClassName("adv-sf");
    for (var i = 0; i < advSFs.length; i++) {
        var specName = advSFs[i].getElementsByTagName("h6")[0].textContent;
        var advFVals = [];
        var advChks = advSFs[i].getElementsByClassName("adv-sf-chk");
        for (var x = 0; x < advChks.length; x++) {
            if ((advChks[x].getElementsByTagName("input")[0].checked == true) && (advChks[x].getElementsByTagName("input")[0].disabled == false)) {
                advFVals.push(advChks[x].getElementsByTagName("label")[0].textContent);
            }
        }
        if (advFVals.length != 0) {
            advFields[specName] = advFVals;
        }
    }
    jsData["catFields"] = catFields;
    if (filterType != "cat") {
        jsData["brandFields"] = brandFields;
    }
    loadResults(0, jsData);
    if (filterType == "cat") {
        loadBrandFilters(jsData);

        var brList = document.getElementsByClassName("brand-sf-chk");
        if (brList.length == 1) {
            brChkBox = brList[0].getElementsByTagName("input")[0];
            brChkBox.checked = true;
            brChkBox.disabled = true;
        } else if (brList.length > 1) {
            brChkBox = brList[0].getElementsByTagName("input")[0];
            brChkBox.checked = false;
            brChkBox.disabled = false;
        }
    }
    if (catChks.length == 1 || jsData["catFields"].length == 1) {

        if (catFields[0] != null) {
            var brandListParam = "";
            if (filterType == "brand") {
                brandListParam = "&brandList=" + encodeURIComponent(JSON.stringify(brandFields));
            }

            var fillReq = new XMLHttpRequest();
            fillReq.onreadystatechange = function () {
                if (fillReq.readyState == 4 && fillReq.status == 200) {
                    document.getElementById("advanced-filter-div").innerHTML = fillReq.responseText;
                }
            }
            fillReq.open("GET", "get_advanced_filters.jsp?catName=" + catFields[0] + brandListParam, true);
            fillReq.send();
        }
    } else {
        document.getElementById("advanced-filter-div").innerHTML = "";
    }

}


function loadBrandFilters(jsData) {
    var catName = document.getElementById("cat-combo-box").value;
    var brandName = document.getElementById("brand-combo-box").value;
    var searchText = encodeURIComponent(document.getElementById("search-box").value.trim());
    var fromPrice = document.getElementById("price-from").value;
    var toPrice = document.getElementById("price-to").value;

    var catParam = "";
    var brandParam = "";
    if (catName != "Any") {
        catParam = "&catName=" + encodeURIComponent(catName);
    }
    if (brandName != "Any") {
        brandParam = "&brandName=" + encodeURIComponent(brandName);
    }

    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            var brandMap = JSON.parse(resp);
            var bfi = "";
            for (let b in brandMap) {
                bfi += '<div class="d-inline-flex flex-row check-box-div brand-sf-chk"><label class="chklabel">' + b + '<input onchange="applysearchFilters(\'brand\');" type="checkbox"><span class="checkmark"></span></label><span class="d-flex d-sm-flex d-md-flex d-lg-flex d-xl-flex justify-content-center align-items-center align-self-center justify-content-sm-center align-items-sm-center justify-content-md-center align-items-md-center justify-content-lg-center align-items-lg-center align-items-xl-center res-count">' + brandMap[b] + '</span></div>';

            }
            document.getElementById("brand-filter").innerHTML = bfi;
        }
    }
    req.open("GET", "LoadBrandFilters?jsonData=" + encodeURIComponent(JSON.stringify(jsData)) + "&searchText=" + searchText + catParam + brandParam + "&fromPrice=" + fromPrice + "&toPrice=" + toPrice, false);
    req.send();
}

function applyAdvancedsearchFilters(pageID) {
    var jsData = {};

    var catFields = [];
    var catChks = document.getElementsByClassName("cat-sf-chk");
    if (catChks.length == 1) {
        if (catChks[0].getElementsByTagName("input")[0].checked == true) {
            catFields.push(catChks[0].getElementsByTagName("label")[0].textContent);
        }
    } else {
        for (var i = 0; i < catChks.length; i++) {
            if (catChks[i].getElementsByTagName("input")[0].checked == true && catChks[i].getElementsByTagName("input")[0].disabled == false) {
                catFields.push(catChks[i].getElementsByTagName("label")[0].textContent);
            }
        }
    }

    var brandFields = [];
    var brChks = document.getElementsByClassName("brand-sf-chk");
    for (var i = 0; i < brChks.length; i++) {
        if (brChks[i].getElementsByTagName("input")[0].checked == true && brChks[i].getElementsByTagName("input")[0].disabled == false) {
            brandFields.push(brChks[i].getElementsByTagName("label")[0].textContent);
        }
    }

    var advFields = {};
    var advSFs = document.getElementsByClassName("adv-sf");
    for (var i = 0; i < advSFs.length; i++) {
        var specName = advSFs[i].getElementsByTagName("h6")[0].textContent;
        var advFVals = [];
        var advChks = advSFs[i].getElementsByClassName("adv-sf-chk");
        for (var x = 0; x < advChks.length; x++) {
            if ((advChks[x].getElementsByTagName("input")[0].checked == true) && (advChks[x].getElementsByTagName("input")[0].disabled == false)) {
                advFVals.push(advChks[x].getElementsByTagName("label")[0].textContent);
            }
        }
        if (advFVals.length != 0) {
            advFields[specName] = advFVals;
        }
    }
    jsData["catFields"] = catFields;
    jsData["brandFields"] = brandFields;


    if (pageID == null) {
        pageID = 0;
    }
    loadResults(pageID, jsData, advFields);
}

function initSearchBox() {
    var action = function () {
        loadResults();
        loadSearchFilters();
    };
    
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var suggestions = JSON.parse(req.responseText);
            initAutoComplete(document.getElementById("search-box"), suggestions, action);
        }
    };
    req.open("GET", "LoadSuggestions", true);
    req.send();
}

