package controller;

import com.google.gson.Gson;
import hibernate.ApplicationSetting;
import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.OrderStatus;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author laksh
 */
@WebServlet(name = "AdminAppGetReportsInitData", urlPatterns = {"/AdminAppGetReportsInitData"})
public class AdminAppGetReportsInitData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        Gson g = new Gson();
        double delProfitMargin = Double.parseDouble(((ApplicationSetting) s.load(ApplicationSetting.class, "delivery_profit_margin")).getValue());

        Calendar cal = Calendar.getInstance();
        String[] dayNames = new String[7];
        for (int i = 6; i >= 0; i--) {
            dayNames[i] = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT_FORMAT, Locale.US);
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

        responseData.put("dayNames", dayNames);
        responseData.put("earningValuesw", earningValuesw);
        responseData.put("profitValuesw", profitValuesw);

        responseData.put("days", days);
        responseData.put("earningValuesm", earningValuesm);
        responseData.put("profitValuesm", profitValuesm);

        responseData.put("monthNames", monthNames);
        responseData.put("earningValuesy", earningValuesy);
        responseData.put("profitValuesy", profitValuesy);

        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
