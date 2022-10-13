<%@page import="model.Validation"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Collections"%>
<%@page import="hibernate.ProductImage"%>
<%@page import="java.util.Iterator"%>
<%@page import="hibernate.Stock"%>
<%@page import="hibernate.Product"%>
<%@page import="java.util.List"%>
<%@page import="org.hibernate.criterion.Order"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="hibernate.Wishlist"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="hibernate.Customer"%>
<%@page import="java.text.DecimalFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    Customer c = (Customer) request.getSession().getAttribute("cust");
    if (c == null) {
        response.getWriter().write("nocust");
    } else {
        DecimalFormat df = new DecimalFormat("#,##0.##");
        Session s = HiberUtil.getSessionFactory().openSession();
        Criteria wishCR = s.createCriteria(Wishlist.class);
        wishCR.add(Restrictions.eq("customer", c));
        wishCR.addOrder(Order.desc("id"));
        List<Wishlist> wishItems = wishCR.list();
%>
<i class="fa fa-close close-btn" onclick="closeWishlist();"></i>
<h5><i class="fas fa-list"></i>Your Wishlist (<span id="wsh-count"><%=wishItems.size() %></span>)</h5>
<div class="cart-details" id="wish-details">
    <%
        if (wishItems.size() == 0) {
    %>
    <div class="d-flex flex-column flex-sm-column flex-md-row flex-lg-row flex-xl-row cart-product">
        <div class="d-flex flex-column align-items-center mx-auto">
            <h4><i class="fas fa-list" style="padding-right: 10px;"></i>Your Wishlist is Empty !</h4><span class="text-center">Browse our wide rage of Computer Parts &amp; add what you fancy here<br>by clicking [&nbsp;<i class="fa fa-heart"></i>&nbsp;]<br></span></div>
    </div>
    <%
    } else {
        for (Wishlist w : wishItems) {
            Product p = w.getProduct();
            ArrayList<String> imgList = new ArrayList();
            for (Iterator iterator = p.getProductImages().iterator(); iterator.hasNext();) {
                ProductImage pi = (ProductImage) iterator.next();
                imgList.add(pi.getId().getImage());
            }
            Collections.sort(imgList);
            if (p.getCategory().getActive() == 1 && p.getBrand().getActive() == 1) {
                Stock stk = null;
                for (Iterator iterator = p.getStocks().iterator(); iterator.hasNext();) {
                    Stock st = (Stock) iterator.next();
                    if (st.getActive() == 1) {
                        if (stk == null) {
                            stk = st;
                        } else if ((st.getSellingPrice() * (100 - st.getDiscount()) / 100) < (stk.getSellingPrice() * (100 - stk.getDiscount()) / 100)) {
                            stk = st;
                        }
                    }
                }
                if (stk == null) {
    %>
    <div class="d-flex flex-column flex-sm-column flex-md-row flex-lg-row flex-xl-row cart-product" id="wish-product-<%= w.getId()%>">
        <i class="fa fa-close cart-remove-btn" onclick="removeWishlistProduct(<%= w.getId()%>, <%= p.getId()%>);"></i>
        <div class="d-flex flex-column flex-sm-column flex-md-row"><img src="<%= Validation.getNextProductImage(imgList, 1)%>">
            <div class="d-flex flex-column align-items-center align-self-center align-items-md-start cart-product-details">
                <h5><%=p.getBrand().getName()%> <%=p.getName()%></h5>
                <h6><%=p.getBrand().getName()%> <%=p.getCategory().getName()%></h6>
                <div class="row">
                    <div class="col-auto">
                        <div class="det"><i class="fas fa-money-check-alt"></i><span>N/A</span></div>
                        <div class="det"><i class="fas fa-dolly-flatbed"></i><span>Not Available Anymore.</span></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%
    } else {
        String searchURL = response.encodeURL("search.jsp?cat=" + p.getCategory().getName() + "&brand=" + p.getBrand().getName());
        String prodURL = response.encodeURL("product.jsp?id=" + stk.getId());
        double sellprice = stk.getSellingPrice() * (100 - stk.getDiscount()) / 100;
    %>
    <div class="d-flex flex-column flex-sm-column flex-md-row flex-lg-row flex-xl-row cart-product" id="wish-product-<%= w.getId()%>">
        <i class="fa fa-close cart-remove-btn" onclick="removeWishlistProduct(<%= w.getId()%>, <%= p.getId()%>);"></i>
        <div class="d-flex flex-column flex-sm-column flex-md-row"><img src="<%= Validation.getNextProductImage(imgList, 1)%>">
            <div class="d-flex flex-column align-items-center align-self-center align-items-md-start cart-product-details">
                <a href="<%= prodURL%>"><h5><%=p.getBrand().getName()%> <%=p.getName()%></h5></a>
                <a href="<%= searchURL%>"><h6><%=p.getBrand().getName()%> <%=p.getCategory().getName()%></h6></a>
                <div class="row">
                    <div class="col-auto">
                        <div class="det"><i class="fas fa-money-check-alt"></i><span>Rs. <%= df.format(sellprice)%></span></div>
                                <%
                                    if (stk.getQty() == 0) {
                                %>
                        <div class="det"><i class="fas fa-dolly-flatbed"></i><span>Out of Stock.</span></div>
                    </div>
                    <%
                    } else {
                    %>
                    <div class="det"><i class="fas fa-dolly-flatbed"></i><span><%= stk.getQty()%> Available</span></div>
                </div>
                    <div class="col d-flex"><button class="btn btn-primary" type="button" onclick="moveToCart(<%=stk.getId() %>, <%= w.getId()%>, <%= p.getId()%>);"><i class="fas fa-cart-plus"></i>Move To Cart</button></div>
                <%
                    }
                %>
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
<%
    }
%>