<%@page import="hibernate.Stock"%>
<%@page import="hibernate.ShippingAddress"%>
<%@page import="java.util.List"%>
<%@page import="org.hibernate.criterion.Order"%>
<%@page import="hibernate.Cart"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="org.hibernate.Criteria"%>

<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="hibernate.Customer"%>
<%@page import="java.text.DecimalFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    DecimalFormat df = new DecimalFormat("#,##0.##");

    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    double delFee = 0;
    String addrString = request.getParameter("addrID");
    if (addrString != null) {
        int addrID = Integer.parseInt(addrString);
        Criteria addrCR = s.createCriteria(ShippingAddress.class);
        addrCR.add(Restrictions.eq("id", addrID));
        ShippingAddress addr = (ShippingAddress) addrCR.uniqueResult();
        delFee = addr.getDistrict().getDeliveryFee();
    }

    String stk = request.getParameter("stockID");
    if (stk == null) {
        Customer cust = (Customer) session.getAttribute("cust");
        Criteria cartCR = s.createCriteria(Cart.class);
        cartCR.add(Restrictions.eq("customer", cust));
        cartCR.addOrder(Order.asc("id"));
        List<Cart> cartItemList = cartCR.list();

        double tot = 0;
        for (Cart crt : cartItemList) {
            if (crt.getStock().getProduct().getCategory().getActive() == 1 && crt.getStock().getProduct().getBrand().getActive() == 1 && crt.getStock().getActive() == 1 && crt.getStock().getQty() >= crt.getQty()) {
                int qty = crt.getQty();
                double itemTot = qty * crt.getStock().getSellingPrice() * (100 - crt.getStock().getDiscount()) / 100;
                tot += itemTot;
                String discount = "";
                if (crt.getStock().getDiscount() != 0) {
                    discount = "( " + df.format(crt.getStock().getDiscount()) + " % off ) ";
                }
%>
<tr>
    <td><%= crt.getStock().getProduct().getBrand().getName()%> <%= crt.getStock().getProduct().getName()%></td>
    <td class="text-center"><%= qty%></td>
    <td class="text-right"><%= discount + df.format(itemTot)%></td>
</tr>
<%
        }
    }
%>
<tr>
    <td class="thead" colspan="2">Sub Total (Rs.)</td>
    <td class="text-right"><%= df.format(tot)%></td>
</tr>
<tr>
    <td class="thead" colspan="2">Delivery Fees (Rs.)</td>
    <td class="text-right"><%= df.format(delFee)%></td>
</tr>
<tr>
    <td class="thead" colspan="2">Net Total (Rs.)</td>
    <td class="text-right" id="net-tot"><%= df.format(tot + delFee)%></td>
</tr>
<%
} else {
    int stockID = Integer.parseInt(stk);
    Stock st = (Stock) s.load(Stock.class, stockID);
    int qty = Integer.parseInt(request.getParameter("qty"));
    if (qty <= 0) {
        qty = 1;
    }
    double tot = 0;
    if (st.getProduct().getCategory().getActive() == 1 && st.getProduct().getBrand().getActive() == 1 && st.getActive() == 1 && st.getQty() >= qty) {
        tot = qty * st.getSellingPrice() * (100 - st.getDiscount()) / 100;

        String discount = "";
        if (st.getDiscount() != 0) {
            discount = "( " + df.format(st.getDiscount()) + " % off ) ";
        }
%>
<tr>
    <td><a href="product.jsp?id=<%= st.getId()%>"><%= st.getProduct().getBrand().getName()%> <%= st.getProduct().getName()%></a></td>
    <td class="text-center"><%= qty%></td>
    <td class="text-right"><%= discount + df.format(tot)%></td>
</tr>
<%
        }
%>
<tr>
    <td class="thead" colspan="2">Sub Total (Rs.)</td>
    <td class="text-right"><%= df.format(tot)%></td>
</tr>
<tr>
    <td class="thead" colspan="2">Delivery Fees (Rs.)</td>
    <td class="text-right"><%= df.format(delFee)%></td>
</tr>
<tr>
    <td class="thead" colspan="2">Net Total (Rs.)</td>
    <td class="text-right" id="net-tot"><%= df.format(tot + delFee)%></td>
</tr>
<%
    }
    s.close();
%>