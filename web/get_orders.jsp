<%@page import="org.hibernate.criterion.MatchMode"%>
<%@page import="org.hibernate.criterion.Order"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="model.OrderStatus"%>
<%@page import="java.util.List"%>
<%@page import="hibernate.Invoice"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
    String odrStat = request.getParameter("odrStat");
    String delMethod = request.getParameter("odrDel");
    String orderBy = request.getParameter("odrBy");
    String filterBy = request.getParameter("filterBy");

    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    Criteria invCR = s.createCriteria(Invoice.class);
    if (odrStat != null) {
        if (!odrStat.isEmpty()) {
            if (odrStat.equals("12")) {
                invCR.add(Restrictions.or(Restrictions.eq("orderStatus", OrderStatus.RECEIVED), Restrictions.eq("orderStatus", OrderStatus.DISPATCHED)));
            } else {
                invCR.add(Restrictions.eq("orderStatus", Byte.parseByte(odrStat)));
            }

        }
    }
    if (delMethod != null) {
        if (!delMethod.isEmpty()) {
            invCR.add(Restrictions.eq("deliveryMethod", Byte.parseByte(delMethod)));
        }
    }
    if (filterBy != null) {
        if (!filterBy.isEmpty()) {
            invCR.add(Restrictions.sqlRestriction("id LIKE '" + filterBy + "%'"));
        }
    }
    if (orderBy.equals("Order ID Desc.")) {
        invCR.addOrder(Order.desc("id"));
    } else if (orderBy.equals("Order ID Asc.")) {
        invCR.addOrder(Order.asc("id"));
    }
    List<Invoice> invList = invCR.list();
    for (Invoice inv : invList) {
        String[] sel = {"", "", "", ""};
        sel[inv.getOrderStatus()] = " selected";
%>
<tr>
    <td><%=inv.getId()%></td>
    <td>
        <select id="cmb-<%=inv.getId()%>" onchange="enableBtn(<%=inv.getId()%>);">
            <option value="0"<%=sel[0]%>>Pending Payment</option>
            <option value="1"<%=sel[1]%>>Received</option>
            <option value="2"<%=sel[2]%>>Dispatched</option>
            <option value="3"<%=sel[3]%>>Completed</option>
        </select>
    </td>
    <td><button class="btn btn-primary" type="button" onclick="saveStatus(<%=inv.getId()%>)" id="btn-<%=inv.getId()%>" disabled=""><i class="fas fa-envelope"></i>Save & Send E-mail</button></td>
    <%
        if (inv.getDeliveryMethod() == (byte) 0) {
    %>
    <td>Free</td>
    <%
    } else {
    %>
    <td>Expedited</td>
    <%
        }
    %>
    <td><%=sdf.format(inv.getCreatedOn())%></td>
    <td><a href="review_order.jsp?orderID=<%=inv.getId()%>" target="_blank"><button class="btn btn-primary" type="button"><i class="fas fa-box-open"></i>View Order</button></a></td>
</tr>
<%    }
    s.close();
%>