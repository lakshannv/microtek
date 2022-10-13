<%@page import="model.OrderStatus"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="hibernate.Invoice"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="hibernate.Customer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<nav class="navbar navbar-light navbar-expand-lg fixed-top main-nav" onload="loadSuggestions();">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center" href="index.jsp"><img class="nav-logo" src="assets/img/Logo.png" />MicroTek</a>
        <button data-toggle="collapse" data-target="#navcol-1" class="navbar-toggler"><span class="sr-only">Toggle navigation</span><span class="navbar-toggler-icon"></span></button>
        <div class="collapse navbar-collapse" id="navcol-1">
            <ul class="nav navbar-nav ml-auto">
                <li class="nav-item d-flex align-items-center" role="presentation">
                    <div class="d-flex nav-search-box autocomplete"><input id="nav-search-box" type="search" placeholder="Search..."><i class="fas fa-search" onclick="goToSearch();"></i></div>
                </li>
                <li role="presentation" class="nav-item"><a class="nav-link" href="search.jsp"><i class="fas fa-dolly-flatbed nav-ico"></i>Products</a></li>
                
                <%
                    Customer sesCust = (Customer) session.getAttribute("cust");
                    if (sesCust != null) {
                %>
                
                <li class="nav-item" role="presentation">
                    <a class="nav-link d-flex align-items-center cart-nav-btn" onclick="showWishlist();">
                        <i class="fa fa-heart nav-ico"></i>WishList<span class="cart-count" id="wish-count">0</span>
                        <span class="rubberBand animated cart-tooltip" id="wish-tt"></span>
                    </a>
                </li>
                
                <%
                    }
                %>

                <li class="nav-item" role="presentation">
                    <a class="nav-link d-flex align-items-center cart-nav-btn" onclick="showCart();">
                        <i class="fa fa-shopping-cart nav-ico"></i>Cart<span class="cart-count" id="cart-count">0</span>
                        <span class="rubberBand animated cart-tooltip" id="cart-tt"></span>
                    </a>
                </li>
                <%
                    if (sesCust == null) {
                %>
                <li role="presentation" class="nav-item"><a class="nav-link" href="login.jsp"><i class="fas fa-user-circle nav-ico"></i>Log In</a></li>
                <li role="presentation" class="nav-item"><a class="nav-link" href="signup.jsp"><i class="fa fa-user-plus nav-ico"></i>Sign Up</a></li>
                    <%
                    } else {
                        SessionFactory sf = HiberUtil.getSessionFactory();
                        Session s = sf.openSession();
                        Customer cust = (Customer) s.load(Customer.class, sesCust.getId());
                        Criteria invCR = s.createCriteria(Invoice.class);
                        invCR.add(Restrictions.eq("customer", cust));
                        invCR.add(Restrictions.ne("orderStatus", OrderStatus.COMPLETED));
                    %>
                <li role="presentation" class="nav-item dropdown"><a class="nav-link dropdown-toggle" href="#" data-toggle="dropdown" aria-expanded="false"><i class="fa fa-user-circle-o nav-ico"></i>My Account</a>
                    <div class="pulse animated dropdown-menu dropdown-menu-right nav-acc-div" role="menu">
                        <%
                            String img = cust.getImage();
                            if (img == null) {
                                img = "avt.png";
                            }
                        %>
                        <img src="assets/img/avatars/<%= img%>" />
                        <h4><%= cust.getFname()%> <%= cust.getLname()%></h4>
                        <h5><%= cust.getUsername()%></h5>
                        <a href="account.jsp"><button class="btn btn-primary" type="button"><i class="fas fa-user-cog nav-ico"></i>Manage Account</button></a>
                        <a href="pending_orders.jsp"><button class="btn btn-primary" type="button"><i class="fas fa-clock nav-ico"></i>Pending Orders<span class="badge btn-badge"><%=invCR.list().size()%></span></button></a>
                        <a href="purchase_history.jsp"><button class="btn btn-primary" type="button"><i class="fas fa-history nav-ico"></i>Purchase History</button></a>
                        <a href="Logout"><button class="btn btn-primary" type="button"><i class="fas fa-sign-out-alt nav-ico"></i>LogOut</button></a>
                    </div>
                </li>
                <%
                        s.close();
                    }
                %>
            </ul>
        </div>
    </div>
</nav>

<div id="cart-popup-bg" class="popup-bg" style="display: none;" onclick="closeCartBG();">
    <div class="d-flex flex-column pulse animated" id="cart-popup">
    </div>
</div>

<div id="wish-popup-bg" class="popup-bg" style="display: none;" onclick="closeWishlistBG();">
    <div class="d-flex flex-column pulse animated" id="wish-popup">
    </div>
</div>