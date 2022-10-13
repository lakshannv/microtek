function deleteReview(pID, isQuickFeedBack) {
    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            resp = req.responseText;
            if (resp == "ok") {
                if (isQuickFeedBack) {
                    fbBtn = document.getElementById("feedback-btn-" + pID);
                    fbBtn.innerHTML = "FeedBack";
                    fbBtn.setAttribute("onclick", "openFeedBackDialog(" + pID + ");");
                    closeModal("rev-popup-bg");
                } else {
                    stkID = getParameter("id");
                    window.location = "product.jsp?id=" + stkID + "&action=rev";
                }
            } else if (resp == "err") {
                showMsg("An error occured !", "Couldn't delete your review.");
            }
        }
    };
    req.open("GET", "DeleteReview?pID=" + pID, true);
    req.send();
}

function updateReview(pID, isQuickFeedBack) {
    var desc = document.getElementById("rev-desc").value.trim();
    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            resp = req.responseText;
            if (resp == "ok") {
                if (isQuickFeedBack) {
                    fbBtn = document.getElementById("feedback-btn-" + pID);
                    fbBtn.innerHTML = "<i class=\"fa fa-check-circle\"></i>FeedBack";
                    fbBtn.setAttribute("onclick", "openFeedBackDialog(" + pID + ", true);");
                    closeModal("rev-popup-bg");
                    document.getElementById("rev-rat-" + pID).innerHTML = custRating;
                    document.getElementById("rev-cont-" + pID).textContent = desc;
                } else {
                    stkID = getParameter("id");
                    window.location = "product.jsp?id=" + stkID + "&action=rev";
                }
            } else if (resp == "emp") {
                showMsg("Invalid Review", "Your Review can't be empty.");
            } else if (resp == "err") {
                showMsg("An error occured !", "Couldn't update your review.");
            }
        }
    };
    req.open("POST", "UpdateReview", true);
    req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    req.send("pID=" + pID + "&rating=" + custRating + "&desc=" + desc);
}

function addReview(pID, isQuickFeedBack) {
    var desc = document.getElementById("rev-desc").value.trim();
    var req = new XMLHttpRequest();

    req.onreadystatechange = function () {
        if (req.readyState == 4 && req.status == 200) {
            resp = req.responseText;
            if (resp == "ok") {
                if (isQuickFeedBack) {
                    fbBtn = document.getElementById("feedback-btn-" + pID);
                    fbBtn.innerHTML = "<i class=\"fa fa-check-circle\"></i>FeedBack";
                    fbBtn.setAttribute("onclick", "openFeedBackDialog(" + pID + ", true);");
                    closeModal("rev-popup-bg");
                    document.getElementById("rev-rat-" + pID).innerHTML = custRating;
                    document.getElementById("rev-cont-" + pID).textContent = desc;
                } else {
                    stkID = getParameter("id");
                    window.location = "product.jsp?id=" + stkID + "&action=rev";
                }
            } else if (resp == "rat") {
                showMsg("Invalid Rating", "Your must give at least one star for your review.");
            } else if (resp == "emp") {
                showMsg("Invalid Review", "Your Review can't be empty.");
            } else if (resp == "err") {
                showMsg("An error occured !", "Couldn't update your review.");
            }
        }
    };
    req.open("POST", "AddReview", true);
    req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    req.send("pID=" + pID + "&rating=" + custRating + "&desc=" + desc);
}

function openFeedBackDialog(pID, exists) {
    document.getElementById("rev-prod").innerHTML = "<i class=\"fas fa-box-open\"></i>" + document.getElementById("prod-link-" + pID).textContent + "<br>";
    var auBtn = document.getElementById("au-btn");
    var delBtn = document.getElementById("rev-del-btn");
    if (exists) {
        delBtn.style.display = "initial";
        auBtn.setAttribute("onclick", "updateReview(" + pID + ", true);");
        delBtn.setAttribute("onclick", "deleteReview(" + pID + ", true);");
        auBtn.innerHTML = "<i class=\"fas fa-edit\"></i>" + "Update Review";
        var rat = parseInt(document.getElementById("rev-rat-" + pID).textContent);
        var cont = document.getElementById("rev-cont-" + pID).textContent;
        setRating(rat);
        document.getElementById("rev-desc").value = cont;
    } else {
        delBtn.style.display = "none";
        auBtn.setAttribute("onclick", "addReview(" + pID + ", true);");
        auBtn.innerHTML = "<i class=\"fas fa-edit\"></i>" + "Post Review";
        setRating(0);
        document.getElementById("rev-desc").value = "";
    }
    showModal('rev-popup-bg');

}