package controller;

import com.google.gson.Gson;
import hibernate.HiberUtil;
import hibernate.User;
import hibernate.UserType;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
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

@WebServlet(name = "AdminAppGetUserMngInitData", urlPatterns = {"/AdminAppGetUserMngInitData"})
public class AdminAppGetUserMngInitData extends HttpServlet {

    DecimalFormat df = new DecimalFormat("#,##0.##");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();
        HashMap<String, Object> responseData = new HashMap();
        LinkedList<LinkedHashMap<String, Object>> usrList = new LinkedList();
        LinkedHashMap<String, String> userTypeMap = new LinkedHashMap();
        LinkedList<String> userTypeNameList = new LinkedList();
        Gson g = new Gson();

        String userID = request.getParameter("userID");

        Criteria usrTypeCR = s.createCriteria(UserType.class);
        usrTypeCR.add(Restrictions.ne("id", 1));
        List<UserType> userTypeList = usrTypeCR.list();
        for (UserType usrType : userTypeList) {
            userTypeMap.put(String.valueOf(usrType.getId()), usrType.getName());
            userTypeNameList.add(usrType.getName());
        }

        User usr = (User) s.load(User.class, Integer.parseInt(userID));

        Criteria usrCR = s.createCriteria(User.class);
        List<User> userList = usrCR.list();
        if (usr.getUserType().getId() != 1) {
            userList.remove(0);
        }

        for (User u : userList) {
            LinkedHashMap<String, Object> user = new LinkedHashMap();
            user.put("id", String.valueOf(u.getId()));
            user.put("userTypeID", String.valueOf(u.getUserType().getId()));
            user.put("userTypeName", u.getUserType().getName());
            user.put("un", u.getUsername());
            user.put("pw", u.getPassword());
            user.put("status", u.getActive() == (byte) 1);
            usrList.add(user);
        }

        responseData.put("userList", usrList);
        responseData.put("userTypeMap", userTypeMap);
        responseData.put("userTypeNameList", userTypeNameList);
        response.getWriter().write(g.toJson(responseData));
        s.close();
    }

}
