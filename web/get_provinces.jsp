<%@page import="java.util.List"%>
<%@page import="hibernate.Province"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    Criteria provinceCR = s.createCriteria(Province.class);

    for (Province p : (List<Province>) provinceCR.list()) {
%>
<option value="<%= p.getId()%>"><%= p.getName()%></option>
<%
    }
    s.close();
%>