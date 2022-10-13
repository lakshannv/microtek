package controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hibernate.City;
import hibernate.Customer;
import hibernate.District;
import hibernate.HiberUtil;
import hibernate.Province;
import hibernate.ShippingAddress;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.list.TreeList;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppAddNewAddress", urlPatterns = {"/AppAddNewAddress"})
public class AppAddNewAddress extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String addr = request.getParameter("addr");
        int custID = Integer.parseInt(request.getParameter("custID"));
        int provinceID = Integer.parseInt(request.getParameter("provinceID"));
        int districtID = Integer.parseInt(request.getParameter("districtID"));
        int cityID = Integer.parseInt(request.getParameter("cityID"));

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        Customer cust = (Customer) s.load(Customer.class, custID);

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
        custSet.add(cust);

        ShippingAddress shipAddr = new ShippingAddress(cty, dist, prv, addr, custSet);
        s.save(shipAddr);
        s.beginTransaction().commit();

        HashMap<String, Object> responeDataMap = new HashMap();
        TreeMap<String, String> addressMap = new TreeMap();
        TreeList addressList = new TreeList();

        Set<ShippingAddress> addrSet = cust.getShippingAddresses();
        for (Iterator<ShippingAddress> it = addrSet.iterator(); it.hasNext();) {
            ShippingAddress address = it.next();

            addressMap.put(address.getName() + ", " + address.getCity().getName() + ", " + address.getDistrict().getName(), String.valueOf(address.getId()));

        }

        addressList.addAll(addressMap.keySet());

        responeDataMap.put("addressMap", addressMap);
        responeDataMap.put("addressList", addressList);
        response.getWriter().write(new Gson().toJson(responeDataMap));
        s.close();

    }
}
