package controller;

import com.google.gson.Gson;
import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

@WebServlet(name = "AdminAppGetOrderMngInitData", urlPatterns = {"/AdminAppGetOrderMngInitData"})
public class AdminAppGetOrderMngInitData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy hh:mm a");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        LinkedList<LinkedHashMap<String, String>> orderList = new LinkedList();
        Gson g = new Gson();

        String odrStat = request.getParameter("odrStat");
        String delMethod = request.getParameter("odrDel");
        String orderBy = request.getParameter("odrBy");
        String filterBy = request.getParameter("filterBy");

        Criteria invCR = s.createCriteria(Invoice.class);
        if (odrStat != null) {
            if (!odrStat.isEmpty()) {
                if (odrStat.equals("12")) {
                    invCR.add(Restrictions.or(Restrictions.eq("orderStatus", OrderStatus.RECEIVED), Restrictions.eq("orderStatus", OrderStatus.DISPATCHED)));
                } else {
                    invCR.add(Restrictions.eq("orderStatus", Byte.parseByte(odrStat)));
                }

            }
        }
        if (delMethod != null) {
            if (!delMethod.isEmpty()) {
                invCR.add(Restrictions.eq("deliveryMethod", Byte.parseByte(delMethod)));
            }
        }
        if (filterBy != null) {
            if (!filterBy.isEmpty()) {
                invCR.add(Restrictions.sqlRestriction("id LIKE '" + filterBy + "%'"));
            }
        }
        if (orderBy.equals("Order ID Desc.")) {
            invCR.addOrder(Order.desc("id"));
        } else if (orderBy.equals("Order ID Asc.")) {
            invCR.addOrder(Order.asc("id"));
        }
        
        Calendar cal = Calendar.getInstance();
        List<Invoice> invList = invCR.list();
        
        for (Invoice inv : invList) {
            cal.setTime(inv.getCreatedOn());
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            
            LinkedHashMap<String, String> order = new LinkedHashMap();
            cal.setTime(inv.getCreatedOn());
            double tot = 0;
            Set<InvoiceItem> invItemSet = inv.getInvoiceItems();
            for (Iterator<InvoiceItem> it = invItemSet.iterator(); it.hasNext();) {
                InvoiceItem invItem = it.next();
                tot += invItem.getQty() * invItem.getStock().getSellingPrice() * (100 - invItem.getDiscount()) / 100;
            }

            order.put("id", String.valueOf(inv.getId()));
            order.put("title", "Order ID : " + inv.getId() + " - " + dayOfMonth + Validation.getNthSmall(dayOfMonth) + " " + sdf.format(cal.getTime()));
            order.put("status", String.valueOf(inv.getOrderStatus()));
            if (inv.getDeliveryMethod() == (byte) 0) {
                 order.put("delMethod", "Free Delivery");
            } else {
                order.put("delMethod", "Expedited Delivery");
            }
           
            order.put("details", invItemSet.size() + " item(s) - " + df.format(tot + inv.getDelFee()) + " LKR");
            orderList.add(order);
        }

        responseData.put("orderList", orderList);
        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
