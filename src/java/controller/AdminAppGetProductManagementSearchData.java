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
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author laksh
 */
@WebServlet(name = "AdminAppGetProductManagementSearchData", urlPatterns = {"/AdminAppGetProductManagementSearchData"})
public class AdminAppGetProductManagementSearchData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        Gson g = new Gson();

        String catName = request.getParameter("catName");
        String brandName = request.getParameter("brandName");
        String searchText = request.getParameter("searchText");
        String sortBy = request.getParameter("sortBy");

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
        Criteria cr = s.createCriteria(Stock.class);
        if (!prodList.isEmpty()) {
            cr.add(Restrictions.in("product", prodList));
        }

        switch (sortBy) {
            case "Stock ID Asc.":
                cr.addOrder(Order.asc("id"));
                break;
            case "Stock ID Desc.":
                cr.addOrder(Order.desc("id"));
                break;
            case "Product Name Asc.":
                cr.createAlias("product", "product");
                cr.setFetchMode("product", FetchMode.JOIN);
                cr.addOrder(Order.asc("product.name"));
                break;
            case "Product Name Desc.":
                cr.createAlias("product", "product");
                cr.setFetchMode("product", FetchMode.JOIN);
                cr.addOrder(Order.desc("product.name"));
                break;
            case "Buying Price Asc.":
                cr.addOrder(Order.asc("buyingPrice"));
                break;
            case "Buying Price Desc.":
                cr.addOrder(Order.desc("buyingPrice"));
                break;
            case "Selling Price Asc.":
                cr.addOrder(Order.asc("sellingPrice"));
                break;
            case "Selling Price Desc.":
                cr.addOrder(Order.desc("sellingPrice"));
                break;
            case "Stock Qty Asc.":
                cr.addOrder(Order.asc("qty"));
                break;
            case "Stock Qty Desc.":
                cr.addOrder(Order.desc("qty"));
                break;
            case "Discount Asc.":
                cr.addOrder(Order.asc("discount"));
                break;
            case "Discount Desc.":
                cr.addOrder(Order.desc("discount"));
                break;
            case "Warranty Asc.":
                cr.addOrder(Order.asc("warranty"));
                break;
            case "Warranty Desc.":
                cr.addOrder(Order.desc("warranty"));
                break;
            case "Date Cerated Asc.":
                cr.addOrder(Order.asc("createdOn"));
                break;
            case "Date Cerated Desc.":
                cr.addOrder(Order.desc("createdOn"));
                break;
            case "Active Status":
                cr.addOrder(Order.asc("active"));
                break;
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
