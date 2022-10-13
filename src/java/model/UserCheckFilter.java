package model;

import com.google.gson.Gson;
import hibernate.HiberUtil;
import hibernate.User;
import hibernate.UserPrivilege;
import java.io.IOException;
import java.util.Iterator;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebFilter(filterName = "UserCheckFilter", urlPatterns = {"/dashboard.jsp", "/usr_mng.jsp", "/odr_mng.jsp", "/prod_mng.jsp", "/review_order.jsp", "/del_mng.jsp", "/reports.jsp"})
public class UserCheckFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        User usr = (User) req.getSession().getAttribute("usr");
        if (usr == null) {
            Gson g = new Gson();
            String param = "";

            if (req.getRequestURI().contains("dashboard.jsp")) {
                param = "?postAction=dashboard.jsp";
            } else if (req.getRequestURI().contains("usr_mng.jsp")) {
                param = "?postAction=usr_mng.jsp";
            } else if (req.getRequestURI().contains("odr_mng.jsp")) {
                param = "?postAction=odr_mng.jsp";
            } else if (req.getRequestURI().contains("prod_mng.jsp")) {
                param = "?postAction=prod_mng.jsp";
            } else if (req.getRequestURI().contains("review_order.jsp")) {
                param = "?postAction=review_order.jsp";
            } else if (req.getRequestURI().contains("del_mng.jsp")) {
                param = "?postAction=del_mng.jsp";
            } else if (req.getRequestURI().contains("reports.jsp")) {
                param = "?postAction=reports.jsp";
            }
            if (!param.isEmpty()) {
                param += "&params=" + g.toJson(request.getParameterMap());
            }
            resp.sendRedirect("admin_login.jsp" + param);
        } else {
            if (hasPrivilege(usr, req)) {
                chain.doFilter(request, response);
            } else {
                req.getRequestDispatcher("AdminRedirect").forward(request, response);
            }

        }
    }

    private boolean hasPrivilege(User sesUsr, HttpServletRequest req) {

        boolean b = false;
        boolean hasDashboard = false;
        boolean hasUser = false;
        boolean hasProduct = false;
        boolean hasOrder = false;
        boolean hasDelivery = false;
        boolean hasReports = false;

        req.getSession().setAttribute("hasDashboard", hasDashboard);
        req.getSession().setAttribute("hasUser", hasUser);
        req.getSession().setAttribute("hasProduct", hasProduct);
        req.getSession().setAttribute("hasOrder", hasOrder);
        req.getSession().setAttribute("hasDelivery", hasDelivery);
        req.getSession().setAttribute("hasReports", hasReports);

        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        User usr = (User) s.load(User.class, sesUsr.getId());

        if (usr.getActive() == 1) {
            for (Iterator<UserPrivilege> iterator = usr.getUserType().getUserPrivileges().iterator(); iterator.hasNext();) {
                UserPrivilege up = iterator.next();
                if (up.getName().equals("Dashboard")) {
                    hasDashboard = true;
                }
                if (up.getName().equals("User Management")) {
                    hasUser = true;
                }
                if (up.getName().equals("Product Management")) {
                    hasProduct = true;
                }
                if (up.getName().equals("Order Management")) {
                    hasOrder = true;
                }
                if (up.getName().equals("Delivery Management")) {
                    hasDelivery = true;
                }
                if (up.getName().equals("Sales & Reports")) {
                    hasReports = true;
                }
            }

            req.getSession().setAttribute("hasDashboard", hasDashboard);
            req.getSession().setAttribute("hasUser", hasUser);
            req.getSession().setAttribute("hasProduct", hasProduct);
            req.getSession().setAttribute("hasOrder", hasOrder);
            req.getSession().setAttribute("hasDelivery", hasDelivery);
            req.getSession().setAttribute("hasReports", hasReports);

            if (req.getRequestURI().contains("dashboard.jsp")) {
                if (hasDashboard) {
                    b = true;
                }
            } else if (req.getRequestURI().contains("usr_mng.jsp")) {
                if (hasUser) {
                    b = true;
                }
            } else if (req.getRequestURI().contains("odr_mng.jsp")) {
                if (hasOrder) {
                    b = true;
                }
            } else if (req.getRequestURI().contains("prod_mng.jsp")) {
                if (hasProduct) {
                    b = true;
                }
            } else if (req.getRequestURI().contains("review_order.jsp")) {
                if (hasOrder) {
                    b = true;
                }
            } else if (req.getRequestURI().contains("del_mng.jsp")) {
                if (hasDelivery) {
                    b = true;
                }
            } else if (req.getRequestURI().contains("reports.jsp")) {
                if (hasReports) {
                    b = true;
                }
            }
        }

        s.close();
        return b;
    }

    @Override
    public void destroy() {

    }
}
