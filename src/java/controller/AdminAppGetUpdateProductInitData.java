package controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hibernate.Brand;
import hibernate.Category;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.ProductHasSpec;
import hibernate.ProductImage;
import hibernate.Spec;
import hibernate.Stock;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author laksh
 */
@WebServlet(name = "AdminAppGetUpdateProductInitData", urlPatterns = {"/AdminAppGetUpdateProductInitData"})
public class AdminAppGetUpdateProductInitData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        Gson g = new Gson();

        String stockID = request.getParameter("stockID");
        Stock stock = (Stock) s.load(Stock.class, Integer.parseInt(stockID));

        LinkedTreeMap<String, String> catMap = new LinkedTreeMap();
        Criteria crCategory = s.createCriteria(Category.class);
        crCategory.addOrder(Order.asc("name"));
        List<Category> l = (List<Category>) crCategory.list();
        for (Category c : l) {
            catMap.put(String.valueOf(c.getName()), String.valueOf(c.getId()));
        }

        LinkedTreeMap<String, String> brandMap = new LinkedTreeMap();
        Criteria crBrand = s.createCriteria(Brand.class);
        crBrand.addOrder(Order.asc("name"));
        List<Brand> lb = (List<Brand>) crBrand.list();
        for (Brand c : lb) {
            brandMap.put(String.valueOf(c.getName()), String.valueOf(c.getId()));
        }

        responseData.put("catMap", catMap);
        responseData.put("brandMap", brandMap);

        HashMap<Spec, String> setSpecsMap = new HashMap();

        Criteria specCR = s.createCriteria(ProductHasSpec.class);
        specCR.add(Restrictions.eq("product", stock.getProduct()));
        List<ProductHasSpec> setSpecList = specCR.list();
        for (ProductHasSpec ps : setSpecList) {
            setSpecsMap.put(ps.getSpec(), ps.getSpecValue());
        }

        LinkedList<LinkedList<String>> specsList = new LinkedList();
        Category category = stock.getProduct().getCategory();
        Set<Spec> specSet = category.getSpecs();
        for (Spec spec : specSet) {
            LinkedList<String> specItem = new LinkedList();
            specItem.add(String.valueOf(spec.getId()));
            if (spec.getUnit() == null) {
                specItem.add(spec.getSpecName());
            } else {
                specItem.add(spec.getSpecName() + " (" + spec.getUnit() + ")");
            }
            if (setSpecsMap.keySet().contains(spec)) {
                specItem.add(setSpecsMap.get(spec));
            }
            specsList.add(specItem);
        }
        
        

        ArrayList<String> imageList = new ArrayList<>();
        Criteria imgCR = s.createCriteria(ProductImage.class);
        imgCR.add(Restrictions.eq("product", stock.getProduct()));
        List<ProductImage> imgList = imgCR.list();
        for (ProductImage pi : imgList) {
            imageList.add(pi.getId().getImage());
        }

        Product p = stock.getProduct();
        responseData.put("productID", String.valueOf(p.getId()));
        responseData.put("name", p.getName());
        responseData.put("brandName", p.getBrand().getName());
        responseData.put("catName", p.getCategory().getName());
        responseData.put("qty", String.valueOf(stock.getQty()));
        responseData.put("buyprice", stock.getBuyingPrice());
        responseData.put("sellprice", stock.getSellingPrice());
        responseData.put("discount", stock.getDiscount());
        responseData.put("desc", stock.getProduct().getDescription());
        responseData.put("warranty", stock.getWarranty());
        responseData.put("specList", specsList);
        responseData.put("imageList", imageList);

        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
