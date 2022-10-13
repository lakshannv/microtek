package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hibernate.HiberUtil;
import hibernate.UserPrivilege;
import hibernate.UserType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "UpdateUserType", urlPatterns = {"/UpdateUserType"})
public class UpdateUserType extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        try {
            int uTypeID = Integer.parseInt(request.getParameter("uTypeID"));
            if (uTypeID == 1) {
                response.getWriter().write("err");
            } else {
                String typeName = request.getParameter("typeName").trim();
            if (Validation.isValidName(typeName)) {
                typeName = Validation.getValidatedName(typeName);

                Criteria uTypeCR = s.createCriteria(UserType.class);
                uTypeCR.add(Restrictions.eq("name", typeName));

                UserType usrType = (UserType) s.load(UserType.class, uTypeID);

                boolean proceed = false;
                if (uTypeCR.list().isEmpty()) {
                    proceed = true;
                } else {
                    if (usrType.getName().equals(typeName)) {
                        proceed = true;
                    }
                }

                if (proceed) {
                    Gson g = new Gson();
                    TypeToken tt = new TypeToken<ArrayList<Integer>>() {
                    };
                    Set<UserPrivilege> prvSet = new LinkedHashSet();
                    ArrayList<Integer> prvList = g.fromJson(request.getParameter("prvData"), tt.getType());
                    if (prvList.isEmpty()) {
                        response.getWriter().write("prv");
                    } else {
                        for (Integer prvID : prvList) {
                            prvSet.add((UserPrivilege) s.load(UserPrivilege.class, prvID));
                        }

                        usrType.setName(typeName);
                        usrType.setUserPrivileges(prvSet);
                        s.update(usrType);
                        s.beginTransaction().commit();
                        response.getWriter().write("ok");
                    }
                } else {
                    response.getWriter().write("dup");
                }
            } else {
                response.getWriter().write("inv");
            }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("err");
        }
        s.close();
    }

}
