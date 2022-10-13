package hibernate;
// Generated Mar 10, 2021 4:50:38 PM by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Spec generated by hbm2java
 */
public class Spec  implements java.io.Serializable {


     private Integer id;
     private Category category;
     private String specName;
     private String unit;
     private Byte isKey;
     private Set productHasSpecs = new HashSet(0);

    public Spec() {
    }

	
    public Spec(Category category) {
        this.category = category;
    }
    public Spec(Category category, String specName, String unit, Byte isKey, Set productHasSpecs) {
       this.category = category;
       this.specName = specName;
       this.unit = unit;
       this.isKey = isKey;
       this.productHasSpecs = productHasSpecs;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Category getCategory() {
        return this.category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    public String getSpecName() {
        return this.specName;
    }
    
    public void setSpecName(String specName) {
        this.specName = specName;
    }
    public String getUnit() {
        return this.unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public Byte getIsKey() {
        return this.isKey;
    }
    
    public void setIsKey(Byte isKey) {
        this.isKey = isKey;
    }
    public Set getProductHasSpecs() {
        return this.productHasSpecs;
    }
    
    public void setProductHasSpecs(Set productHasSpecs) {
        this.productHasSpecs = productHasSpecs;
    }




}

