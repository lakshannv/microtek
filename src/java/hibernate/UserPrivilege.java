package hibernate;
// Generated Mar 10, 2021 4:50:38 PM by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * UserPrivilege generated by hbm2java
 */
public class UserPrivilege  implements java.io.Serializable {


     private Integer id;
     private String name;
     private Set userTypes = new HashSet(0);

    public UserPrivilege() {
    }

    public UserPrivilege(String name, Set userTypes) {
       this.name = name;
       this.userTypes = userTypes;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public Set getUserTypes() {
        return this.userTypes;
    }
    
    public void setUserTypes(Set userTypes) {
        this.userTypes = userTypes;
    }




}

