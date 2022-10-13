package controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hibernate.ApplicationSetting;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.ProductImage;
import hibernate.ProductReview;
import hibernate.Stock;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppGetHomeData", urlPatterns = {"/AppGetHomeData"})
public class AppGetHomeData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HashMap<String, Object> homeDataMap = new HashMap();

        LinkedList<LinkedTreeMap<String, Object>> catList = new LinkedList();
        LinkedList<LinkedTreeMap<String, Object>> newArrivalsList = new LinkedList();
        LinkedList<LinkedTreeMap<String, Object>> hotDealsList = new LinkedList();

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        Gson g = new Gson();

        Criteria stocktCR1 = s.createCriteria(Stock.class);
        stocktCR1.add(Restrictions.eq("active", (byte) 1));
        stocktCR1.addOrder(Order.desc("createdOn"));
        stocktCR1.setMaxResults(6);
        List<Stock> featuredList = stocktCR1.list();

        Criteria stocktCR2 = s.createCriteria(Stock.class);
        stocktCR2.add(Restrictions.eq("active", (byte) 1));
        stocktCR2.add(Restrictions.ne("discount", 0d));
        stocktCR2.addOrder(Order.desc("discount"));
        stocktCR2.setMaxResults(6);
        List<Stock> discountedList = stocktCR2.list();

        Criteria stockCR = s.createCriteria(Stock.class, "StockTable");
        stockCR.add(Restrictions.eq("active", (byte) 1));
        stockCR.createCriteria("StockTable.product", "ProductTable");
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("ProductTable.category"));
        projectionList.add(Projections.alias(Projections.count("ProductTable.category"), "CatCount"));
        stockCR.setProjection(projectionList);
        stockCR.addOrder(Order.desc("CatCount"));

        List<Object[]> result = stockCR.list();

        for (Object[] o : result) {
            Category c = (Category) o[0];
            if (c.getActive() == 1) {
                Product p = null;
                Criteria prodCR = s.createCriteria(Product.class);
                prodCR.add(Restrictions.eq("category", c));
                prodCR.addOrder(Order.desc("id"));
                for (Product prod : (List<Product>) prodCR.list()) {
                    if (prod.getBrand().getActive() == 1) {
                        p = prod;
                        break;
                    }
                }
                if (p != null) {
                    ArrayList<String> imgList = new ArrayList();
                    for (Iterator iterator = p.getProductImages().iterator(); iterator.hasNext();) {
                        ProductImage pi = (ProductImage) iterator.next();
                        imgList.add(pi.getId().getImage());
                    }
                    Collections.sort(imgList);

                    LinkedTreeMap<String, Object> category = new LinkedTreeMap();
                    category.put("id", c.getId());
                    category.put("name", c.getName());
                    category.put("image", Validation.getNextProductImage(imgList, 1));

                    catList.add(category);
                }
            }
        }

        for (Stock stk : featuredList) {
            if (stk.getProduct().getCategory().getActive() == 1 && stk.getProduct().getBrand().getActive() == 1) {
                String availableStock = String.valueOf(stk.getQty());
                if (availableStock.equals("0")) {
                    availableStock = "Out of Stock";
                }

                ArrayList<String> imgList = new ArrayList();
                for (Iterator iterator = stk.getProduct().getProductImages().iterator(); iterator.hasNext();) {
                    ProductImage pi = (ProductImage) iterator.next();
                    imgList.add(pi.getId().getImage());
                }
                Collections.sort(imgList);

                String price;
                if (stk.getDiscount() == 0) {
                    price = df.format(stk.getSellingPrice());
                } else {
                    price = df.format(stk.getSellingPrice() * (100 - stk.getDiscount()) / 100) + " (" + df.format(stk.getDiscount()) + " % off)";
                }

                double avgRating = 0;
                double tot = 0;
                Set<ProductReview> prSet = stk.getProduct().getProductReviews();
                for (Iterator<ProductReview> revIT = prSet.iterator(); revIT.hasNext();) {
                    ProductReview pr = revIT.next();
                    tot += pr.getRating();
                }
                if (!prSet.isEmpty()) {
                    avgRating = tot / prSet.size();
                }

                LinkedTreeMap<String, Object> newArrival = new LinkedTreeMap();
                newArrival.put("id", String.valueOf(stk.getId()));
                newArrival.put("name", stk.getProduct().getBrand().getName() + " " + stk.getProduct().getName());
                newArrival.put("rating", avgRating);
                newArrival.put("stock", availableStock);
                newArrival.put("price", price);
                newArrival.put("image", Validation.getNextProductImage(imgList, 1));

                newArrivalsList.add(newArrival);
            }
        }
        
        for (Stock stk : discountedList) {
            if (stk.getProduct().getCategory().getActive() == 1 && stk.getProduct().getBrand().getActive() == 1) {
                
                double discountedPrice = stk.getSellingPrice() * (100 - stk.getDiscount()) / 100;
                
                String availableStock = String.valueOf(stk.getQty());
                if (availableStock.equals("0")) {
                    availableStock = "Out of Stock";
                }

                ArrayList<String> imgList = new ArrayList();
                for (Iterator iterator = stk.getProduct().getProductImages().iterator(); iterator.hasNext();) {
                    ProductImage pi = (ProductImage) iterator.next();
                    imgList.add(pi.getId().getImage());
                }
                Collections.sort(imgList);

                String price;
                if (stk.getDiscount() == 0) {
                    price = df.format(stk.getSellingPrice());
                } else {
                    price = df.format(stk.getSellingPrice() * (100 - stk.getDiscount()) / 100) + " (" + df.format(stk.getDiscount()) + "% off)";
                }

                double avgRating = 0;
                double tot = 0;
                Set<ProductReview> prSet = stk.getProduct().getProductReviews();
                for (Iterator<ProductReview> revIT = prSet.iterator(); revIT.hasNext();) {
                    ProductReview pr = revIT.next();
                    tot += pr.getRating();
                }
                if (!prSet.isEmpty()) {
                    avgRating = tot / prSet.size();
                }

                LinkedTreeMap<String, Object> hotDeal = new LinkedTreeMap();
                hotDeal.put("id", String.valueOf(stk.getId()));
                hotDeal.put("name", stk.getProduct().getBrand().getName() + " " + stk.getProduct().getName());
                hotDeal.put("rating", avgRating);
                hotDeal.put("stock", availableStock);
                hotDeal.put("price", price);
                hotDeal.put("discount", df.format(stk.getDiscount()) + "% off");
                hotDeal.put("save", df.format(stk.getSellingPrice() - discountedPrice));
                hotDeal.put("image", Validation.getNextProductImage(imgList, 1));

                hotDealsList.add(hotDeal);
            }
        }
        
        String notice_message = ((ApplicationSetting) s.load(ApplicationSetting.class, "notice_message")).getValue();

        homeDataMap.put("catList", catList);
        homeDataMap.put("newArrivalsList", newArrivalsList);
        homeDataMap.put("hotDealsList", hotDealsList);
        homeDataMap.put("notice_message", notice_message);
        response.getWriter().write(g.toJson(homeDataMap));
        s.close();
    }

}
