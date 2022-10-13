<%@page import="org.hibernate.criterion.Order"%>
<%@page import="hibernate.District"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="hibernate.Province"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    int provinceID = Integer.parseInt(request.getParameter("provinceID"));
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();

    Criteria provinceCR = s.createCriteria(Province.class);
    provinceCR.add(Restrictions.eq("id", provinceID));
    Province p = (Province) provinceCR.uniqueResult();

    Criteria distCR = s.createCriteria(District.class);
    distCR.add(Restrictions.eq("province", p));
    distCR.addOrder(Order.asc("name"));;
    for (District d : (List<District>) distCR.list()) {
%>
<option value="<%= d.getId()%>"><%= d.getName()%></option>
<%
    }
    s.close();
%>