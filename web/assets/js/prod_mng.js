function setBrandIMGHeight() {
    var w = document.getElementById("s-1").offsetWidth;
    for (var i = 1; i <= 4; i++) {
        document.getElementById("s-" + i).style.height = w + "px";
    }
}

$(window).resize(setBrandIMGHeight);
$(window).scroll(setBrandIMGHeight);

function loadCategoryTable() {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            document.getElementById("tbody-cat").innerHTML = resp;
            if (selectedCatRow != null) {
                setActiveCatRow(parseInt(selectedCatRow.cells[0].textContent));
            }
            loadCategoryComboBox();
        }
    };
    req.open("GET", "LoadCats", true);
    req.send();
}

function loadCategoryComboBox() {
    var rows = document.getElementById("cat-table").rows;
    var cmb = document.getElementById("cat-combo-box");
    var cmbSearch = document.getElementById("cat-combo-box-search");
    var isFirstTime = false;
    if (cmb.value == "") {
        isFirstTime = true;
    }
    cmb.innerHTML = "";
    cmbSearch.innerHTML = "<option>Any</option>";
    for (var i = 1; i < rows.length; i++) {
        if (rows[i].cells[2].getElementsByTagName('input')[0].checked) {
            var ops = document.createElement("option");
            ops.innerHTML = rows[i].cells[1].textContent
            cmbSearch.appendChild(ops);
            var op = document.createElement("option");
            op.innerHTML = rows[i].cells[1].textContent
            cmb.appendChild(op);
        }
    }
    if (isFirstTime) {
        loadProductSpecTable();
    }
}

function addNewCategory() {
    var catName = document.getElementById("newCatName").value;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadCategoryTable();
                closeModal('cat-popup-bg');
                document.getElementById("newCatName").value = "";
            } else if (resp == "inv") {
                showToolTip("cat-tt", "Invalid Category Name");
            } else if (resp == "dup") {
                showToolTip("cat-tt", "Already exists");
            } else {
                showMsg("Error", "Couldn't add new Category.");
            }

        }
    };
    req.open("GET", "AddNewCat?catName=" + encodeURIComponent(catName), true);
    req.send();
}

var selectedCatRow;
function setActiveCatRow(catID) {
    var rows = document.getElementById("cat-table").rows;
    var hasCatID = false;
    for (var i = 0; i < rows.length; i++) {
        if (!hasCatID) {
            if (parseInt(rows[i].cells[0].textContent) == catID) {
                hasCatID = true;
            }
        }
        rows[i].className = "";
    }
    if (hasCatID) {
        selectedCatRow = document.getElementById("cat-tr-" + catID);
        selectedCatRow.className = "tr-active";
        loadSpecTable(catID);
        document.getElementById("cat-edit-btn").style.display = "initial";
        document.getElementById("cat-del-btn").style.display = "initial";
        document.getElementById("spec-ad-btn").style.display = "initial";
        var catName = selectedCatRow.cells[1].textContent;
        document.getElementById("selected-cat-spec").innerHTML = "Specs of " + catName;
        document.getElementById("oldCatName").innerHTML = catName;
        document.getElementById("updateCatName").value = catName;
        document.getElementById("cat-update-btn").setAttribute("onclick", "updateCategory(" + catID + ");");
        document.getElementById("cat-del-btn").setAttribute("onclick", "showCategoryDeletePopUp(" + catID + ", '" + catName + "');");
        document.getElementById("spec-add-btn").setAttribute("onclick", "addNewSpec(" + catID + ");");
        document.getElementById("isActiveCat").checked = selectedCatRow.cells[2].getElementsByTagName('input')[0].checked;
    } else {
        document.getElementById("selected-cat-spec").innerHTML = "Specifications";
        loadSpecTable(-1);
        document.getElementById("cat-edit-btn").style.display = "none";
        document.getElementById("cat-del-btn").style.display = "none";
        document.getElementById("spec-ad-btn").style.display = "none";
    }
}

function updateCategory(catID) {
    var newCatName = document.getElementById("updateCatName").value;
    var isActive = document.getElementById("isActiveCat").checked;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadCategoryTable();
                closeModal('cat-update-popup-bg');
            } else if (resp == "inv") {
                showToolTip("cat-update-tt", "Invalid Category Name");
            } else if (resp == "dup") {
                showToolTip("cat-update-tt", "Already exists");
            } else {
                showMsg("An Error Occurred !", "Couldn't Update Category.");
            }
        }
    };
    req.open("GET", "UpdateCat?catID=" + catID + "&catName=" + encodeURIComponent(newCatName) + "&isActive=" + isActive, true);
    req.send();
}

function showCategoryDeletePopUp(catID, catName) {
    document.getElementById("delete-confirm-btn").setAttribute("onclick", "deleteCategory(" + catID + ");");
    document.getElementById("del-itm").innerHTML = catName;
    showModal("delete-popup-bg");
}

function deleteCategory(catID) {
    closeModal('delete-popup-bg');
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadCategoryTable();
                setActiveCatRow(-1);
                loadCategoryComboBox();
                loadProductSpecTable();
            } else if (resp == "ex") {
                showMsg("Can't Delete !", "There are products registered to this category.");
            } else {
                showMsg("An Error Occurred !", "Couldn't Delete Category.");
            }
        }
    };
    req.open("GET", "DeleteCat?catID=" + catID, true);
    req.send();
}

function loadSpecTable(catID) {
    if (catID > 0) {
        var req = new XMLHttpRequest();
        req.onreadystatechange = function () {
            if (req.readyState == 4 && req.status == 200) {
                var resp = req.responseText;
                document.getElementById("tbody-spec").innerHTML = resp;
                if (selectedSpecRow != null) {
                    setActiveSpecRow(parseInt(selectedSpecRow.cells[0].textContent));
                }
            }
        };
        req.open("GET", "LoadSpecs?catID=" + catID, true);
        req.send();
    } else {
        document.getElementById("tbody-spec").innerHTML = "";
        document.getElementById("spec-edit-btn").style.display = "none";
        document.getElementById("spec-del-btn").style.display = "none";
    }
}

function loadProductSpecTable() {
    var catName = document.getElementById("cat-combo-box").value;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            document.getElementById("tbody-product-spec").innerHTML = resp;

            if (selectedStockRow != null) {
                var specData = JSON.parse(selectedStockRow.cells[12].textContent);
                var specRows = document.getElementById("product-spec-table").rows;
                for (var i = 0; i < specRows.length; i++) {
                    var specID = specRows[i].cells[0].textContent;
                    var v = specData[specID];
                    if (v != null) {
                        document.getElementById("product-spec-" + specID).value = v;
                    }
                }
            }
        }
    };
    req.open("GET", "LoadSpecs?catName=" + encodeURIComponent(catName), true);
    req.send();
}

function addNewSpec(catID) {
    var specName = document.getElementById("newSpecName").value;
    var specUnit = document.getElementById("newSpecUnit").value;
    var isKey = document.getElementById("newSpecIsKey").checked;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadSpecTable(catID);
                closeModal('spec-popup-bg');
                document.getElementById("newSpecName").value = "";
                document.getElementById("newSpecUnit").value = "";
                refreshProductSpectable();
            } else if (resp == "inv") {
                showToolTip("spec-name-tt", "Invalid Spec Name");
            } else if (resp == "dup") {
                showToolTip("spec-name-tt", "Already exists");
            } else {
                showMsg("An Error Occurred !", "Couldn't add new Specification.");
            }

        }
    };
    req.open("GET", "AddNewSpec?catID=" + catID + "&specName=" + encodeURIComponent(specName) + "&specUnit=" + encodeURIComponent(specUnit) + "&isKey=" + isKey, true);
    req.send();
}

var selectedSpecRow;
function setActiveSpecRow(specID) {
    var rows = document.getElementById("spec-table").rows;
    var hasSpecID = false;
    var isFirst;
    var isLast;
    for (var i = 0; i < rows.length; i++) {
        if (!hasSpecID) {
            if (parseInt(rows[i].cells[0].textContent) == specID) {
                hasSpecID = true;
                if (i == 1) {
                    isFirst = true;
                }
                if (i == rows.length - 1) {
                    isLast = true;
                }
            }
        }
        rows[i].className = "";
    }
    if (hasSpecID) {
        selectedSpecRow = document.getElementById("spec-tr-" + specID);
        selectedSpecRow.className = "tr-active";
        document.getElementById("spec-ad-btn").style.marginLeft = "8px";
        document.getElementById("spec-edit-btn").style.display = "initial";
        document.getElementById("spec-del-btn").style.display = "initial";
        var specName = selectedSpecRow.cells[1].textContent;
        var specUnit = selectedSpecRow.cells[2].textContent;
        if (specUnit == "N/A") {
            specUnit = "";
        }
        var isKey = selectedSpecRow.cells[3].getElementsByTagName('input')[0].checked;
        document.getElementById("oldSpecName").innerHTML = specName;
        document.getElementById("updateSpecName").value = specName;
        document.getElementById("updateSpecUnit").value = specUnit;
        document.getElementById("updateIsKey").checked = isKey;
        document.getElementById("spec-update-btn").setAttribute("onclick", "updateSpec(" + specID + ");");
        document.getElementById("spec-del-btn").setAttribute("onclick", "showSpecDeletePopUp(" + specID + ", '" + specName + "');");
        if (isFirst) {
            document.getElementById("spec-moveup-btn").style.display = "none";
            document.getElementById("spec-movedown-btn").style.marginLeft = "0px";
        } else {
            document.getElementById("spec-moveup-btn").style.display = "initial";
            document.getElementById("spec-movedown-btn").style.removeProperty('margin-left');
        }
        if (isLast) {
            document.getElementById("spec-movedown-btn").style.display = "none";
        } else {
            document.getElementById("spec-movedown-btn").style.display = "initial";
        }
    } else {
        document.getElementById("spec-ad-btn").style.marginLeft = "0px";
        document.getElementById("spec-edit-btn").style.display = "none";
        document.getElementById("spec-del-btn").style.display = "none";
        document.getElementById("spec-moveup-btn").style.display = "none";
        document.getElementById("spec-movedown-btn").style.display = "none";
    }
}

function showSpecDeletePopUp(specID, specName) {
    document.getElementById("delete-confirm-btn").setAttribute("onclick", "deleteSpec(" + specID + ");");
    document.getElementById("del-itm").innerHTML = specName;
    showModal("delete-popup-bg");
}

function deleteSpec(specID) {
    closeModal('delete-popup-bg');
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadSpecTable(selectedCatRow.cells[0].textContent);
                refreshProductSpectable();
            } else {
                showMsg("An Error Occurred !", "Couldn't Delete Spec.");
            }
        }
    };
    req.open("GET", "DeleteSpec?specID=" + specID, false);
    req.send();
}

function updateSpec(specID) {
    var newSpecName = document.getElementById("updateSpecName").value;
    var newSpecUnit = document.getElementById("updateSpecUnit").value;
    var newIsKey = document.getElementById("updateIsKey").checked;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadSpecTable(parseInt(selectedCatRow.cells[0].textContent));
                closeModal('spec-update-popup-bg');
                refreshProductSpectable();
            } else if (resp == "inv") {
                showToolTip("spec-name-update-tt", "Invalid Spec Name");
            } else if (resp == "dup") {
                showToolTip("spec-name-update-tt", "Already exists");
                showToolTip("spec-unit-update-tt", "Already exists");
            } else {
                showMsg("An Error Occurred !", "Couldn't Update Category.");
            }
        }
    };
    req.open("GET", "UpdateSpec?specID=" + specID + "&specName=" + encodeURIComponent(newSpecName) + "&specUnit=" + encodeURIComponent(newSpecUnit) + "&isKey=" + newIsKey, true);
    req.send();
}

function refreshProductSpectable() {
    var oldCatName = selectedCatRow.cells[1].textContent;
    if (document.getElementById("cat-combo-box").value == oldCatName) {
        loadProductSpecTable();
    }
}

function loadBrandTable() {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            document.getElementById("tbody-brand").innerHTML = resp;
            if (selectedBrandRow != null) {
                setActiveBrandRow(parseInt(selectedBrandRow.cells[0].textContent));
            }
            loadBrandComboBox();
        }
    };
    req.open("GET", "LoadBrands", true);
    req.send();
}

function loadBrandComboBox() {
    var rows = document.getElementById("brand-table").rows;
    var cmb = document.getElementById("brand-combo-box");
    var cmbSearch = document.getElementById("brand-combo-box-search");
    cmb.innerHTML = "";
    cmbSearch.innerHTML = "<option>Any</option>";
    for (var i = 1; i < rows.length; i++) {
        if (rows[i].cells[2].getElementsByTagName('input')[0].checked) {
            var ops = document.createElement("option");
            ops.innerHTML = rows[i].cells[1].textContent
            cmbSearch.appendChild(ops);
            var op = document.createElement("option");
            op.innerHTML = rows[i].cells[1].textContent
            cmb.appendChild(op);
        }
    }
}

function addNewBrand() {
    var brandName = document.getElementById("newBrandName").value;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadBrandTable();
                closeModal('brand-popup-bg');
                document.getElementById("newBrandName").value = "";
            } else if (resp == "inv") {
                showToolTip("brand-tt", "Invalid Brand Name");
            } else if (resp == "dup") {
                showToolTip("brand-tt", "Already exists");
            } else {
                showMsg("Error", "Couldn't add new Brand.");
            }
        }
    };
    req.open("GET", "AddNewBrand?brandName=" + encodeURIComponent(brandName), true);
    req.send();
}

var selectedBrandRow;
function setActiveBrandRow(brandID) {
    var rows = document.getElementById("brand-table").rows;
    var hasBrandID = false;
    for (var i = 0; i < rows.length; i++) {
        if (!hasBrandID) {
            if (parseInt(rows[i].cells[0].textContent) == brandID) {
                hasBrandID = true;
            }
        }
        rows[i].className = "";
    }
    if (hasBrandID) {
        selectedBrandRow = document.getElementById("brand-tr-" + brandID);
        selectedBrandRow.className = "tr-active";
        loadBrandDDetails(brandID);
        document.getElementById("brand-edit-btn").style.display = "initial";
        document.getElementById("brand-del-btn").style.display = "initial";
        document.getElementById("brand-detail-save-btn").style.display = "initial";
        var brandName = selectedBrandRow.cells[1].textContent;
        document.getElementById("selected-brand-name").innerHTML = "Details of " + brandName;
        document.getElementById("oldBrandName").innerHTML = brandName;
        document.getElementById("updateBrandName").value = brandName;
        document.getElementById("brand-update-btn").setAttribute("onclick", "updateBrand(" + brandID + ");");
        document.getElementById("brand-detail-save-btn").setAttribute("onclick", "saveBrandDetails(" + brandID + ");");
        document.getElementById("brand-del-btn").setAttribute("onclick", "showBrandDeletePopUp(" + brandID + ", '" + brandName + "');");
        document.getElementById("isActiveBrand").checked = selectedBrandRow.cells[2].getElementsByTagName('input')[0].checked;
    } else {
        document.getElementById("selected-brand-name").innerHTML = "Brand Details";
        document.getElementById("sup-web").value = "";
        document.getElementById("main-web").value = ""
        document.getElementById("brand-img").src = "assets/img/brands/brand.png";
        ;
        document.getElementById("brand-edit-btn").style.display = "none";
        document.getElementById("brand-del-btn").style.display = "none";
        document.getElementById("brand-detail-save-btn").style.display = "none";
    }
}

function loadBrandDDetails(brandID) {
    var mainWeb = selectedBrandRow.cells[3].textContent;
    var supWeb = selectedBrandRow.cells[4].textContent;
    if (mainWeb == "null") {
        document.getElementById("main-web").value = "";
    } else {
        document.getElementById("main-web").value = mainWeb;
    }
    if (supWeb == "null") {
        document.getElementById("sup-web").value = "";
    } else {
        document.getElementById("sup-web").value = supWeb;
    }
    if (selectedBrandRow.cells[5].textContent == "true") {
        document.getElementById("brand-img").src = "assets/img/brands/" + brandID;
        document.getElementById('img-rmv-btn').style.display = "initial";
    } else {
        document.getElementById("brand-img").src = "assets/img/brands/brand.png";
        document.getElementById('img-rmv-btn').style.display = "none";
    }
}

function saveBrandDetails(brandID) {
    var mainWeb = document.getElementById("main-web").value;
    var supWeb = document.getElementById("sup-web").value;
    var req = new XMLHttpRequest();
    var fi = document.getElementById("f");
    var isImageSet = fi.files.length != 0;
    var formData = new FormData();
    formData.append("brandID", brandID);
    formData.append("mainWeb", mainWeb);
    formData.append("supWeb", supWeb);
    var isrc = document.getElementById("brand-img").src;
    if (isrc.indexOf("assets/img/brands/brand.png") == -1) {
        formData.append("img", "yes");
    } else {
        formData.append("img", "no");
    }
    if (isImageSet) {
        formData.append("f", fi.files[0]);
    }

    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState === 4 && req.status === 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadBrandTable();
                showMsg("Success", "Brand Details has been saved.");
            } else {
                showMsg("Error", "Couldn't Save Brand Details.");
            }
        }
    };
    req.open("POST", "UpdateBrand", true);
    req.send(formData);
}

function updateBrand(brandID) {
    var newBrandName = document.getElementById("updateBrandName").value;
    var isActive = document.getElementById("isActiveBrand").checked;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadBrandTable();
                closeModal('brand-update-popup-bg');
            } else if (resp == "inv") {
                showToolTip("brand-update-tt", "Invalid Brand Name");
            } else if (resp == "dup") {
                showToolTip("brand-update-tt", "Already exists");
            } else {
                showMsg("An Error Occurred !", "Couldn't Update Brand.");
            }
        }
    };
    req.open("GET", "UpdateBrand?brandID=" + brandID + "&brandName=" + encodeURIComponent(newBrandName) + "&isActive=" + isActive, true);
    req.send();
}

function showBrandDeletePopUp(brandID, brandName) {
    document.getElementById("delete-confirm-btn").setAttribute("onclick", "deleteBrand(" + brandID + ");");
    document.getElementById("del-itm").innerHTML = brandName;
    showModal("delete-popup-bg");
}

function deleteBrand(brandID) {
    closeModal('delete-popup-bg');
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadBrandTable();
                setActiveBrandRow(-1);
            } else if (resp == "ex") {
                showMsg("Can't Delete !", "There are products registered to this Brand.");
            } else {
                showMsg("An Error Occurred !", "Couldn't Delete Brand.");
            }
        }
    };
    req.open("GET", "DeleteBrand?brandID=" + brandID, false);
    req.send();
}


function loadStockTable() {
    var catName = document.getElementById("cat-combo-box-search").value;
    var brandName = document.getElementById("brand-combo-box-search").value;
    var searchText = encodeURIComponent(document.getElementById("search-box").value.trim());
    var sortBy = document.getElementById("product-sort-by").value;
    
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
            document.getElementById("tbody-stock").innerHTML = resp;
            if (selectedStockRow != null) {
                setActiveStockRow(parseInt(selectedStockRow.cells[0].textContent));
            }
        }
    };
    req.open("GET", "LoadProducts?searchText=" + searchText + catParam + brandParam + "&sortBy=" + sortBy, true);
    req.send();
}

function addNewProduct() {

    var catName = document.getElementById("cat-combo-box").value.trim();
    var brandName = document.getElementById("brand-combo-box").value.trim();
    var pName = document.getElementById("product-name").value.trim();
    var pQty = document.getElementById("product-qty").value;
    var bPrice = document.getElementById("product-bprice").value;
    var sPrice = document.getElementById("product-sprice").value;
    var discount = document.getElementById("product-discount").value;
    var warranty = document.getElementById("product-warranty").value;
    var desc = document.getElementById("product-desc").value;

    var specs = {};
    var rows = document.getElementById("product-spec-table").rows;
    for (var i = 1, max = rows.length; i < max; i++) {
        var v = rows[i].cells[2].getElementsByTagName('input')[0].value.trim();
        var specID = rows[i].cells[0].textContent;
        if (v != "") {
            specs[specID] = v;
        }
    }
    var specList = JSON.stringify(specs);

    var formData = new FormData();
    formData.append("catName", catName);
    formData.append("brandName", brandName);
    formData.append("pName", pName);
    formData.append("pQty", pQty);
    formData.append("bPrice", bPrice);
    formData.append("sPrice", sPrice);
    formData.append("discount", discount);
    formData.append("warranty", warranty);
    formData.append("desc", desc);
    formData.append("specList", specList);

    var f1 = document.getElementById("f-1");
    if (f1.files.length != 0) {
        formData.append("f1", f1.files[0]);
    }
    var f2 = document.getElementById("f-2");
    if (f2.files.length != 0) {
        formData.append("f2", f2.files[0]);
    }
    var f3 = document.getElementById("f-3");
    if (f3.files.length != 0) {
        formData.append("f3", f3.files[0]);
    }
    var f4 = document.getElementById("f-4");
    if (f4.files.length != 0) {
        formData.append("f4", f4.files[0]);
    }

    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp.startsWith("ok")) {
                loadStockTable();
                setActiveStockRow(-1);
                showMsg("Success !", "The new product has been added.");
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't add new Specification.");
            } else {
                var msg = "Please Check the following:";
                var errList = JSON.parse(resp);
                for (var i = 0; i < errList.length; i++) {
                    var err = errList[i];
                    if (err == "qty") {
                        showToolTip("product-qty-tt", "Invalid Amount");
                        msg = msg + "<br><br> - The amount you've set as qty is not vaild.";
                    }
                    if (err == "bp") {
                        showToolTip("product-bprice-tt", "Invalid Amount");
                        msg = msg + "<br><br> - The amount you've set as Buying Price is not vaild.";
                    }
                    if (err == "sp") {
                        showToolTip("product-sprice-tt", "Invalid Amount");
                        msg = msg + "<br><br> - The amount you've set as Selling Price is not vaild.";
                    }
                    if (err == "bpmax") {
                        showToolTip("product-bprice-tt", "Buying Price can't be higer than Selling Price");
                        msg = msg + "<br><br> - Buying Price can't be higer than Selling Price.";
                    }
                    if (err == "disc") {
                        showToolTip("product-discount-tt", "Invalid Amount");
                        msg = msg + "<br><br> - The amount you've set as Discount is not vaild. Enter a value between 0 and 100.<br>You can also leave that empty if the product has no discount.";
                    }
                    if (err == "dup") {
                        showToolTip("product-name-tt", "Already Exists");
                        msg = msg + "<br><br> - " + brandName + " " + pName + " in " + catName + " category, already exists.";
                    }
                }
                showMsg("Check Again !", msg);
            }

        }
    };
    req.open("POST", "AddNewProduct", true);
    req.send(formData);
}

var selectedStockRow;
function setActiveStockRow(stockID) {
    var rows = document.getElementById("stock-table").rows;
    var hasStockID = false;
    for (var i = 0; i < rows.length; i++) {
        if (!hasStockID) {
            if (parseInt(rows[i].cells[0].textContent) == stockID) {
                hasStockID = true;
            }
        }
        rows[i].className = "";
    }
    document.getElementById("rmv-btn-1").click();
    document.getElementById("rmv-btn-2").click();
    document.getElementById("rmv-btn-3").click();
    document.getElementById("rmv-btn-4").click();
    if (hasStockID) {
        selectedStockRow = document.getElementById("stock-tr-" + stockID);
        selectedStockRow.className = "tr-active";
        document.getElementById("stock-edit-btn").style.display = "initial";
        document.getElementById("stock-del-btn").style.display = "initial";
        document.getElementById("stock-deactive-btn").style.display = "initial";
        var cmb = document.getElementById("cat-combo-box");
        cmb.value = selectedStockRow.cells[1].textContent;
        loadProductSpecTable();
        document.getElementById("brand-combo-box").value = selectedStockRow.cells[2].textContent;
        document.getElementById("product-name").value = selectedStockRow.cells[3].textContent;
        document.getElementById("product-qty").value = selectedStockRow.cells[6].textContent;
        document.getElementById("product-bprice").value = selectedStockRow.cells[4].textContent;
        document.getElementById("product-sprice").value = selectedStockRow.cells[5].textContent;
        document.getElementById("product-discount").value = selectedStockRow.cells[7].textContent.slice(0, -1).trim();
        document.getElementById("product-warranty").value = selectedStockRow.cells[8].textContent;
        dacBtn = document.getElementById("dac-sts");
        dacIco = document.getElementById("dac-ico");
        if (selectedStockRow.cells[10].getElementsByTagName("input")[0].checked == true) {
            dacBtn.innerHTML = "Deactivate";
            dacIco.className = "fas fa-ban";
        } else {
            dacBtn.innerHTML = "Activate";
            dacIco.className = "fas fa-check-circle";
        }
        document.getElementById("product-desc").value = selectedStockRow.cells[11].textContent;
        document.getElementById("stock-edit-btn").setAttribute("onclick", "priceCheck(" + stockID + ");");
        document.getElementById("stock-deactive-btn").setAttribute("onclick", "deactiveStock(" + stockID + ");");
        document.getElementById("stock-del-btn").setAttribute("onclick", "showStockDeletePopUp(" + stockID + ", '" + selectedStockRow.cells[3].textContent + " (Stock ID - " + stockID + ")');");

        var imgData = JSON.parse(selectedStockRow.cells[13].textContent);
        for (var i = 0; i < imgData.length; i++) {
            if (imgData[i].includes("f1")) {
                document.getElementById("s-1").src = "assets/img/products/" + imgData[i];
                document.getElementById("rmv-btn-1").style.display = "initial";
            } else if (imgData[i].includes("f2")) {
                document.getElementById("s-2").src = "assets/img/products/" + imgData[i];
                document.getElementById("rmv-btn-2").style.display = "initial";
            } else if (imgData[i].includes("f3")) {
                document.getElementById("s-3").src = "assets/img/products/" + imgData[i];
                document.getElementById("rmv-btn-3").style.display = "initial";
            } else if (imgData[i].includes("f4")) {
                document.getElementById("s-4").src = "assets/img/products/" + imgData[i];
                document.getElementById("rmv-btn-4").style.display = "initial";
            }
        }

        hideAllToolTips();

    } else {
        document.getElementById("product-name").value = "";
        document.getElementById("product-qty").value = "";
        document.getElementById("product-bprice").value = "";
        document.getElementById("product-sprice").value = "";
        document.getElementById("product-discount").value = "";
        document.getElementById("product-warranty").value = "";
        document.getElementById("product-desc").value = "";
        selectedStockRow = null;
        var specRows = document.getElementById("product-spec-table").rows;
        for (var i = 1; i < specRows.length; i++) {
            document.getElementById("product-spec-" + specRows[i].cells[0].textContent).value = "";
        }
        document.getElementById("stock-edit-btn").style.display = "none";
        document.getElementById("stock-del-btn").style.display = "none";
        document.getElementById("stock-deactive-btn").style.display = "none";
    }
}

function priceCheck(stockID) {
    var fromPrice = selectedStockRow.cells[5].textContent;
    var toPrice = document.getElementById("product-sprice").value;
    if (parseFloat(toPrice) == parseFloat(fromPrice)) {
        upDateStock(stockID);
    } else {
        document.getElementById("price-change-from").innerHTML = fromPrice;
        document.getElementById("price-change-to").innerHTML = toPrice;
        document.getElementById("stk-up-add").setAttribute("onclick", "closeModal('price-popup-bg'); upDateStock(" + stockID + ");");
        document.getElementById("stk-up-ext").setAttribute("onclick", "closeModal('price-popup-bg'); upDateStock(" + stockID + ", true);");
        showModal("price-popup-bg");
    }
}

function upDateStock(stockID, updateExt) {
    var catName = document.getElementById("cat-combo-box").value.trim();
    var brandName = document.getElementById("brand-combo-box").value.trim();
    var pName = document.getElementById("product-name").value.trim();
    var pQty = document.getElementById("product-qty").value;
    var bPrice = document.getElementById("product-bprice").value;
    var sPrice = document.getElementById("product-sprice").value;
    var discount = document.getElementById("product-discount").value;
    var warranty = document.getElementById("product-warranty").value;
    var desc = document.getElementById("product-desc").value;

    var specs = {};
    var rows = document.getElementById("product-spec-table").rows;
    for (var i = 1, max = rows.length; i < max; i++) {
        var v = rows[i].cells[2].getElementsByTagName('input')[0].value.trim();
        var specID = rows[i].cells[0].textContent;
        if (v != "") {
            specs[specID] = v;
        }
    }
    var specList = JSON.stringify(specs);

    var formData = new FormData();
    formData.append("stockID", stockID);
    formData.append("catName", catName);
    formData.append("brandName", brandName);
    formData.append("pName", pName);
    formData.append("pQty", pQty);
    formData.append("bPrice", bPrice);
    formData.append("sPrice", sPrice);
    formData.append("discount", discount);
    formData.append("warranty", warranty);
    formData.append("desc", desc);
    formData.append("specList", specList);
    if (updateExt == true) {
        formData.append("updateExt", "true");
    }

    for (var i = 1; i <= 4; i++) {
        var isrc = document.getElementById("s-" + i).src;
        if (isrc.indexOf("assets/img/products/def.png") == -1) {
            formData.append("f" + i + "-img", "yes");
        } else {
            formData.append("f" + i + "-img", "no");
        }
    }

    var f1 = document.getElementById("f-1");
    if (f1.files.length != 0) {
        formData.append("f1", f1.files[0]);
    }
    var f2 = document.getElementById("f-2");
    if (f2.files.length != 0) {
        formData.append("f2", f2.files[0]);
    }
    var f3 = document.getElementById("f-3");
    if (f3.files.length != 0) {
        formData.append("f3", f3.files[0]);
    }
    var f4 = document.getElementById("f-4");
    if (f4.files.length != 0) {
        formData.append("f4", f4.files[0]);
    }

    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadStockTable();
                setActiveStockRow(-1);
                showMsg("Success !", brandName + " " + pName + " (Stock ID " + stockID + ") has been updated.");
            } else if (resp == "pricefailok") {
                loadStockTable();
                setActiveStockRow(-1);
                showMsg("Couldn't update selling price !", "There are invoices already associated with this stock. If you want to change the selling price of this stock, click 'Add as a new Stock'.");
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't add new Specification.");
            } else {
                var msg = "Please Check the following:";
                var errList = JSON.parse(resp);
                for (var i = 0; i < errList.length; i++) {
                    var err = errList[i];
                    if (err == "qty") {
                        showToolTip("product-qty-tt", "Invalid Amount");
                        msg = msg + "<br><br> - The amount you've set as qty is not vaild.";
                    }
                    if (err == "bp") {
                        showToolTip("product-bprice-tt", "Invalid Amount");
                        msg = msg + "<br><br> - The amount you've set as Buying Price is not vaild.";
                    }
                    if (err == "sp") {
                        showToolTip("product-sprice-tt", "Invalid Amount");
                        msg = msg + "<br><br> - The amount you've set as Selling Price is not vaild.";
                    }
                    if (err == "bpmax") {
                        showToolTip("product-bprice-tt", "Buying Price can't be higer than Selling Price");
                        msg = msg + "<br><br> - Buying Price can't be higer than Selling Price.";
                    }
                    if (err == "disc") {
                        showToolTip("product-discount-tt", "Invalid Amount");
                        msg = msg + "<br><br> - The amount you've set as Discount is not vaild. Enter a value between 0 and 100.<br>You can also leave that empty if the product has no discount.";
                    }
                    if (err == "dup") {
                        showToolTip("product-name-tt", "Already Exists");
                        msg = msg + "<br><br> - " + brandName + " " + pName + " in " + catName + " category, already exists.";
                    }
                }
                showMsg("Check Again !", msg);
            }

        }
    };
    req.open("POST", "UpdateProduct", true);
    req.send(formData);

}

function deactiveStock(stockID) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadStockTable();
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't Deactivate the stock.");
            }
        }
    };
    req.open("GET", "UpdateProduct?stockID=" + stockID, true);
    req.send();
}

function showStockDeletePopUp(stockID, stockName) {
    document.getElementById("delete-confirm-btn").setAttribute("onclick", "deleteStock(" + stockID + ");");
    document.getElementById("del-itm").innerHTML = stockName;
    showModal("delete-popup-bg");
}

function deleteStock(stockID) {
    closeModal('delete-popup-bg');
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                loadStockTable();
                setActiveStockRow(-1);
            } else if (resp == "ex-inv") {
                showMsg("Can't Delete !", "There are invoice items associated with this product.");
            } else if (resp == "ex-cart") {
                showMsg("Can't Delete !", "There are users who have cart items associated with this product.");
            } else if (resp == "ex-rev") {
                showMsg("Can't Delete !", "There are products reviews of users associated with this product.");
            } else {
                showMsg("An Error Occurred !", "Couldn't Delete Stock.");
            }
        }
    };
    req.open("GET", "DeleteProduct?stockID=" + stockID, true);
    req.send();
}

function moveSpecUp() {
    var specID = selectedSpecRow.cells[0].textContent;

    var rows = document.getElementById("spec-table").rows;
    var previousSpecID;
    for (var i = 0; i < rows.length; i++) {
        if (parseInt(rows[i].cells[0].textContent) == specID) {
            previousSpecID = parseInt(rows[i - 1].cells[0].textContent);
            break;
        }
    }
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                setActiveSpecRow(previousSpecID);
                loadSpecTable(selectedCatRow.cells[0].textContent);
                loadStockTable();
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't re-arrange.");
            }
        }
    };
    req.open("GET", "MoveSpec?specID=" + specID + "&previousSpecID=" + previousSpecID, true);
    req.send();
}

function moveSpecDown() {
    var specID = selectedSpecRow.cells[0].textContent;

    var rows = document.getElementById("spec-table").rows;
    var nextSpecID;
    for (var i = 0; i < rows.length; i++) {
        if (parseInt(rows[i].cells[0].textContent) == specID) {
            nextSpecID = parseInt(rows[i + 1].cells[0].textContent);
            break;
        }
    }
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                setActiveSpecRow(nextSpecID);
                loadSpecTable(selectedCatRow.cells[0].textContent);
                loadStockTable();
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't re-arrange.");
            }
        }
    };
    req.open("GET", "MoveSpec?specID=" + nextSpecID + "&previousSpecID=" + specID, true);
    req.send();
}