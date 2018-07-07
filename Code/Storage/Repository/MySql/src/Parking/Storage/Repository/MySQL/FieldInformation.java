/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL;

public class FieldInformation {

    public FieldInformation() {

    }

    public FieldInformation(String name, Class type, Boolean isCollection, Boolean isNullable, Boolean isPrimary) {
        this.Name = name;
        this.Type = type;
        this.IsCollection = isCollection;
        this.IsNullable = isNullable;
        this.IsPrimary = isPrimary;
    }

    private String Name;
    private Class Type;
    private Boolean IsCollection;
    private Boolean IsNullable;
    private Boolean IsPrimary;

    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @param Name the Name to set
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * @return the Type
     */
    public Class getType() {
        return this.Type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Class type) {
        this.Type = type;
    }

    /**
     * @return the IsCollection
     */
    public Boolean getIsCollection() {
        return IsCollection;
    }

    /**
     * @param IsCollection the IsCollection to set
     */
    public void setIsCollection(Boolean IsCollection) {
        this.IsCollection = IsCollection;
    }

    /**
     * @return the IsNullable
     */
    public Boolean getIsNullable() {
        return IsNullable;
    }

    /**
     * @param IsNullable the IsNullable to set
     */
    public void setIsNullable(Boolean IsNullable) {
        this.IsNullable = IsNullable;
    }

    /**
     * @return the IsPrimary
     */
    public Boolean getIsPrimary() {
        return IsPrimary;
    }

    /**
     * @param isPrimary Indicates whether the field is the primary key
     */
    public void setIsPrimary(Boolean isPrimary) {
        this.IsPrimary = isPrimary;
    }
    
    @Override
    public String toString()
    {
        return this.getName();
    }
}
