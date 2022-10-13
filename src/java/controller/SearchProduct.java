package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hibernate.Brand;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.ProductHasSpec;
import hibernate.Spec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ProductComparator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SearchProduct", urlPatterns = {"/SearchProduct"})
public class SearchProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        String catName = request.getParameter("catName");
        String brandName = request.getParameter("brandName");
        String searchText = request.getParameter("searchText");
        String jsonData = request.getParameter("jsonData");
        String advJsData = request.getParameter("advJsData");
        String fromPrice = request.getParameter("fromPrice");
        String toPrice = request.getParameter("toPrice");
        String sortBy = request.getParameter("sortBy");

        Criteria productCR = s.createCriteria(Product.class);
        Category c = null;
        if (catName != null) {
            Criteria catCR = s.createCriteria(Category.class);
            catCR.add(Restrictions.eq("name", catName));
            c = (Category) catCR.uniqueResult();
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

            Gson g = new Gson();
            if (jsonData != null) {
                TypeToken tt = new TypeToken<HashMap<String, ArrayList<String>>>() {
                };
                HashMap<String, ArrayList<String>> FilterMap = g.fromJson(jsonData, tt.getType());

                int catFldCount = FilterMap.get("catFields").size();
                Criterion[] catResList = new Criterion[catFldCount];
                for (int i = 0; i < catFldCount; i++) {
                    Criteria catCR = s.createCriteria(Category.class);
                    catCR.add(Restrictions.eq("name", FilterMap.get("catFields").get(i)));
                    c = (Category) catCR.uniqueResult();
                    catResList[i] = Restrictions.eq("category", c);
                }
                productCR.add(Restrictions.or(catResList));

                if (FilterMap.containsKey("brandFields")) {
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

            }

            HashSet<Product> prodHasSpecSet = new HashSet();
            if (advJsData != null) {
                TypeToken tt = new TypeToken<HashMap<String, ArrayList<String>>>() {
                };
                HashMap<String, ArrayList<String>> specMap = g.fromJson(advJsData, tt.getType());
                for (Iterator<String> iterator = specMap.keySet().iterator(); iterator.hasNext();) {
                    String specName = iterator.next();

                    Criteria specCR = s.createCriteria(Spec.class);
                    specCR.add(Restrictions.eq("category", c));
                    specCR.add(Restrictions.eq("specName", specName));
                    Spec sp = (Spec) specCR.uniqueResult();

                    HashSet<Product> prodSet = new HashSet();
                    for (String spVal : specMap.get(specName)) {
                        Criteria proSpecCR = s.createCriteria(ProductHasSpec.class);
                        proSpecCR.add(Restrictions.eq("spec", sp));
                        if (sp.getUnit() == null) {
                            proSpecCR.add(Restrictions.eq("specValue", spVal));
                        } else {
                            proSpecCR.add(Restrictions.eq("specValue", spVal.substring(0, spVal.length() - (sp.getUnit().length() + 1))));
                        }

                        List<ProductHasSpec> phsList = proSpecCR.list();
                        for (ProductHasSpec phs : phsList) {
                            prodSet.add(phs.getProduct());
                        }
                    }
                    if (prodHasSpecSet.isEmpty()) {
                        prodHasSpecSet.addAll(prodSet);
                    } else {
                        prodHasSpecSet.retainAll(prodSet);
                    }
                }
            }

            List<Product> prodList = productCR.list();
            if (advJsData != null) {
                prodList.retainAll(prodHasSpecSet);
            }

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

            if (!fromPrice.isEmpty()) {
                request.setAttribute("fromPrice", fromPrice);
            }
            if (!toPrice.isEmpty()) {
                request.setAttribute("toPrice", toPrice);
            }
            request.setAttribute("prodList", prodList);
            request.getSession().setAttribute("pageLimit", request.getParameter("pageLimit"));
            request.setAttribute("pageID", request.getParameter("p"));
            request.getRequestDispatcher("get_search_products.jsp").forward(request, response);
            s.close();

        }
    }
