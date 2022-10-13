<%@page import="hibernate.User"%>
<%@page import="java.util.ArrayList"%>
<%@page import="hibernate.UserPrivilege"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.List"%>
<%@page import="hibernate.UserType"%>
<%@page import="org.hibernate.Criteria"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="org.hibernate.Session"%>
<%@page import="hibernate.HiberUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    SessionFactory sf = HiberUtil.getSessionFactory();
    Session s = sf.openSession();
    Criteria usrTypeCR = s.createCriteria(UserType.class);
    List<UserType> userTypeList = usrTypeCR.list();
    Gson g = new Gson();
    
    User sesUsr = (User) session.getAttribute("usr");
    User usr = (User) s.load(User.class, sesUsr.getId());

    for (UserType uType : userTypeList) {
        if (uType.getId() != 1) {
            ArrayList<Integer> prvList = new ArrayList();
            for (Iterator<UserPrivilege> it = uType.getUserPrivileges().iterator(); it.hasNext();) {
                UserPrivilege prv = it.next();
                prvList.add(prv.getId());
            }
            String dis = "";
            if (!uType.getUsers().isEmpty()) {
                dis = " disabled=\"\"";
            }
            String disUT = "";
            if (uType.getId() == usr.getUserType().getId()) {
                disUT = " disabled=\"\"";
            }
%>
<tr id="ut-row-<%=uType.getId()%>">
    <td><%=uType.getName()%></td>
    <td><%=uType.getUsers().size()%></td>
    <td>
        <button class="btn btn-primary" type="button" onclick="showDeleteUserTypePopup(<%=uType.getId()%>);"<%=dis%>><i class="far fa-trash-alt"></i>Remove</button>
        <button class="btn btn-primary" type="button" onclick="showUpdateUserTypePopup(<%=uType.getId()%>);"<%=disUT%>><i class="fas fa-user-edit"></i>Edit</button>
    </td>
    <td style="display: none;"><%=g.toJson(prvList)%></td>
</tr>
<%
        }
    }
    s.close();
%>
