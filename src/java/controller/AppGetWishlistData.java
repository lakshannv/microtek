package controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hibernate.Cart;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.ProductImage;
import hibernate.Stock;
import hibernate.Wishlist;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppGetWishlistData", urlPatterns = {"/AppGetWishlistData"})
public class AppGetWishlistData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int custID = Integer.parseInt(request.getParameter("custID"));

        LinkedList<LinkedTreeMap<String, Object>> wishList = new LinkedList();

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        Customer c = (Customer) s.load(Customer.class, custID);
        Criteria wishCR = s.createCriteria(Wishlist.class);
        wishCR.add(Restrictions.eq("customer", c));
        wishCR.addOrder(Order.desc("id"));
        List<Wishlist> wishItems = wishCR.list();

        for (Wishlist w : wishItems) {
            Product p = w.getProduct();

            LinkedTreeMap<String, Object> item = new LinkedTreeMap();

            ArrayList<String> imgList = new ArrayList();
            for (Iterator iterator = p.getProductImages().iterator(); iterator.hasNext();) {
                ProductImage pi = (ProductImage) iterator.next();
                imgList.add(pi.getId().getImage());
            }
            Collections.sort(imgList);
            if (p.getCategory().getActive() == 1 && p.getBrand().getActive() == 1) {
                Stock stk = null;
                for (Iterator iterator = p.getStocks().iterator(); iterator.hasNext();) {
                    Stock st = (Stock) iterator.next();
                    if (st.getActive() == 1) {
                        if (stk == null) {
                            stk = st;
                        } else if ((st.getSellingPrice() * (100 - st.getDiscount()) / 100) < (stk.getSellingPrice() * (100 - stk.getDiscount()) / 100)) {
                            stk = st;
                        }
                    }
                }
                item.put("id", String.valueOf(w.getId()));
                item.put("productID", String.valueOf(p.getId()));
                item.put("title", p.getBrand().getName() + " " + p.getName());
                item.put("image", Validation.getNextProductImage(imgList, 1));
                if (stk == null) {
                    item.put("hasStock", false);
                } else {
                    item.put("hasStock", true);
                    item.put("stockID", String.valueOf(stk.getId()));
                    item.put("stocksLeft", String.valueOf(stk.getQty()));
                    item.put("price", stk.getSellingPrice());
                    item.put("discount", stk.getDiscount());
                    item.put("discountedPrice", (stk.getSellingPrice() * (100 - stk.getDiscount()) / 100));
                }
            }
            wishList.add(item);
        }

        response.getWriter().write(new Gson().toJson(wishList));
        s.close();
    }

}
