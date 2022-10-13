package controller;

import com.google.gson.Gson;
import hibernate.HiberUtil;
import hibernate.User;
import hibernate.UserPrivilege;
import hibernate.UserType;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AdminAppGetUserTypeMngInitData", urlPatterns = {"/AdminAppGetUserTypeMngInitData"})
public class AdminAppGetUserTypeMngInitData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        LinkedList<LinkedHashMap<String, Object>> usrTypeList = new LinkedList();
        Gson g = new Gson();

        String userID = request.getParameter("userID");
        User usr = (User) s.load(User.class, Integer.parseInt(userID));

        Criteria usrTypeCR = s.createCriteria(UserType.class);
        usrTypeCR.add(Restrictions.ne("id", 1));
        List<UserType> userTypeList = usrTypeCR.list();
        for (UserType uType : userTypeList) {

            ArrayList<String> prvList = new ArrayList();
            for (Iterator<UserPrivilege> it = uType.getUserPrivileges().iterator(); it.hasNext();) {
                UserPrivilege prv = it.next();
                prvList.add(String.valueOf(prv.getId()));
            }

            LinkedHashMap<String, Object> userType = new LinkedHashMap();

            userType.put("id", String.valueOf(uType.getId()));
            userType.put("name", uType.getName());
            userType.put("userCount", String.valueOf(uType.getUsers().size()));
            userType.put("prvList", prvList);
            userType.put("allowEdit", uType.getId() != usr.getUserType().getId());
            userType.put("allowDelete", uType.getUsers().isEmpty());
            usrTypeList.add(userType);
        }

        responseData.put("userTypeList", usrTypeList);
        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
