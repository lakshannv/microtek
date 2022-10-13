<%@page import="java.util.Locale"%>
<%@page import="hibernate.ApplicationSetting"%>
<%@page import="hibernate.InvoiceItem"%>
<%@page import="java.util.Iterator"%>
<%@page import="model.OrderStatus"%>
<%@page import="java.util.List"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="hibernate.Invoice"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="org.hibernate.Session"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="hibernate.HiberUtil"%>
<%@page import="java.text.DecimalFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    DecimalFormat df2 = new DecimalFormat("0.##");
    Session s = HiberUtil.getSessionFactory().openSession();
    double delProfitMargin = Double.parseDouble(((ApplicationSetting) s.load(ApplicationSetting.class, "delivery_profit_margin")).getValue());

    Calendar cal = Calendar.getInstance();
    String[] dayNames = new String[7];
    for (int i = 6; i >= 0; i--) {
        dayNames[i] = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
        cal.add(Calendar.DAY_OF_MONTH, -1);
    }
    cal = Calendar.getInstance();
    double[] earningValuesw = new double[7];
    double[] profitValuesw = new double[7];
    for (int i = 6; i >= 0; i--) {
        double chartEarningTot = 0;
        double chartProfit = 0;
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
        earningValuesw[i] = chartEarningTot;
        profitValuesw[i] = chartProfit;
        cal.add(Calendar.DAY_OF_MONTH, -1);
    }

    cal = Calendar.getInstance();
    int dayCount = cal.get(Calendar.DAY_OF_MONTH);
    String[] days = new String[dayCount];
    for (int i = dayCount - 1; i >= 0; i--) {
        days[i] = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.DAY_OF_MONTH, -1);
    }
    cal = Calendar.getInstance();
    double[] earningValuesm = new double[dayCount];
    double[] profitValuesm = new double[dayCount];
    for (int i = dayCount - 1; i >= 0; i--) {
        double chartEarningTot = 0;
        double chartProfit = 0;
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
        earningValuesm[i] = chartEarningTot;
        profitValuesm[i] = chartProfit;
        cal.add(Calendar.DAY_OF_MONTH, -1);
    }

    cal = Calendar.getInstance();
    int monthCount = cal.get(Calendar.MONTH) + 1;
    String[] monthNames = new String[monthCount];
    for (int i = cal.get(Calendar.MONTH); i >= 0; i--) {
        monthNames[i] = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        cal.add(Calendar.MONTH, -1);
    }
    cal = Calendar.getInstance();
    double[] earningValuesy = new double[monthCount];
    double[] profitValuesy = new double[monthCount];
    for (int i = monthCount - 1; i >= 0; i--) {
        double chartEarningTot = 0;
        double chartProfit = 0;
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startT = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
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
        earningValuesy[i] = chartEarningTot;
        profitValuesy[i] = chartProfit;
        cal.add(Calendar.MONTH, -1);
    }

    Gson g = new Gson();
    String xAxisDataw = g.toJson(dayNames).replace("\"", "&quot;");
    String earningDataw = g.toJson(earningValuesw);
    String profitDataw = g.toJson(profitValuesw);

    String xAxisDatam = g.toJson(days).replace("\"", "&quot;");
    String earningDatam = g.toJson(earningValuesm);
    String profitDatam = g.toJson(profitValuesm);

    String xAxisDatay = g.toJson(monthNames).replace("\"", "&quot;");
    String earningDatay = g.toJson(earningValuesy);
    String profitDatay = g.toJson(profitValuesy);

    String weekChartData = "{&quot;type&quot;:&quot;line&quot;,&quot;data&quot;:{&quot;labels&quot;:" + xAxisDataw + ",&quot;datasets&quot;:[{&quot;label&quot;:&quot;Earnings&quot;,&quot;fill&quot;:true,&quot;data&quot;:" + earningDataw + ",&quot;backgroundColor&quot;:&quot;rgba(255,0,0,0.15)&quot;,&quot;borderColor&quot;:&quot;rgb(255,37,37)&quot;},{&quot;label&quot;:&quot;Profit&quot;,&quot;fill&quot;:true,&quot;data&quot;:" + profitDataw + ",&quot;borderColor&quot;:&quot;#ff7a00&quot;,&quot;backgroundColor&quot;:&quot;rgba(255,123,105,0.18)&quot;}]},&quot;options&quot;:{&quot;maintainAspectRatio&quot;:false,&quot;legend&quot;:{&quot;display&quot;:true,&quot;reverse&quot;:false},&quot;title&quot;:{&quot;display&quot;:false},&quot;scales&quot;:{&quot;xAxes&quot;:[{&quot;gridLines&quot;:{&quot;drawBorder&quot;:false,&quot;drawTicks&quot;:false,&quot;borderDash&quot;:[&quot;0&quot;],&quot;zeroLineBorderDash&quot;:[&quot;0&quot;],&quot;drawOnChartArea&quot;:false},&quot;ticks&quot;:{&quot;fontColor&quot;:&quot;#cbcbcb&quot;,&quot;beginAtZero&quot;:false,&quot;padding&quot;:20}}],&quot;yAxes&quot;:[{&quot;gridLines&quot;:{&quot;drawBorder&quot;:false,&quot;drawTicks&quot;:false,&quot;borderDash&quot;:[&quot;0&quot;],&quot;zeroLineBorderDash&quot;:[&quot;0&quot;]},&quot;ticks&quot;:{&quot;fontColor&quot;:&quot;#cbcbcb&quot;,&quot;beginAtZero&quot;:false,&quot;padding&quot;:20}}]}}}";
    String monthChartData = "{&quot;type&quot;:&quot;line&quot;,&quot;data&quot;:{&quot;labels&quot;:" + xAxisDatam + ",&quot;datasets&quot;:[{&quot;label&quot;:&quot;Earnings&quot;,&quot;fill&quot;:true,&quot;data&quot;:" + earningDatam + ",&quot;backgroundColor&quot;:&quot;rgba(255,0,0,0.15)&quot;,&quot;borderColor&quot;:&quot;rgb(255,37,37)&quot;},{&quot;label&quot;:&quot;Profit&quot;,&quot;fill&quot;:true,&quot;data&quot;:" + profitDatam + ",&quot;borderColor&quot;:&quot;#ff7a00&quot;,&quot;backgroundColor&quot;:&quot;rgba(255,123,105,0.18)&quot;}]},&quot;options&quot;:{&quot;maintainAspectRatio&quot;:false,&quot;legend&quot;:{&quot;display&quot;:true,&quot;reverse&quot;:false},&quot;title&quot;:{&quot;display&quot;:false},&quot;scales&quot;:{&quot;xAxes&quot;:[{&quot;gridLines&quot;:{&quot;drawBorder&quot;:false,&quot;drawTicks&quot;:false,&quot;borderDash&quot;:[&quot;0&quot;],&quot;zeroLineBorderDash&quot;:[&quot;0&quot;],&quot;drawOnChartArea&quot;:false},&quot;ticks&quot;:{&quot;fontColor&quot;:&quot;#cbcbcb&quot;,&quot;beginAtZero&quot;:false,&quot;padding&quot;:20}}],&quot;yAxes&quot;:[{&quot;gridLines&quot;:{&quot;drawBorder&quot;:false,&quot;drawTicks&quot;:false,&quot;borderDash&quot;:[&quot;0&quot;],&quot;zeroLineBorderDash&quot;:[&quot;0&quot;]},&quot;ticks&quot;:{&quot;fontColor&quot;:&quot;#cbcbcb&quot;,&quot;beginAtZero&quot;:false,&quot;padding&quot;:20}}]}}}";
    String yearChartData = "{&quot;type&quot;:&quot;line&quot;,&quot;data&quot;:{&quot;labels&quot;:" + xAxisDatay + ",&quot;datasets&quot;:[{&quot;label&quot;:&quot;Earnings&quot;,&quot;fill&quot;:true,&quot;data&quot;:" + earningDatay + ",&quot;backgroundColor&quot;:&quot;rgba(255,0,0,0.15)&quot;,&quot;borderColor&quot;:&quot;rgb(255,37,37)&quot;},{&quot;label&quot;:&quot;Profit&quot;,&quot;fill&quot;:true,&quot;data&quot;:" + profitDatay + ",&quot;borderColor&quot;:&quot;#ff7a00&quot;,&quot;backgroundColor&quot;:&quot;rgba(255,123,105,0.18)&quot;}]},&quot;options&quot;:{&quot;maintainAspectRatio&quot;:false,&quot;legend&quot;:{&quot;display&quot;:true,&quot;reverse&quot;:false},&quot;title&quot;:{&quot;display&quot;:false},&quot;scales&quot;:{&quot;xAxes&quot;:[{&quot;gridLines&quot;:{&quot;drawBorder&quot;:false,&quot;drawTicks&quot;:false,&quot;borderDash&quot;:[&quot;0&quot;],&quot;zeroLineBorderDash&quot;:[&quot;0&quot;],&quot;drawOnChartArea&quot;:false},&quot;ticks&quot;:{&quot;fontColor&quot;:&quot;#cbcbcb&quot;,&quot;beginAtZero&quot;:false,&quot;padding&quot;:20}}],&quot;yAxes&quot;:[{&quot;gridLines&quot;:{&quot;drawBorder&quot;:false,&quot;drawTicks&quot;:false,&quot;borderDash&quot;:[&quot;0&quot;],&quot;zeroLineBorderDash&quot;:[&quot;0&quot;]},&quot;ticks&quot;:{&quot;fontColor&quot;:&quot;#cbcbcb&quot;,&quot;beginAtZero&quot;:false,&quot;padding&quot;:20}}]}}}";
%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="incl_header.jsp" />
        <title>MicroTek Sales & Reports</title>
    </head>
    <body>
        <div class="admin-container">
            <jsp:include page="incl_admin_navbar.jsp" />
            <jsp:include page="incl_msgbox.jsp" />
            <div class="d-flex">
                <jsp:include page="incl_admin_pane.jsp" />
                <div id="dash-content">
                    <div class="card shadow" style="margin-bottom: 10px;">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h4><i class="fas fa-chart-line"></i><strong>How This week went...</strong><br></h4><button class="btn btn-primary" type="button" onclick="generateSalesReport('Weekly');">Generate Weekly Sales Report</button></div>
                        <div class="card-body">
                            <div class="chart-area" style="min-height: 400px;">
                                <canvas data-bs-chart="<%=weekChartData%>"></canvas>
                            </div>
                        </div>
                    </div>
                    <div class="card shadow" style="margin-bottom: 10px;">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h4><i class="fas fa-chart-line"></i><strong>How This month looks so far...</strong><br></h4><button class="btn btn-primary" type="button" onclick="generateSalesReport('Monthly');">Generate Monthly Sales Report</button></div>
                        <div class="card-body">
                            <div class="chart-area" style="min-height: 400px;">
                                <canvas data-bs-chart="<%=monthChartData%>"></canvas>
                            </div>
                        </div>
                    </div>
                    <div class="card shadow">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h4><i class="fas fa-chart-line"></i><strong>How's things going this year..</strong><br></h4><button class="btn btn-primary" type="button" onclick="generateSalesReport('Annual');">Generate Annual Sales Report</button></div>
                        <div class="card-body">
                            <div class="chart-area" style="min-height: 400px;">
                                <canvas data-bs-chart="<%=yearChartData%>"></canvas>
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
        <script src="assets/js/reports.js"></script>
        <script>
                                document.getElementById("dash-link-6").className += " active";
                                document.getElementById("nav-link-6").className += " active";
        </script>
    </body>
</html>
<%
    s.close();
%>