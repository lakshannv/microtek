package controller;

import hibernate.Cart;
import hibernate.City;
import hibernate.Customer;
import hibernate.District;
import hibernate.HiberUtil;
import hibernate.Invoice;
import hibernate.InvoiceItem;
import hibernate.ShippingAddress;
import hibernate.Stock;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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

@WebServlet(name = "AppCheckOrder", urlPatterns = {"/AppCheckOrder"})
public class AppCheckOrder extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        int custID = Integer.parseInt(request.getParameter("custID"));

        Customer cust = (Customer) s.load(Customer.class, custID);

        boolean isValid = false;
        boolean isModified = false;
        ShippingAddress addr = null;
        ShippingAddress billingAddr = null;
        District dist = null;
        District billingDist = null;
        try {
            String oID = request.getParameter("orderID");
            if (oID == null) {
                String stk = request.getParameter("stockID");
                if (stk == null) {
                    Criteria cartCR = s.createCriteria(Cart.class);
                    cartCR.add(Restrictions.eq("customer", cust));
                    List<Cart> cartItemList = cartCR.list();
                    for (Cart crt : cartItemList) {
                        if (crt.getStock().getProduct().getCategory().getActive() == 1 && crt.getStock().getProduct().getBrand().getActive() == 1 && crt.getStock().getActive() == 1 && crt.getStock().getQty() >= crt.getQty()) {
                            isValid = true;
                            break;
                        }
                    }
                } else {
                    int stockID = Integer.parseInt(stk);
                    int qty = Integer.parseInt(request.getParameter("qty"));
                    Stock st = (Stock) s.load(Stock.class, stockID);
                    if (st.getProduct().getCategory().getActive() == 1 && st.getProduct().getBrand().getActive() == 1 && st.getActive() == 1 && st.getQty() >= qty) {
                        isValid = true;
                    }
                }

                int addrID = Integer.parseInt(request.getParameter("addrID"));
                int billingAddrID = Integer.parseInt(request.getParameter("billingAddrID"));
                addr = (ShippingAddress) s.load(ShippingAddress.class, addrID);
                if (billingAddrID != 0) {
                    billingAddr = (ShippingAddress) s.load(ShippingAddress.class, billingAddrID);
                }
            } else {
                int invID = Integer.parseInt(oID);
                Invoice inv = (Invoice) s.load(Invoice.class, invID);
                Set<InvoiceItem> invItemSet = inv.getInvoiceItems();
                for (Iterator<InvoiceItem> iterator = invItemSet.iterator(); iterator.hasNext();) {
                    InvoiceItem invItem = iterator.next();
                    Stock stk = invItem.getStock();
                    if (stk.getProduct().getCategory().getActive() == 1 && stk.getProduct().getBrand().getActive() == 1 && stk.getActive() == 1 && stk.getQty() >= invItem.getQty()) {
                        if (!isValid) {
                            isValid = true;
                        }
                    } else {
                        s.delete(invItem);
                        if (!isModified) {
                            isModified = true;
                        }
                    }
                }
                if (inv.getBillingCityId() != 0) {
                    billingDist = ((City) s.load(City.class, inv.getBillingCityId())).getDistrict();
                }
                dist = inv.getCity().getDistrict();
            }
            if (isModified) {
                s.beginTransaction().commit();
                response.getWriter().write("mod");
            }
            if (isValid) {
                if (addr != null) {
                    dist = addr.getDistrict();
                }
                if (dist.getActive() == 1) {
                    if (billingAddr != null) {
                        billingDist = billingAddr.getDistrict();
                    }
                    if (billingDist == null) {
                        response.getWriter().write("ok");
                    } else {
                        if (billingDist.getActive() == 1) {
                            response.getWriter().write("ok");
                        } else {
                            response.getWriter().write("del" + billingDist.getName());
                        }
                    }
                } else {
                    response.getWriter().write("del" + dist.getName());
                }
            } else {
                response.getWriter().write("inv");
            }
        } catch (Exception e) {
            response.getWriter().write("err");
        }

        s.close();
    }

}
