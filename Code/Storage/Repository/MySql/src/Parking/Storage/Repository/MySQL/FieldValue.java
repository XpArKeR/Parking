/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL;

import java.util.Collection;

public class FieldValue {

    public FieldValue(String key, Object value, Boolean isCollection, Boolean isReferencedObject) {
        this.Key = key;
        this.Value = value;
        this.IsCollection = isCollection;
        this.IsReferencedObject = isReferencedObject;
    }

    private String Key;
    private Boolean IsCollection = false;
    private Boolean IsReferencedObject = false;
    private Object Value;

    @Override
    public String toString() {
        return String.format("%s (%s / %s): %s", this.getKey(), this.getIsCollection(), this.getIsReferencedObject(), this.getValue());
    }

    /**
     * @return the Key
     */
    public String getKey() {
        return Key;
    }

    /**
     * @param Key the Key to set
     */
    public void setKey(String Key) {
        this.Key = Key;
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
     * @return the IsReferencedObject
     */
    public Boolean getIsReferencedObject() {
        return IsReferencedObject;
    }

    /**
     * @param IsReferencedObject the IsReferencedObject to set
     */
    public void setIsReferencedObject(Boolean IsReferencedObject) {
        this.IsReferencedObject = IsReferencedObject;
    }

    /**
     * @return the Value
     */
    public Object getValue() {
        return Value;
    }

    /**
     * @param Value the Value to set
     */
    public void setValue(Object Value) {
        this.Value = Value;
    }
}
