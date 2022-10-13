package controller;

import hibernate.ApplicationSetting;
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
import model.HashGeneratorUtils;
import model.OrderStatus;
import org.hibernate.Session;

@WebServlet(name = "ConfirmPayment", urlPatterns = {"/ConfirmPayment"})
public class ConfirmPayment extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Session s = HiberUtil.getSessionFactory().openSession();

        String merchID = request.getParameter("merchant_id");
        String orderID = request.getParameter("order_id");
        String amount = request.getParameter("payhere_amount");
        String currency = request.getParameter("payhere_currency");
        String status = request.getParameter("status_code");
        String orderType = request.getParameter("custom_1");
        int custID = Integer.parseInt(request.getParameter("custom_2"));
        String md5sig = request.getParameter("md5sig");

        String localMD5sig = HashGeneratorUtils.generateMD5(merchID + orderID + amount + currency + status + HashGeneratorUtils.generateMD5(((ApplicationSetting) s.load(ApplicationSetting.class, "merchant_secret")).getValue()));

        System.out.println("merch : " + merchID);
        System.out.println("order_id : " + orderID);
        System.out.println("payhere_amount : " + amount);
        System.out.println("status_code : " + status);
        System.out.println("orderType : " + orderType);
        System.out.println("custID : " + custID);
        System.out.println("status_message : " + request.getParameter("status_message"));
        System.out.println("md5sig : " + md5sig);

        System.out.println("local md5sig : " + localMD5sig);

        int invID = Integer.parseInt(request.getParameter("order_id"));
        int statusCode = Integer.parseInt(request.getParameter("status_code"));

        if (md5sig.equals(localMD5sig) && statusCode == 2) {
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
        }
        s.close();
    }

}
