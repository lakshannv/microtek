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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AppSearchProduct", urlPatterns = {"/AppSearchProduct"})
public class AppSearchProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        HashMap<String, Object> searchDataMap = new HashMap();
        LinkedList<LinkedTreeMap<String, Object>> productList = new LinkedList();

        String catName = request.getParameter("catName");
        String brandName = request.getParameter("brandName");
        String searchText = request.getParameter("searchText");
        String fromPriceString = request.getParameter("fromPrice");
        String toPriceString = request.getParameter("toPrice");
        String sortBy = request.getParameter("sortBy");
        int custID = Integer.parseInt(request.getParameter("custID"));
        Customer cust = (Customer) s.load(Customer.class, custID);

        if (sortBy == null) {
            sortBy = "Newly Added";
        }

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
        if (searchText != null) {
            if (!searchText.trim().isEmpty()) {
                Criteria sbrandCR = s.createCriteria(Brand.class);
                sbrandCR.add(Restrictions.like("name", searchText, MatchMode.START));
                List<Brand> sbrList = sbrandCR.list();

                Criteria scatCR = s.createCriteria(Category.class);
                scatCR.add(Restrictions.like("name", searchText, MatchMode.START));
                List<Category> scatList = scatCR.list();

                if (sbrList.isEmpty() && scatList.isEmpty()) {
                    String firstWord = searchText.split(" ")[0];
                    Brand b = (Brand) s.createCriteria(Brand.class).add(Restrictions.eq("name", firstWord)).uniqueResult();
                    if (b == null) {
                        productCR.add(Restrictions.like("name", searchText, MatchMode.ANYWHERE));
                    } else {
                        productCR.add(Restrictions.like("name", searchText.substring(firstWord.length() + 1), MatchMode.ANYWHERE));
                    }
                } else {
                    ArrayList<Criterion> crList = new ArrayList<>();
                    crList.add(Restrictions.like("name", searchText, MatchMode.ANYWHERE));
                    if (!sbrList.isEmpty()) {
                        crList.add(Restrictions.in("brand", sbrList));
                    }
                    if (!scatList.isEmpty()) {
                        crList.add(Restrictions.in("category", scatList));
                    }
                    Criterion[] resList = new Criterion[crList.size()];
                    for (int i = 0; i < crList.size(); i++) {
                        resList[i] = crList.get(i);
                    }
                    productCR.add(Restrictions.or(resList));
                }
            }
        }

        List<Product> prodList = productCR.list();

        switch (sortBy) {
            case "Newly Added":
                Collections.sort(prodList, ProductComparator.getComparator(ProductComparator.NEWLY_ADDED));
                break;
            case "Price Low to High":
                Collections.sort(prodList, ProductComparator.getComparator(ProductComparator.PRICE_LOW_TO_HIGH));
                break;
            case "Price High to Low":
                Collections.sort(prodList, ProductComparator.getComparator(ProductComparator.PRICE_HIGH_TO_LOW));
                break;
            case "Low Stocks":
                Collections.sort(prodList, ProductComparator.getComparator(ProductComparator.LOW_STOCKS));
                break;
        }

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
                    double sellPrice = stk.getSellingPrice() * (100 - stk.getDiscount()) / 100;
                    boolean isWithinPriceRange = true;
                    if (fromPriceString != null) {
                        double fromPrice = Double.parseDouble(fromPriceString);
                        if (sellPrice < fromPrice) {
                            isWithinPriceRange = false;
                        }
                    }
                    if (toPriceString != null) {
                        double toPrice = Double.parseDouble(toPriceString);
                        if (sellPrice > toPrice) {
                            isWithinPriceRange = false;
                        }
                    }
                    if (isWithinPriceRange) {
                        refinedProdMap.put(p, stk);
                    }
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

        searchDataMap.put("productList", productList);
        response.getWriter().write(new Gson().toJson(searchDataMap));
        s.close();

    }
}
