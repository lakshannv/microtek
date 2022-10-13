package controller;

import hibernate.ApplicationSetting;
import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import hibernate.User;
import hibernate.UserPrivilege;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import model.ReportItem;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ViewSalesReport", urlPatterns = {"/ViewSalesReport"})
public class ViewSalesReport extends HttpServlet {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Session s = HiberUtil.getSessionFactory().openSession();
            User sesUsr = (User) request.getSession().getAttribute("usr");
            if (sesUsr == null) {
                request.getRequestDispatcher("404.jsp").forward(request, response);
            } else {
                User usr = (User) s.load(User.class, sesUsr.getId());
                UserPrivilege up = (UserPrivilege) s.load(UserPrivilege.class, 1);
                if (usr.getUserType().getUserPrivileges().contains(up)) {

                    double delProfitMargin = Double.parseDouble(((ApplicationSetting) s.load(ApplicationSetting.class, "delivery_profit_margin")).getValue());
                    ArrayList<ReportItem> reportItemList = new ArrayList<>();
                    String type = request.getParameter("type");

                    if (type.equals("Weekly")) {
                        Calendar cal = Calendar.getInstance();
                        String[] dayNames = new String[7];
                        for (int i = 6; i >= 0; i--) {
                            dayNames[i] = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
                            cal.add(Calendar.DAY_OF_MONTH, -1);
                        }

                        cal = Calendar.getInstance();
                        double[] earningValues = new double[7];
                        double[] profitValues = new double[7];
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
                            earningValues[i] = chartEarningTot;
                            profitValues[i] = chartProfit;
                            cal.add(Calendar.DAY_OF_MONTH, -1);
                        }

                        HashMap<String, Object> hm = new HashMap<>();
                        hm.put("TITLE", "Weekly Sales Report");
                        hm.put("DATE", sdf.format(new Date()));
                        hm.put("XUNIT", "Day");
                        for (int i = 0; i < dayNames.length; i++) {
                            reportItemList.add(new ReportItem(dayNames[i], earningValues[i], profitValues[i]));
                        }
                        InputStream is = getServletContext().getResourceAsStream("/WEB-INF/classes/reports/sales.jasper");
                        JasperPrint jp = JasperFillManager.fillReport(is, hm, new JRBeanCollectionDataSource(reportItemList));
                        JasperExportManager.exportReportToPdfStream(jp, response.getOutputStream());
                    } else if (type.equals("Monthly")) {
                        Calendar cal = Calendar.getInstance();
                        int dayCount = cal.get(Calendar.DAY_OF_MONTH);
                        String[] days = new String[dayCount];
                        for (int i = dayCount - 1; i >= 0; i--) {
                            days[i] =  String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
                            cal.add(Calendar.DAY_OF_MONTH, -1);
                        }

                        cal = Calendar.getInstance();
                        double[] earningValues = new double[dayCount];
                        double[] profitValues = new double[dayCount];
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
                            earningValues[i] = chartEarningTot;
                            profitValues[i] = chartProfit;
                            cal.add(Calendar.DAY_OF_MONTH, -1);
                        }
                        
                        cal = Calendar.getInstance();
                        HashMap<String, Object> hm = new HashMap<>();
                        hm.put("TITLE", "Monthly Sales Report - " + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + Calendar.getInstance().get(Calendar.YEAR));
                        hm.put("DATE", sdf.format(new Date()));
                        hm.put("XUNIT", "Date");
                        for (int i = 0; i < days.length; i++) {
                            reportItemList.add(new ReportItem(days[i], earningValues[i], profitValues[i]));
                        }
                        InputStream is = getServletContext().getResourceAsStream("/WEB-INF/classes/reports/sales.jasper");
                        JasperPrint jp = JasperFillManager.fillReport(is, hm, new JRBeanCollectionDataSource(reportItemList));
                        JasperExportManager.exportReportToPdfStream(jp, response.getOutputStream());
                    } else if (type.equals("Annual")) {
                        Calendar cal = Calendar.getInstance();
                        int monthCount = cal.get(Calendar.MONTH) + 1;
                        String[] monthNames = new String[monthCount];
                        for (int i = cal.get(Calendar.MONTH); i >= 0; i--) {
                            monthNames[i] = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
                            cal.add(Calendar.MONTH, -1);
                        }

                        cal = Calendar.getInstance();
                        double[] earningValues = new double[monthCount];
                        double[] profitValues = new double[monthCount];
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
                            earningValues[i] = chartEarningTot;
                            profitValues[i] = chartProfit;
                            cal.add(Calendar.MONTH, -1);
                        }

                        HashMap<String, Object> hm = new HashMap<>();
                        hm.put("TITLE", "Annual Sales Report - " + Calendar.getInstance().get(Calendar.YEAR));
                        hm.put("DATE", sdf.format(new Date()));
                        hm.put("XUNIT", "Month");
                        for (int i = 0; i < monthNames.length; i++) {
                            reportItemList.add(new ReportItem(monthNames[i], earningValues[i], profitValues[i]));
                        }
                        InputStream is = getServletContext().getResourceAsStream("/WEB-INF/classes/reports/sales.jasper");
                        JasperPrint jp = JasperFillManager.fillReport(is, hm, new JRBeanCollectionDataSource(reportItemList));
                        JasperExportManager.exportReportToPdfStream(jp, response.getOutputStream());
                    } else {
                        request.getRequestDispatcher("404.jsp").forward(request, response);
                    }

                } else {
                    request.getRequestDispatcher("404.jsp").forward(request, response);
                }
            }
        } catch (Exception ex) {
            request.getRequestDispatcher("500.jsp").forward(request, response);
        }
    }
}
