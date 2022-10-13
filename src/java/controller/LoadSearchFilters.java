package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hibernate.Brand;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.Stock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadSearchFilters", urlPatterns = {"/LoadSearchFilters"})
public class LoadSearchFilters extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        String catName = request.getParameter("catName");
        String brandName = request.getParameter("brandName");
        String searchText = request.getParameter("searchText");
        String fPrice = request.getParameter("fromPrice");
        String tPrice = request.getParameter("toPrice");
        String jsonData = request.getParameter("jsonData");

        Criteria productCR = s.createCriteria(Product.class);

        if (catName != null) {
            Criteria catCR = s.createCriteria(Category.class);
            catCR.add(Restrictions.eq("name", catName));
            Category c = (Category) catCR.uniqueResult();
            productCR.add(Restrictions.eq("category", c));
        }
        if (brandName != null) {
            Criteria brandCR = s.createCriteria(Brand.class);
            brandCR.add(Restrictions.eq("name", brandName));
            Brand b = (Brand) brandCR.uniqueResult();
            productCR.add(Restrictions.eq("brand", b));
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
        if (jsonData != null) {
            Gson g = new Gson();
            TypeToken tt = new TypeToken<HashMap<String, ArrayList<String>>>() {
            };
            HashMap<String, ArrayList<String>> FilterMap = g.fromJson(jsonData, tt.getType());

            int catFldCount = FilterMap.get("catFields").size();
            Criterion[] catResList = new Criterion[catFldCount];
            for (int i = 0; i < catFldCount; i++) {
                Criteria catCR = s.createCriteria(Category.class);
                catCR.add(Restrictions.eq("name", FilterMap.get("catFields").get(i)));
                Category c = (Category) catCR.uniqueResult();
                catResList[i] = Restrictions.eq("category", c);
            }
            productCR.add(Restrictions.or(catResList));

            int brFldCount = FilterMap.get("brandFields").size();
            Criterion[] brandResList = new Criterion[brFldCount];
            for (int i = 0; i < brFldCount; i++) {
                Criteria brandCR = s.createCriteria(Brand.class);
                brandCR.add(Restrictions.eq("name", FilterMap.get("brandFields").get(i)));
                Brand b = (Brand) brandCR.uniqueResult();
                brandResList[i] = Restrictions.eq("brand", b);
            }
            productCR.add(Restrictions.or(brandResList));
        }

        List<Product> prodList = productCR.list();

        HashMap<String, Object> resData = new HashMap();

        TreeMap<String, Integer> catMap = new TreeMap();
        TreeMap<String, Integer> brandMap = new TreeMap();

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
                    if (fPrice != null) {
                        if (!fPrice.isEmpty()) {
                            double fromPrice = Double.parseDouble(fPrice);
                            if (sellPrice < fromPrice) {
                                isWithinPriceRange = false;
                            }
                        }
                    }
                    if (tPrice != null) {
                        if (!tPrice.isEmpty()) {
                            double toPrice = Double.parseDouble(tPrice);
                            if (sellPrice > toPrice) {
                                isWithinPriceRange = false;
                            }
                        }
                    }

                    if (isWithinPriceRange) {
                        String cName = p.getCategory().getName();
                        if (catMap.get(cName) == null) {
                            catMap.put(cName, 1);
                        } else {
                            catMap.put(cName, catMap.get(cName) + 1);
                        }

                        String bName = p.getBrand().getName();
                        if (brandMap.get(bName) == null) {
                            brandMap.put(bName, 1);
                        } else {
                            brandMap.put(bName, brandMap.get(bName) + 1);
                        }
                    }
                }
            }
        }

        resData.put("catMap", catMap);
        resData.put("brandMap", brandMap);

        Gson g = new Gson();
        response.getWriter().write(g.toJson(resData));

        s.close();
    }
}
