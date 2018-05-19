/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL;

import Parking.Core.BaseObject;
import Parking.Core.EntityObject;
import static Parking.Storage.Repository.MySQL.Utils.isClassCollection;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
        T returnValue = null;
        
        if (type == null)
        {
            throw new IllegalArgumentException("A Class must be provided!");
        }
        
        if ((reference == null) ||(reference.isEmpty()))
        {
            return returnValue;
        }
        
        String tableName = Utils.GetTableName(type.getName());
        
        Connection connection = this.repository.GetConnection();
        
        try{
            Statement statement = connection.createStatement();
            
            String query = String.format("SELECT * FROM %s WHERE Reference='%s'", tableName, reference);
            
            ResultSet resultSet = statement.executeQuery(query);
            
            if (resultSet.first())
            {
                try
                {
                    returnValue = (T)type.newInstance();
                }
                catch (Exception exception)
                {
                    
                }
                
                if (returnValue != null)
                {
                    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                    for (int counter = 1; counter < resultSetMetaData.getColumnCount(); counter++)
                    {
                        String typeName = resultSetMetaData.getColumnTypeName(counter);

                        switch (typeName)
                        {
                            case "VARCHAR":                                
                                returnValue.SetProperty(resultSetMetaData.getColumnName(counter), resultSet.getString(counter));
                                break;
                                
                            default:
                                System.out.println(String.format("unsupport sql type... %s", typeName));
                        }
                    }
                }
            }
        }
        catch (SQLException exception)
        {            
            System.out.println(exception);
        }
        
        
        return returnValue;
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
        
        Class entityClass = entityObject.getClass();
        
        String tableName = Utils.GetTableName(entityClass.getName());
        
        Boolean useUpdate = false;
        
        Connection connection = this.repository.GetConnection();
        
        try{
            Statement statement = connection.createStatement();
            
            ResultSet resultSet = statement.executeQuery(String.format("SELECT COUNT(*) FROM %s WHERE Reference='%s'", tableName, entityObject.Reference));
            
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
            
        StringBuilder commandBuilder = new StringBuilder();
        
        if (!useUpdate) {                        
            commandBuilder.append(String.format("INSERT INTO %s (", tableName));
            StringBuilder valuesBuilder = new StringBuilder();            
            
            for (Field field : entityClass.getFields()){                
                if (!isClassCollection(field.getType())) {
                    commandBuilder.append(String.format("%s, ", field.getName()));                        
                    valuesBuilder.append(String.format("%s, ", Utils.GetValue(entityObject, field)));
                }
            }
            
            commandBuilder.deleteCharAt(commandBuilder.length() - 2);
            valuesBuilder.deleteCharAt(valuesBuilder.length() - 2);
            
            commandBuilder.append(String.format(") VALUES (%s)", valuesBuilder.toString()));
        }
        else {
            commandBuilder.append(String.format("UPDATE %s SET ", tableName));
                        
            for (Field field : entityClass.getFields())
            {
                if (Utils.IsEligable(field))
                {
                    if (!isClassCollection(field.getType())) 
                    {
                        commandBuilder.append(String.format("%s=%s, ",field.getName(), Utils.GetValue(entityObject, field)));
                    }
                }
            }
            
            commandBuilder.deleteCharAt(commandBuilder.length() - 2);
            
            commandBuilder.append(String.format(" WHERE Reference='%s'", entityObject.Reference));
        }
        
        String command = commandBuilder.toString();
        
        if (!command.isEmpty()) {
            try
            {
                Statement statement = connection.createStatement();

                Boolean isResultSet = statement.execute(command);

                if (isResultSet) 
                {
                    ResultSet resultSet = statement.getResultSet();
                }
                else 
                {

                }
                
                isSuccessful = true;
            }
            catch (SQLException exception)
            {    
                System.out.println(exception);
            }
        }
        
        return isSuccessful;
    }
}
