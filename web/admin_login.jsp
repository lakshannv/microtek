<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    if (session.getAttribute("usr") == null) {
%>
<html>
    <head>
        <title>MicroTek Admin Login</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body style="background-image: radial-gradient(rgba(0,0,0,0), rgb(0,0,0)), url('assets/img/bgs/login.jpg');">
        <jsp:include page="incl_msgbox.jsp" />
        <div class="container d-flex justify-content-center align-items-center login-form-div">
            <div class="jello animated login-form">
                <h4><i class="fas fa-user-shield"></i>MicroTek Admin Login</h4>
                <div class="d-flex justify-content-between align-items-center field-row"><span>User Name</span><input id="un" oninput="hideToolTip('un-tt');" type="text"><span id="un-tt" class="shake animated field-tooltip"></span></div>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Password</span><input id="pw" oninput="hideToolTip('pw-tt');" type="password"><span id="pw-tt" class="shake animated field-tooltip"></span></div>
                <div class="d-flex justify-content-end align-items-center field-row">
                    <div class="d-inline-flex flex-row check-box-div"><label class="chklabel">Remember Me<input id="rem" type="checkbox" checked=""><span class="checkmark"></span></label></div>
                </div>
                <div class="d-flex align-items-center field-row"><button class="btn btn-primary flex-fill" type="button" onclick="login();"><i class="fas fa-user-shield"></i>Login</button></div>
            </div>
        </div>
        <script src="assets/js/jquery.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="assets/js/chart.min.js"></script>
        <script src="assets/js/bs-init.js"></script>
        <script src="assets/js/aos.js"></script>
        <script src="assets/js/Product-Viewer-1.js"></script>
        <script src="assets/js/Product-Viewer.js"></script>
        <script src="assets/js/srcipt.js"></script>
        <script src="assets/js/Swiper-Slider.js"></script>
        <script src="assets/js/admin_login.js"></script>
    </body>
</html>
<%
    } else {
        request.getRequestDispatcher("AdminRedirect").forward(request, response);
    }
%>