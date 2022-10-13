<%@page import="java.util.List"%>
<%@page import="hibernate.Category"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    List<Category> catList = (List<Category>) request.getAttribute("catList");
    for (Category c : catList) {
%>
<tr id="cat-tr-<%= c.getId()%>" onclick="setActiveCatRow(<%= c.getId()%>)">
    <td><%= c.getId()%></td>
    <td><%= c.getName()%></td>
    <td>
        <div class="check-box-div">
            <label class="chklabel">
                <%
                    if (c.getActive() == 1) {
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
</tr>
<%
    }
%>
