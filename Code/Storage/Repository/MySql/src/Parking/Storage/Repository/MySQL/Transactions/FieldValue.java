/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL.Transactions;

/**
 *
 * @author noldi
 */
public class FieldValue 
{
    public FieldValue(String key, Object value, Boolean isCollection)
    {
        this.Key = key;
        this.Value = value;
        this.IsCollection = isCollection;
    }
    
    public String Key;
    public Boolean IsCollection = false;
    public Object Value;    
}
