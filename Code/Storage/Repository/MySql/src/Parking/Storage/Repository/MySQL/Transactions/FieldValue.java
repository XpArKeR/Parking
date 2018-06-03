/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL.Transactions;

import java.util.Collection;

/**
 *
 * @author noldi
 */
public class FieldValue 
{    
    public FieldValue(String key, Object value, Boolean isCollection, Boolean isReferencedObject)
    {
        this.Key = key;
        this.Value = value;
        this.IsCollection = isCollection;        
        this.IsReferencedObject = isReferencedObject;
    }
    
    public String Key;
    public Boolean IsCollection = false;    
    public Boolean IsReferencedObject = false;
    public Object Value;    
            
    @Override
    public String toString()
    {
        return String.format("%s (%s / %s): %s", this.Key, this.IsCollection, this.IsReferencedObject, this.Value);
    }
}
