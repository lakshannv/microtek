<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="hibernate.InvoiceItem"%>
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
    DecimalFormat df = new DecimalFormat("#,##0.##");
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    Customer c = (Customer) session.getAttribute("cust");
    Criteria invCR = s.createCriteria(Invoice.class);
    invCR.add(Restrictions.eq("customer", c));
    invCR.add(Restrictions.ne("orderStatus", OrderStatus.PAYMENT_PENDING));
    invCR.addOrder(Order.desc("id"));
    List<Invoice> invList = invCR.list();
%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek Purchase History</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body style="background-image: url('assets/img/bgs/search.jpg');" onload="loadCart(); loadWishlist();">
        <jsp:include page="incl_navbar.jsp" />
        <div class="container" style="margin-top: 80px;">
            <div class="pend-odr-div">
                <h4 style="font-weight: 800;"><i class="fas fa-history"></i>Purchase History (<%= invList.size()%>)</h4>
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th class="align-middle"><i class="fas fa-file-invoice"></i>Order ID</th>
                                <th class="align-middle"><i class="fas fa-spinner"></i>Order Status</th>
                                <th class="align-middle text-center"><i class="fas fa-calendar-plus"></i>Order Placed on</th>
                                <th class="align-middle text-center"><i class="fas fa-dolly"></i># of Items</th>
                                <th class="align-middle text-right"><i class="fas fa-donate"></i>Amount (Rs.)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy hh:mm a");
                                for (Invoice inv : invList) {
                            %>
                            <tr onclick="viewOrder(<%= inv.getId()%>)">
                                <td><%= inv.getId()%></td>
                                <%
                                    switch (inv.getOrderStatus()) {
                                        case OrderStatus.COMPLETED:
                                %>
                                <td><i class="fas fa-check-circle"></i>Order Fulfilled.</td>
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
                                    cal.setTime(inv.getCreatedOn());
                                    int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

                                %>

                                <td class="text-center"><%= dayOfMonth + Validation.getNth(dayOfMonth) + " " + sdf.format(cal.getTime())%></td>
                                <%
                                    double tot = 0;
                                    Set<InvoiceItem> invItemSet = inv.getInvoiceItems();
                                    for (Iterator<InvoiceItem> it = invItemSet.iterator(); it.hasNext();) {
                                        InvoiceItem invItem = it.next();
                                        tot += invItem.getQty() * invItem.getStock().getSellingPrice() * (100 - invItem.getDiscount()) / 100;
                                    }
                                %>
                                <td class="text-center"><%= invItemSet.size()%></td>
                                <td class="text-right"><%= df.format(tot + inv.getDelFee())%></td>
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