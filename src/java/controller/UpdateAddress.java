package controller;

import hibernate.City;
import hibernate.Customer;
import hibernate.District;
import hibernate.HiberUtil;
import hibernate.Province;
import hibernate.ShippingAddress;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "UpdateAddress", urlPatterns = {"/UpdateAddress"})
public class UpdateAddress extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int addrID = Integer.parseInt(request.getParameter("addrID"));
            String addr = request.getParameter("addr");
            if (addr.isEmpty()) {
                response.getWriter().write("inv");
            } else {
                int provinceID = Integer.parseInt(request.getParameter("provinceID"));
                int districtID = Integer.parseInt(request.getParameter("districtID"));
                int cityID = Integer.parseInt(request.getParameter("cityID"));

                SessionFactory sf = HiberUtil.getSessionFactory();
                Session s = sf.openSession();

                Criteria provinceCR = s.createCriteria(Province.class);
                provinceCR.add(Restrictions.eq("id", provinceID));
                Province prv = (Province) provinceCR.uniqueResult();

                Criteria distCR = s.createCriteria(District.class);
                distCR.add(Restrictions.eq("id", districtID));
                District dist = (District) distCR.uniqueResult();

                Criteria cityCR = s.createCriteria(City.class);
                cityCR.add(Restrictions.eq("id", cityID));
                City cty = (City) cityCR.uniqueResult();

                Set<Customer> custSet = new LinkedHashSet();
                custSet.add((Customer) request.getSession().getAttribute("cust"));

                ShippingAddress shipAddr = (ShippingAddress) s.load(ShippingAddress.class, addrID);
                shipAddr.setName(addr);
                shipAddr.setCity(cty);
                shipAddr.setDistrict(dist);
                shipAddr.setProvince(prv);
                s.update(shipAddr);
                s.beginTransaction().commit();
                s.close();
                response.getWriter().write("ok");
            }
        } catch (Exception e) {
            response.getWriter().write("err");
        }
    }
}
