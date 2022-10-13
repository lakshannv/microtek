package controller;

import com.google.gson.Gson;
import hibernate.HiberUtil;
import hibernate.Product;
import hibernate.ProductImage;
import hibernate.Stock;
import java.io.IOException;
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

@WebServlet(name = "Test", urlPatterns = {"/Test"})
public class Test extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        LinkedList<HashMap<String, Object>> productList = new LinkedList();
        
        Criteria cr = s.createCriteria(Stock.class);
        List<Stock> stockList = cr.list();
        
        for (Stock stock : stockList) {
            HashMap<String, Object> item = new HashMap();
            item.put("name", stock.getProduct().getName());
            item.put("price", stock.getSellingPrice());
            
            Product p = stock.getProduct();
            ArrayList<String> imgList = new ArrayList();
            for (Iterator iterator = p.getProductImages().iterator(); iterator.hasNext();) {
                ProductImage pi = (ProductImage) iterator.next();
                imgList.add(pi.getId().getImage());
            }
            Collections.sort(imgList);
            
            item.put("img", Validation.getNextProductImage(imgList, 1));
            
            productList.add(item);
        }
        
        Gson g = new Gson();
        
        response.getWriter().write(g.toJson(productList));
        
        s.close();
    }


}
