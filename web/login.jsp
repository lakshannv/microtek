<%
    if (session.getAttribute("cust") == null) {
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek Login</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body style="background-image: radial-gradient(rgba(0,0,0,0), rgb(0,0,0)), url('assets/img/bgs/login.jpg');">
        <jsp:include page="incl_msgbox.jsp" />

        <div id="forgot-popup-bg" class="admin-popup-bg" style="display: none;">
            <div class="d-flex flex-column pulse animated modal-pop" id="forgot-popup"><i class="fa fa-close close-btn" onclick="closeModal('forgot-popup-bg');"></i>
                <h5><i class="fas fa-key"></i>Reset Your Password</h5>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Forgot your password ? or username ? or both ?<br>Don't worry. We've got you covered.<br></span></div>
                <div class="d-flex justify-content-between align-items-center field-row"><span>E-mail</span><input id="eml" oninput="hideToolTip('eml-tt');" type="text"><span class="shake animated field-tooltip" id="eml-tt"></span></div>
                <div class="d-flex justify-content-around align-items-center r-field">
                    <button class="btn btn-primary" type="button" onclick="sendOTP();" id="send-otp-btn"><i class="fas fa-mail-bulk"></i>Send OTP</button>
                </div>

                <div class="otp" id="otp-div" style="display: none;"><strong><span>Hello <span id="usr-name"></span> !</strong><br>An OTP has been sent&nbsp; to your email.<br>Please check your email and enter the OTP<br>you received below.<br></span>
                    <div class="d-flex justify-content-between align-items-center field-row"><span>OTP</span><input id="otp" oninput="hideToolTip('otp-tt');" type="text" class="text-center"><span class="shake animated field-tooltip" id="otp-tt"></span></div>
                    <button class="btn btn-primary" type="button" id="submit-otp-btn" onclick="submitOTP();"><i class="fas fa-upload"></i>Submit</button>
                </div>
            </div>
        </div>
        <div id="reset-popup-bg" class="admin-popup-bg" style="display: none;">
            <div class="d-flex flex-column pulse animated modal-pop" id="reset-popup"><i class="fa fa-close close-btn" onclick="closeModal('reset-popup-bg');"></i>
                <h5><i class="fas fa-key"></i>Reset Your Password</h5><span>Please enter a strong new password. And this time,<br>make sure you remember it. In case you've forgotten<br>your username also, it's <span id="usr-name-rs"></span>.</span>
                <div class="d-flex justify-content-between align-items-center field-row"><span>New Password</span><input type="password" id="new-pw"><span class="shake animated field-tooltip" id="new-pw-tt"></span></div>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Confirm</span><input type="password" id="pw-con"><span class="shake animated field-tooltip" id="pw-con-tt"></span></div>
                <div class="d-flex justify-content-around align-items-center r-field">
                    <button class="btn btn-primary flex-fill" type="button" id="reset-btn"><i class="fas fa-user-shield"></i>Reset Password</button>
                </div>
            </div>
        </div>

        <div class="container d-flex justify-content-center align-items-center login-form-div">
            <div class="jello animated login-form">
                <h4 onclick="goToURL('index.jsp');" style="cursor: pointer;"><i class="fas fa-user-shield"></i>MicroTek Login</h4>
                <div class="d-flex justify-content-between align-items-center field-row"><span>User Name</span><input id="un" oninput="hideToolTip('un-tt');" type="text"><span id="un-tt" class="shake animated field-tooltip"></span></div>
                <div class="d-flex justify-content-between align-items-center field-row"><span>Password</span><input id="pw" oninput="hideToolTip('pw-tt');" type="password"><span id="pw-tt" class="shake animated field-tooltip"></span></div>
                <div class="d-flex justify-content-between align-items-center field-row"><a href="#" onclick="showModal('forgot-popup-bg');">Forgot Password ?</a>
                    <div class="d-inline-flex flex-row check-box-div"><label class="chklabel">Remember Me<input id="rem" type="checkbox" checked=""><span class="checkmark"></span></label></div>
                </div>
                <div class="d-flex justify-content-between align-items-center field-row">
                    <button class="btn btn-primary" type="button" onclick="goToSignUp();"><i class="fas fa-user-plus"></i>New Here ?</button>
                    <button class="btn btn-primary" type="button" onclick="login();"><i class="fas fa-user-shield"></i>Login</button>
                </div>
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
        <script src="assets/js/login.js"></script>
    </body>
</html>
<%
    } else {
        response.sendRedirect("index.jsp");
    }
%>
