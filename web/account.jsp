<%@page import="model.Validation"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="hibernate.Customer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    SimpleDateFormat sdf = new SimpleDateFormat("yyy/MM/dd hh:mm a");
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    Customer sesCust = (Customer) session.getAttribute("cust");
    Customer cust = (Customer) s.load(Customer.class, sesCust.getId());
%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek Account</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body style="background-image: url('assets/img/bgs/search.jpg');" onload="loadDistricts(); setIMGHeight(); loadCart(); loadWishlist();">
        <jsp:include page="incl_navbar.jsp" />
        <jsp:include page="incl_msgbox.jsp" />
        <div id="addr-popup-bg" class="admin-popup-bg" style="display: none;" onclick="closeModalBG('addr-popup-bg');">
            <div class="d-flex flex-column pulse animated modal-pop" id="addr-popup"><i class="fa fa-close close-btn" onclick="closeModal('addr-popup-bg');"></i>
                <h5 id="addr-title"><i class="fas fa-map-marker-alt"></i>Add New Address</h5>
                <div class="d-flex field-row">
                    <textarea class="flex-fill" id="addr" oninput="hideToolTip('addr-tt');" placeholder="Enter your Home / Office or Apartment No, Street No etc."></textarea>
                    <span class="shake animated field-tooltip" id="addr-tt"></span>
                </div>
                <div class="d-flex justify-content-between align-items-center field-row">
                    <div class="input-group-prepend"><label class="input-group-text">Province</label>
                        <select id="province" onchange="loadDistricts();" class="shadow-lg search-select">
                            <jsp:include page="get_provinces.jsp" />
                        </select>
                    </div>
                </div>
                <div class="d-flex justify-content-between align-items-center field-row">
                    <div class="input-group-prepend"><label class="input-group-text">District</label>
                        <select id="district" onchange="loadCities();" class="shadow-lg search-select">

                        </select>
                    </div>
                </div>
                <div class="d-flex justify-content-between align-items-center field-row">
                    <div class="input-group-prepend"><label class="input-group-text">City</label>
                        <select id="city" class="shadow-lg search-select">

                        </select>
                    </div>
                </div>
                <div class="d-flex justify-content-end align-items-center r-field">
                    <button class="btn btn-primary" type="button" id="au-btn"></button>
                </div>
            </div>
        </div>

        <div class="container" style="margin-top: 80px;">
            <div class="row">
                <div class="col-12 col-lg-7">
                    <div data-aos="zoom-in-up" class="acc-det">
                        <h4 style="margin-bottom: 20px;"><i class="fas fa-address-card"></i>Customer Details</h4>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>First Name</span><input id="fn" oninput="hideToolTip('fn-tt');" type="text" placeholder="Enter your first name" value="<%=cust.getFname()%>"><span class="shake animated field-tooltip" id="fn-tt"></span></div>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>Last Name</span><input id="ln" oninput="hideToolTip('ln-tt');" type="text" placeholder="Enter your last name" value="<%=cust.getLname()%>"><span class="shake animated field-tooltip" id="ln-tt"></span></div>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>Mobile</span><input id="mob" oninput="hideToolTip('mob-tt');" type="text" placeholder="Enter your mobile number" value="<%=cust.getMobile()%>"><span class="shake animated field-tooltip" id="mob-tt"></span></div>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>E - mail</span><input id="eml" oninput="hideToolTip('eml-tt');" type="text" placeholder="Enter your email address" value="<%=cust.getEmail()%>"><span class="shake animated field-tooltip" id="eml-tt"></span></div>
                        <div class="row justify-content-between table-control-div">
                            <div class="col-sm-auto col-md-auto col-lg-auto col-xl-auto">
                                <h4><i class="fas fa-map-marker-alt"></i>Saved addresses</h4>
                            </div>
                            <div class="col-auto">
                                <div class="d-flex justify-content-xl-end btn-row">
                                    <button id="addr-del-btn" class="btn btn-primary" type="button" style="display: none;"><i class="fas fa-trash-alt"></i></button>
                                    <button id="addr-edit-btn" onclick="showUpdateAddressPopup()" class="btn btn-primary" type="button" style="display: none;"><i class="fas fa-edit"></i></button>
                                    <button class="btn btn-primary" type="button" onclick="showNewAddressPopup();"><i class="fas fa-plus-circle"></i></button>
                                </div>
                            </div>
                        </div>
                        <div class="table-responsive addr-table">
                            <table id="addr-table" class="table table-hover table-sm">
                                <tbody id="addr-table-body">
                                    <jsp:include page="get_acc_addresses.jsp" />
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <div data-aos="zoom-in-up" data-aos-delay="150" class="acc-img-pane img-pane">
                        <img id="s" src="<%= Validation.getCustomertImage(cust)%>">
                        <div><input class="d-none" type="file" id="f" accept="image/*" onchange="viewIMG('f', 's', 'img-rmv-btn', 'assets/img/avatars/avt.png');"></div>
                        <div class="d-flex justify-content-between">
                            <button class="btn btn-primary flex-fill" type="button" onclick="openFC('f');"><i class="fas fa-camera"></i>Choose a photo</button>
                            <%
                                String display = "";
                                if (Validation.getCustomertImage(cust).equals("assets/img/avatars/avt.png")) {
                                    display = "display: none; ";
                                }
                            %>
                            <button class="btn btn-primary" id="img-rmv-btn" type="button" onclick="removeIMG('f', 's', 'img-rmv-btn', 'assets/img/avatars/avt.png');" style="<%=display%>margin-left: 10px;"><i class="fas fa-trash-alt"></i>Remove photo</button>
                        </div>
                    </div>
                    <div class="d-flex justify-content-end field-row"><button class="btn btn-primary flex-fill" type="button" onclick="upDateDetails();"><i class="fas fa-save"></i>Update Details</button></div>
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <div data-aos="zoom-in-up" data-aos-delay="300" class="acc-det acc-pw" style="margin-top: 10px;">
                        <h4><i class="fas fa-user-shield"></i>Account Details</h4>
                        <div class="d-flex justify-content-between acc-det-itm"><span>Created On</span><span><%=sdf.format(cust.getCreatedOn())%></span></div>
                        <div class="d-flex justify-content-between"><span>UserName</span><span><%=cust.getUsername()%></span></div>
                        <h4 style="margin-top: 10px;margin-bottom: 20px;"><i class="fas fa-user-lock"></i>Password Reset</h4>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>Current Password</span><input id="pw-old" type="password" placeholder="Enter curent password"><span class="shake animated field-tooltip" id="pw-old-tt"></span></div>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>New Password</span><input id="pw" type="password" placeholder="Enter new password"><span class="shake animated field-tooltip" id="pw-tt"></span></div>
                        <div class="d-flex justify-content-between align-items-center field-row"><span>Confirm</span><input id="pw-con" type="password" placeholder="Re-type the password again"><span class="shake animated field-tooltip" id="pw-con-tt"></span></div>
                        <div class="d-flex justify-content-end field-row"><button class="btn btn-primary flex-fill" type="button" onclick="resetPassword();"><i class="fas fa-user-lock"></i>Change Password</button></div>
                    </div>
                </div>
            </div>
        </div>
        <script src="assets/js/jquery.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="assets/js/chart.min.js"></script>
        <script src="assets/js/bs-init.js"></script>
        <script src="assets/js/aos.js"></script>
        <script src="assets/js/autocomplete.js"></script>
        <script src="assets/js/Product-Viewer-1.js"></script>
        <script src="assets/js/Product-Viewer.js"></script>
        <script src="assets/js/srcipt.js"></script>
        <script src="assets/js/Swiper-Slider.js"></script>
        <script src="assets/js/account.js"></script>
    </body>
</html>
