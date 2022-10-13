package controller;

import hibernate.HiberUtil;
import hibernate.Invoice;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Email;
import model.Utils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "UpdateOrderStatus", urlPatterns = {"/UpdateOrderStatus"})
public class UpdateOrderStatus extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        try {
            int invID = Integer.parseInt(request.getParameter("invID"));
            byte odrStat = Byte.parseByte(request.getParameter("odrStat"));
            Invoice inv = (Invoice) s.load(Invoice.class, invID);
            inv.setOrderStatus(odrStat);
            inv.setLastChangedOn(new Date());
            s.save(inv);
            s.beginTransaction().commit();
            String subject = "";
            String content = "";
            switch (odrStat) {
                case 0:
                    subject = "Order Payment is pending !";
                    content = "<h1 style='color: red;'>The payment for your order (Order ID - " + inv.getId() + ") is pending...</h1><h4>Your order won't be received until you complete the payment.<br>You can review your order by clicking <a href=\"http://localhost:8080/MicroTek/order.jsp?orderID=" + inv.getId() + "\">here</a></h4>";
                    break;
                case 1:
                    subject = "Order Received !";
                    content = "<h1 style='color: red;'>Your order (Order ID - " + inv.getId() + ") is Received...</h1><h4>We'll notify as soon as your order gets dispatched.<br>You can review your order by clicking <a href=\"http://localhost:8080/MicroTek/order.jsp?orderID=" + inv.getId() + "\">here</a></h4>";
                    break;
                case 2:
                    subject = "Order Dispatched !";
                    content = "<h1 style='color: red;'>Your order (Order ID - " + inv.getId() + ") is Dispatched...</h1><h4>Your order is on it's way to you.<br>You can review your order by clicking <a href=\"http://localhost:8080/MicroTek/order.jsp?orderID=" + inv.getId() + "\">here</a></h4>";
                    break;
                case 3:
                    subject = "Order Completed !";
                    content = "<h1 style='color: red;'>Your order (Order ID - " + inv.getId() + ") is Completed...</h1><h4>Don't forget to give your feedback on the products you bought.<br>You can review your order and its products by clicking <a href=\"http://localhost:8080/MicroTek/order.jsp?orderID=" + inv.getId() + "\">here</a></h4>";
                    break;
            }

            final String subjectf = subject;
            final String contentf = content;
            String email = inv.getCustomer().getEmail();
            new Thread() {
                @Override
                public void run() {
                    try {
                        Email.send(email, "MicroTek - " + subjectf, contentf);
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();

            subject = "";
            content = "";
            switch (odrStat) {
                case 0:
                    subject = "Order Payment is pending !";
                    content = "The payment for your order (Order ID - " + inv.getId() + ") is pending. Your order won't be received until you complete the payment.";
                    break;
                case 1:
                    subject = "Order Received !";
                    content = "Your order (Order ID - " + inv.getId() + ") is Received. We'll notify you as soon as your order gets dispatched.";
                    break;
                case 2:
                    subject = "Order Dispatched !";
                    content = "Your order (Order ID - " + inv.getId() + ") is Dispatched. Your order is on it's way to you.";
                    break;
                case 3:
                    subject = "Order Completed !";
                    content = "Your order (Order ID - " + inv.getId() + ") is Completed. Don't forget to give your feedback on the products you bought.";
                    break;
            }
            Utils.sendFCMNotification(inv.getCustomer().getFcmToken(), subject, content, null);

            response.getWriter().write("ok");
        } catch (Exception e) {
            response.getWriter().write("err");
        }
        s.close();
    }
}
