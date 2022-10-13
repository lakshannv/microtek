<%@page import="hibernate.City"%>
<%@page import="hibernate.ApplicationSetting"%>
<%@page import="hibernate.ProductReview"%>
<%@page import="org.hibernate.ObjectNotFoundException"%>
<%@page import="hibernate.Customer"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="hibernate.InvoiceItem"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="model.Validation"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="model.OrderStatus"%>
<%@page import="hibernate.Invoice"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    DecimalFormat df = new DecimalFormat("#,##0.##");
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    try {
        int invID = Integer.parseInt(request.getParameter("orderID"));
        Invoice inv = (Invoice) s.load(Invoice.class, invID);

        int freeDelWithin = Integer.parseInt(((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_within")).getValue());
        String freeDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_time_unit")).getValue();
        String expDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_time_unit")).getValue();
        int expDelWithin = Integer.parseInt(((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_within")).getValue());
%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek Order Details</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body style="background-image: url('assets/img/bgs/search.jpg');">
        <jsp:include page="incl_admin_navbar_contain.jsp" />

        <div class="container" style="margin-top: 80px;">
            <div class="ord-div">
                <h3 style="font-weight: 800;">
                    <%
                        if (inv.getOrderStatus() == OrderStatus.RECEIVED) {
                    %>
                    <i class="far fa-check-circle"></i>
                    <%
                    } else if (inv.getOrderStatus() == OrderStatus.PAYMENT_PENDING) {
                    %>
                    <i class="fas fa-exclamation-circle"></i>
                    <%
                    } else if (inv.getOrderStatus() == OrderStatus.DISPATCHED) {
                    %>
                    <i class="fas fa-shipping-fast"></i>
                    <%
                    } else if (inv.getOrderStatus() == OrderStatus.COMPLETED) {
                    %>
                    <i class="fas fa-check-circle"></i>
                    <%
                        }
                    %>
                    Order ID - <%= inv.getId()%> [<%=OrderStatus.getStatus(inv.getOrderStatus())%>]</h3>


                <div>
                    <div class="row">
                        <div class="col-lg-5 col-xl-6">
                            <div data-aos="zoom-in-up" class="odr-addr-div">
                                <div class="row">
                                    <div class="col-12 col-sm-6">
                                        <h5><i class="fas fa-map-marker-alt"></i>Deliver to</h5>
                                        <p><%= inv.getAddress()%><br><%= inv.getCity().getName()%><br><%= inv.getCity().getDistrict().getName()%><br><%= inv.getCity().getDistrict().getProvince().getName()%> Province<br>Sri Lanka<br></p>
                                    </div>
                                    <div class="col">
                                        <h5><i class="fas fa-envelope-open-text"></i>Billing Address</h5>
                                        <%
                                            if (inv.getBillingCityId() == 0) {
                                        %>
                                        <p>Same As Delivery Address</p>
                                        <%
                                            } else {
                                                City billCity = (City) s.load(City.class, inv.getBillingCityId());
                                        %>
                                        <p><%= inv.getBillingAddress()%><br><%= billCity.getName()%><br><%= billCity.getDistrict().getName()%><br><%= billCity.getDistrict().getProvince().getName()%> Province<br>Sri Lanka<br></p>
                                        <%
                                            }
                                        %>
                                    </div>
                                </div>
                                <h5><i class="fas fa-shipping-fast"></i>Delivery Method</h5>
                                <%
                                    double delFee = 0;
                                    SimpleDateFormat estSDF = new SimpleDateFormat("MMMM yyyy");
                                    String estDate = "";
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(inv.getCreatedOn());
                                    if (inv.getDeliveryMethod() == (byte) 0) {
                                        c.add(Calendar.DATE, Validation.getFulfilmentWithin(freeDelWithin, freeDelTimeUnit));
                                        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                                        estDate = "Before " + dayOfMonth + Validation.getNth(dayOfMonth) + " " + estSDF.format(c.getTime());
                                %>
                                <p>Free Delivery</p>
                                <%
                                } else {
                                    c.add(Calendar.DATE, Validation.getFulfilmentWithin(expDelWithin, expDelTimeUnit));
                                    int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                                    estDate = "Before " + dayOfMonth + Validation.getNth(dayOfMonth) + " " + estSDF.format(c.getTime());
                                    delFee = inv.getCity().getDistrict().getDeliveryFee();
                                %>
                                <p>Expedited Delivery</p>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                        <div class="col">
                            <div data-aos="zoom-in-up" data-aos-delay="150" class="odr-det-div">
                                <h5><i class="fas fa-file-invoice"></i>Order Details</h5>
                                <div class="table-responsive">
                                    <table class="table">
                                        <tbody>
                                            <tr>
                                                <td>Customer Name</td>
                                                <td><%= inv.getCustomer().getFname()%> <%= inv.getCustomer().getLname()%></td>
                                            </tr>
                                            <tr>
                                                <td>Customer Mobile</td>
                                                <td><%= inv.getCustomer().getMobile()%></td>
                                            </tr>

                                            <%
                                                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy hh:mm a");
                                                if (inv.getOrderStatus() == OrderStatus.DISPATCHED || inv.getOrderStatus() == OrderStatus.COMPLETED) {
                                                    c.setTime(inv.getCreatedOn());
                                                    int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                                            %>
                                        <td>Order Placed On</td>
                                        <td><%= dayOfMonth + Validation.getNth(dayOfMonth) + " " + sdf.format(c.getTime())%></td>
                                        <%
                                            }
                                        %>

                                        <tr>
                                            <%
                                                if (inv.getOrderStatus() == OrderStatus.RECEIVED) {
                                                    c.setTime(inv.getCreatedOn());
                                            %>
                                            <td>Order Placed On</td>
                                            <%
                                            } else if (inv.getOrderStatus() == OrderStatus.PAYMENT_PENDING) {
                                                c.setTime(inv.getCreatedOn());
                                            %>
                                            <td>Order Created On</td>
                                            <%
                                            } else if (inv.getOrderStatus() == OrderStatus.DISPATCHED) {
                                                c.setTime(inv.getLastChangedOn());
                                            %>
                                            <td>Order Dispatched On</td>
                                            <%
                                            } else if (inv.getOrderStatus() == OrderStatus.COMPLETED) {
                                                c.setTime(inv.getLastChangedOn());
                                            %>
                                            <td>Order Completed On</td>
                                            <%
                                                }
                                                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                                            %>
                                            <td><%= dayOfMonth + Validation.getNth(dayOfMonth) + " " + sdf.format(c.getTime())%></td>
                                        </tr>
                                        <tr>
                                            <td>Order Status</td>
                                            <td><%= OrderStatus.getStatus(inv.getOrderStatus())%></td>
                                        </tr>
                                        <tr>
                                            <td>Estimated Fulfillment</td>
                                            <%
                                                if (inv.getOrderStatus() == OrderStatus.PAYMENT_PENDING) {
                                            %>
                                            <td>N/A</td>                                                
                                            <%
                                            } else {
                                            %>
                                            <td><%= estDate%></td>
                                            <%
                                                }
                                            %>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <div class="d-flex flex-column justify-content-end flex-md-row">
                                    <a href="odr_mng.jsp"><button class="btn btn-primary" type="button"><i class="fas fa-history"></i>Manage Orders</button></a>
                                    <%
                                        if (inv.getOrderStatus() != OrderStatus.PAYMENT_PENDING) {
                                    %>
                                    <button class="btn btn-primary" type="button" onclick="generateInvoice(<%= inv.getId()%>);"><i class="fas fa-file-invoice"></i>View Invoice</button>
                                    <%
                                        }
                                    %>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div data-aos="zoom-in-up" data-aos-delay="300" class="delivery-div" style="margin-top: 10px;">
                        <h4><i class="fas fa-file-invoice-dollar"></i>Order Items</h4>
                        <div class="odr-div">
                            <div class="table-responsive table-bordered">
                                <table class="table table-bordered table-hover table-sm">
                                    <thead>
                                        <tr>
                                            <th>Item</th>
                                            <th>Category</th>
                                            <th>Brand</th>
                                            <th>Qty</th>
                                            <th>Total (Rs.)</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            double tot = 0;
                                            for (Iterator<InvoiceItem> iterator = inv.getInvoiceItems().iterator(); iterator.hasNext();) {
                                                InvoiceItem invItem = iterator.next();
                                                double itemTot = 0;
                                                String discount = "";
                                                if (inv.getOrderStatus() == OrderStatus.PAYMENT_PENDING) {
                                                    itemTot = invItem.getQty() * invItem.getStock().getSellingPrice() * (100 - invItem.getStock().getDiscount()) / 100;
                                                    if (invItem.getStock().getDiscount() != 0) {
                                                        discount = "( " + df.format(invItem.getStock().getDiscount()) + " % off ) ";
                                                    }
                                                } else {
                                                    itemTot = invItem.getQty() * invItem.getStock().getSellingPrice() * (100 - invItem.getDiscount()) / 100;
                                                    if (invItem.getDiscount() != 0) {
                                                        discount = "( " + df.format(invItem.getDiscount()) + " % off ) ";
                                                    }
                                                }
                                                tot += itemTot;
                                        %>
                                        <tr>                                            
                                            <td><a href="product.jsp?id=<%= invItem.getStock().getId()%>"><%= invItem.getStock().getProduct().getName()%></a></td>
                                            <td class="text-center"><a href="search.jsp?cat=<%= invItem.getStock().getProduct().getCategory().getName()%>"><%= invItem.getStock().getProduct().getCategory().getName()%></a></td>
                                            <td class="text-center"><a href="search.jsp?brand=<%= invItem.getStock().getProduct().getBrand().getName()%>"><%= invItem.getStock().getProduct().getBrand().getName()%></a></td>
                                            <td class="text-center"><%= invItem.getQty()%></td>
                                            <td class="text-right"><%= discount + df.format(itemTot)%></td>
                                        </tr>
                                        <%
                                            }
                                        %>

                                        <tr>
                                            <td class="thead" colspan="4">Sub Total (Rs.)</td>
                                            <td class="text-right"><%= df.format(tot)%></td>
                                        </tr>
                                        <tr>
                                            <td class="thead" colspan="4">Delivery Fees (Rs.)</td>
                                            <td class="text-right"><%= df.format(delFee)%></td>
                                        </tr>
                                        <tr>
                                            <td class="thead" colspan="4">Net Total (Rs.)</td>
                                            <td class="text-right"><%= df.format(delFee + tot)%></td>
                                        </tr>
                                    </tbody>
                                </table>
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
        <script src="assets/js/Product-Viewer-1.js"></script>
        <script src="assets/js/Product-Viewer.js"></script>
        <script src="assets/js/srcipt.js"></script>
        <script src="assets/js/Swiper-Slider.js"></script>
        <script type="text/javascript" src="https://www.payhere.lk/lib/payhere.js"></script>
        <script src="assets/js/checkout.js"></script>
        <script src="assets/js/product.js"></script>
    </body>
</html>
<%
    } catch (ObjectNotFoundException e) {
        response.sendRedirect("pending_orders.jsp");
    }
    s.close();
%>