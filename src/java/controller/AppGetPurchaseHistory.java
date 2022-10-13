package controller;

import com.google.gson.Gson;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.OrderStatus;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppGetPurchaseHistory", urlPatterns = {"/AppGetPurchaseHistory"})
public class AppGetPurchaseHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Gson g = new Gson();
        DecimalFormat df = new DecimalFormat("#,##0.##");

        LinkedList<HashMap<String, String>> orderList = new LinkedList();

        int custID = Integer.parseInt(request.getParameter("custID"));
        Customer c = (Customer) s.load(Customer.class, custID);

        Criteria invCR = s.createCriteria(Invoice.class);
        invCR.add(Restrictions.eq("customer", c));
        invCR.add(Restrictions.ne("orderStatus", OrderStatus.PAYMENT_PENDING));
        invCR.addOrder(Order.desc("id"));
        List<Invoice> invList = invCR.list();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy hh:mm a");
        for (Invoice inv : invList) {
            HashMap<String, String> order = new HashMap();

            cal.setTime(inv.getCreatedOn());
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            double tot = 0;
            Set<InvoiceItem> invItemSet = inv.getInvoiceItems();
            for (Iterator<InvoiceItem> it = invItemSet.iterator(); it.hasNext();) {
                InvoiceItem invItem = it.next();
                tot += invItem.getQty() * invItem.getStock().getSellingPrice() * (100 - invItem.getDiscount()) / 100;
            }

            order.put("id", String.valueOf(inv.getId()));
            order.put("title", "Order ID : " + inv.getId() + " - " + dayOfMonth + Validation.getNthSmall(dayOfMonth) + " " + sdf.format(cal.getTime()));
            order.put("status", String.valueOf(inv.getOrderStatus()));
            order.put("details", invItemSet.size() + " item(s) - " + df.format(tot + inv.getDelFee()) + " LKR");
            orderList.add(order);
        }
        response.getWriter().write(g.toJson(orderList));
        s.close();
    }

}
