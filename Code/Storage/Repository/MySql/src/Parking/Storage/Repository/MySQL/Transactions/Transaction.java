/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL.Transactions;

import Parking.Core.EntityObject;
import Parking.Storage.Repository.MySQL.Utils;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author noldi
 */
public class Transaction 
{
    private Connection connection;
    private ArrayList<PreparedStatement> statements;
    
    public Transaction(Connection connection)
    {
        this.connection = connection;
        this.statements = new ArrayList<>();
    }
    
    public void Prepare()
    {
        try
        {
            this.connection.setAutoCommit(false);
        }
        catch (SQLException sqlException)
        {
            
        }        
    }
    
    public void Save(EntityObject entityObject)
    {
        Boolean useUpdate = false;
        
        Class entityClass = entityObject.getClass();
        
        String tableName = Utils.GetTableName(entityClass);
        
        try
        {
            Statement statement = connection.createStatement();
            
            ResultSet resultSet = statement.executeQuery(String.format("SELECT COUNT(ID) FROM %s WHERE ID='%s'", tableName, entityObject.ID));
            
            if (resultSet.first())
            {
                if (resultSet.getInt(1) > 0)
                {
                    useUpdate = true;
                }                
            }
        }
        catch (SQLException exception)
        {            
            System.out.println(exception);
        }
        
        Map<String, Object> fieldValues = Utils.GetFieldValues(entityObject);
        
        if (useUpdate)
        {
            
        }
        else
        {
            
        }
    }
    
    public Boolean Commit()
    {
        return false;
    }
}
