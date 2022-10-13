<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.LinkedHashSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="hibernate.Product"%>
<%@page import="org.hibernate.criterion.Order"%>
<%@page import="org.hibernate.criterion.ProjectionList"%>
<%@page import="org.hibernate.criterion.Projections"%>
<%@page import="hibernate.ApplicationSetting"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.Locale"%>
<%@page import="hibernate.Customer"%>
<%@page import="hibernate.InvoiceItem"%>
<%@page import="java.util.Iterator"%>
<%@page import="model.OrderStatus"%>
<%@page import="java.util.List"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="hibernate.Invoice"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="java.text.DecimalFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    DecimalFormat df = new DecimalFormat("#,##0.##");
    DecimalFormat df2 = new DecimalFormat("0.##");
    SessionFactory sf = HiberUtil.getSessionFactory();
    long initSusTime = 30;
    long multiplier = 60;
    long remainingTime = 0;
    if (application.getAttribute("initSusTime") != null) {
        initSusTime = Long.parseLong(String.valueOf(application.getAttribute("initSusTime")));
    }
    if (application.getAttribute("multiplier") != null) {
        multiplier = Long.parseLong(String.valueOf(application.getAttribute("multiplier")));
    }
    if (application.getAttribute("remainingTime") != null) {
        remainingTime = Long.parseLong(String.valueOf(application.getAttribute("remainingTime")));
    }

    Session s = sf.openSession();

    String noticeContent = "";
    ApplicationSetting as = (ApplicationSetting) s.load(ApplicationSetting.class, "notice_message");
    if (as.getValue() != null) {
        noticeContent = as.getValue();
    }

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    Date startTime = cal.getTime();
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    Date endTime = cal.getTime();

    Criteria invCR = s.createCriteria(Invoice.class);
    invCR.add(Restrictions.and(Restrictions.ge("createdOn", startTime), Restrictions.le("createdOn", endTime)));
    invCR.add(Restrictions.ne("orderStatus", OrderStatus.PAYMENT_PENDING));

    double delProfitMargin = Double.parseDouble(((ApplicationSetting) s.load(ApplicationSetting.class, "delivery_profit_margin")).getValue());
    double earningTot = 0;
    double profit = 0;
    List<Invoice> invList = invCR.list();
    for (Invoice inv : invList) {
        for (Iterator<InvoiceItem> it = inv.getInvoiceItems().iterator(); it.hasNext();) {
            InvoiceItem invItem = it.next();
            double itemTot = invItem.getQty() * invItem.getStock().getSellingPrice() * (100 - invItem.getDiscount()) / 100;
            earningTot += itemTot;
            profit += itemTot - (invItem.getQty() * invItem.getStock().getBuyingPrice());
        }
        earningTot += inv.getDelFee();
        profit += inv.getDelFee() * delProfitMargin / 100;
    }

    Criteria custCR = s.createCriteria(Customer.class);
    custCR.add(Restrictions.and(Restrictions.ge("createdOn", startTime), Restrictions.le("createdOn", endTime)));

    String[] dayNames = new String[7];
    for (int i = 6; i >= 0; i--) {
        dayNames[i] = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
        cal.add(Calendar.DAY_OF_MONTH, -1);
    }

    cal = Calendar.getInstance();
    double[] earningValues = new double[7];
    double[] profitValues = new double[7];
    earningValues[6] = earningTot;
    profitValues[6] = profit;
    for (int i = 5; i >= 0; i--) {
        double chartEarningTot = 0;
        double chartProfit = 0;
        cal.add(Calendar.DAY_OF_MONTH, -1);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startT = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date endT = cal.getTime();

        Criteria inCR = s.createCriteria(Invoice.class);
        inCR.add(Restrictions.and(Restrictions.ge("createdOn", startT), Restrictions.le("createdOn", endT)));
        inCR.add(Restrictions.ne("orderStatus", OrderStatus.PAYMENT_PENDING));
        for (Invoice inv : (List<Invoice>) inCR.list()) {
            for (Iterator<InvoiceItem> it = inv.getInvoiceItems().iterator(); it.hasNext();) {
                InvoiceItem invItem = it.next();
                double itemTot = invItem.getQty() * invItem.getStock().getSellingPrice() * (100 - invItem.getDiscount()) / 100;
                chartEarningTot += itemTot;
                chartProfit += itemTot - (invItem.getQty() * invItem.getStock().getBuyingPrice());
            }
            chartEarningTot += inv.getDelFee();
            chartProfit += inv.getDelFee() * delProfitMargin / 100;
        }
        earningValues[i] = chartEarningTot;
        profitValues[i] = chartProfit;
    }

    Gson g = new Gson();
    String xAxisData = g.toJson(dayNames).replace("\"", "&quot;");
    String earningData = g.toJson(earningValues);
    String profitData = g.toJson(profitValues);

    String chartData = "{&quot;type&quot;:&quot;line&quot;,&quot;data&quot;:{&quot;labels&quot;:" + xAxisData + ",&quot;datasets&quot;:[{&quot;label&quot;:&quot;Earnings&quot;,&quot;fill&quot;:true,&quot;data&quot;:" + earningData + ",&quot;backgroundColor&quot;:&quot;rgba(255,0,0,0.15)&quot;,&quot;borderColor&quot;:&quot;rgb(255,37,37)&quot;},{&quot;label&quot;:&quot;Profit&quot;,&quot;fill&quot;:true,&quot;data&quot;:" + profitData + ",&quot;borderColor&quot;:&quot;#ff7a00&quot;,&quot;backgroundColor&quot;:&quot;rgba(255,123,105,0.18)&quot;}]},&quot;options&quot;:{&quot;maintainAspectRatio&quot;:false,&quot;legend&quot;:{&quot;display&quot;:true,&quot;reverse&quot;:false},&quot;title&quot;:{&quot;display&quot;:false},&quot;scales&quot;:{&quot;xAxes&quot;:[{&quot;gridLines&quot;:{&quot;drawBorder&quot;:false,&quot;drawTicks&quot;:false,&quot;borderDash&quot;:[&quot;0&quot;],&quot;zeroLineBorderDash&quot;:[&quot;0&quot;],&quot;drawOnChartArea&quot;:false},&quot;ticks&quot;:{&quot;fontColor&quot;:&quot;#cbcbcb&quot;,&quot;beginAtZero&quot;:false,&quot;padding&quot;:20}}],&quot;yAxes&quot;:[{&quot;gridLines&quot;:{&quot;drawBorder&quot;:false,&quot;drawTicks&quot;:false,&quot;borderDash&quot;:[&quot;0&quot;],&quot;zeroLineBorderDash&quot;:[&quot;0&quot;]},&quot;ticks&quot;:{&quot;fontColor&quot;:&quot;#cbcbcb&quot;,&quot;beginAtZero&quot;:false,&quot;padding&quot;:20}}]}}}";

    String merchant_id = ((ApplicationSetting) s.load(ApplicationSetting.class, "merchant_id")).getValue();
    String merchant_secret = ((ApplicationSetting) s.load(ApplicationSetting.class, "merchant_secret")).getValue();
    String smtp_host = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_host")).getValue();
    String smtp_port = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_port")).getValue();
    String smtp_sender = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_sender")).getValue();
    String smtp_password = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_password")).getValue();
%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="incl_header.jsp" />
        <title>MicroTek Dashboard</title>
    </head>
    <body>
        <div class="admin-container">
            <jsp:include page="incl_admin_navbar.jsp" />
            <jsp:include page="incl_msgbox.jsp" />
            <div class="d-flex">
                <jsp:include page="incl_admin_pane.jsp" />
                <div id="dash-content">
                    <div class="d-flex flex-column flex-md-row daily-div">
                        <div class="card" data-aos="zoom-in">
                            <div class="card-body">
                                <h4 class="card-title"><i class="fa fa-dollar"></i>Daily Earnings</h4>
                                <h6 class="text-muted card-subtitle mb-1">Rs. <%=df.format(earningTot)%></h6>
                            </div>
                        </div>
                        <div class="card" data-aos="zoom-in" data-aos-delay="150">
                            <div class="card-body">
                                <h4 class="card-title"><i class="fas fa-chart-bar"></i>Daily Profit</h4>
                                <h6 class="text-muted card-subtitle mb-2">Rs. <%=df.format(profit)%></h6>
                            </div>
                        </div>
                        <div class="card" data-aos="zoom-in" data-aos-delay="300">
                            <div class="card-body">
                                <h4 class="card-title"><i class="fa fa-user-plus"></i>Daily Signups</h4>
                                <h6 class="text-muted card-subtitle mb-2"><%=custCR.list().size()%></h6>
                            </div>
                        </div>
                        <div class="card" data-aos="zoom-in" data-aos-delay="450">
                            <a href="odr_mng.jsp">
                                <div class="card-body">
                                    <h4 class="card-title"><i class="fas fa-shipping-fast"></i>New Orders</h4>
                                    <h6 class="text-muted card-subtitle mb-2"><%=invList.size()%></h6>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="row no-gutters" style="margin-top: 10px;margin-bottom: 10px;">
                        <div class="col-12 col-xl-8">
                            <div class="card shadow mb-4 dash-chart" style="height: 100%;">
                                <div class="card-header">
                                    <h4><i class="fas fa-chart-line"></i>This week's Earnings & Profit Overview<br></h4>
                                </div>
                                <div class="card-body">
                                    <div class="chart-area" style="height: 300px;">
                                        <canvas data-bs-chart="<%=chartData%>"></canvas>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-12 col-xl-4">
                            <div class="card shadow" style="height: 100%;">
                                <div class="card-header">
                                    <h4 class="text-center"><i class="fas fa-chart-pie"></i>Today's Revenue Overview<br></h4>
                                </div>
                                <div class="card-body d-flex justify-content-center align-items-center">
                                    <div style="width: 100%;"><canvas data-bs-chart="{&quot;type&quot;:&quot;pie&quot;,&quot;data&quot;:{&quot;labels&quot;:[&quot;Expense&quot;,&quot;Profit&quot;],&quot;datasets&quot;:[{&quot;label&quot;:&quot;Revenue&quot;,&quot;backgroundColor&quot;:[&quot;rgba(179,0,0,0.7)&quot;,&quot;#be4400&quot;],&quot;borderColor&quot;:[&quot;#ff8179&quot;,&quot;#ff8179&quot;],&quot;data&quot;:[&quot;<%=df2.format(earningTot - profit)%>&quot;,&quot;<%=df2.format(profit)%>&quot;]}]},&quot;options&quot;:{&quot;maintainAspectRatio&quot;:true,&quot;legend&quot;:{&quot;display&quot;:true},&quot;title&quot;:{&quot;display&quot;:false,&quot;text&quot;:&quot;&quot;}}}"></canvas></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <%
                        Criteria invoiceCR = s.createCriteria(Invoice.class);
                        invoiceCR.add(Restrictions.ne("orderStatus", OrderStatus.PAYMENT_PENDING));

                        Criteria invItemCR = s.createCriteria(InvoiceItem.class, "InvoiceItemTable");
                        invItemCR.createCriteria("InvoiceItemTable.stock", "StockTable");

                        invItemCR.add(Restrictions.in("invoice", invoiceCR.list()));
                        ProjectionList projectionList = Projections.projectionList();
                        projectionList.add(Projections.groupProperty("StockTable.product"));
                        projectionList.add(Projections.alias(Projections.count("StockTable.product"), "productCount"));
                        invItemCR.setProjection(projectionList);
                        invItemCR.addOrder(Order.desc("productCount"));

                        List<Object[]> result = invItemCR.list();
                    %>

                    <div class="card-group">
                        <div class="card" data-aos="slide-up">
                            <div class="card-body">
                                <h4 class="card-title"><i class="fas fa-window-restore"></i>Top Categories</h4>
                                <div class="table-responsive">
                                    <table class="table">
                                        <tbody>
                                            <%
                                                LinkedHashMap<String, Long> prvCatMap = new LinkedHashMap();
                                                for (int i = 0; i < result.size() && prvCatMap.size() < 3; i++) {
                                                    Object[] resultItem = result.get(i);
                                                    String catName = ((Product) resultItem[0]).getCategory().getName();
                                                    if (prvCatMap.containsKey(catName)) {
                                                        prvCatMap.put(catName, ((Long) resultItem[1]) + prvCatMap.get(catName));
                                                    } else {
                                                        prvCatMap.put(catName, (Long) resultItem[1]);
                                            %>
                                            <tr>
                                                <td><%=prvCatMap.size()%></td>
                                                <td><%=catName%></td>
                                            </tr>
                                            <%
                                                    }
                                                }
                                            %>
                                        </tbody>
                                    </table>
                                </div>
                                <div style="min-height: 300px;"><canvas data-bs-chart="{&quot;type&quot;:&quot;doughnut&quot;,&quot;data&quot;:{&quot;labels&quot;:<%=g.toJson(prvCatMap.keySet()).replace("\"", "&quot;")%>,&quot;datasets&quot;:[{&quot;label&quot;:&quot;Categories&quot;,&quot;backgroundColor&quot;:[&quot;#a30000&quot;,&quot;#c87800&quot;,&quot;rgba(213,213,213,0.54)&quot;],&quot;borderColor&quot;:[&quot;#000000&quot;,&quot;#000000&quot;,&quot;#000000&quot;],&quot;data&quot;:<%=g.toJson(prvCatMap.values()).replace("\"", "&quot;")%>}]},&quot;options&quot;:{&quot;maintainAspectRatio&quot;:false,&quot;legend&quot;:{&quot;display&quot;:true,&quot;position&quot;:&quot;bottom&quot;,&quot;reverse&quot;:false},&quot;title&quot;:{&quot;display&quot;:false}}}"></canvas></div>
                            </div>
                        </div>
                        <div class="card" data-aos="slide-up" data-aos-delay="300">
                            <div class="card-body">
                                <h4 class="card-title"><i class="fas fa-tags"></i>Top Brands<br></h4>
                                <div class="table-responsive">
                                    <table class="table">
                                        <tbody>
                                            <%
                                                LinkedHashMap<String, Long> prvBrandMap = new LinkedHashMap();
                                                for (int i = 0; i < result.size() && prvBrandMap.size() < 3; i++) {
                                                    Object[] resultItem = result.get(i);
                                                    String brandName = ((Product) resultItem[0]).getBrand().getName();
                                                    if (prvBrandMap.containsKey(brandName)) {
                                                        prvBrandMap.put(brandName, ((Long) resultItem[1]) + prvBrandMap.get(brandName));
                                                    } else {
                                                        prvBrandMap.put(brandName, (Long) resultItem[1]);
                                            %>
                                            <tr>
                                                <td><%=prvBrandMap.size()%></td>
                                                <td><%=brandName%></td>
                                            </tr>
                                            <%
                                                    }
                                                }
                                            %>
                                        </tbody>
                                    </table>
                                </div>
                                <div style="min-height: 300px;"><canvas data-bs-chart="{&quot;type&quot;:&quot;doughnut&quot;,&quot;data&quot;:{&quot;labels&quot;:<%=g.toJson(prvBrandMap.keySet()).replace("\"", "&quot;")%>,&quot;datasets&quot;:[{&quot;label&quot;:&quot;Categories&quot;,&quot;backgroundColor&quot;:[&quot;#a30000&quot;,&quot;#c87800&quot;,&quot;rgba(213,213,213,0.54)&quot;],&quot;borderColor&quot;:[&quot;#000000&quot;,&quot;#000000&quot;,&quot;#000000&quot;],&quot;data&quot;:<%=g.toJson(prvBrandMap.values()).replace("\"", "&quot;")%>}]},&quot;options&quot;:{&quot;maintainAspectRatio&quot;:false,&quot;legend&quot;:{&quot;display&quot;:true,&quot;position&quot;:&quot;bottom&quot;,&quot;reverse&quot;:false},&quot;title&quot;:{&quot;display&quot;:false}}}"></canvas></div>
                            </div>
                        </div>
                        <div class="card" data-aos="slide-up" data-aos-delay="600">
                            <div class="card-body">
                                <h4 class="card-title"><i class="fas fa-box-open"></i>Top Products<br></h4>
                                <div class="table-responsive">
                                    <table class="table">
                                        <tbody>
                                            <%
                                                LinkedHashMap<String, Long> prodMap = new LinkedHashMap();
                                                for (int i = 0; i < 3; i++) {
                                                    Object[] resultItem = result.get(i);
                                                    Product p = (Product) resultItem[0];
                                                    prodMap.put(p.getBrand().getName() + " " + p.getName(), (Long) resultItem[1]);
                                            %>
                                            <tr>
                                                <td><%=i + 1%></td>
                                                <td><%=p.getBrand().getName() + " " + p.getName()%></td>
                                            </tr>
                                            <%
                                                }
                                            %>
                                        </tbody>
                                    </table>
                                </div>
                                <div style="min-height: 300px;"><canvas data-bs-chart="{&quot;type&quot;:&quot;doughnut&quot;,&quot;data&quot;:{&quot;labels&quot;:<%=g.toJson(prodMap.keySet()).replace("\"", "&quot;")%>,&quot;datasets&quot;:[{&quot;label&quot;:&quot;Categories&quot;,&quot;backgroundColor&quot;:[&quot;#a30000&quot;,&quot;#c87800&quot;,&quot;rgba(213,213,213,0.54)&quot;],&quot;borderColor&quot;:[&quot;#000000&quot;,&quot;#000000&quot;,&quot;#000000&quot;],&quot;data&quot;:<%=g.toJson(prodMap.values()).replace("\"", "&quot;")%>}]},&quot;options&quot;:{&quot;maintainAspectRatio&quot;:false,&quot;legend&quot;:{&quot;display&quot;:true,&quot;position&quot;:&quot;bottom&quot;,&quot;reverse&quot;:false},&quot;title&quot;:{&quot;display&quot;:false}}}"></canvas></div>
                            </div>
                        </div>
                    </div>
                    <div class="card" style="margin-top: 10px;">
                        <div class="card-body">
                            <h4 class="card-title"><i class="fa fa-gears"></i>Application Settings</h4>
                            <div class="settings-div">
                                <h6 class="d-flex align-items-center align-content-center mb-2"><i class="fas fa-info-circle"></i>Notice<button class="btn btn-primary noti-btn" type="button" style="margin-left: 12px;" onclick="updateNotice();">Apply Notice</button></h6>
                                <div class="d-flex">
                                    <textarea class="flex-fill" placeholder="This notification will be shown when a customer visits the homepage" id="notice-text"><%=noticeContent%></textarea>
                                </div>
                            </div>
                            <div class="settings-div sus">
                                <h6 class="mb-2"><i class="fas fa-pause-circle"></i>Suspend Application</h6>
                                <div class="d-flex flex-column align-items-center flex-sm-row">
                                    <div class="d-flex align-items-center">
                                        <span>Suspend Duration :&nbsp;</span>
                                        <%
                                            if (multiplier == 0) {
                                        %>
                                        <input type="number" id="sus-time" value="<%=initSusTime%>" disabled="">
                                        <%
                                        } else {
                                        %>
                                        <input type="number" id="sus-time" value="<%=initSusTime%>">
                                        <%
                                            }
                                        %>
                                        <select class="sus-sel" id="sus-sel" onchange="checkSuspendDuration();">
                                            <%
                                                if (multiplier == 60) {
                                            %>
                                            <option value="60" selected="">Minutes</option>
                                            <%
                                            } else {
                                            %>
                                            <option value="60">Minutes</option>
                                            <%
                                                }
                                            %>

                                            <%
                                                if (multiplier == 3600) {
                                            %>
                                            <option value="3600" selected="">Hours</option>
                                            <%
                                            } else {
                                            %>
                                            <option value="3600">Hours</option>
                                            <%
                                                }
                                            %>

                                            <%
                                                if (multiplier == 86400) {
                                            %>
                                            <option value="86400" selected="">Days</option>
                                            <%
                                            } else {
                                            %>
                                            <option value="86400">Days</option>
                                            <%
                                                }
                                            %>

                                            <%
                                                if (multiplier == 0) {
                                            %>
                                            <option value="0" selected="">Indefinitely</option>
                                            <%
                                            } else {
                                            %>
                                            <option value="0">Indefinitely</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <button class="btn btn-primary sus-btn" type="button" onclick="suspendApp();">Suspend</button>
                                    <%
                                        if (remainingTime <= 0) {
                                    %>
                                    <button class="btn btn-primary sus-btn" type="button" onclick="resumeApp();" id="sus-res-btn" disabled="">Resume</button>
                                    <%
                                    } else {
                                    %>
                                    <button class="btn btn-primary sus-btn" type="button" onclick="resumeApp();" id="sus-res-btn">Resume</button>
                                    <%
                                        }
                                    %>
                                </div>
                            </div>
                            <div class="settings-div emlsett">
                                <h6><i class="fas fa-mail-bulk"></i>Email Settings<button class="btn btn-primary noti-btn" type="button" style="margin-left: 10px;" onclick="updateSMTPDetails();">Apply</button></h6>
                                <div class="d-flex justify-content-between align-items-center settings-field"><span>SMTP Host :</span><input type="text" value="<%=smtp_host%>" id="host"></div>
                                <div class="d-flex justify-content-between align-items-center settings-field"><span>SMTP Port :</span><input type="text" value="<%=smtp_port%>" id="port"></div>
                                <div class="d-flex justify-content-between align-items-center settings-field"><span>Sender :</span><input type="text" value="<%=smtp_sender%>" id="sender"></div>
                                <div class="d-flex justify-content-between align-items-center settings-field"><span>Password :</span><input type="password" value="<%=smtp_password%>" style="text-indent: 10px;" id="pw"></div>
                            </div>
                            <div class="settings-div pgsett">
                                <h6><i class="fas fa-mail-bulk"></i>Payment Gateway Settings<button class="btn btn-primary noti-btn" type="button" style="margin-left: 10px;" onclick="updatePGDetails();">Apply</button></h6>
                                <div class="d-flex justify-content-between align-items-center settings-field"><span>Merchant ID :</span><input type="text" value="<%=merchant_id%>" id="merchant_id"></div>
                                <div class="d-flex justify-content-between align-items-center settings-field"><span>Merchant Secret&nbsp;:</span><input type="text" value="<%=merchant_secret%>" id="merchant_secret"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="assets/js/jquery.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="assets/js/chart.min.js"></script>
        <script src="assets/js/bs-init.js"></script>
        <script src="assets/js/aos.js"></script>
        <script src="assets/js/srcipt.js"></script>
        <script src="assets/js/dashboard.js"></script>
        <script>
                                        document.getElementById("dash-link-1").className += " active";
                                        document.getElementById("nav-link-1").className += " active";
        </script>
    </body>
</html>
<%
    s.close();
%>