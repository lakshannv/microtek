var remainingTime;
var st = document.getElementById("rem");

var intID;

function startTimeOut(t) {
    remainingTime = Number(t);
    var d = Math.floor(remainingTime / (3600 * 24));
    var h = Math.floor(remainingTime % (3600 * 24) / 3600);
    var m = Math.floor(remainingTime % 3600 / 60);
    var s = Math.floor(remainingTime % 60);

    var dDisplay = d > 0 ? d + (d == 1 ? " day, " : " Days, ") : "";
    var hDisplay = h > 0 ? h + (h == 1 ? " hour, " : " Hours, ") : "";
    var mDisplay = m > 0 ? m + (m == 1 ? " minute, " : " Minutes, ") : "";
    var sDisplay = s > 0 ? s + (s == 1 ? " second" : " Seconds") : "";
    st.innerHTML = dDisplay + hDisplay + mDisplay + sDisplay;

    intID = setInterval(countDown, 1000);
}

function countDown() {
    if (remainingTime > 0) {
        remainingTime--;
        var d = Math.floor(remainingTime / (3600 * 24));
        var h = Math.floor(remainingTime % (3600 * 24) / 3600);
        var m = Math.floor(remainingTime % 3600 / 60);
        var s = Math.floor(remainingTime % 60);

        var dDisplay = d > 0 ? d + (d == 1 ? " day, " : " Days, ") : "";
        var hDisplay = h > 0 ? h + (h == 1 ? " hour, " : " Hours, ") : "";
        var mDisplay = m > 0 ? m + (m == 1 ? " minute, " : " Minutes, ") : "";
        var sDisplay = s > 0 ? s + (s == 1 ? " second" : " Seconds") : "";
        var s = dDisplay + hDisplay + mDisplay + sDisplay;
        if (s == "") {
            st.parentNode.innerHTML = "Heading Home...";
        } else {
            st.innerHTML = s;
        }
    } else {
        clearInterval(intID);
        window.location = "index.jsp";
    }
}