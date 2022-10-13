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
        Customer sesCust = (Customer) session.getAttribute("cust");
        Customer cust = (Customer) s.load(Customer.class, sesCust.getId());
        if (cust.getId() == inv.getCustomer().getId()) {
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
    <body style="background-image: url('assets/img/bgs/search.jpg');" onload="loadCart(); loadWishlist();">
        <jsp:include page="incl_navbar.jsp" />
        <jsp:include page="incl_msgbox.jsp" />

        <div id="rev-popup-bg" class="admin-popup-bg" style="display: none;" onclick="closeModalBG('rev-popup-bg');">
            <div class="d-flex flex-column pulse animated modal-pop" id="rev-popup"><i class="fa fa-close close-btn" onclick="closeModal('rev-popup-bg');"></i>
                <h5><i class="fas fa-comments"></i>FeedBack</h5>
                <div class="p-review">
                    <div>
                        <h6 id="rev-prod"></h6>
                        <h6 class="rating">Your Rating
                            <i class="far fa-star" id="st1" onclick="setRating(1);" onmouseover="starHover(1);" onmouseout="starOut();"></i>
                            <i class="far fa-star" id="st2" onclick="setRating(2);" onmouseover="starHover(2);" onmouseout="starOut();"></i>
                            <i class="far fa-star" id="st3" onclick="setRating(3);" onmouseover="starHover(3);" onmouseout="starOut();"></i>
                            <i class="far fa-star" id="st4" onclick="setRating(4);" onmouseover="starHover(4);" onmouseout="starOut();"></i>
                            <i class="far fa-star" id="st5" onclick="setRating(5);" onmouseover="starHover(5);" onmouseout="starOut();"></i>
                        </h6>
                    </div>
                    <div class="review-txt"><textarea placeholder="Write your review here..." id="rev-desc"></textarea>
                        <div class="d-flex d-sm-flex d-md-flex d-lg-flex d-xl-flex flex-column justify-content-end flex-sm-row justify-content-sm-end justify-content-md-end justify-content-lg-end justify-content-xl-end" style="margin-top: 10px;">
                            <button class="btn btn-primary" type="button" style="display: none;" id="rev-del-btn"><i class="fas fa-minus-circle"></i>Delete Review</button>
                            <button class="btn btn-primary" type="button" id="au-btn"><i class="fas fa-edit"></i>Add Review</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container" style="margin-top: 80px;">
            <div class="ord-div">
                <%
                    if (inv.getOrderStatus() == OrderStatus.RECEIVED) {
                %>
                <h3 style="font-weight: 800;"><i class="far fa-check-circle"></i>Your order (Order ID - <%= inv.getId()%>) has been received !</h3>
                <h4>We'll notify you as soon as your order gets dispatched..</h4>
                <%
                } else if (inv.getOrderStatus() == OrderStatus.PAYMENT_PENDING) {
                %>
                <h3 style="font-weight: 800;"><i class="fas fa-exclamation-circle"></i>Payment of your order (Order ID - <%= inv.getId()%>) is Pending...</h3>
                <h4>The payment process of your order is not completed !</h4>
                <%
                } else if (inv.getOrderStatus() == OrderStatus.DISPATCHED) {
                %>
                <h3 style="font-weight: 800;"><i class="fas fa-shipping-fast"></i>Order Dispatched... (Order ID - <%= inv.getId()%>)</h3>
                <h4>Your order has been dispatched from our warehouse. See you soon..</h4>
                <%
                } else if (inv.getOrderStatus() == OrderStatus.COMPLETED) {
                %>
                <h3 style="font-weight: 800;"><i class="fas fa-check-circle"></i>Order Completed. (Order ID - <%= inv.getId()%>)</h3>
                <h4>Don't forget to give your feedback...</h4>
                <%
                    }
                %>


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
                                <%                                    double delFee = 0;
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
                                    delFee = inv.getDelFee();
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
                                                <td>Order ID</td>
                                                <td><%= inv.getId()%></td>
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
                                    <%
                                        if (inv.getOrderStatus() == OrderStatus.COMPLETED) {
                                    %>
                                    <a href="purchase_history.jsp"><button class="btn btn-primary" type="button"><i class="fas fa-history"></i>View Purchase History</button></a>
                                    <%
                                    } else {
                                    %>
                                    <a href="pending_orders.jsp"><button class="btn btn-primary" type="button"><i class="fas fa-clock"></i>View Pending Orders</button></a>
                                    <%
                                        }
                                        if (inv.getOrderStatus() != OrderStatus.PAYMENT_PENDING) {
                                    %>
                                    <button class="btn btn-primary" type="button" onclick="generateInvoice(<%= inv.getId()%>);"><i class="fas fa-file-invoice"></i>Generate Invoice</button>
                                    <%
                                    } else {
                                    %>
                                    <button class="btn btn-primary" type="button" onclick="discardOrder(<%= inv.getId()%>);"><i class="fas fa-ban"></i>Discard Order</button>
                                    <button class="btn btn-primary" type="button" onclick="resumeCheckout(<%= inv.getId()%>);"><i class="fas fa-file-invoice"></i>Complete Payment</button>
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
                                                ProductReview existingPR = null;
                                                for (Iterator<ProductReview> it = invItem.getStock().getProduct().getProductReviews().iterator(); it.hasNext();) {
                                                    ProductReview pr = it.next();
                                                    if (pr.getCustomer().getId() == cust.getId()) {
                                                        existingPR = pr;
                                                        break;
                                                    }
                                                }
                                        %>
                                        <tr>
                                            <%
                                                if (inv.getOrderStatus() == OrderStatus.COMPLETED) {
                                            %>
                                            <td class="d-flex flex-column justify-content-between flex-lg-row"><a href="product.jsp?id=<%= invItem.getStock().getId()%>&action=rev" id="prod-link-<%= invItem.getStock().getProduct().getId()%>"><%= invItem.getStock().getProduct().getName()%></a>
                                                <%
                                                    if (existingPR == null) {
                                                %>
                                                <div style="display: none;" id="rev-rat-<%= invItem.getStock().getProduct().getId()%>"></div>
                                                <div style="display: none;" id="rev-cont-<%= invItem.getStock().getProduct().getId()%>"></div>
                                                <button class="btn btn-primary" type="button" onclick="openFeedBackDialog(<%= invItem.getStock().getProduct().getId()%>);" id="feedback-btn-<%= invItem.getStock().getProduct().getId()%>">
                                                    <%
                                                    } else {
                                                    %>
                                                    <div style="display: none;" id="rev-rat-<%= invItem.getStock().getProduct().getId()%>"><%= existingPR.getRating()%></div>
                                                    <div style="display: none;" id="rev-cont-<%= invItem.getStock().getProduct().getId()%>"><%= existingPR.getContent()%></div>
                                                    <button class="btn btn-primary" type="button" onclick="openFeedBackDialog(<%= invItem.getStock().getProduct().getId()%>, true);" id="feedback-btn-<%= invItem.getStock().getProduct().getId()%>"><i class="fa fa-check-circle"></i>
                                                        <%
                                                            }
                                                        %>
                                                        Feedback</button>
                                            </td>
                                            <%
                                            } else {
                                            %>
                                            <td><a href="product.jsp?id=<%= invItem.getStock().getId()%>"><%= invItem.getStock().getProduct().getName()%></a></td>
                                                <%
                                                    }
                                                %>
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
        <script src="assets/js/autocomplete.js"></script>
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
        } else {
            response.sendRedirect("pending_orders.jsp");
        }
    } catch (ObjectNotFoundException e) {
        response.sendRedirect("pending_orders.jsp");
    }
    s.close();
%>