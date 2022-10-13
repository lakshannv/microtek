<%@page import="hibernate.Stock"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.hibernate.criterion.Order"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="model.Validation"%>
<%@page import="java.util.Collections"%>
<%@page import="hibernate.ProductImage"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="hibernate.Customer"%>
<%@page import="java.util.LinkedList"%>
<%@page import="java.util.LinkedList"%>
<%@page import="hibernate.Cart"%>
<%@page import="java.text.DecimalFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    DecimalFormat df = new DecimalFormat("#,##0.##");
    LinkedList<Cart> cartItemList = new LinkedList();
    Customer c = (Customer) request.getSession().getAttribute("cust");
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    if (c == null) {
        cartItemList = (LinkedList<Cart>) request.getSession().getAttribute("cartItemList");
        if (cartItemList == null) {
            cartItemList = new LinkedList();
        }
    } else {
        Criteria cartCR = s.createCriteria(Cart.class);
        cartCR.add(Restrictions.eq("customer", c));
        cartCR.addOrder(Order.asc("id"));
        cartItemList.addAll(cartCR.list());
    }
    int cartCount = 0;
    LinkedList<Cart> refinedCartItemList = new LinkedList();
    for (Cart crt : cartItemList) {
        Stock stk = (Stock) s.load(Stock.class, crt.getStock().getId());
        if (stk.getProduct().getCategory().getActive() == 1 && stk.getProduct().getBrand().getActive() == 1 && stk.getActive() == 1 && stk.getQty() > 0) {
            refinedCartItemList.add(crt);
            cartCount++;
        }
    }
    double tot = 0;
    String isDisabled = "";
%>
<i class="fa fa-close close-btn" onclick="closeCart();"></i>
<h5><i class="fa fa-shopping-cart"></i>Your Shopping Cart (<span id="crt-count"><%=cartCount%></span>)</h5>
<div class="cart-details" id="cart-details">

    <%
        if (cartCount == 0) {
            isDisabled = " disabled";
    %>
    <div class="d-flex flex-column flex-sm-column flex-md-row flex-lg-row flex-xl-row cart-product">
        <div class="d-flex flex-column align-items-center mx-auto">
            <h4><i class="fas fa-shopping-basket" style="padding-right: 10px;"></i>Your Cart is Empty !</h4>
            <span class="text-center">Browse our wide rage of Computer Parts &amp; add what you fancy here<br />by clicking [ <i class="fas fa-cart-plus"></i> Add to cart ]<br /></span>
        </div>
    </div>
    <%    
    } else {
        for (Cart crt : cartItemList) {
            ArrayList<String> imgList = new ArrayList();
            for (Iterator iterator = crt.getStock().getProduct().getProductImages().iterator(); iterator.hasNext();) {
                ProductImage pi = (ProductImage) iterator.next();
                imgList.add(pi.getId().getImage());
            }
            Collections.sort(imgList);
            String searchURL = response.encodeURL("search.jsp?cat=" + crt.getStock().getProduct().getCategory().getName() + "&brand=" + crt.getStock().getProduct().getBrand().getName());
            String prodURL = response.encodeURL("product.jsp?id=" + crt.getStock().getId());
            double sellprice = crt.getStock().getSellingPrice() * (100 - crt.getStock().getDiscount()) / 100;
            if (refinedCartItemList.contains(crt)) {
                if (crt.getQty() > crt.getStock().getQty()) {
                    crt.setQty(crt.getStock().getQty());
                    s.update(crt);
                    s.beginTransaction().commit();
                }
    %>
    <div class="d-flex flex-column flex-sm-column flex-md-row flex-lg-row flex-xl-row cart-product" id="cart-product-<%= crt.getStock().getId()%>">
        <i class="fa fa-close cart-remove-btn" onclick="removeCartProduct(<%= crt.getStock().getId()%>);"></i>
        <div class="d-flex flex-column flex-sm-column flex-md-row">
            <img src="<%= Validation.getNextProductImage(imgList, 1)%>" onclick="goToURL('<%= prodURL%>');">
            <div class="d-flex flex-column align-items-center align-self-center align-items-md-start cart-product-details">
                <a href="<%= prodURL%>"><h5><%= crt.getStock().getProduct().getName()%></h5></a>
                <a href="<%= searchURL%>"><h6><%= crt.getStock().getProduct().getBrand().getName()%> <%= crt.getStock().getProduct().getCategory().getName()%></h6></a>
                <div class="row">
                    <div class="col-auto">
                        <div class="det"><i class="fas fa-money-check-alt"></i><span>Rs. <%= df.format(sellprice)%></span></div>
                        <div class="det"><i class="fas fa-dolly-flatbed"></i><span><%= crt.getStock().getQty()%> Available</span></div>
                    </div>
                    <div class="col d-flex">
                        <div class="d-flex align-items-center">

                            <div class="num-box" id="cart-num-<%= crt.getStock().getId()%>" max="<%= crt.getStock().getQty()%>">
                                <span class="next" onclick="numberBoxNext('cart-num-<%= crt.getStock().getId()%>'); calculateCartTotal(<%= crt.getStock().getId()%>);"></span>
                                <span class="prev" onclick="numberBoxPrev('cart-num-<%= crt.getStock().getId()%>'); calculateCartTotal(<%= crt.getStock().getId()%>);"></span>
                                <div class="d-flex justify-content-center num-box-val">
                                    <span id="val-cart-num-<%= crt.getStock().getId()%>"><%= crt.getQty()%></span>
                                    <span class="sel">Selected</span>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%
        tot += crt.getQty() * sellprice;
    } else {
        if (crt.getStock().getProduct().getCategory().getActive() == 1 && crt.getStock().getProduct().getBrand().getActive() == 1 && crt.getStock().getActive() == 1 && crt.getStock().getQty() == 0) {
    %>
    <div class="d-flex flex-column flex-sm-column flex-md-row flex-lg-row flex-xl-row cart-product" id="cart-product-<%= crt.getStock().getId()%>">
        <i class="fa fa-close cart-remove-btn" onclick="removeCartProduct(<%= crt.getStock().getId()%>);"></i>
        <div class="d-flex flex-column flex-sm-column flex-md-row">
            <img src="<%= Validation.getNextProductImage(imgList, 1)%>" onclick="goToURL('<%= prodURL%>');">
            <div class="d-flex flex-column align-items-center align-self-center align-items-md-start cart-product-details">
                <a href="<%= prodURL%>"><h5><%= crt.getStock().getProduct().getName()%></h5></a>
                <a href="<%= searchURL%>"><h6><%= crt.getStock().getProduct().getBrand().getName()%> <%= crt.getStock().getProduct().getCategory().getName()%></h6></a>
                <div class="row">
                    <div class="col-auto">
                        <div class="det"><i class="fas fa-money-check-alt"></i><span>Rs. <%= df.format(sellprice)%></span></div>
                        <div class="det"><i class="fas fa-dolly-flatbed"></i><span>Out of Stock</span></div>
                    </div>

                </div>
            </div>
        </div>
    </div>
    <%
    } else {
    %>
    <div class="d-flex flex-column flex-sm-column flex-md-row flex-lg-row flex-xl-row cart-product" id="cart-product-<%= crt.getStock().getId()%>">
        <i class="fa fa-close cart-remove-btn" onclick="removeCartProduct(<%= crt.getStock().getId()%>);"></i>
        <div class="d-flex flex-column flex-sm-column flex-md-row">
            <img src="<%= Validation.getNextProductImage(imgList, 1)%>">
            <div class="d-flex flex-column align-items-center align-self-center align-items-md-start cart-product-details">
                <del><h5><%= crt.getStock().getProduct().getName()%></h5></del>
                <a href="<%= searchURL%>"><h6><%= crt.getStock().getProduct().getBrand().getName()%> <%= crt.getStock().getProduct().getCategory().getName()%></h6></a>
                <div class="row">
                    <div class="col-auto">
                        <div class="det"><i class="fas fa-money-check-alt"></i><span>Rs. <%= df.format(sellprice)%></span></div>
                        <div class="det"><i class="fas fa-dolly-flatbed"></i><span>Not Available Any More</span></div>
                    </div>

                </div>
            </div>
        </div>
    </div>
    <%
                    }
                }
            }
        }
        s.close();
    %>
</div>

<div class="d-flex flex-column flex-md-row row cart-summary">
    <div class="col d-flex d-md-flex justify-content-between align-items-center align-items-md-center cart-total">
        <span>Grand Total</span><span id="cart-total">Rs. <%= df.format(tot)%></span>
    </div>
    <div class="col-auto d-md-flex justify-content-end align-items-md-center">
        <a href="checkout.jsp?action=cart"><button class="btn btn-primary" type="button"<%= isDisabled%>><i class="fas fa-credit-card"></i>Proceed to Checkout</button></a>
    </div>
</div>