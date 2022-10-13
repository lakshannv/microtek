<%@page import="hibernate.City"%>
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
    int distID = Integer.parseInt(request.getParameter("distID"));
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();

    Criteria distCR = s.createCriteria(District.class);
    distCR.add(Restrictions.eq("id", distID));
    District d = (District) distCR.uniqueResult();

    Criteria cityCR = s.createCriteria(City.class);
    cityCR.add(Restrictions.eq("district", d));
    cityCR.addOrder(Order.asc("name"));
    for (City c : (List<City>) cityCR.list()) {
%>
<option value="<%= c.getId()%>"><%= c.getName()%></option>
<%
    }
    s.close();
%>