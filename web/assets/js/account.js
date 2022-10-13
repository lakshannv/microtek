function setIMGHeight() {
    var w = document.getElementById("s").offsetWidth;
    document.getElementById("s").style.height = w + "px";
}

var selectedAddrRow;
function setActiveAddressRow(addrID) {
    var rows = document.getElementById("addr-table").rows;
    var hasAddrID = false;
    for (var i = 0; i < rows.length; i++) {
        if (!hasAddrID) {
            if (parseInt(rows[i].cells[1].textContent) == addrID) {
                hasAddrID = true;
            }
        }
        rows[i].className = "";
    }
    if (hasAddrID) {
        selectedAddrRow = document.getElementById("addr-tr-" + addrID);
        selectedAddrRow.className = "active";
        document.getElementById("addr-edit-btn").style.display = "initial";
        document.getElementById("addr-del-btn").style.display = "initial";
        document.getElementById("addr-del-btn").setAttribute("onclick", "deleteAddress(" + selectedAddrRow.cells[1].textContent + ");");
    } else {
        document.getElementById("addr-edit-btn").style.display = "none";
        document.getElementById("addr-del-btn").style.display = "none";
    }
}

function showNewAddressPopup() {
    document.getElementById("addr-title").innerHTML = "<i class=\"fas fa-map-marker-alt\"></i>Add New Address";
    document.getElementById("addr").value = "";
    var auBtn = document.getElementById("au-btn");
    auBtn.innerHTML = "<i class=\"fas fa-plus-circle\"></i>Add";
    auBtn.setAttribute("onclick", "addAddress();");
    showModal('addr-popup-bg');
}

function showUpdateAddressPopup() {
    document.getElementById("addr-title").innerHTML = "<i class=\"fas fa-map-marker-alt\"></i>Update Address";
    document.getElementById("addr").value = selectedAddrRow.cells[2].textContent;
    document.getElementById("province").value = selectedAddrRow.cells[5].textContent;


    var provinceID = document.getElementById("province").value;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            document.getElementById("district").innerHTML = req.responseText;
            document.getElementById("district").value = selectedAddrRow.cells[4].textContent;
        }
    };
    req.open("GET", "get_districts.jsp?provinceID=" + provinceID, true);
    req.send();



    var distID = selectedAddrRow.cells[4].textContent;
    var req2 = new XMLHttpRequest();
    req2.onreadystatechange = function () {
        if (req2.readyState == 4 && req2.status == 200) {
            document.getElementById("city").innerHTML = req2.responseText;
            document.getElementById("city").value = selectedAddrRow.cells[3].textContent;
        }
    };
    req2.open("GET", "get_cities.jsp?distID=" + distID, true);
    req2.send();


    var auBtn = document.getElementById("au-btn");
    auBtn.innerHTML = "<i class=\"fas fa-edit\"></i>Update";
    auBtn.setAttribute("onclick", "updateAddress(" + selectedAddrRow.cells[1].textContent + ");");
    showModal('addr-popup-bg');
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
                loadAddresses();
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

function loadAddresses() {
    document.getElementById("addr-edit-btn").style.display = "none";
    document.getElementById("addr-del-btn").style.display = "none";
    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            document.getElementById("addr-table-body").innerHTML = req.responseText;
        }
    };
    req.open("GET", "get_acc_addresses.jsp", true);
    req.send();
}

function updateAddress(addrID) {
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
                loadAddresses();
                closeModal("addr-popup-bg");
                document.getElementById("addr").value = "";
            } else if (resp == "inv") {
                showToolTip("addr-tt", "Invalid Address");
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't update the address.");
            }
        }
    };
    req.open("GET", "UpdateAddress?addrID=" + addrID + "&addr=" + encodeURIComponent(addr) + "&provinceID=" + provinceID + "&districtID=" + districtID + "&cityID=" + cityID, true);
    req.send();
}

function deleteAddress(addrID) {
    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            resp = req.responseText;
            if (resp == "ok") {
                loadAddresses();
            } else if (resp == "exi") {
                showMsg("Already in use !", "Can't delete this address because there're invoices related to this address.");
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't update the address.");
            }
        }
    };
    req.open("GET", "DeleteAddress?addrID=" + addrID, true);
    req.send();
}

function upDateDetails() {
    hideAllToolTips();
    var fn = document.getElementById("fn").value.trim();
    var ln = document.getElementById("ln").value.trim();
    var mob = document.getElementById("mob").value.trim();
    var eml = document.getElementById("eml").value.trim();

    var formData = new FormData();
    formData.append("fn", fn);
    formData.append("ln", ln);
    formData.append("mob", mob);
    formData.append("eml", eml);

    var isrc = document.getElementById("s").src;
    if (isrc.indexOf("assets/img/avatars/avt.png") == -1) {
        formData.append("hasImage", "true");
    }

    var f = document.getElementById("f");
    if (f.files.length != 0) {
        formData.append("f", f.files[0]);
    }

    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                showMsg("Success !", "Details have been updated.");
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't update details.");
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
                }
                showMsg("Check Again !", msg);
            }

        }
    };
    req.open("POST", "UpdateCustomer", true);
    req.send(formData);
}

function resetPassword() {
    hideAllToolTips();
    var oldPw = document.getElementById("pw-old").value;
    var pw = document.getElementById("pw").value;
    var pwCon = document.getElementById("pw-con").value;

    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                document.getElementById("pw-old").value = "";
                document.getElementById("pw").value = "";
                document.getElementById("pw-con").value = "";
                showMsg("Success !", "Password has been reset.");
            } else if (resp == "err") {
                showMsg("An Error Occurred !", "Couldn't reset password.");
            } else {
                var msg = "Please Check the following:";
                var errList = JSON.parse(resp);
                for (var i = 0; i < errList.length; i++) {
                    var err = errList[i];
                    if (err == "inv-pw") {
                        showToolTip("pw-tt", "Empty Password");
                        msg = msg + "<br><br> - The new password cannot be empty.";
                    }
                    if (err == "wrong-pw") {
                        showToolTip("pw-old-tt", "Wrong Password");
                        msg = msg + "<br><br> - Please enter correct current password.";
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
    req.open("POST", "ResetPassword", true);
    req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    req.send("oldPw=" + oldPw + "&pw=" + pw + "&pwCon=" + pwCon);
}