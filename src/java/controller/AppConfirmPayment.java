package controller;

import hibernate.Cart;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import hibernate.Stock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.OrderStatus;
import org.hibernate.Session;

@WebServlet(name = "AppConfirmPayment", urlPatterns = {"/AppConfirmPayment"})
public class AppConfirmPayment extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Session s = HiberUtil.getSessionFactory().openSession();

        int custID = Integer.parseInt(request.getParameter("custID"));

        int invID = Integer.parseInt(request.getParameter("orderID"));
        String orderType = request.getParameter("orderType");

        Invoice inv = (Invoice) s.load(Invoice.class, invID);
        inv.setOrderStatus(OrderStatus.RECEIVED);
        Date d = new Date();
        inv.setCreatedOn(d);
        inv.setLastChangedOn(d);
        s.update(inv);
        Set<InvoiceItem> invItemSet = inv.getInvoiceItems();
        for (Iterator<InvoiceItem> iterator = invItemSet.iterator(); iterator.hasNext();) {
            InvoiceItem invItem = iterator.next();
            Stock stk = invItem.getStock();
            int newQty = stk.getQty() - invItem.getQty();
            if (newQty < 0) {
                newQty = 0;
            }
            stk.setQty(newQty);
            s.update(stk);
        }

        if (orderType.equals("cart")) {
            Customer c = (Customer) s.load(Customer.class, custID);
            Set<Cart> cartItemSet = c.getCarts();
            List<Stock> stkList = new ArrayList();
            for (Iterator<InvoiceItem> iterator = invItemSet.iterator(); iterator.hasNext();) {
                InvoiceItem invItem = iterator.next();
                stkList.add(invItem.getStock());
            }

            for (Iterator<Cart> iterator = cartItemSet.iterator(); iterator.hasNext();) {
                Cart crt = iterator.next();
                if (stkList.contains(crt.getStock())) {
                    s.delete(crt);
                }
            }
        }
        s.beginTransaction().commit();
        s.close();
    }

}
