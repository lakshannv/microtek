<%@page import="java.util.Iterator"%>
<%@page import="hibernate.ShippingAddress"%>
<%@page import="java.util.Set"%>
<%@page import="hibernate.Customer"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();

    Customer sesCust = (Customer) session.getAttribute("cust");
    Customer c = (Customer) s.load(Customer.class, sesCust.getId());

    Set<ShippingAddress> addrSet = c.getShippingAddresses();
    for (Iterator<ShippingAddress> it = addrSet.iterator(); it.hasNext();) {
        ShippingAddress addr = it.next();
%>
<tr id="addr-tr-<%= addr.getId()%>" onclick="setActiveAddressRow(<%= addr.getId()%>)">
    <td><%=addr.getName()%>, <%=addr.getCity().getName()%>, <%=addr.getDistrict().getName()%>, <%=addr.getProvince().getName()%> Province.</td>
    <td style="display: none;" ><%= addr.getId()%></td>
    <td style="display: none;" ><%= addr.getName()%></td>
    <td style="display: none;" ><%= addr.getCity().getId()%></td>
    <td style="display: none;" ><%= addr.getDistrict().getId()%></td>
    <td style="display: none;" ><%= addr.getProvince().getId()%></td>
</tr>
<%
    }
    s.close();
%>