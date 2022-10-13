<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="hibernate.ShippingAddress"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="hibernate.Customer"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    Customer cust = (Customer) request.getSession().getAttribute("cust");
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();

    Criteria custCR = s.createCriteria(Customer.class);
    custCR.add(Restrictions.eq("id", cust.getId()));
    Customer c = (Customer) custCR.uniqueResult();

    Set<ShippingAddress> addrSet = c.getShippingAddresses();
    for (Iterator<ShippingAddress> it = addrSet.iterator(); it.hasNext();) {
        ShippingAddress addr = it.next();
%>
<option value="<%= addr.getId()%>"><%= addr.getName()%>, <%= addr.getCity().getName() %>, <%= addr.getDistrict().getName() %></option>
<%
    }
    s.close();
%>