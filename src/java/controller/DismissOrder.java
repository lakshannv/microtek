package controller;

import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "DismissOrder", urlPatterns = {"/DismissOrder"})
public class DismissOrder extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int invID = Integer.parseInt(request.getParameter("orderID"));
        
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Invoice inv = (Invoice) s.load(Invoice.class, invID);
        Set<InvoiceItem> invItemSet = inv.getInvoiceItems();
        for (Iterator<InvoiceItem> iterator = invItemSet.iterator(); iterator.hasNext();) {
            InvoiceItem invItem = iterator.next();
            s.delete(invItem);
        }
        s.delete(inv);
        s.beginTransaction().commit();
        s.close();
    }
}
