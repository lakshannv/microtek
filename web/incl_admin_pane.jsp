<%@page import="hibernate.User"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<!DOCTYPE html>
<%
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    User sesUsr = (User) session.getAttribute("usr");
    User usr = (User) s.load(User.class, sesUsr.getId());
    boolean hasDashboard = (Boolean) session.getAttribute("hasDashboard");
    boolean hasUser = (Boolean) session.getAttribute("hasUser");
    boolean hasProduct = (Boolean) session.getAttribute("hasProduct");
    boolean hasOrder = (Boolean) session.getAttribute("hasOrder");
    boolean hasDelivery = (Boolean) session.getAttribute("hasDelivery");
    boolean hasReports = (Boolean) session.getAttribute("hasReports");
%>
<div class="admin-pane">
    <h2 class="d-flex justify-content-between align-items-center"><img class="nav-logo" src="assets/img/Logo.png" />MicroTek</h2>
    <h6 class="d-flex justify-content-end"><i class="fa fa-gears"></i>Admin Panel</h6>
    <div class="admin-pane-usr-det">
        <div class="d-flex justify-content-between"><span><i class="fas fa-user-shield"></i>User :</span><span><%=usr.getUsername()%></span></div>
        <div class="d-flex justify-content-between"><span><i class="fas fa-user-tag"></i>Type :</span><span><%=usr.getUserType().getName()%></span></div>
    </div>
    <div class="dash-nav">
        <%
            if (hasDashboard) {
        %>
        <div class="dash-nav-link" id="dash-link-1" onclick="goToURL('dashboard.jsp');"><i class="fa fa-dashboard"></i><span>Dashboard</span></div>
        <%
            }
            if (hasUser) {
        %>
        <div class="dash-nav-link" id="dash-link-2" onclick="goToURL('usr_mng.jsp');"><i class="fa fa-user-circle-o"></i><span>User Management</span></div>
        <%
            }
            if (hasProduct) {
        %>
        <div class="dash-nav-link" id="dash-link-3" onclick="goToURL('prod_mng.jsp');"><i class="fas fa-dolly-flatbed"></i><span>Product Management</span></div>
        <%
            }
            if (hasOrder) {
        %>
        <div class="dash-nav-link" id="dash-link-4" onclick="goToURL('odr_mng.jsp');"><i class="far fa-newspaper"></i><span>Order Management</span></div>
        <%
            }
            if (hasDelivery) {
        %>
        <div class="dash-nav-link" id="dash-link-5" onclick="goToURL('del_mng.jsp');"><i class="fas fa-shipping-fast"></i><span>Delivery Management</span></div>
        <%
            }
            if (hasReports) {
        %>
        <div class="dash-nav-link" id="dash-link-6" onclick="goToURL('reports.jsp');"><i class="fas fa-chart-line"></i><span>Sales & Reports</span></div>
        <%
            }
            s.close();
        %>
        <div class="dash-nav-link" onclick="goToURL('AdminLogout');"><i class="fas fa-sign-out-alt"></i><span>Log Out</span></div>
    </div>
</div>