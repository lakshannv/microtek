<%
    if (session.getAttribute("cust") == null) {
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek SignUp</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body style="background-image: url('assets/img/bgs/signup.jpg');" onload="loadDistricts(); setIMGHeight();">
        
        <jsp:include page="incl_msgbox.jsp" />
        
        <div class="container d-flex justify-content-center signin-div">
            <div class="row d-flex align-items-center sig-div">
                <div class="col d-flex flex-column align-items-center">
                    <div data-aos="zoom-in-up" class="signin-form">
                        <h4><i class="fas fa-grin-tongue-wink"></i>New Here?</h4>
                        <p>Sign Up with MicroTek to enjoy the all new featues of MicroTek &amp; get the best possible deals in Sri Lanka for PC hardware...</p>
                    </div>
                    <div data-aos="zoom-in-up" data-aos-delay="300" class="img-pane">
                        <img id="s" src="assets/img/avatars/avt.png">
                        <div><input class="d-none" type="file" id="f" accept="image/*" onchange="viewIMG('f', 's', 'img-rmv-btn', 'assets/img/avatars/avt.png');"></div>
                        <div class="d-flex justify-content-around field-row">
                            <button class="btn btn-primary" type="button" onclick="openFC('f');"><i class="fas fa-camera"></i>Choose a photo</button>
                            <button class="btn btn-primary" id="img-rmv-btn" type="button" onclick="removeIMG('f', 's', 'img-rmv-btn', 'assets/img/avatars/avt.png');" style="display: none;"><i class="fas fa-trash-alt"></i>Remove photo</button>
                        </div>
                    </div>
                </div>
                <div class="col d-flex justify-content-center">
                    <div data-aos="zoom-in-up" data-aos-delay="150" class="signin-form">
                        <h4 style="margin-bottom: 20px;"><i class="fas fa-user-plus"></i>Sign Up - Details</h4>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>First Name</span><input id="fn" oninput="hideToolTip('fn-tt');" type="text" placeholder="Enter your first name"><span class="shake animated field-tooltip" id="fn-tt"></span></div>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>Last Name</span><input id="ln" oninput="hideToolTip('ln-tt');" type="text" placeholder="Enter your last name"><span class="shake animated field-tooltip" id="ln-tt"></span></div>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>Mobile</span><input id="mob" oninput="hideToolTip('mob-tt');" type="text" placeholder="Enter your mobile number"><span class="shake animated field-tooltip" id="mob-tt"></span></div>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>E - mail</span><input id="eml" oninput="hideToolTip('eml-tt');" type="text" placeholder="Enter your email address"><span class="shake animated field-tooltip" id="eml-tt"></span></div>
                        <div class="d-flex justify-content-between align-items-center field-row">
                            <span>Address</span><textarea id="addr" oninput="hideToolTip('addr-tt');" placeholder="This will be your default shipping address"></textarea><span class="shake animated field-tooltip" id="addr-tt"></span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center field-row">
                            <div class="input-group-prepend">
                                <label class="input-group-text">Province</label>
                                <select id="province" onchange="loadDistricts();" class="shadow-lg search-select">
                                    <jsp:include page="get_provinces.jsp" />
                                </select>
                            </div>
                        </div>
                        <div class="d-flex justify-content-between align-items-center field-row">
                            <div class="input-group-prepend">
                                <label class="input-group-text">District</label>
                                <select id="district" onchange="loadCities();" class="shadow-lg search-select">

                                </select>
                            </div>
                        </div>
                        <div class="d-flex justify-content-between align-items-center field-row">
                            <div class="input-group-prepend">
                                <label class="input-group-text">City</label>
                                <select id="city" class="shadow-lg search-select">

                                </select>
                            </div>
                        </div>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>User Name</span><input id="un" oninput="hideToolTip('un-tt');" type="text" placeholder="Enter any username you like"><span class="shake animated field-tooltip" id="un-tt"></span></div>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>Password</span><input id="pw" oninput="hideToolTip('pw-tt');" type="password" placeholder="Enter a strong password"><span class="shake animated field-tooltip" id="pw-tt"></span></div>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>Confirm</span><input id="pw-con" oninput="hideToolTip('pw-con-tt');" type="password" placeholder="Re-type the password again"><span class="shake animated field-tooltip" id="pw-con-tt"></span></div>
                        <div class="d-flex justify-content-between field-row"><button class="btn btn-primary" type="button" onclick="goToLogin();"><i class="fas fa-user-check"></i>Already a user?</button><button onclick="signUp();" class="btn btn-primary" type="button"><i class="fas fa-user-plus"></i>Sign Up</button></div>
                    </div>
                </div>
            </div>
        </div>
        <script src="assets/js/jquery.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="assets/js/chart.min.js"></script>
        <script src="assets/js/bs-init.js"></script>
        <script src="assets/js/aos.js"></script>
        <script src="assets/js/srcipt.js"></script>
        <script src="assets/js/signup.js"></script>
    </body>
</html>
<%
    } else {
        response.sendRedirect("search.jsp");
    }
%>