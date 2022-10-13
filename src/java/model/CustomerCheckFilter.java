package model;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "CustomerCheckFilter", urlPatterns = {"/checkout.jsp", "/order.jsp", "/pending_orders.jsp", "/purchase_history.jsp", "/account.jsp"})
public class CustomerCheckFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (req.getSession().getAttribute("cust") == null) {
            Gson g = new Gson();
            String param = "";

            if (req.getRequestURI().contains("checkout.jsp")) {
                param = "?postAction=checkout.jsp";
            } else if (req.getRequestURI().contains("order.jsp")) {
                param = "?postAction=order.jsp";
            } else if (req.getRequestURI().contains("pending_orders.jsp")) {
                param = "?postAction=pending_orders.jsp";
            } else if (req.getRequestURI().contains("purchase_history.jsp")) {
                param = "?postAction=purchase_history.jsp";
            } else if (req.getRequestURI().contains("account.jsp")) {
                param = "?postAction=account.jsp";
            }
            if (!param.isEmpty()) {
                param += "&params=" + g.toJson(request.getParameterMap());
            }
            resp.sendRedirect("login.jsp" + param);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
