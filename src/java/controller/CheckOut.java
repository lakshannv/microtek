package controller;

import com.google.gson.Gson;
import hibernate.ApplicationSetting;
import hibernate.Cart;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import hibernate.InvoiceItemId;
import hibernate.ShippingAddress;
import hibernate.Stock;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.OrderStatus;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "CheckOut", urlPatterns = {"/CheckOut"})
public class CheckOut extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Customer cust = (Customer) request.getSession().getAttribute("cust");
        HashMap<String, Object> hm = new HashMap();
        hm.put("sandbox", true);
        hm.put("merchant_id", ((ApplicationSetting) s.load(ApplicationSetting.class, "merchant_id")).getValue());
        hm.put("notify_url", "https://c82ce3aa64d1.ngrok.io/MicroTek/ConfirmPayment");
        hm.put("return_url", "https://google.com");
        hm.put("cancel_url", "https://google.com");
        hm.put("currency", "LKR");
        hm.put("country", "Sri Lanka");
        hm.put("first_name", cust.getFname());
        hm.put("last_name", cust.getLname());
        hm.put("email", cust.getEmail());
        hm.put("phone", cust.getMobile());
        hm.put("custom_2", String.valueOf(cust.getId()));

        String oID = request.getParameter("orderID");
        String itemName = "Bulk Order";
        if (oID == null) {
            double delFee = 0;
            String addrString = request.getParameter("addrID");
            int addrID = Integer.parseInt(addrString);
            int billingAddrID = Integer.parseInt(request.getParameter("billingAddrID"));
            Criteria addrCR = s.createCriteria(ShippingAddress.class);
            addrCR.add(Restrictions.eq("id", addrID));
            ShippingAddress addr = (ShippingAddress) addrCR.uniqueResult();
            if (request.getParameter("delMethod") != null) {
                if (request.getParameter("delMethod").equals("exp")) {
                    delFee = addr.getDistrict().getDeliveryFee();
                }
            }

            double tot = 0;
            String stk = request.getParameter("stockID");
            String deliveryMethod = request.getParameter("delMethod");
            byte delMethod = 0;
            if (deliveryMethod != null) {
                if (deliveryMethod.equals("exp")) {
                    delMethod = 1;
                }
            }

            Set<InvoiceItem> invItemSet = new HashSet();
            if (stk == null) {
                Criteria cartCR = s.createCriteria(Cart.class);
                cartCR.add(Restrictions.eq("customer", cust));
                cartCR.addOrder(Order.asc("id"));
                List<Cart> cartItemList = cartCR.list();
                for (Cart crt : cartItemList) {
                    if (crt.getStock().getProduct().getCategory().getActive() == 1 && crt.getStock().getProduct().getBrand().getActive() == 1 && crt.getStock().getActive() == 1 && crt.getStock().getQty() >= crt.getQty()) {
                        int qty = crt.getQty();
                        double itemTot = qty * crt.getStock().getSellingPrice() * (100 - crt.getStock().getDiscount()) / 100;
                        tot += itemTot;

                        InvoiceItem invItem = new InvoiceItem();
                        invItem.setStock(crt.getStock());
                        invItem.setQty(qty);
                        invItem.setDiscount(crt.getStock().getDiscount());
                        invItemSet.add(invItem);
                    }
                }
            } else {
                int stockID = Integer.parseInt(stk);
                Stock st = (Stock) s.load(Stock.class, stockID);
                int qty = Integer.parseInt(request.getParameter("qty"));
                tot = qty * st.getSellingPrice() * (100 - st.getDiscount()) / 100;

                InvoiceItem invItem = new InvoiceItem();
                invItem.setStock(st);
                invItem.setQty(qty);
                invItem.setDiscount(st.getDiscount());
                invItemSet.add(invItem);

                itemName = st.getProduct().getBrand().getName() + " " + st.getProduct().getName();
            }
            Date d = new Date();
            String billingAddr = null;
            int billingCityID = 0;
            if (billingAddrID != 0) {
                ShippingAddress ba = (ShippingAddress) s.load(ShippingAddress.class, billingAddrID);
                billingAddr = ba.getName();
                billingCityID = ba.getCity().getId();
            }
            Invoice inv = new Invoice(addr.getCity(), cust, delMethod, delFee, addr.getName(), billingAddr, billingCityID, d, d, OrderStatus.PAYMENT_PENDING, null);
            s.save(inv);
            for (Iterator<InvoiceItem> iterator = invItemSet.iterator(); iterator.hasNext();) {
                InvoiceItem invItem = iterator.next();
                invItem.setId(new InvoiceItemId(inv.getId(), invItem.getStock().getId()));
                s.save(invItem);
            }

            hm.put("order_id", String.valueOf(inv.getId()));
            hm.put("items", itemName);
            hm.put("amount", String.valueOf(tot + delFee));
            hm.put("address", addr.getName());
            hm.put("city", addr.getCity().getName());
        } else {
            int invID = Integer.parseInt(oID);
            Invoice inv = (Invoice) s.load(Invoice.class, invID);
            double tot = 0;
            Set<InvoiceItem> invItemSet = inv.getInvoiceItems();
            for (Iterator<InvoiceItem> iterator = invItemSet.iterator(); iterator.hasNext();) {
                InvoiceItem invItem = iterator.next();
                if (invItemSet.size() == 1) {
                    itemName = invItem.getStock().getProduct().getBrand().getName() + " " + invItem.getStock().getProduct().getName();
                }
                tot = tot + (invItem.getQty() * invItem.getStock().getSellingPrice()) * (100 - invItem.getStock().getDiscount()) / 100;
                invItem.setDiscount(invItem.getStock().getDiscount());
                s.update(invItem);
            }
            double delFee = 0;
            if (inv.getDeliveryMethod() == (byte) 1) {
                delFee = inv.getCity().getDistrict().getDeliveryFee();
                inv.setDelFee(delFee);
                s.update(inv);
            }

            hm.put("order_id", oID);
            hm.put("items", itemName);
            hm.put("amount", String.valueOf(tot + delFee));
            hm.put("address", inv.getAddress());
            hm.put("city", inv.getCity().getName());

        }
        if (itemName.equals("Bulk Order")) {
            hm.put("custom_1", "cart");
        } else {
            hm.put("custom_1", "item");
        }

        Gson g = new Gson();
        response.getWriter().write(g.toJson(hm));
        s.beginTransaction().commit();
        s.close();
    }

}
