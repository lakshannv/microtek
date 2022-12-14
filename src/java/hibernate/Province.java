package hibernate;
// Generated Mar 10, 2021 4:50:38 PM by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Province generated by hbm2java
 */
public class Province  implements java.io.Serializable {


     private Integer id;
     private String name;
     private Set districts = new HashSet(0);
     private Set shippingAddresses = new HashSet(0);

    public Province() {
    }

	
    public Province(String name) {
        this.name = name;
    }
    public Province(String name, Set districts, Set shippingAddresses) {
       this.name = name;
       this.districts = districts;
       this.shippingAddresses = shippingAddresses;
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
    public Set getDistricts() {
        return this.districts;
    }
    
    public void setDistricts(Set districts) {
        this.districts = districts;
    }
    public Set getShippingAddresses() {
        return this.shippingAddresses;
    }
    
    public void setShippingAddresses(Set shippingAddresses) {
        this.shippingAddresses = shippingAddresses;
    }




}


