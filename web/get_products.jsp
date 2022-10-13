<%@page import="java.util.HashMap"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="hibernate.Stock"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
    DecimalFormat df = new DecimalFormat("0.##");
    List<Stock> stockList = (List<Stock>) request.getAttribute("stockList");
    HashMap<Integer, String> specMap = (HashMap<Integer, String>) request.getAttribute("specMap");
    HashMap<Integer, String> imgMap = (HashMap<Integer, String>) request.getAttribute("imgMap");
    for (Stock s : stockList) {
%>
<tr id="stock-tr-<%= s.getId()%>" onclick="setActiveStockRow(<%= s.getId()%>)">
    <td><%= s.getId() %></td>
    <td><%= s.getProduct().getCategory().getName() %></td>
    <td><%= s.getProduct().getBrand().getName() %></td>
    <td><%= s.getProduct().getName() %></td>
    <td><%= df.format(s.getBuyingPrice()) %></td>
    <td><%= df.format(s.getSellingPrice()) %></td>
    <td><%= s.getQty() %></td>
    <td><%= df.format(s.getDiscount()) %> %</td>
    <td class="text-center"><%= s.getWarranty() %></td>
    <td class="text-center"><%= sdf.format(s.getCreatedOn()) %></td>
    <td>
        <div class="check-box-div">
            <label class="chklabel">
                <%
                    if (s.getActive() == 1) {
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
    <td style="display: none;"><%= s.getProduct().getDescription() %></td>
    <td style="display: none;"><%= specMap.get(s.getId()) %></td>
    <td style="display: none;"><%= imgMap.get(s.getId()) %></td>
</tr>
<%    }
%>
