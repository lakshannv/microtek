<%@page import="hibernate.UserPrivilege"%>
<%@page import="java.util.Iterator"%>
<%@page import="hibernate.User"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek Order Management</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body onload="loadOrders();">
        <jsp:include page="incl_msgbox.jsp" />
        <div class="admin-container">
        <jsp:include page="incl_admin_navbar.jsp" />
        <div class="d-flex">
            <jsp:include page="incl_admin_pane.jsp" />
            <div id="dash-content">
                <div class="search-panel">
                    <div class="row">
                        <div class="col-sm-12 col-md-6 col-lg-4 col-xl-4 my-sm-auto my-md-auto my-lg-auto mx-xl-auto comp">
                            <div class="input-group">
                                <div class="input-group-prepend"><label class="input-group-text">Order Status</label>
                                    <select class="shadow-lg search-select" id="odr-stat" onchange="loadOrders(); setHeading();">
                                        <option value="">All</option>
                                        <option value="12" selected="">Ongoing</option>
                                        <option value="0">Payment Pending</option>
                                        <option value="1">Received</option>
                                        <option value="2">Dispatched</option>
                                        <option value="3">Completed</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-12 col-md-6 col-lg-4 col-xl-4 comp">
                            <div class="input-group">
                                <div class="input-group-prepend"><label class="input-group-text">Delivery</label>
                                    <select class="shadow-lg search-select" id="odr-del" onchange="loadOrders(); setHeading();">
                                        <option value="" selected="">Any</option>
                                        <option value="1">Expedited</option>
                                        <option value="0">Free</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-12 col-md-12 col-lg-4 col-xl-4 comp">
                            <div class="input-group">
                                <div class="input-group-prepend"><label class="input-group-text">Sort By</label>
                                    <select class="shadow-lg search-select" id="odr-by" onchange="loadOrders();">
                                        <option>Order ID Desc.</option>
                                        <option>Order ID Asc.</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin-top: 10px;">
                        <div class="col"><input type="search" class="search-box" placeholder="Filter by Order ID.." id="filter-by" oninput="loadOrders();"></div>
                    </div>
                </div>
                <h3 style="margin-top: 10px;"><span id="odr-cat">Ongoing</span> Orders (<span id="odr-count"></span>)</h3>
                <div class="table-responsive odr-mng-table">
                    <table class="table" id="odr-mng-table">
                        <thead>
                            <tr>
                                <th><i class="fas fa-file-invoice"></i>Order ID</th>
                                <th><i class="fas fa-stopwatch"></i>Order Status</th>
                                <th><i class="fas fa-envelope-open-text"></i>E-mail Notification</th>
                                <th><i class="fas fa-shipping-fast"></i>Delivery</th>
                                <th><i class="far fa-calendar-plus"></i>Order Placed On</th>
                                <th><i class="fa fa-file-text"></i>Review Order</th>
                            </tr>
                        </thead>
                        <tbody id="odr-mng-tbody">
                            
                        </tbody>
                    </table>
                </div>
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
    <script src="assets/js/odr_mng.js"></script>
    <script>
            document.getElementById("dash-link-4").className += " active";
            document.getElementById("nav-link-4").className += " active";
        </script>
    </body>
</html>