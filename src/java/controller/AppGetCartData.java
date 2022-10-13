package controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hibernate.Cart;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.ProductImage;
import hibernate.Stock;
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

@WebServlet(name = "AppGetCartData", urlPatterns = {"/AppGetCartData"})
public class AppGetCartData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int custID = Integer.parseInt(request.getParameter("custID"));

        HashMap<String, Object> cartDataMap = new HashMap();
        LinkedList<LinkedTreeMap<String, Object>> cartList = new LinkedList();

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        Customer c = (Customer) s.load(Customer.class, custID);

        Criteria cartCR = s.createCriteria(Cart.class);
        cartCR.add(Restrictions.eq("customer", c));
        cartCR.addOrder(Order.asc("id"));
        List<Cart> cartItemList = cartItemList = cartCR.list();

        LinkedList<Cart> refinedCartItemList = new LinkedList();

        for (Cart crt : cartItemList) {
            Stock stk = (Stock) s.load(Stock.class, crt.getStock().getId());
            if (stk.getProduct().getCategory().getActive() == 1 && stk.getProduct().getBrand().getActive() == 1 && stk.getActive() == 1 && stk.getQty() > 0) {
                refinedCartItemList.add(crt);
            }
        }
        double tot = 0;

        for (Cart crt : cartItemList) {

            LinkedTreeMap<String, Object> item = new LinkedTreeMap();
            item.put("id", String.valueOf(crt.getId()));
            item.put("productID", String.valueOf(crt.getStock().getId()));
            item.put("title", crt.getStock().getProduct().getBrand().getName() + " " +  crt.getStock().getProduct().getName());
            item.put("stocksLeft", String.valueOf(crt.getStock().getQty()));
            item.put("selected", String.valueOf(crt.getQty()));
            item.put("price", crt.getStock().getSellingPrice());
            item.put("discount", crt.getStock().getDiscount());
            item.put("discountedPrice", (crt.getStock().getSellingPrice() * (100 - crt.getStock().getDiscount()) / 100));

            ArrayList<String> imgList = new ArrayList();
            for (Iterator iterator = crt.getStock().getProduct().getProductImages().iterator(); iterator.hasNext();) {
                ProductImage pi = (ProductImage) iterator.next();
                imgList.add(pi.getId().getImage());
            }
            Collections.sort(imgList);
            item.put("image", Validation.getNextProductImage(imgList, 1));
            double sellprice = crt.getStock().getSellingPrice() * (100 - crt.getStock().getDiscount()) / 100;
            if (refinedCartItemList.contains(crt)) {
                if (crt.getQty() > crt.getStock().getQty()) {
                    crt.setQty(crt.getStock().getQty());
                    s.update(crt);
                    s.beginTransaction().commit();
                }
                tot += crt.getQty() * sellprice;
            } else {
                if (crt.getStock().getProduct().getCategory().getActive() == 1 && crt.getStock().getProduct().getBrand().getActive() == 1 && crt.getStock().getActive() == 1 && crt.getStock().getQty() == 0) {
                    item.put("unavialableDueto", "Out of Stock");
                } else {
                    item.put("unavialableDueto", "Not Available Any More");
                }
            }
            cartList.add(item);
        }
        
        
        cartDataMap.put("tot", tot);
        cartDataMap.put("cartCount", cartItemList.size());
        

        cartDataMap.put("cartList", cartList);
        response.getWriter().write(new Gson().toJson(cartDataMap));
        s.close();
    }

}
