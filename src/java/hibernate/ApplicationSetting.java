package hibernate;
// Generated Mar 10, 2021 4:50:38 PM by Hibernate Tools 4.3.1



/**
 * ApplicationSetting generated by hbm2java
 */
public class ApplicationSetting  implements java.io.Serializable {


     private String name;
     private String value;

    public ApplicationSetting() {
    }

	
    public ApplicationSetting(String name) {
        this.name = name;
    }
    public ApplicationSetting(String name, String value) {
       this.name = name;
       this.value = value;
    }
   
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }




}


