package controller;

import hibernate.City;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import hibernate.Stock;
import hibernate.User;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.OrderItem;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "AppViewInvoice", urlPatterns = {"/AppViewInvoice"})
public class AppViewInvoice extends HttpServlet {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int invID = Integer.parseInt(request.getParameter("orderID"));
            SessionFactory sf = HiberUtil.getSessionFactory();
            Session s = sf.openSession();
            Invoice inv = (Invoice) s.load(Invoice.class, invID);

            String billingAddress = "[ Same as Delivery address ]";
            if (inv.getBillingCityId() != 0) {
                City c = (City) s.load(City.class, inv.getBillingCityId());
                billingAddress = inv.getBillingAddress() + ", " + c.getName() + ", " + c.getDistrict().getName() + ", " + c.getDistrict().getProvince().getName() + " Province.";
            }
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("INV_ID", inv.getId());
            hm.put("INV_DATE", sdf.format(inv.getCreatedOn()));
            hm.put("CUST", inv.getCustomer().getFname() + " " + inv.getCustomer().getLname());
            hm.put("TEL", inv.getCustomer().getMobile());
            hm.put("ADDR", inv.getAddress() + ", " + inv.getCity().getName() + ", " + inv.getCity().getDistrict().getName() + ", " + inv.getCity().getDistrict().getProvince().getName() + " Province.");
            hm.put("BILL_ADDR", billingAddress);
            hm.put("DEL_FEE", inv.getDelFee());

            ArrayList<OrderItem> orderItemList = new ArrayList<>();
            Set<InvoiceItem> invItemSet = inv.getInvoiceItems();
            DecimalFormat df = new DecimalFormat("#,##0.##");
            for (Iterator<InvoiceItem> iterator = invItemSet.iterator(); iterator.hasNext();) {
                InvoiceItem invItem = iterator.next();
                Stock stk = invItem.getStock();
                String discount = "";
                if (invItem.getDiscount() != 0) {
                    discount = " (" + df.format(invItem.getDiscount()) + "% off)";
                }
                orderItemList.add(new OrderItem(stk.getId(), stk.getProduct().getBrand().getName() + " " + stk.getProduct().getName() + discount, stk.getProduct().getCategory().getName(), stk.getSellingPrice() * (100 - invItem.getDiscount()) / 100, invItem.getQty()));
            }

            InputStream is = getServletContext().getResourceAsStream("/WEB-INF/classes/reports/invoice.jasper");
            JasperPrint jp = JasperFillManager.fillReport(is, hm, new JRBeanCollectionDataSource(orderItemList));

            JasperExportManager.exportReportToPdfStream(jp, response.getOutputStream());
            s.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
