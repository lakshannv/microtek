package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "CheckOTP", urlPatterns = {"/CheckOTP"})
public class CheckOTP extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String otp = request.getParameter("otp");
        try {
            if (otp.isEmpty()) {
                response.getWriter().write("emp");
            } else {
                String sesOTP = (String) request.getSession().getAttribute("otp");
                if (sesOTP == null) {
                    response.getWriter().write("inv");
                } else {
                    if (sesOTP.equals(otp)) {
                        response.getWriter().write("ok");
                    } else {
                        response.getWriter().write("inv");
                    }                    
                }
            }
        } catch (Exception e) {
            response.getWriter().write("err");
            e.printStackTrace();
        }
    }

}
