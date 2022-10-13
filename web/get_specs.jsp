<%@page import="hibernate.Spec"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    List<Spec> specList = (List<Spec>) request.getAttribute("specList");
    if (request.getAttribute("toPTable") == null) {

        for (Spec s : specList) {
            String specUnit = s.getUnit();
            if (specUnit == null) {
                specUnit = "N/A";
            }
%>

<tr id="spec-tr-<%= s.getId()%>" onclick="setActiveSpecRow(<%= s.getId()%>)">
    <td><%= s.getId()%></td>
    <td><%= s.getSpecName()%></td>
    <td class="text-center"><%= specUnit%></td>
    <td>
        <div class="check-box-div">
            <label class="chklabel">
                <%
                    if (s.getIsKey() == 1) {
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

} else {
    for (Spec s : specList) {
        String specUnit = s.getUnit();
        if (specUnit == null) {
            specUnit = "N/A";
        }
%>
<tr>
    <td class="align-middle" style="display: none;" ><%= s.getId() %></td>
    <td class="align-middle"><%= s.getSpecName()%></td>
    <td><input id="product-spec-<%= s.getId()%>" type="text" oninput="hideToolTip('product-spec-<%= s.getId()%>-tt');"><span class="shake animated field-tooltip" id="product-spec-<%= s.getId()%>-tt"></span></td>
    <td class="text-center align-middle"><%= specUnit%></td>
</tr>

<%
        }
    }
%>
