package Parking.Core;

import java.util.Date;
import java.util.UUID;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author noldi
 */
public class BaseObject extends Object
{
    public BaseObject()
    {
        this.instanceID = UUID.randomUUID().toString();
        this.ModifiedOn = new Date();
    }
    
    private String instanceID;
    public String ID;
    public Date ModifiedOn;
    
    public String GetInstanceID()
    {
        return this.instanceID;
    }
    
    public void SetProperty(String propertyName, Object value)
    {
        switch (propertyName)
        {
            
            case "ID":
            case "Parking.Core.BaseObject.ID":
                this.ID = (String)value;
                break;
                
            case "ModifiedOn":
            case "Parking.Core.BaseObject.ModifiedOn":
                this.ModifiedOn = (Date)value;
        }
    }
}
