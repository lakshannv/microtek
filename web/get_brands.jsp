<%@page import="java.io.File"%>
<%@page import="hibernate.Brand"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    List<Brand> brandList = (List<Brand>) request.getAttribute("brandList");
    for (Brand b : brandList) {
        File f = new File(getServletContext().getRealPath("") + "/assets/img/brands/" + b.getId());
%>
<tr id="brand-tr-<%= b.getId()%>" onclick="setActiveBrandRow(<%= b.getId()%>)">
    <td><%= b.getId()%></td>
    <td><%= b.getName()%></td>
    <td>
        <div class="check-box-div">
            <label class="chklabel">
                <%
                    if (b.getActive() == 1) {
                %>
                <input type="checkbox" checked="" disabled>
                <%
                } else {
                %>
                <input type="checkbox" disabled>
                <%
                    }
                %>
                <span class="checkmark"></span>
            </label>
        </div>
    </td>
    <td style="display: none;"><%= b.getMainWebsite()%></td>
    <td style="display: none;"><%= b.getSupportWebsite()%></td>
    <td style="display: none;"><%= f.exists()%></td>
</tr>
<%
    }
%>
