<%@page import="hibernate.Stock"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.TreeSet"%>
<%@page import="hibernate.Category"%>
<%@page import="hibernate.Product"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="hibernate.Brand"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Session s = HiberUtil.getSessionFactory().openSession();
    TreeSet<String> brandSet = new TreeSet();

    String catName = request.getParameter("catName");
    Criteria prodCR = s.createCriteria(Product.class);
    if (!catName.equalsIgnoreCase("Any")) {
        Criteria catCR = s.createCriteria(Category.class);
        catCR.add(Restrictions.eq("name", catName));
        Category c = (Category) catCR.uniqueResult();
        prodCR.add(Restrictions.eq("category", c));
    }
    for (Product p : (List<Product>) prodCR.list()) {
        boolean hasStock = false;
        for (Iterator<Stock> it = p.getStocks().iterator(); it.hasNext();) {
            Stock stk = it.next();
            if (stk.getActive() == 1) {
                hasStock = true;
                break;
            }
        }
        if (p.getCategory().getActive() == 1 && p.getBrand().getActive() == 1 && hasStock == true) {
            brandSet.add(p.getBrand().getName());
        }
    }
%>
<!DOCTYPE html>
<optgroup label="Select Brand">
    <option selected="">Any</option>
    <%
        for (String b : brandSet) {
    %>
    <option><%= b%></option>
    <%
        }
    %>
</optgroup>
<%
    s.close();
%>