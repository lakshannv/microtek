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

@WebServlet(name = "LoadBrandFilters", urlPatterns = {"/LoadBrandFilters"})
public class LoadBrandFilters extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        String jsonData = request.getParameter("jsonData");
        String catName = request.getParameter("catName");
        String brandName = request.getParameter("brandName");
        String searchText = request.getParameter("searchText");
        String fromPriceParam = request.getParameter("fromPrice");
        String toPriceParam = request.getParameter("toPrice");

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

        Criteria productCR = s.createCriteria(Product.class);
        productCR.add(Restrictions.or(catResList));
        
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
                    productCR.add(Restrictions.like("name", searchText, MatchMode.ANYWHERE));
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
                    if (!fromPriceParam.isEmpty()) {
                        double fromPrice = Double.parseDouble((String) fromPriceParam);
                        if (sellPrice < fromPrice) {
                            isWithinPriceRange = false;
                        }
                    }
                    if (!toPriceParam.isEmpty()) {
                        double toPrice = Double.parseDouble((String) toPriceParam);
                        if (sellPrice > toPrice) {
                            isWithinPriceRange = false;
                        }
                    }
                    if (isWithinPriceRange) {
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

        response.getWriter().write(g.toJson(brandMap));

        s.close();
    }
}
