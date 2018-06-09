/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL;

import Parking.Core.BaseObject;
import Parking.Core.EntityObject;
import Parking.Storage.QueryParameters;
import Parking.Storage.TransactionParameters;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class MySQLLoader 
{
    private MySQLRepository repository;
    private HashMap<String, BaseObject> loadedObjects;
 
    public MySQLLoader(MySQLRepository repository)
    {
        this.repository = repository;
        this.loadedObjects = new HashMap<>();
    }
    
    public <T extends BaseObject> T Get(String reference, Class type)
    {
        T returnValue = null;
        
        if (type == null)
        {
            throw new IllegalArgumentException("A Class must be provided!");
        }
        
        if (!this.repository.CheckType(type))
        {
            throw new IllegalArgumentException(String.format("Critical Error. Type %s can't be initialized!", type.getName()));
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
                    this.Populate(returnValue, type, resultSet, connection);
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
        
        if (!this.repository.CheckType(type))
        {
            throw new IllegalArgumentException(String.format("Critical Error. Type %s can't be initialized!", type.getName()));
        }
        
        if (id.isEmpty())
        {
            return returnValue;
        }
        
        if (!this.loadedObjects.containsKey(id))
        {
            String tableName = Utils.GetTableName(type);

            Connection connection = this.repository.GetConnection();

            try
            {
                Statement statement = connection.createStatement();

                String query = String.format("SELECT * FROM %s WHERE ID='%s' LIMIT 1", tableName, id);

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
                        this.Populate(returnValue, type, resultSet, connection);
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
        }
        else
        {
            returnValue = (T)this.loadedObjects.get(id);
        }
        
        return returnValue;
    }
    
    protected void Populate(BaseObject baseObject,Class type, ResultSet resultSet, Connection connection)
    {
        if (baseObject != null)
        {
            try
            {
                baseObject.ID = resultSet.getString("ID");
                
                if (!loadedObjects.containsKey(baseObject.ID))
                {
                    loadedObjects.put(baseObject.ID, baseObject);
                }
                               
                for (Field field : type.getFields())
                {
                    if (!"ID".equals(field.getName()))
                    {
                        if (Utils.IsClassCollection(field.getType()))
                        {
                            ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
                            Class<?> listGenericType = (Class<?>) fieldType.getActualTypeArguments()[0];

                            if (EntityObject.class.isAssignableFrom(listGenericType))
                            {
                                this.LoadListField(baseObject, field, listGenericType, connection);
                            }                            
                        }
                        else if (EntityObject.class.isAssignableFrom(field.getType()))
                        {
                            String referencedObjectID = resultSet.getString(field.getName());

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
                                    try
                                    {
                                        field.set(baseObject, referencedObject);                                
                                    }
                                    catch (IllegalAccessException exception)
                                    {

                                    }
                                }
                            }
                        }
                        else
                        {
                            Object value = resultSet.getObject(field.getName());

                            try
                            {
                                field.set(baseObject, value);
                            }
                            catch (IllegalAccessException exception)
                            {

                            }
                        }
                    }
                }
            }
            catch (SQLException exception)
            {            
                System.out.println(exception);
            }
        }   
    }
    
    private void LoadListField(BaseObject baseObject, Field field, Class entityType, Connection connection)
    {        
        if (!this.repository.CheckType(baseObject, field))
        {
            throw new IllegalArgumentException(String.format("Critical Error. Type %s-%s can't be initialized!", baseObject.getClass().getName(), field.getName()));
        }
        
        
    }
        
    public <T extends BaseObject> ArrayList<T> Search(Class type, QueryParameters queryParameters) 
    {
        if (!this.repository.CheckType(type))
        {
            throw new IllegalArgumentException(String.format("Critical Error. Type %s can't be initialized!", type.getName()));
        }
                        
        return null;
    }
               
    public Boolean Save(EntityObject entityObject, TransactionParameters transactionParameters) 
    {        
        Boolean isSuccessful = false;
     
        Class type = entityObject.getClass();
        
        if (!this.repository.CheckType(type))
        {
            throw new IllegalArgumentException(String.format("Critical Error. Type %s can't be initialized!", type.getName()));
        }
        
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
