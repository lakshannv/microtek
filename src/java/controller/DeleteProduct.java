/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import hibernate.Cart;
import hibernate.HiberUtil;
import hibernate.InvoiceItem;
import hibernate.Product;
import hibernate.ProductHasSpec;
import hibernate.ProductImage;
import hibernate.ProductReview;
import hibernate.Stock;
import hibernate.Wishlist;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "DeleteProduct", urlPatterns = {"/DeleteProduct"})
public class DeleteProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int stockID = Integer.parseInt(request.getParameter("stockID"));

            SessionFactory sf = HiberUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria cr = s.createCriteria(Stock.class);
            cr.add(Restrictions.eq("id", stockID));

            Stock existingStock = (Stock) cr.uniqueResult();

            Criteria invCR = s.createCriteria(InvoiceItem.class);
            invCR.add(Restrictions.eq("stock", existingStock));

            if (invCR.list().isEmpty()) {
                Criteria cartCR = s.createCriteria(Cart.class);
                cartCR.add(Restrictions.eq("stock", existingStock));
                if (cartCR.list().isEmpty()) {
                    Criteria prodRevCR = s.createCriteria(ProductReview.class);
                    prodRevCR.add(Restrictions.eq("product", existingStock.getProduct()));
                    
                    Criteria wishCR = s.createCriteria(Wishlist.class);
                    wishCR.add(Restrictions.eq("product", existingStock.getProduct()));
                    
                    Criteria stockCR = s.createCriteria(Stock.class);
                    stockCR.add(Restrictions.eq("product", existingStock.getProduct()));
                    if (prodRevCR.list().isEmpty() && wishCR.list().isEmpty()) {

                        if (stockCR.list().size() == 1) {
                            Criteria prodRSpecCR = s.createCriteria(ProductHasSpec.class);
                            prodRSpecCR.add(Restrictions.eq("product", existingStock.getProduct()));
                            for (ProductHasSpec ps : (List<ProductHasSpec>) prodRSpecCR.list()) {
                                s.delete(ps);
                            }
                            Criteria prodImgCR = s.createCriteria(ProductImage.class);
                            prodImgCR.add(Restrictions.eq("product", existingStock.getProduct()));
                            for (ProductImage pImg : (List<ProductImage>) prodImgCR.list()) {
                                s.delete(pImg);
                                File f = new File(getServletContext().getRealPath("") + "//assets//img/products//" + pImg.getId().getImage());
                                if (f.exists()) {
                                    f.delete();
                                }
                            }
                            s.delete(existingStock);
                            s.delete(existingStock.getProduct());
                        } else {
                            s.delete(existingStock);
                        }
                        response.getWriter().write("ok");
                    } else {
                        if (stockCR.list().size() == 1) {
                            response.getWriter().write("ex-rev");
                        } else {
                            s.delete(existingStock);
                            response.getWriter().write("ok");
                        }
                    }

                } else {
                    response.getWriter().write("ex-cart");
                }
            } else {
                response.getWriter().write("ex-inv");
            }

            Transaction t = s.beginTransaction();
            t.commit();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
    }
}
