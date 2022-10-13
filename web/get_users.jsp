<%@page import="java.util.List"%>
<%@page import="hibernate.UserType"%>
<%@page import="hibernate.User"%>
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
    
    User sesUsr = (User) session.getAttribute("usr");
    User usr = (User) s.load(User.class, sesUsr.getId());
    
    Criteria usrCR = s.createCriteria(User.class);
    List<User> userList = usrCR.list();
    if (usr.getUserType().getId() != 1) {
        userList.remove(0);
    }

    for (User u : userList) {
        String dis = "";
        if (u.getUserType().getId() == 1 || u.getId() == usr.getId()) {
            dis = " disabled=\"\"";
        }
%>
<tr>
    <td><input type="text" value="<%=u.getUsername()%>" id="usr-txtbox-<%=u.getId()%>" oninput="enableBtn(<%=u.getId()%>);"></td>
    <td class="d-flex">
        <input type="password" value="<%=u.getPassword()%>" id="usr-pwbox-<%=u.getId()%>" oninput="enableBtn(<%=u.getId()%>);">
        <button class="btn btn-primary" type="button" title="Show/Hide Password" onclick="togglePassWord(<%=u.getId()%>)" id="pw-btn-<%=u.getId()%>"><i class="far fa-eye"></i></button>
    </td>
    <td>
        <select id="cmb-<%=u.getId()%>" onchange="enableBtn(<%=u.getId()%>);"<%=dis%>>
            <%
                for (UserType usrType : userTypeList) {
                    if (u.getUserType().getId() == 1) {
            %>
            <option value="<%=usrType.getId()%>" selected=""><%=usrType.getName()%></option>
            <%
                break;
            } else {
                if (usrType.getId() != 1) {
                    String sel = "";
                    if (u.getUserType().getId() == usrType.getId()) {
                        sel = " selected=\"\"";
                    }
            %>
            <option value="<%=usrType.getId()%>"<%=sel%>><%=usrType.getName()%></option>
            <%
                        }
                    }
                }
            %>
        </select>
    </td>
    <td>
        <button class="btn btn-primary" type="button" onclick="showDeleteUserPopup(<%=u.getId()%>);"<%=dis%>><i class="far fa-trash-alt"></i>Remove</button>
        <button class="btn btn-primary" type="button" onclick="blockUser(<%=u.getId()%>)"<%=dis%> id="usr-block-btn-<%=u.getId()%>" isActive="<%=u.getActive() %>">
            <%
                if (u.getActive() == 1) {
            %>
            <i class="fas fa-ban"></i>Block
            <%
                } else {
            %>
            <i class="fas fa-chevron-circle-up"></i></i>Unblock
            <%
                }
            %>
        </button>
        <button class="btn btn-primary" type="button" disabled="" id="usr-save-btn-<%=u.getId()%>" onclick="updateUser(<%=u.getId()%>)"><i class="far fa-save"></i>Save</button>
    </td>
</tr>
<%
    }
    s.close();
%>
