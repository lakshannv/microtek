<%@page import="org.hibernate.SessionFactory"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="hibernate.ApplicationSetting"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    String freeDelWithin = ((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_within")).getValue();
    String freeDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_time_unit")).getValue();
    String expDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_time_unit")).getValue();
    String expDelWithin = ((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_within")).getValue();
    boolean isCart = false;
    if (request.getParameter("stockID") == null) {
        isCart = true;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek Checkout</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <%
        String cartLoadingMethod = "loadCart();";
        if (isCart) {
            cartLoadingMethod = "observeCart(); loadOrderItems();";
        }
    %>
    <body style="background-image: url('assets/img/bgs/search.jpg');" onload="loadBillingAddresses(); loadFulfillmentDates(); loadAddressDetails(); loadDeliveryDates(); loadWishlist(); <%= cartLoadingMethod%> loadDistricts();">
        <jsp:include page="incl_navbar.jsp" />
        <jsp:include page="incl_msgbox.jsp" />

        <div id="addr-popup-bg" class="admin-popup-bg" style="display: none;" onclick="closeModalBG('addr-popup-bg');">
            <div class="d-flex flex-column pulse animated modal-pop" id="addr-popup"><i class="fa fa-close close-btn" onclick="closeModal('addr-popup-bg');"></i>
                <h5><i class="fas fa-map-marker-alt"></i>Add New Address</h5>
                <div class="d-flex field-row">
                    <textarea class="flex-fill" id="addr" oninput="hideToolTip('addr-tt');" placeholder="Enter your Home / Office or Apartment No, Street No etc."></textarea>
                    <span class="shake animated field-tooltip" id="addr-tt">Invalid UserName</span>
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
                    <button class="btn btn-primary" type="button" onclick="addAddress();"><i class="far fa-calendar-plus"></i>Add</button>
                </div>
            </div>
        </div>

        <div class="container" style="margin-top: 80px;">
            <div class="row">
                <div class="col-sm-12 col-lg-8">
                    <div data-aos="zoom-in-up" class="delivery-div">
                        <h4><i class="fas fa-shipping-fast"></i>Delivery Details</h4>
                        <div class="input-group">
                            <div class="input-group-prepend">
                                <label class="input-group-text">Shipping Address</label>
                                <select class="shadow-lg search-select" id="addr-cmb" onchange="loadBillingAddresses(); loadAddressDetails();">
                                    <jsp:include page="get_addresses.jsp" />
                                </select>
                            </div>
                        </div>
                        <div class="d-flex flex-column justify-content-between align-items-center flex-lg-row del-div">
                            <span id="addr-det"></span>
                            <button class="btn btn-primary" type="button" onclick="showModal('addr-popup-bg')"><i class="fas fa-map-marker-alt"></i>Add New Address</button>
                        </div>

                        <div class="del-div">
                            <div class="table-responsive table-bordered">
                                <table class="table table-bordered table-hover table-sm">
                                    <thead>
                                        <tr>
                                            <th class="text-center"><i class="fas fa-shipping-fast"></i>Delivery Method</th>
                                            <th class="text-center"><i class="fas fa-money-check"></i>Cost</th>
                                            <th class="text-center"><i class="far fa-calendar-alt"></i>Fulfillment</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>
                                                <div class="d-inline-flex flex-row check-box-div"><label class="chklabel">Free Delivery<input type="radio" checked="" name="delm" onchange="loadDeliveryDates();"><span class="checkmark"></span></label></div>
                                            </td>
                                            <td class="text-right">Free</td>
                                            <td class="text-center" id="free-del-cell" within="<%=freeDelWithin%>" timeUnit="<%=freeDelTimeUnit%>">Within <%=freeDelWithin%> <%=freeDelTimeUnit%></td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <div class="d-inline-flex flex-row check-box-div"><label class="chklabel">Expedited&nbsp;Delivery<input id="exp-del" type="radio" name="delm" onchange="loadDeliveryDates();"><span class="checkmark"></span></label></div>
                                            </td>
                                            <td class="text-right">Rs. <span id="del-fee"></span></td>
                                            <td class="text-center" id="exp-del-cell" within="<%=expDelWithin%>" timeUnit="<%=expDelTimeUnit%>">Within <%=expDelWithin%> <%=expDelTimeUnit%></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <div class="input-group" style="margin-top: 10px;">
                            <div class="input-group-prepend"><label class="input-group-text">Billing Address</label>
                                <select class="shadow-lg search-select" id="bill-addr-cmb" onchange="setBillingAddress();">
                                    <option value="0" selected="">[ Same as Shipping Address ]</option>

                                </select>
                            </div>
                        </div>
                    </div>

                    <div data-aos="zoom-in-up" data-aos-delay="150" class="delivery-div" style="margin-top: 10px;">
                        <h4><i class="fas fa-file-invoice"></i>Order Details</h4>
                        <div class="odr-div">
                            <div class="table-responsive table-bordered">
                                <table class="table table-bordered table-hover table-sm">
                                    <thead>
                                        <tr>
                                            <th>Item</th>
                                            <th>Qty</th>
                                            <th>Total (Rs.)</th>
                                        </tr>
                                    </thead>
                                    <tbody id="order-items">

                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <div data-aos="zoom-in-up" data-aos-delay="300" class="delivery-div delivery-div-side">
                        <div data-aos="zoom-in-left" data-aos-delay="400" class="pay-div">
                            <div class="pay-div-head">
                                <h5><i class="fas fa-map-marker-alt"></i>Deliver to</h5>
                            </div>
                            <div class="pay-div-det"><span id="del-addr"></span></div>
                        </div>
                        <div data-aos="zoom-in-left" data-aos-delay="600" class="pay-div">
                            <div class="pay-div-head">
                                <h5><i class="fas fa-envelope-open-text"></i>Billing Address</h5>
                            </div>
                            <div class="pay-div-det"><span id="bill-addr">Same As Shipping Address</span></div>
                        </div>
                        <div data-aos="zoom-in-left" data-aos-delay="800" class="pay-div">
                            <div class="pay-div-head">
                                <h5><i class="fas fa-shipping-fast"></i>Delivery Method</h5>
                            </div>
                            <div class="pay-div-det">
                                <h6 id="del-method"></h6><span id="del-method-desc"><br></span></div>
                        </div>
                        <div data-aos="zoom-in-left" data-aos-delay="1000" class="pay-div">
                            <div class="pay-div-head">
                                <h5><i class="fas fa-money-check-alt"></i>Payment</h5>
                            </div>
                            <div class="pay-div-det"><span>For security concerns, we don't save your credit card information. You can make your payment via PayHere.<br></span><img src="assets/img/payhere.png">
                                <div class="d-flex justify-content-between tot">
                                    <span>Total</span><span id="order-tot"></span>
                                </div>
                                <button class="btn btn-primary" type="button" onclick="checkOut();"><i class="fas fa-money-check"></i>Complete Payment</button>
                            </div>
                        </div>
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
        <script type="text/javascript" src="https://www.payhere.lk/lib/payhere.js"></script>
        <script src="assets/js/checkout.js"></script>
    </body>
</html>
<%
    s.close();
%>