package controller;

import com.google.gson.Gson;
import hibernate.ApplicationSetting;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import hibernate.Product;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.OrderStatus;
import static model.Validation.sortMapByValue;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author laksh
 */
@WebServlet(name = "AdminAppGetDashboardInitData", urlPatterns = {"/AdminAppGetDashboardInitData"})
public class AdminAppGetDashboardInitData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        Gson g = new Gson();
        DecimalFormat df = new DecimalFormat("#,##0.##");
        DecimalFormat df2 = new DecimalFormat("0.##");

        long initSusTime = 30;
        long multiplier = 60;
        long remainingTime = 0;
        if (getServletContext().getAttribute("initSusTime") != null) {
            initSusTime = Long.parseLong(String.valueOf(getServletContext().getAttribute("initSusTime")));
        }
        if (getServletContext().getAttribute("multiplier") != null) {
            multiplier = Long.parseLong(String.valueOf(getServletContext().getAttribute("multiplier")));
        }
        if (getServletContext().getAttribute("remainingTime") != null) {
            remainingTime = Long.parseLong(String.valueOf(getServletContext().getAttribute("remainingTime")));
        }

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
            dayNames[i] = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
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

        String merchant_id = ((ApplicationSetting) s.load(ApplicationSetting.class, "merchant_id")).getValue();
        String merchant_secret = ((ApplicationSetting) s.load(ApplicationSetting.class, "merchant_secret_app")).getValue();
        String smtp_host = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_host")).getValue();
        String smtp_port = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_port")).getValue();
        String smtp_sender = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_sender")).getValue();
        String smtp_password = ((ApplicationSetting) s.load(ApplicationSetting.class, "smtp_password")).getValue();

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

        LinkedHashMap<String, Long> prvCatMap = new LinkedHashMap();
        for (int i = 0; i < result.size(); i++) {
            Object[] resultItem = result.get(i);
            String catName = ((Product) resultItem[0]).getCategory().getName();
            if (prvCatMap.containsKey(catName)) {
                prvCatMap.put(catName, ((Long) resultItem[1]) + prvCatMap.get(catName));
            } else {
                prvCatMap.put(catName, (Long) resultItem[1]);

            }
        }

        Map<String, Long> sortedCatMap = sortMapByValue(prvCatMap);

        LinkedList<String> topCatNamesList = new LinkedList();
        LinkedList<String> topCatValuesList = new LinkedList();
        for (Map.Entry<String, Long> entry : sortedCatMap.entrySet()) {
            topCatNamesList.add(entry.getKey());
            topCatValuesList.add(String.valueOf(entry.getValue()));
        }

        LinkedHashMap<String, Long> prvBrandMap = new LinkedHashMap();
        for (int i = 0; i < result.size(); i++) {
            Object[] resultItem = result.get(i);
            String brandName = ((Product) resultItem[0]).getBrand().getName();
            if (prvBrandMap.containsKey(brandName)) {
                prvBrandMap.put(brandName, ((Long) resultItem[1]) + prvBrandMap.get(brandName));
            } else {
                prvBrandMap.put(brandName, (Long) resultItem[1]);

            }
        }
        Map<String, Long> sortedBrandMap = sortMapByValue(prvBrandMap);

        LinkedList<String> topBrandNamesList = new LinkedList();
        LinkedList<String> topBrandValuesList = new LinkedList();
        for (Map.Entry<String, Long> entry : sortedBrandMap.entrySet()) {
            topBrandNamesList.add(entry.getKey());
            topBrandValuesList.add(String.valueOf(entry.getValue()));
        }

        responseData.put("dayNames", dayNames);
        responseData.put("earningValues", earningValues);
        responseData.put("profitValues", profitValues);

        responseData.put("dailyEarnings", df.format(earningTot));
        responseData.put("dailyProfit", df.format(profit));
        responseData.put("dailySignups", String.valueOf(custCR.list().size()));
        responseData.put("newOrdersCount", String.valueOf(invList.size()));

        responseData.put("noticeContent", noticeContent);
        responseData.put("merchant_id", merchant_id);
        responseData.put("merchant_secret", merchant_secret);
        responseData.put("smtp_host", smtp_host);
        responseData.put("smtp_port", smtp_port);
        responseData.put("smtp_sender", smtp_sender);
        responseData.put("smtp_password", smtp_password);
        responseData.put("initSusTime", String.valueOf(initSusTime));
        responseData.put("multiplier", String.valueOf(multiplier));
        responseData.put("isSuspended", remainingTime <= 0);

        responseData.put("topCatNamesList", topCatNamesList);
        responseData.put("topCatValuesList", topCatValuesList);
        responseData.put("topBrandNamesList", topBrandNamesList);
        responseData.put("topBrandValuesList", topBrandValuesList);

        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
