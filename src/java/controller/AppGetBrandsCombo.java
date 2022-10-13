package controller;

import com.google.gson.Gson;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.Stock;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author laksh
 */
@WebServlet(name = "AppGetBrandsCombo", urlPatterns = {"/AppGetBrandsCombo"})
public class AppGetBrandsCombo extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Session s = HiberUtil.getSessionFactory().openSession();
        HashMap<String, Object> responseData = new HashMap();
        
        TreeSet<String> brandSet = new TreeSet();
        LinkedList<String> brandList = new LinkedList();
        brandList.add("Any");

        double minPrice = 0;
        double maxPrice = 0;

        String catName = request.getParameter("catName");
        Criteria prodCR = s.createCriteria(Product.class);
        if (!catName.equalsIgnoreCase("Any")) {
            Criteria catCR = s.createCriteria(Category.class);
            catCR.add(Restrictions.eq("name", catName));
            Category c = (Category) catCR.uniqueResult();
            prodCR.add(Restrictions.eq("category", c));
        }
        for (Product p : (List<Product>) prodCR.list()) {
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

            if (p.getCategory().getActive() == 1 && p.getBrand().getActive() == 1 && stk != null) {
                brandSet.add(p.getBrand().getName());
                double price = stk.getSellingPrice() * ((100 - stk.getDiscount()) / 100);
                if (minPrice == 0) {
                    minPrice = price;
                }
                if (price < minPrice) {
                    minPrice = price;
                }
                if (price > maxPrice) {
                    maxPrice = price;
                }
            }
        }

        brandList.addAll(brandSet);
        
        responseData.put("maxPrice", String.valueOf(maxPrice));
        responseData.put("minPrice", String.valueOf(minPrice));
        responseData.put("brandList", brandList);
        response.getWriter().write(new Gson().toJson(responseData));
        s.close();
    }

}
