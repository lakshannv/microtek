<!DOCTYPE html>
<%
    boolean hasDashboard = (Boolean) session.getAttribute("hasDashboard");
    boolean hasUser = (Boolean) session.getAttribute("hasUser");
    boolean hasProduct = (Boolean) session.getAttribute("hasProduct");
    boolean hasOrder = (Boolean) session.getAttribute("hasOrder");
    boolean hasDelivery = (Boolean) session.getAttribute("hasDelivery");
    boolean hasReports = (Boolean) session.getAttribute("hasReports");
%>
<nav class="navbar navbar-light navbar-expand-xl ap-nav">
    <div class="container-fluid">
        <a class="navbar-brand d-flex align-items-center" href="#"><img class="nav-logo" src="assets/img/Logo.png" />MicroTek Admin Panel</a>
        <button data-toggle="collapse" class="navbar-toggler" data-target="#navcol-1"><span class="sr-only">Toggle navigation</span><span class="navbar-toggler-icon"></span></button>
        <div class="collapse navbar-collapse" id="navcol-1">
            <ul class="nav navbar-nav ml-auto">
                <%
                    if (hasDashboard) {
                %>
                <li class="nav-item" role="presentation"><a class="nav-link" href="dashboard.jsp" id="nav-link-1"><i class="fa fa-dashboard nav-ico"></i>DashBoard</a></li>
                    <%
                        }
                        if (hasUser) {
                    %>
                <li class="nav-item" role="presentation"><a class="nav-link" href="usr_mng.jsp" id="nav-link-2"><i class="fa fa-user-circle-o nav-ico"></i>User Management</a></li>
                    <%
                        }
                        if (hasProduct) {
                    %>
                <li class="nav-item" role="presentation"><a class="nav-link" href="prod_mng.jsp" id="nav-link-3"><i class="fas fa-dolly-flatbed nav-ico"></i>Product Management</a></li>
                    <%
                        }
                        if (hasOrder) {
                    %>
                <li class="nav-item" role="presentation"><a class="nav-link" href="odr_mng.jsp" id="nav-link-4"><i class="far fa-newspaper nav-ico"></i>Order Management</a></li>
                    <%
                        }
                        if (hasDelivery) {
                    %>
                <li class="nav-item" role="presentation"><a class="nav-link" href="del_mng.jsp" id="nav-link-5"><i class="fas fa-shipping-fast nav-ico"></i>Delivery Management</a></li>
                    <%
                        }
                        if (hasReports) {
                    %>
                <li class="nav-item" role="presentation"><a class="nav-link" href="reports.jsp" id="nav-link-6"><i class="fas fa-chart-line nav-ico"></i>Sales & Reports</a></li>
                    <%
                        }
                    %>
                <li class="nav-item" role="presentation"><a class="nav-link" href="AdminLogout"><i class="fas fa-sign-out-alt nav-ico"></i>Log Out</a></li>
            </ul>
        </div>
    </div>
</nav>