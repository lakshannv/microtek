package controller;

import com.google.gson.Gson;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Invoice;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.OrderStatus;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppGetAccData", urlPatterns = {"/AppGetAccData"})
public class AppGetAccData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HashMap<String, Object> responeDataMap = new HashMap();
        Gson g = new Gson();

        Session s = HiberUtil.getSessionFactory().openSession();
        int custID = Integer.parseInt(request.getParameter("custID"));

        Customer c = (Customer) s.load(Customer.class, custID);
        
        Criteria invCR = s.createCriteria(Invoice.class);
        invCR.add(Restrictions.eq("customer", c));
        invCR.add(Restrictions.ne("orderStatus", OrderStatus.COMPLETED));
        invCR.addOrder(Order.desc("id"));
        List<Invoice> invList = invCR.list();
        
        responeDataMap.put("pendingOrderCount", String.valueOf(invList.size()));
        
        invCR = s.createCriteria(Invoice.class);
        invCR.add(Restrictions.eq("customer", c));
        invCR.add(Restrictions.ne("orderStatus", OrderStatus.PAYMENT_PENDING));
        invCR.addOrder(Order.desc("id"));
        invList = invCR.list();
        
        responeDataMap.put("purchaseOrderCount", String.valueOf(invList.size()));
        

        response.getWriter().write(g.toJson(responeDataMap));
        s.beginTransaction().commit();
        s.close();
    }

}
