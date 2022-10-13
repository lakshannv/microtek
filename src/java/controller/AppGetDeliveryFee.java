package controller;

import hibernate.HiberUtil;
import hibernate.ShippingAddress;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;

@WebServlet(name = "AppGetDeliveryFee", urlPatterns = {"/AppGetDeliveryFee"})
public class AppGetDeliveryFee extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int addrID = Integer.parseInt(request.getParameter("addrID"));
        Session s = HiberUtil.getSessionFactory().openSession();
        ShippingAddress addr = (ShippingAddress) s.load(ShippingAddress.class, addrID);
        response.getWriter().write(String.valueOf(addr.getDistrict().getDeliveryFee()));
        s.close();
    }

}
