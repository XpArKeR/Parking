/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL;

import Parking.Core.BaseObject;
import Parking.Core.EntityObject;
import Parking.Storage.Repository.MySQL.Transaction;
import Parking.Storage.TransactionParameters;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

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
        
        String tableName = Utils.GetTableName(type);
        
        Connection connection = this.repository.GetConnection();
        
        try
        {
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
                    System.out.println(String.format("Exception whilst creating instance of type %s.", type.getName()));
                }                                
                
                if (returnValue != null)
                {
                    this.Populate(returnValue, type, resultSet);
                }
            }
            else
            {
                //System.out.println(String.format("Referenced Object not found: %s (%s).", reference, type.getName()));
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
        T returnValue = null;
        
        if (type == null)
        {
            throw new IllegalArgumentException("A Class must be provided!");
        }
        
        if (id.isEmpty())
        {
            return returnValue;
        }
        
        String tableName = Utils.GetTableName(type);
        
        Connection connection = this.repository.GetConnection();
        
        try
        {
            Statement statement = connection.createStatement();
            
            String query = String.format("SELECT * FROM %s WHERE ID='%s'", tableName, id);
            
            ResultSet resultSet = statement.executeQuery(query);
            
            if (resultSet.first())
            {
                try
                {
                    returnValue = (T)type.newInstance();                    
                }
                catch (Exception exception)
                {
                    System.out.println(String.format("Exception whilst creating instance of type %s.", type.getName()));
                }                                
                
                if (returnValue != null)
                {
                    this.Populate(returnValue, type, resultSet);
                }
            }
            else
            {
                System.out.println(String.format("Referenced Object not found: %s (%s).", id, type.getName()));
            }
        }
        catch (SQLException exception)
        {            
            System.out.println(exception);
        }
        
        return returnValue;
    }
    
    protected void Populate(BaseObject baseObject,Class type, ResultSet resultSet)
    {
        if (baseObject != null)
        {
            try
            {
                HashMap<String, BaseObject> loadedObjects = new HashMap<String, BaseObject>();

                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                for (int counter = 1; counter < resultSetMetaData.getColumnCount(); counter++)
                {
                    String columnName = resultSetMetaData.getColumnName(counter);

                    Field field = null;

                    try
                    {
                        field = type.getField(columnName);                                              

                        if (EntityObject.class.isAssignableFrom(field.getType()))
                        {
                            String referencedObjectID = resultSet.getString(counter);

                            if (!referencedObjectID.isEmpty())
                            {
                                EntityObject referencedObject = null;

                                if (loadedObjects.containsKey(referencedObjectID))
                                {
                                    referencedObject = (EntityObject)loadedObjects.get(referencedObjectID);
                                }
                                else
                                {
                                    referencedObject = this.GetByID(referencedObjectID, field.getType());
                                    
                                    if (referencedObject != null)
                                    {
                                        loadedObjects.put(referencedObjectID, referencedObject);
                                    }
                                }

                                if (referencedObject != null)
                                {
                                    field.set(baseObject, referencedObject);                                
                                }
                            }
                        }
                        else if (Utils.IsClassCollection(field.getType()))
                        {
                            Class collectionType = field.getType().getComponentType();
                            
                            
                        }
                        else
                        {
                            Object value = resultSet.getObject(counter);

                            if ("ID".equals(columnName))
                            {
                                if (!loadedObjects.containsKey((String)value))
                                {
                                    loadedObjects.put((String)value, baseObject);
                                }
                            }

                            field.set(baseObject, value);
                        }
                    }
                    catch (Exception exception)
                    {
                        System.out.println("Exception when converting Data from database...");
                    }
                }
            }
            catch (SQLException exception)
            {            
                System.out.println(exception);
            }
        }   
    }
    
    public <T extends BaseObject> ArrayList<T> Search(Class type) 
    {
        return null;
    }
           
    public Boolean Save(EntityObject entityObject, TransactionParameters transactionParameters) 
    {
        Boolean isSuccessful = false;
                                
        Transaction transaction = new Transaction(this.repository, transactionParameters);
        transaction.Prepare();
        transaction.Save(entityObject);
        
        if (transaction.Commit())
        {
            isSuccessful = true;
        }
        
        return isSuccessful;
    }
}
