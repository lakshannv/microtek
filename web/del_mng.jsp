<%@page import="java.util.List"%>
<%@page import="hibernate.District"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="hibernate.ApplicationSetting"%>
<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="hibernate.HiberUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    DecimalFormat df = new DecimalFormat("#0.##");
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    int freeDelWithin = Integer.parseInt(((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_within")).getValue());
    String freeDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_time_unit")).getValue();
    String expDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_time_unit")).getValue();
    int expDelWithin = Integer.parseInt(((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_within")).getValue());
    double delProfitMargin = Double.parseDouble(((ApplicationSetting) s.load(ApplicationSetting.class, "delivery_profit_margin")).getValue());

    String[] ftu = {"", "", ""};
    if (freeDelTimeUnit.equals("Days")) {
        ftu[0] = " selected=\"\"";
    } else if (freeDelTimeUnit.equals("Weeks")) {
        ftu[1] = " selected=\"\"";
    } else if (freeDelTimeUnit.equals("Months")) {
        ftu[2] = " selected=\"\"";
    }

    String[] etu = {"", "", ""};
    if (expDelTimeUnit.equals("Days")) {
        etu[0] = " selected=\"\"";
    } else if (freeDelTimeUnit.equals("Weeks")) {
        etu[1] = " selected=\"\"";
    } else if (freeDelTimeUnit.equals("Months")) {
        etu[2] = " selected=\"\"";
    }

    Criteria distCR = s.createCriteria(District.class);
    List<District> distList = distCR.list();
%>
<!DOCTYPE html>
<html>
    <head>
        <title>MicroTek Delivery Management</title>
        <jsp:include page="incl_header.jsp" />
    </head>
    <body>
        <jsp:include page="incl_msgbox.jsp" />
        <div class="admin-container">
            <jsp:include page="incl_admin_navbar.jsp" />
            <div class="d-flex">
                <jsp:include page="incl_admin_pane.jsp" />
                <div id="dash-content">
                    <div class="usr-table-control-div">
                        <h4><i class="fas fa-clock"></i>Fulfillment Period</h4>
                        <div class="d-flex justify-content-xl-end btn-row"></div>
                    </div>
                    <div class="table-responsive">
                        <table class="table del-ful-table">
                            <thead>
                                <tr>
                                    <th>Delivery Method</th>
                                    <th>Fulfillment</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>Free Delivery</td>
                                    <td>Within<input id="free-time" oninput="enableDelFulBtn('free-time');" type="number" min="1" value="<%=freeDelWithin%>">
                                        <select onchange="enableDelFulBtn('free-time');" id="free-time-unit">
                                            <option<%=ftu[0]%>>Days</option>
                                            <option<%=ftu[1]%>>Weeks</option>
                                            <option<%=ftu[2]%>>Months</option>
                                        </select>
                                        <button id="free-time-btn" class="btn btn-primary" type="button" disabled="" onclick="saveDelFulfillment();"><i class="fas fa-check-circle"></i>Apply</button>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Expedited Delivery</td>
                                    <td>Within<input id="exp-time"oninput="enableDelFulBtn('exp-time');" type="number" min="1" value="<%=expDelWithin%>">
                                        <select onchange="enableDelFulBtn('exp-time');" id="exp-time-unit">
                                            <option<%=etu[0]%>>Days</option>
                                            <option<%=etu[1]%>>Weeks</option>
                                            <option<%=etu[2]%>>Months</option>
                                        </select>
                                        <button id="exp-time-btn" class="btn btn-primary" type="button" disabled="" onclick="saveDelFulfillment();"><i class="fas fa-check-circle"></i>Apply</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="usr-table-control-div">
                        <h4><i class="fas fa-money-check-alt"></i>Delivery Fees</h4>
                        <div class="d-flex justify-content-xl-end btn-row"></div>
                    </div>
                    <div class="d-flex align-items-center" style="margin-bottom: 10px;">
                        <span style="font-size: 17px;">Delivery Profit Margin :</span>
                        <input type="number" id="del-prof-margin" min="5" step="5" value="<%=df.format(delProfitMargin)%>" oninput="enableDelProfitBtn();">
                        <span style="margin-left: 10px;"><i class="fas fa-percent"></i></span>
                        <button class="btn btn-primary" id="del-prof-margin-btn" type="button" disabled="" onclick="saveDelProfitMargin();"><i class="fas fa-check-circle"></i>Apply</button>
                    </div>
                    <div class="table-responsive">
                        <table class="table del-fee-table">
                            <thead>
                                <tr>
                                    <th>District</th>
                                    <th>Delivery Cost (Rs.)</th>
                                    <th><strong>Delivery Fee (Rs.)</strong><br></th>
                                    <th>Controls</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    for (District d : distList) {
                                        String chk = "";
                                        if (d.getActive() == 1) {
                                            chk = " checked=\"\"";
                                        }
                                %>
                                <tr>
                                    <td><%=d.getName() %></td>
                                    <td><input type="number" min="100" step="100" id="del-cost-<%=d.getId() %>" oninput="calcDelFee(<%=d.getId() %>); enableDelFeeBtn(<%=d.getId() %>);" value="<%=df.format(d.getDeliveryFee() * (100 - delProfitMargin) / 100) %>"></td>
                                    <td><input type="number" min="100" step="100" id="del-fee-<%=d.getId() %>" oninput="calcDelCost(<%=d.getId() %>); enableDelFeeBtn(<%=d.getId() %>);"value="<%=df.format(d.getDeliveryFee()) %>"></td>
                                    <td>
                                        <div class="d-flex justify-content-center align-items-center mx-auto">
                                            <label class="chklabel">Deliver to this District<input id="del-chk-<%=d.getId() %>" onchange="enableDelFeeBtn(<%=d.getId() %>);" type="checkbox"<%=chk %>><span class="checkmark"></span></label>
                                            <button class="btn btn-primary" type="button" disabled="" id="del-fee-btn-<%=d.getId() %>" onclick="saveDelFee(<%=d.getId() %>);"><i class="fas fa-check-circle"></i>Apply</button>
                                        </div>
                                    </td>
                                </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <script src="assets/js/jquery.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="assets/js/chart.min.js"></script>
        <script src="assets/js/bs-init.js"></script>
        <script src="assets/js/aos.js"></script>
        <script src="assets/js/Product-Viewer-1.js"></script>
        <script src="assets/js/Product-Viewer.js"></script>
        <script src="assets/js/srcipt.js"></script>
        <script src="assets/js/Swiper-Slider.js"></script>
        <script src="assets/js/del_mng.js"></script>
        <script>
            document.getElementById("dash-link-5").className += " active";
            document.getElementById("nav-link-5").className += " active";
        </script>
    </body>
</html>
<%
    s.close();
%>