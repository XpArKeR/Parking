/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL;

import Parking.Core.BaseObject;
import Parking.Core.EntityObject;
import java.util.ArrayList;

/**
 *
 * @author noldi
 */
public class MySQLLoader 
{
    private MySQLRepository repository;
 
    public MySQLLoader(MySQLRepository repository)
    {
        this.repository = repository;
    }
    
    public <T extends BaseObject> T Get(String reference, Class type)
    {
        return null;
    }
    
    public <T extends BaseObject> T GetByID(String id, Class type) 
    {
        return null;
    }
    
    public <T extends BaseObject> ArrayList<T> Search(Class type) 
    {
        return null;
    }
    
    public Boolean Save(EntityObject entityObject) 
    {
        return true;
    }
}
