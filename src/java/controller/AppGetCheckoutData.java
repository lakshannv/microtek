package controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hibernate.ApplicationSetting;
import hibernate.Cart;
import hibernate.City;
import hibernate.Customer;
import hibernate.District;
import hibernate.HiberUtil;
import hibernate.Province;
import hibernate.ShippingAddress;
import hibernate.Stock;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.ServletContext;
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

@WebServlet(name = "AppGetCheckoutData", urlPatterns = {"/AppGetCheckoutData"})
public class AppGetCheckoutData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Gson g = new Gson();
        DecimalFormat df = new DecimalFormat("#,##0.##");

        HashMap<String, Object> responeDataMap = new HashMap();
        TreeMap<String, String> addressMap = new TreeMap();
        TreeList addressList = new TreeList();
        LinkedList<LinkedList<String>> orderitemList = new LinkedList();

        LinkedList<Map<String, String>> addressData = new LinkedList();
        LinkedTreeMap<String, String> provinceMap = new LinkedTreeMap();
        TreeMap<String, String> districtMap = new TreeMap();
        TreeMap<String, String> cityMap = new TreeMap();

        String stockID = request.getParameter("stockID");
        int custID = Integer.parseInt(request.getParameter("custID"));

        Customer cust = (Customer) s.load(Customer.class, custID);

        String freeDelWithin = ((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_within")).getValue();
        String freeDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_time_unit")).getValue();
        String expDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_time_unit")).getValue();
        String expDelWithin = ((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_within")).getValue();

        boolean isCart = false;
        if (stockID == null) {
            isCart = true;
        }

        Set<ShippingAddress> addrSet = cust.getShippingAddresses();
        for (Iterator<ShippingAddress> it = addrSet.iterator(); it.hasNext();) {
            ShippingAddress addr = it.next();

            addressMap.put(addr.getName() + ", " + addr.getCity().getName() + ", " + addr.getDistrict().getName(), String.valueOf(addr.getId()));

        }

        addressList.addAll(addressMap.keySet());

        int addrID = Integer.parseInt(addressMap.get(addressList.get(0)));
        ShippingAddress addr = (ShippingAddress) s.load(ShippingAddress.class, addrID);
        double delFee = addr.getDistrict().getDeliveryFee();
        double tot = 0;

        if (isCart) {
            Criteria cartCR = s.createCriteria(Cart.class);
            cartCR.add(Restrictions.eq("customer", cust));
            cartCR.addOrder(Order.asc("id"));
            List<Cart> cartItemList = cartCR.list();

            for (Cart crt : cartItemList) {
                if (crt.getStock().getProduct().getCategory().getActive() == 1 && crt.getStock().getProduct().getBrand().getActive() == 1 && crt.getStock().getActive() == 1 && crt.getStock().getQty() >= crt.getQty()) {
                    int qty = crt.getQty();
                    double itemTot = qty * crt.getStock().getSellingPrice() * (100 - crt.getStock().getDiscount()) / 100;
                    tot += itemTot;
                    String discount = "";
                    if (crt.getStock().getDiscount() != 0) {
                        discount = "( " + df.format(crt.getStock().getDiscount()) + " % off ) ";
                    }

                    LinkedList<String> orderItem = new LinkedList();
                    orderItem.add(crt.getStock().getProduct().getBrand().getName() + " " + crt.getStock().getProduct().getName() + " x " + qty);
                    orderItem.add(discount + df.format(itemTot));
                    orderitemList.add(orderItem);
                }
            }

        } else {
            Stock stk = (Stock) s.load(Stock.class, Integer.parseInt(stockID));
            int qty = Integer.parseInt(request.getParameter("qty"));
            double itemTot = qty * stk.getSellingPrice() * (100 - stk.getDiscount()) / 100;
            tot += itemTot;
            String discount = "";
            if (stk.getDiscount() != 0) {
                discount = "( " + df.format(stk.getDiscount()) + " % off ) ";
            }

            LinkedList<String> orderItem = new LinkedList();
            orderItem.add(stk.getProduct().getBrand().getName() + " " + stk.getProduct().getName() + " x " + qty);
            orderItem.add(discount + df.format(itemTot));
            orderitemList.add(orderItem);
        }

        Criteria cr = s.createCriteria(Province.class);
        cr.addOrder(Order.asc("id"));
        List<Province> proviList = cr.list();
        for (Province p : proviList) {
            provinceMap.put(p.getName(), String.valueOf(p.getId()));
        }
                
        cr = s.createCriteria(District.class);
        cr.add(Restrictions.eq("province", proviList.get(0)));
        cr.addOrder(Order.asc("name"));
        List<District> distList = cr.list();
        for (District d : distList) {
            districtMap.put(d.getName(), String.valueOf(d.getId()));
        }
        
        cr = s.createCriteria(City.class);
        cr.add(Restrictions.eq("district", distList.get(0)));
        cr.addOrder(Order.asc("name"));
        List<City> ctyList = cr.list();
        for (City d : ctyList) {
            cityMap.put(d.getName(), String.valueOf(d.getId()));
        }
        addressData.add(provinceMap);
        addressData.add(districtMap);
        addressData.add(cityMap);
        responeDataMap.put("addressData", addressData);

        responeDataMap.put("subTot", tot);
        responeDataMap.put("delFee", delFee);

        responeDataMap.put("freeDelWithin", freeDelWithin);
        responeDataMap.put("freeDelTimeUnit", freeDelTimeUnit);
        responeDataMap.put("expDelWithin", expDelWithin);
        responeDataMap.put("expDelTimeUnit", expDelTimeUnit);

        responeDataMap.put("addressMap", addressMap);
        responeDataMap.put("addressList", addressList);
        responeDataMap.put("orderitemList", orderitemList);
        response.getWriter().write(g.toJson(responeDataMap));

        s.close();
    }

}
