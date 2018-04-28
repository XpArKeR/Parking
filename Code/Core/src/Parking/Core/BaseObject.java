package Parking.Core;

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
    }
    
    private String instanceID;
    public String ID;
    
    public String GetInstanceID()
    {
        return this.instanceID;
    }
}
