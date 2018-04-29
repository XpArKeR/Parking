/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL;

import Parking.Core.BaseObject;
import Parking.Core.EntityObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        Boolean isSuccessful = false;
        
        String tableName = Utils.GetTableName(entityObject.getClass().getName());
        
        Boolean useUpdate = false;
        
        Connection connection = this.repository.GetConnection();
        
        try{
            Statement statement = connection.createStatement();
            
            ResultSet resultSet = statement.executeQuery(String.format("SELECT COUNT(*) FROM %s WHERE Reference='%s'", tableName, entityObject.Reference));
            
            if (resultSet.first())
            {
                useUpdate = true;
            }
        }
        catch (SQLException exception)
        {            
        }
            
        StringBuilder commandBuilder = new StringBuilder();
        
        if (!useUpdate) {
            
        }
        else {
            
        }
        
        String command = commandBuilder.toString();
        
        if (!command.isEmpty()) {
            try{
            Statement statement = connection.createStatement();
            
            Boolean isResultSet = statement.execute(command);
            
            if (isResultSet) {
                ResultSet resultSet = statement.getResultSet();
            }
            else {
                
            }
        }
        catch (SQLException exception)
        {            
        }
        }
        
        return isSuccessful;
    }
}
