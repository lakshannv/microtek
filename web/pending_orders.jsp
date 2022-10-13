<%@page import="hibernate.ApplicationSetting"%>
<%@page import="model.Validation"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="model.OrderStatus"%>
<%@page import="java.util.List"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="org.hibernate.criterion.Order"%>
<%@page import="hibernate.Invoice"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="hibernate.Customer"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    Customer c = (Customer) session.getAttribute("cust");
    Criteria invCR = s.createCriteria(Invoice.class);
    invCR.add(Restrictions.eq("customer", c));
    invCR.add(Restrictions.ne("orderStatus", OrderStatus.COMPLETED));
    invCR.addOrder(Order.desc("id"));
    List<Invoice> invList = invCR.list();

    int freeDelWithin = Integer.parseInt(((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_within")).getValue());
    String freeDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_time_unit")).getValue();
    String expDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_time_unit")).getValue();
    int expDelWithin = Integer.parseInt(((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_within")).getValue());
%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek Pending Orders</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body style="background-image: url('assets/img/bgs/search.jpg');" onload="loadCart(); loadWishlist();">
        <jsp:include page="incl_navbar.jsp" />
        <div class="container" style="margin-top: 80px;">
            <div class="pend-odr-div">
                <h4 style="font-weight: 800;"><i class="fas fa-clock"></i>Pending Orders (<%= invList.size()%>)</h4>
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th class="align-middle"><i class="fas fa-file-invoice"></i>Order ID</th>
                                <th class="align-middle"><i class="fas fa-spinner"></i>Order Status</th>
                                <th class="align-middle text-center"><i class="fas fa-calendar-plus"></i>Created / Placed / Dispatched on</th>
                                <th class="align-middle text-center"><i class="far fa-calendar-alt"></i>Estimated Fulfillment Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy hh:mm a");
                                SimpleDateFormat estSDF = new SimpleDateFormat("MMMM yyyy");
                                for (Invoice inv : invList) {
                            %>
                            <tr onclick="viewOrder(<%= inv.getId()%>)">
                                <td><%= inv.getId()%></td>
                                <%
                                    switch (inv.getOrderStatus()) {
                                        case OrderStatus.PAYMENT_PENDING:
                                %>
                                <td><i class="fas fa-info-circle"></i>Pending Payment.</td>
                                <%
                                        break;
                                    case OrderStatus.RECEIVED:
                                %>
                                <td><i class="far fa-check-circle"></i>The Order is received & paid for.</td>
                                <%
                                        break;
                                    case OrderStatus.DISPATCHED:
                                %>
                                <td><i class="fas fa-shipping-fast"></i>The Order is dispatched.</td>
                                <%
                                            break;
                                    }
                                    cal.setTime(inv.getLastChangedOn());
                                    int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

                                %>

                                <td class="text-center"><%= dayOfMonth + Validation.getNth(dayOfMonth) + " " + sdf.format(cal.getTime())%></td>
                                <%
                                    String estDate = "";
                                    cal.setTime(inv.getCreatedOn());
                                    if (inv.getDeliveryMethod() == (byte) 0) {
                                        cal.add(Calendar.DATE, Validation.getFulfilmentWithin(freeDelWithin, freeDelTimeUnit));
                                    } else {
                                        cal.add(Calendar.DATE, Validation.getFulfilmentWithin(expDelWithin, expDelTimeUnit));
                                    }
                                    dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                                    estDate = "Before " + dayOfMonth + Validation.getNth(dayOfMonth) + " " + estSDF.format(cal.getTime());
                                %>
                                <td class="text-center"><%= estDate%></td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
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
        <script src="assets/js/Swiper-Slider.js"></script>
        <script>
                                function viewOrder(orderID) {
                                    window.location = "order.jsp?orderID=" + orderID;
                                }
        </script>
    </body>
</html>
<%
    s.close();
%>