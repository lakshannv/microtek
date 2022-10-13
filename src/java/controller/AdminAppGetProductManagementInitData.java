package controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hibernate.Brand;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Product;
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

/**
 *
 * @author laksh
 */
@WebServlet(name = "AdminAppGetProductManagementInitData", urlPatterns = {"/AdminAppGetProductManagementInitData"})
public class AdminAppGetProductManagementInitData extends HttpServlet {
    
    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        Gson g = new Gson();

        LinkedList<String> catList = new LinkedList();
        catList.add("Any");
        Criteria crCategory = s.createCriteria(Category.class);
        crCategory.addOrder(Order.asc("name"));
        List<Category> l = (List<Category>) crCategory.list();
        for (Category c : l) {
            catList.add(String.valueOf(c.getName()));
        }

        LinkedList<String> brandList = new LinkedList();
        brandList.add("Any");
        Criteria crBrand = s.createCriteria(Brand.class);
        crBrand.addOrder(Order.asc("name"));
        List<Brand> lb = (List<Brand>) crBrand.list();
        for (Brand c : lb) {
            brandList.add(String.valueOf(c.getName()));
        }

        responseData.put("catList", catList);
        responseData.put("brandList", brandList);
        
        
        String catName = request.getParameter("catName");
        String brandName = request.getParameter("brandName");


        LinkedList<LinkedTreeMap<String, Object>> stkList = new LinkedList();

        String defaultComboValue = "Any";
        Criteria productCR = s.createCriteria(Product.class);
        if (catName != null) {
            if (!catName.equalsIgnoreCase(defaultComboValue)) {
                Criteria catCR = s.createCriteria(Category.class);
                catCR.add(Restrictions.eq("name", catName));
                Category c = (Category) catCR.uniqueResult();
                productCR.add(Restrictions.eq("category", c));
            }
        }
        if (brandName != null) {
            if (!brandName.equalsIgnoreCase(defaultComboValue)) {
                Criteria brandCR = s.createCriteria(Brand.class);
                brandCR.add(Restrictions.eq("name", brandName));
                Brand b = (Brand) brandCR.uniqueResult();
                productCR.add(Restrictions.eq("brand", b));
            }
        }
        List<Product> prodList = productCR.list();
        Criteria cr = s.createCriteria(Stock.class);
        if (!prodList.isEmpty()) {
            cr.add(Restrictions.in("product", prodList));
        }
        cr.addOrder(Order.asc("id"));
        List<Stock> stockList = (List<Stock>) cr.list();
        if (prodList.isEmpty()) {
            stockList.clear();
        }


        for (Stock stock : stockList) {

            LinkedTreeMap<String, Object> item = new LinkedTreeMap();

            ArrayList<String> imageList = new ArrayList();
            for (Iterator iterator = stock.getProduct().getProductImages().iterator(); iterator.hasNext();) {
                ProductImage pi = (ProductImage) iterator.next();
                imageList.add(pi.getId().getImage());
            }
            Collections.sort(imageList);

            Product p = stock.getProduct();
            item.put("stockID", String.valueOf(stock.getId()));
            item.put("title", p.getBrand().getName() + " " + p.getName());
            item.put("qty", String.valueOf(stock.getQty()));
            item.put("buyprice", df.format(stock.getBuyingPrice()));
            item.put("sellprice", df.format(stock.getSellingPrice()));
            item.put("discount", df.format(stock.getDiscount()) + "% off");
            item.put("image", Validation.getNextProductImage(imageList, 1));
            item.put("brandID", String.valueOf(p.getBrand().getId()));
            item.put("brand", p.getBrand().getName());
            item.put("category", p.getCategory().getName());  
            item.put("isActive", stock.getActive() == (byte) 1);
            stkList.add(item);
        }



        responseData.put("stockList", stkList);

        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
