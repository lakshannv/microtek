package controller;

import com.google.gson.Gson;
import hibernate.ApplicationSetting;
import hibernate.City;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import hibernate.ProductImage;
import hibernate.ProductReview;
import hibernate.Stock;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.OrderStatus;
import model.Validation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "AppGetOrderData", urlPatterns = {"/AppGetOrderData"})
public class AppGetOrderData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Gson g = new Gson();
        DecimalFormat df = new DecimalFormat("#,##0.##");

        HashMap<String, Object> responeDataMap = new HashMap();
        LinkedList<HashMap<String, Object>> orderitemList = new LinkedList();

        int invID = Integer.parseInt(request.getParameter("orderID"));
        Invoice inv = (Invoice) s.load(Invoice.class, invID);
        Customer cust = inv.getCustomer();

        int freeDelWithin = Integer.parseInt(((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_within")).getValue());
        String freeDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "free_del_time_unit")).getValue();
        String expDelTimeUnit = ((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_time_unit")).getValue();
        int expDelWithin = Integer.parseInt(((ApplicationSetting) s.load(ApplicationSetting.class, "exp_del_within")).getValue());

        responeDataMap.put("orderStatus", String.valueOf(inv.getOrderStatus()));
        responeDataMap.put("shipAddr", inv.getAddress() + ", " + inv.getCity().getName() + ", " + inv.getCity().getDistrict().getName() + ", " + inv.getCity().getDistrict().getProvince().getName() + " Province, Sri Lanka.");
        responeDataMap.put("shipAddr", inv.getAddress() + ", " + inv.getCity().getName() + ", " + inv.getCity().getDistrict().getName() + ", " + inv.getCity().getDistrict().getProvince().getName() + " Province, Sri Lanka.");
        if (inv.getBillingCityId() == 0) {
            responeDataMap.put("billAddr", "Same As Shipping Address");
        } else {
            City billCity = (City) s.load(City.class, inv.getBillingCityId());
            responeDataMap.put("billAddr", inv.getBillingAddress() + ", " + billCity.getName() + ", " + billCity.getDistrict().getName() + ", " + billCity.getDistrict().getProvince().getName() + " Province, Sri Lanka.");
        }

        SimpleDateFormat estSDF = new SimpleDateFormat("MMMM yyyy");
        String estDate = "";
        Calendar c = Calendar.getInstance();
        c.setTime(inv.getCreatedOn());
        if (inv.getDeliveryMethod() == (byte) 0) {
            c.add(Calendar.DATE, Validation.getFulfilmentWithin(freeDelWithin, freeDelTimeUnit));
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            estDate = "Before " + dayOfMonth + Validation.getNthSmall(dayOfMonth) + " " + estSDF.format(c.getTime());
            responeDataMap.put("delMethod", "Free Delivery");
        } else {
            c.add(Calendar.DATE, Validation.getFulfilmentWithin(expDelWithin, expDelTimeUnit));
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            estDate = "Before " + dayOfMonth + Validation.getNthSmall(dayOfMonth) + " " + estSDF.format(c.getTime());
            responeDataMap.put("delMethod", "Expedited Delivery");

        }
        if (inv.getOrderStatus() == OrderStatus.PAYMENT_PENDING) {
            estDate = "N / A";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy hh:mm a");
        c.setTime(inv.getCreatedOn());
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        responeDataMap.put("createdOn", dayOfMonth + Validation.getNthSmall(dayOfMonth) + " " + sdf.format(c.getTime()));

        double tot = 0;
        for (Iterator<InvoiceItem> iterator = inv.getInvoiceItems().iterator(); iterator.hasNext();) {
            InvoiceItem invItem = iterator.next();
            double itemTot = 0;
            String discount = "";
            if (inv.getOrderStatus() == OrderStatus.PAYMENT_PENDING) {
                itemTot = invItem.getQty() * invItem.getStock().getSellingPrice() * (100 - invItem.getStock().getDiscount()) / 100;
                if (invItem.getStock().getDiscount() != 0) {
                    discount = "( " + df.format(invItem.getStock().getDiscount()) + " % off ) ";
                }
            } else {
                itemTot = invItem.getQty() * invItem.getStock().getSellingPrice() * (100 - invItem.getDiscount()) / 100;
                if (invItem.getDiscount() != 0) {
                    discount = "( " + df.format(invItem.getDiscount()) + " % off ) ";
                }
            }
            tot += itemTot;
            ProductReview existingPR = null;
            for (Iterator<ProductReview> it = invItem.getStock().getProduct().getProductReviews().iterator(); it.hasNext();) {
                ProductReview pr = it.next();
                if (pr.getCustomer().getId() == cust.getId()) {
                    existingPR = pr;
                    break;
                }
            }

            Stock stk = invItem.getStock();
            ArrayList<String> imgList = new ArrayList();
            for (Iterator iterator2 = stk.getProduct().getProductImages().iterator(); iterator2.hasNext();) {
                ProductImage pi = (ProductImage) iterator2.next();
                imgList.add(pi.getId().getImage());
            }
            Collections.sort(imgList);

            HashMap<String, Object> orderItem = new HashMap();
            orderItem.put("stockID", String.valueOf(stk.getId()));
            orderItem.put("image", Validation.getNextProductImage(imgList, 1));
            orderItem.put("title", stk.getProduct().getBrand().getName() + " " + stk.getProduct().getName());
            orderItem.put("itemTot", invItem.getQty() + " item(s) - " + discount + df.format(itemTot));
            orderItem.put("hasReview", existingPR != null);
            orderitemList.add(orderItem);

        }
        responeDataMap.put("estDate", estDate);
        responeDataMap.put("subtot", tot);
        responeDataMap.put("delFee", inv.getDelFee());
        responeDataMap.put("orderItemList", orderitemList);
        responeDataMap.put("custID", String.valueOf(cust.getId()));
        responeDataMap.put("custName", cust.getFname() + " " + cust.getLname());
        responeDataMap.put("custMoile", cust.getMobile());
        responeDataMap.put("custEmail", cust.getEmail());
        responeDataMap.put("custFCMToken", cust.getFcmToken());
        response.getWriter().write(g.toJson(responeDataMap));
        s.close();
    }

}
