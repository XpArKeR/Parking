/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL.Transactions;

import Parking.Core.EntityObject;
import Parking.Storage.Repository.MySQL.Utils;
import static Parking.Storage.Repository.MySQL.Utils.isClassCollection;
import Parking.Storage.TransactionParameters;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class Transaction 
{
    private Boolean canCommit = false;
    private Boolean isPrepared = false;
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
            this.isPrepared = true;
        }
        catch (SQLException sqlException)
        {
            
        }        
    }
    
    public void Save(EntityObject entityObject) throws IllegalStateException
    {
        this.Save(entityObject, null);
    }
    
    public void Save(EntityObject entityObject, TransactionParameters transactionParameters) throws IllegalStateException
    {
        if (!this.isPrepared)
        {
            throw new IllegalStateException("The Transaction MUST be prepared before Save() is called!");
        }
        
        Boolean isSavingCascade =false;
        
        if (transactionParameters != null)
        {
            isSavingCascade = transactionParameters.IsSavingCascade;
        }
        
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
        
        List<FieldValue> fieldValues = Utils.GetFieldValues(entityObject, entityClass);
        
        if (useUpdate)
        {
            if (this.PrepareUpdateStatement(entityObject, tableName, fieldValues))
            {
                this.canCommit = true;                
            }
        }
        else
        {            
            if (this.PrepareInsertStatement(entityObject, tableName, fieldValues))
            {
                this.canCommit = true;                
            }
        }
    }
    
    public Boolean Commit()
    {
        if ((this.isPrepared) && (this.canCommit))
        {
            try
            {
            this.connection.commit();
            }
            catch (SQLException sqlException)
            {
                try 
                {
                    System.err.print("Transaction is being rolled back");
                    this.connection.rollback();
                } 
                catch(SQLException rollbackException) 
                {
                    System.err.println(rollbackException);
                }
            }
        }
        
        return false;
    }
    
    private Boolean PrepareUpdateStatement(EntityObject entityObject, String tableName, List<FieldValue> fieldValues)
    {
        Boolean isSuccessful = false;
        
        StringBuilder commandBuilder = new StringBuilder();
        
        commandBuilder.append(String.format("Update %s SET ", tableName));
        
        ArrayList<Object> values = new ArrayList<Object>();
        
        for (Entry<String, Object> fieldValue : fieldValues.entrySet())
        {
            if (Utils.IsEligable(fieldValue.getKey()))
            {
                commandBuilder.append(String.format("%s = ?, ", fieldValue.getKey()));
                values.add(fieldValue.getValue());
            }
        }
        
        commandBuilder.deleteCharAt(commandBuilder.length() - 2);
        
        commandBuilder.append(String.format(" WHERE ID='?'", entityObject.ID));
        
        try
        {
            PreparedStatement preparedStatement = this.connection.prepareStatement(commandBuilder.toString());
            
            Integer counter = 1;
            
            for (Object value : fieldValues.entrySet())
            {
                preparedStatement.setObject(counter, value);
                counter++;
            }
            
            preparedStatement.setString(++counter, entityObject.ID);
            
            preparedStatement.executeUpdate();
            isSuccessful = true;
        }
        catch (SQLException exception)
        {
            
        }
        
        return isSuccessful;
    }
    
    private Boolean PrepareInsertStatement(EntityObject entityObject, String tableName, List<FieldValue> fieldValues)
    {
        Boolean isSuccessful = false;
        
        StringBuilder commandBuilder = new StringBuilder();
                        
        commandBuilder.append(String.format("INSERT INTO %s (", tableName));
        String values = "";
        
        for (FieldValue fieldValue : fieldValues)
        {
            if (!fieldValue.IsCollection)
            {                
                commandBuilder.append(String.format("%s, ", fieldValue.Key));
                values += String.format("%s, ", fieldValue.Value);
            }            
        }
        
        commandBuilder.deleteCharAt(commandBuilder.length() - 2);
        
        try
        {
            PreparedStatement preparedStatement = this.connection.prepareStatement(commandBuilder.toString());
            
            Integer counter = 1;
            
            for (Entry<String, Object> fieldValue : fieldValues.entrySet())
            {
                preparedStatement.setObject(counter, fieldValue.getValue());
                counter++;
            }
            
            preparedStatement.executeUpdate();            
            isSuccessful = true;
        }
        catch (SQLException exception)
        {
            
        }
        
        return isSuccessful;
    }
}
