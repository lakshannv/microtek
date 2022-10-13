package controller;

import com.google.gson.Gson;
import hibernate.ApplicationSetting;
import hibernate.HiberUtil;
import hibernate.User;
import hibernate.UserPrivilege;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AdminAppGetInitData", urlPatterns = {"/AdminAppGetInitData"})
public class AdminAppGetInitData extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HashMap<String, Object> initDataMap = new HashMap();

        String un = request.getParameter("un");
        String pw = request.getParameter("pw");
        SessionFactory sf = HiberUtil.getSessionFactory();
        Session s = sf.openSession();

        Gson g = new Gson();

        if (un != null && pw != null) {
            Criteria custCR = s.createCriteria(User.class);
            custCR.add(Restrictions.eq("username", un));
            if (custCR.list().size() == 0) {
                initDataMap.put("status", "reset");
            } else {
                custCR.add(Restrictions.eq("password", pw));
                User u = (User) custCR.uniqueResult();
                if (u == null) {
                    initDataMap.put("status", "reset");
                } else {
                    if (u.getPassword().equals(pw)) {
                        if (u.getActive() == (byte) 1) {
                            
                            ArrayList<Integer> userPrivilegeList = new ArrayList();
                            Set usrTypeSet = u.getUserType().getUserPrivileges();
                            for (Iterator<UserPrivilege> iterator = usrTypeSet.iterator(); iterator.hasNext();) {
                                UserPrivilege userPrivilege = iterator.next();
                                userPrivilegeList.add(userPrivilege.getId());
                            }


                            HashMap<String, Object> usr = new HashMap();
                            usr.put("id", u.getId());
                            usr.put("username", u.getUsername());
                            usr.put("password", u.getPassword());
                            usr.put("active", u.getActive());
                            usr.put("fcmToken", u.getFcmToken());
                            usr.put("userTypeName", u.getUserType().getName());
                            usr.put("userTypeID", u.getUserType().getId());
                            usr.put("userPrivilegeList", userPrivilegeList);

                            initDataMap.put("user", g.toJson(usr));
                        } else {
                            initDataMap.put("status", "blocked");
                        }

                    } else {
                        initDataMap.put("status", "reset");
                    }
                }
            }
        }
        
        String fcmServerKey = ((ApplicationSetting) s.load(ApplicationSetting.class, "fcm_server_key")).getValue();
        
        initDataMap.put("fcmServerKey", fcmServerKey);
        response.getWriter().write(g.toJson(initDataMap));

        s.close();
    }

}
