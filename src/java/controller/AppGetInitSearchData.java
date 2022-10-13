package controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hibernate.Brand;
import hibernate.Category;
import hibernate.Customer;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.ProductImage;
import hibernate.ProductReview;
import hibernate.Stock;
import hibernate.Wishlist;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ProductComparator;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author laksh
 */
@WebServlet(name = "AppGetInitSearchData", urlPatterns = {"/AppGetInitSearchData"})
public class AppGetInitSearchData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        Gson g = new Gson();

        Criteria stockCR = s.createCriteria(Stock.class);
        stockCR.setProjection(Projections.max("sellingPrice"));
        String maxPrice = String.valueOf((double) stockCR.uniqueResult());
        stockCR = s.createCriteria(Stock.class);
        stockCR.setProjection(Projections.min("sellingPrice"));
        String minPrice = String.valueOf((double) stockCR.uniqueResult());

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

        responseData.put("maxPrice", maxPrice);
        responseData.put("minPrice", minPrice);
        responseData.put("catList", catList);
        responseData.put("brandList", brandList);

//Search Items
        LinkedList<LinkedTreeMap<String, Object>> productList = new LinkedList();

        int custID = Integer.parseInt(request.getParameter("custID"));
        String catName = request.getParameter("catName");
        String brandName = request.getParameter("brandName");
        Customer cust = (Customer) s.load(Customer.class, custID);

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
        Collections.sort(prodList, ProductComparator.getComparator(ProductComparator.NEWLY_ADDED));

        Criteria wishCR = s.createCriteria(Wishlist.class);
        wishCR.add(Restrictions.eq("customer", cust));
        List<Integer> wishList = wishCR.list();
        for (Wishlist w : (List<Wishlist>) wishCR.list()) {
            wishList.add(w.getProduct().getId());
        }

        LinkedHashMap<Product, Stock> refinedProdMap = new LinkedHashMap();
        for (Product p : prodList) {
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
                if (stk != null) {
                    refinedProdMap.put(p, stk);
                }
            }
        }

        LinkedList<Product> refinedprodList = new LinkedList();
        for (Iterator<Product> it = refinedProdMap.keySet().iterator(); it.hasNext();) {
            Product p = it.next();
            refinedprodList.add(p);
        }

        for (Product p : refinedprodList) {
            LinkedTreeMap<String, Object> item = new LinkedTreeMap();

            ArrayList<String> imgList = new ArrayList();
            for (Iterator iterator = p.getProductImages().iterator(); iterator.hasNext();) {
                ProductImage pi = (ProductImage) iterator.next();
                imgList.add(pi.getId().getImage());
            }
            Collections.sort(imgList);

            double tot = 0;
            double avgRating = 0;
            Set<ProductReview> prSet = p.getProductReviews();
            for (Iterator<ProductReview> revIT = prSet.iterator(); revIT.hasNext();) {
                ProductReview pr = revIT.next();
                tot += pr.getRating();
            }
            if (!prSet.isEmpty()) {
                avgRating = tot / prSet.size();
            }

            Stock stock = refinedProdMap.get(p);
            item.put("id", String.valueOf(p.getId()));
            item.put("stockID", String.valueOf(stock.getId()));
            item.put("title", p.getBrand().getName() + " " + p.getName());
            item.put("stocksLeft", String.valueOf(stock.getQty()));
            item.put("price", stock.getSellingPrice());
            item.put("discount", stock.getDiscount());
            item.put("discountedPrice", (stock.getSellingPrice() * (100 - stock.getDiscount()) / 100));
            item.put("image", Validation.getNextProductImage(imgList, 1));            
            item.put("brandID", String.valueOf(p.getBrand().getId()));
            item.put("brand", p.getBrand().getName());
            item.put("category", p.getCategory().getName());        
            item.put("rating", String.valueOf(avgRating));
            item.put("isWishListed", wishList.contains(p.getId()));
            productList.add(item);
        }

        responseData.put("productList", productList);

        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
