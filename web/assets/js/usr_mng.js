function loadUsers() {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            document.getElementById("usr-tbody").innerHTML = req.responseText;
            loadUserTypes();
        }
    }
    req.open("GET", "get_users.jsp", true);
    req.send();
}

function loadUserTypes() {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            document.getElementById("usr-type-tbody").innerHTML = req.responseText;
            var rows = document.getElementById("usr-type-table").rows;
            var ops = "";
            for (var i = 1; i < rows.length; i++) {
                ops += "<option value=\"" + rows[i].id.substr(7) + "\">" + rows[i].cells[0].textContent + "</option>";
            }
            document.getElementById("usr-type-cmb").innerHTML = ops;
        }
    }
    req.open("GET", "get_user_types.jsp", true);
    req.send();
}

function enableBtn(uID) {
    var btn = document.getElementById("usr-save-btn-" + uID);
    btn.disabled = false;
    btn.innerHTML = "<i class=\"far fa-save\"></i>Save";
}

function togglePassWord(uID) {
    var btn = document.getElementById("pw-btn-" + uID);
    var PW = document.getElementById("usr-pwbox-" + uID);
    if (PW.type === "password") {
        PW.type = "text";
        btn.innerHTML = "<i class=\"far fa-eye-slash\"></i>";
    } else {
        PW.type = "password";
        btn.innerHTML = "<i class=\"far fa-eye\"></i>";
    }
}

function showDeleteUserPopup(uID) {
    document.getElementById("del-itm").innerHTML = "this user";
    document.getElementById("delete-confirm-btn").setAttribute("onclick", "deleteUser(" + uID + ");");
    showModal("delete-popup-bg");
}

function deleteUser(uID) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                closeModal("delete-popup-bg");
                loadUsers();
            } else if (resp == "err") {
                showMsg("Somthing went wrong !", "Couldn't delete user.");
            }
        }
    }
    req.open("GET", "DeleteUser?uID=" + uID, true);
    req.send();
}

function showDeleteUserTypePopup(uTypeID) {
    document.getElementById("del-itm").innerHTML = document.getElementById("ut-row-" + uTypeID).cells[0].textContent;
    document.getElementById("delete-confirm-btn").setAttribute("onclick", "deleteUserType(" + uTypeID + ");");
    showModal("delete-popup-bg");
}

function deleteUserType(uTypeID) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                closeModal("delete-popup-bg");
                loadUsers();
            } else if (resp == "err") {
                showMsg("Somthing went wrong !", "Couldn't delete User Type.");
            }
        }
    }
    req.open("GET", "DeleteUserType?uTypeID=" + uTypeID, true);
    req.send();
}

function blockUser(uID) {
    var btn = document.getElementById("usr-block-btn-" + uID);
    var block = false;
    var blockText = "<i class=\"fas fa-ban\"></i>Block";
    var unblockText = "<i class=\"fas fa-chevron-circle-up\"></i></i>Unblock";
    if (btn.getAttribute("isActive") == 1) {
        block = true;
    }
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                if (btn.getAttribute("isActive") == 1) {
                    btn.setAttribute("isActive", 0);
                    btn.innerHTML = unblockText;
                } else {
                    btn.setAttribute("isActive", 1);
                    btn.innerHTML = blockText;
                }
            } else if (resp == "err") {
                showMsg("Somthing went wrong !", "Couldn't block user.");
            }
        }
    }
    req.open("GET", "DeleteUser?uID=" + uID + "&block=" + block, true);
    req.send();
}

function updateUser(uID) {
    var btn = document.getElementById("usr-save-btn-" + uID);
    btn.disabled = true;
    btn.innerHTML = "<i class=\"far fa-save\"></i>Saving...";
    var uname = document.getElementById("usr-txtbox-" + uID).value.trim();
    var pw = document.getElementById("usr-pwbox-" + uID).value;
    var uType = document.getElementById("cmb-" + uID).value;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                btn.innerHTML = "<i class=\"far fa-save\"></i>Saved !";
                loadUserTypes();
            } else if (resp == "un") {
                showMsg("Invalid User Name !", "Please check the username you've entered again.");
                enableBtn(uID);
            } else if (resp == "pw") {
                showMsg("Invalid Password !", "Please check the password you've entered again.");
                enableBtn(uID);
            } else if (resp == "dup") {
                showMsg("Duplicate UserName !", "Username '" + uname + "' already exists. Try another username.");
                enableBtn(uID);
            } else if (resp == "err") {
                showMsg("Somthing went wrong !", "Couldn't update user details.");
                enableBtn(uID);
            }
        }
    }
    req.open("POST", "UpdateUser", true);
    req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    req.send("uID=" + uID + "&uname=" + uname + "&pw=" + pw + "&uType=" + uType);
}

function addNewUser() {
    var uname = document.getElementById("un").value.trim();
    var pw = document.getElementById("pw").value;
    var uType = document.getElementById("usr-type-cmb").value;
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                closeModal("usr-popup-bg");
                document.getElementById("un").value = "";
                document.getElementById("pw").value = "";
                loadUsers();
            } else if (resp == "un") {
                showToolTip("un-tt", "Invalid User Name");
            } else if (resp == "pw") {
                showToolTip("pw-tt", "Invalid Passsword");
            } else if (resp == "dup") {
                showToolTip("un-tt", "Already Exists");
            } else if (resp == "err") {
                showMsg("Somthing went wrong !", "Couldn't add new user.");
            }
        }
    }
    req.open("POST", "AddNewUser", true);
    req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    req.send("uname=" + uname + "&pw=" + pw + "&uType=" + uType);
}

function addNewUserType() {
    var typeName = document.getElementById("ut").value.trim();
    var prvList = [];
    for (var i = 1, max = 6; i <= max; i++) {
        if (document.getElementById("prv-" + i).checked) {
            prvList.push(i);
        }
    }
    var prvData = JSON.stringify(prvList);
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                closeModal("usr-type-popup-bg");
                loadUsers();
            } else if (resp == "inv") {
                showToolTip("ut-tt", "Invalid User Type Name");
            } else if (resp == "dup") {
                showToolTip("ut-tt", "Already Exists");
            } else if (resp == "prv") {
                showMsg("Choose Privileges !", "A User type must have at least one privilege.");
            } else if (resp == "err") {
                showMsg("Somthing went wrong !", "Couldn't Add the User Type.");
            }
        }
    }
    req.open("GET", "AddNewUserType?typeName=" + typeName + "&prvData=" + prvData, true);
    req.send();
}

function updateUserType(uTypeID) {
    var typeName = document.getElementById("ut").value.trim();
    var prvList = [];
    for (var i = 1, max = 6; i <= max; i++) {
        if (document.getElementById("prv-" + i).checked) {
            prvList.push(i);
        }
    }
    var prvData = JSON.stringify(prvList);
    var req = new XMLHttpRequest();
    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            var resp = req.responseText;
            if (resp == "ok") {
                closeModal("usr-type-popup-bg");
                loadUsers();
            } else if (resp == "inv") {
                showToolTip("ut-tt", "Invalid User Type Name");
            } else if (resp == "dup") {
                showToolTip("ut-tt", "Already Exists");
            } else if (resp == "prv") {
                showMsg("Choose Privileges !", "A User type must have at least one privilege.");
            } else if (resp == "err") {
                showMsg("Somthing went wrong !", "Couldn't update User Type.");
            }
        }
    }
    req.open("GET", "UpdateUserType?uTypeID=" + uTypeID + "&typeName=" + typeName + "&prvData=" + prvData, true);
    req.send();
}

function showNewUserTypePopup() {
    document.getElementById("usr-type-popup-title").innerHTML = "<i class=\"fas fa-user-cog\"></i>Add New User Type";
    var btn = document.getElementById("usr-type-popup-btn");
    btn.setAttribute("onclick", "addNewUserType();");
    btn.innerHTML = "<i class=\"far fa-save\"></i>Add New User Type";
    document.getElementById("ut").value = "";
    document.getElementById("prv-1").checked = false;
    document.getElementById("prv-2").checked = false;
    document.getElementById("prv-3").checked = false;
    document.getElementById("prv-4").checked = false;
    document.getElementById("prv-5").checked = false;
    document.getElementById("prv-6").checked = false;
    showModal("usr-type-popup-bg");
}

function showUpdateUserTypePopup(uTypeID) {
    document.getElementById("usr-type-popup-title").innerHTML = "<i class=\"fas fa-user-cog\"></i>Update User Type";
    var btn = document.getElementById("usr-type-popup-btn");
    btn.setAttribute("onclick", "updateUserType(" + uTypeID + ");");
    btn.innerHTML = "<i class=\"fas fa-user-check\"></i>Update";

    var row = document.getElementById("ut-row-" + uTypeID);
    document.getElementById("ut").value = row.cells[0].textContent;
    var prvList = JSON.parse(row.cells[3].textContent);
    document.getElementById("prv-1").checked = false;
    document.getElementById("prv-2").checked = false;
    document.getElementById("prv-3").checked = false;
    document.getElementById("prv-4").checked = false;
    document.getElementById("prv-5").checked = false;
    document.getElementById("prv-6").checked = false;
    for (var i = 0, max = prvList.length; i < max; i++) {
        document.getElementById("prv-" + prvList[i]).checked = true;
    }
    showModal("usr-type-popup-bg");
}