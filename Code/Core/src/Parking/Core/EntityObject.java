/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Core;

/**
 *
 * @author noldi
 */
public class EntityObject extends BaseObject
{
    public String Reference;
    
    public Boolean GenerateReference()
    {
        return false;
    }
    
    public Boolean Save()
    {        
        Parking.Storage.Operator storageOperator = Parking.Base.Storage.GetOperator();
        
        if (storageOperator != null)
        {
            return storageOperator.Save(this);
        }
        
        return false;
    }
    
    @Override
    public void SetProperty(String propertyName, Object value)
    {
        switch (propertyName)
        {
            
            case "Refernce":
            case "Parking.Core.EntityObject.Refernce":
                this.Reference = (String)value;
                
            default:
                super.SetProperty(propertyName, value);
        }
    }
}
