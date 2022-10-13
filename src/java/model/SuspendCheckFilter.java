package model;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SuspendCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletContext sc = request.getServletContext();
        if (sc.getAttribute("remainingTime") == null) {
            chain.doFilter(request, response);
        } else {
            long remainingTime = (long) sc.getAttribute("remainingTime");
            if (remainingTime > 0) {
                request.getRequestDispatcher("um.jsp?remainingTime=" + remainingTime).forward(request, response);
            } else if (remainingTime <= -99) {
                request.getRequestDispatcher("um.jsp").forward(request, response);
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

}
