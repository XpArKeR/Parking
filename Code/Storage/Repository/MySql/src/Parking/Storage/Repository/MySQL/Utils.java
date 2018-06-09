/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL;

import Parking.Core.BaseObject;
import Parking.Core.EntityObject;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author noldi
 */
public class Utils {
    private static HashMap<Integer, java.sql.Types> sqlTypesCache = new HashMap<Integer, java.sql.Types>();
    
    public static Boolean CreateTable(MySQLRepository repository, Class type, Class baseType)
    {
        Boolean isSuccessful = false;
        
        StringBuilder commandBuilder = new StringBuilder();
                        
        Parking.Base.Utils.AppendLine(commandBuilder, String.format("CREATE TABLE %s", GetTableName(baseType)));
        
        Parking.Base.Utils.AppendLine(commandBuilder, "(ID varchar(36) NOT NULL PRIMARY KEY");
        Parking.Base.Utils.AppendLine(commandBuilder, ", Reference varchar(255) NOT NULL");
        Parking.Base.Utils.AppendLine(commandBuilder, ", ModifiedOn DateTime");
        
        List<String> fields = new ArrayList<>();
        
        for(Field field : type.getFields())
        {                                                
            if (IsEligable(fields, field)) {
                AddProperty(commandBuilder, field);
                fields.add(field.getName());
            }
        }
                
        Parking.Base.Utils.AppendLine(commandBuilder, ");");
        
        Connection connection = repository.GetConnection();
        
        try{
            PreparedStatement statement = connection.prepareStatement(commandBuilder.toString());
            // Result set get the result of the SQL query
            statement.executeUpdate();
            
            isSuccessful = true;
        }
        catch (SQLException exception)
        {    
            exception.printStackTrace();
        }
        
        return isSuccessful;
    }
    
    public static Boolean UpdateTable(MySQLRepository repository, Class baseClass)
    {
        Boolean isSuccessful = false;
        
        String tableName = Utils.GetTableName(baseClass);
        
        List<String> tableFields = new ArrayList<>();
        
        try {
            DatabaseMetaData metaData = repository.GetConnection().getMetaData();
            
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
                                    
            while (resultSet.next()) {
                tableFields.add(resultSet.getString("COLUMN_NAME"));
            }
        }
        catch (SQLException exception) {
            
        }
        
        Boolean alterTable = false;
        StringBuilder alterCommands = new StringBuilder();
        alterCommands.append(String.format("ALTER TABLE %s", tableName));
        
        for (Field field : baseClass.getFields()){
            if (!tableFields.contains(field.getName())) {
                if (IsClassCollection(field.getType())) {
                    System.out.println(String.format("Unsupported Field type: %s", field.getType().getName()));
                } else {
                    alterTable = true;
                
                    Parking.Base.Utils.AppendLine(alterCommands, GetAlterField(field));
                }
            }
        }
        
        if (alterTable) {
            System.out.println(alterCommands.toString());
        } else {
            isSuccessful = true;
        }
               
        return isSuccessful;
    }
    
    private static String GetAlterField(Field field)
    {
        String fieldCommand = String.format("ADD COLUMN %s %s", field.getName(), GetPropertyType(field));
        
        return fieldCommand;
    }
    
    public static Boolean IsEligable(String fieldName)
    {
        return ((!"ID".equals(fieldName)) 
                && (!"Reference".equals(fieldName)) 
                && (!"ModifiedOn".equals(fieldName)));   
    }
    
    public static Boolean IsEligable(Field field)
    {
        if (field != null)
        {   
            String fieldName = field.getName();
        
            return IsEligable(fieldName);
        }
        
        return false;
    }
    
    private static Boolean IsEligable(List<String> fields, Field field)
    {
        if (IsEligable(field))
        {        
            return  !fields.contains(field.getName());
        }
        
        return false;
    }
    
    public static void AddProperty(StringBuilder commandBuilder, Field field)
    {
        if (IsClassCollection(field.getType())) {
            System.out.println(String.format("Unsupported Field type: %s", field.getType().getName()));
        } else {
            Parking.Base.Utils.AppendLine(commandBuilder, String.format(", %s %s", field.getName(), GetPropertyType(field)));
        }        
    }    
    
    private static String GetPropertyType(Field field)
    {
        String propertyType = "";
                        
        if (String.class.isAssignableFrom(field.getType())) 
        {
            propertyType = "varchar(255)";
        } 
        else if ((Integer.class.isAssignableFrom(field.getType())) || (int.class.isAssignableFrom(field.getType()))) 
        {
            propertyType = "int";
        } 
        else if (EntityObject.class.isAssignableFrom(field.getType())) 
        {        
            propertyType = "varchar(255)";
        }
        else if (Date.class.isAssignableFrom(field.getType())) 
        {        
            propertyType = "DateTime";
        } 
        else 
        {
            System.out.println(String.format("Unsupported Field type: %s", field.getType().getName()));
        }
        
        return propertyType;
    }
    
    public static boolean IsClassCollection(Class c) 
    {
        return Collection.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c);
    }
    
    public static boolean IsReferencedObject(Class c) {
        return EntityObject.class.isAssignableFrom(c);
    }
    
    public static String GetTableName(Class type)
    {
        Class tableType = type;
        
        while (tableType.getSuperclass() != EntityObject.class)
        {
            tableType = tableType.getSuperclass();
        }
        
        return tableType.getName().replace(".", "_");
    }
    
    public static String GetTableName(BaseObject baseObject, Field field)
    {
        return String.format("%s_%s", baseObject.getClass().getName().replace(".", "_"), field.getName());
    }
    
    public static Object GetValue(EntityObject entityObject, Field field)
    {
        Object value = null;
                
        try
        {        
            value = field.get(entityObject);

            Class fieldValueClass = field.getType();

            if (String.class.isAssignableFrom(fieldValueClass)) 
            {
                value = String.format("\"%s\"", value);
            } 
            else if (EntityObject.class.isAssignableFrom(fieldValueClass)) 
            {
                if (value == null)
                {
                    value = "\"\"";
                }
                else
                {
                    value = String.format("\"%s\"", ((EntityObject)value).ID);
                }
            }
            else if ((Integer.class.isAssignableFrom(fieldValueClass)) || (int.class.isAssignableFrom(fieldValueClass))) 
            {
                // skip this...
            }
            else if (java.util.Date.class.isAssignableFrom(fieldValueClass))
            {                
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                value = sdf.format(value);
            }
            else 
            {
                System.out.println(String.format("Unsupported Field type: %s", fieldValueClass.getName()));
            }
        } 
        catch (Exception exception) 
        {
            System.out.println(exception);
        }
        
        return value;
    }
    
    public static java.sql.Types GetSqlType(int typeID)
    {
        java.sql.Types type = null;
        
        if (sqlTypesCache.containsKey(typeID))
        {
            type = sqlTypesCache.get(typeID);
        }
        else
        {
            
        }
        
        return type;
    }
    
    public static List<FieldValue> GetFieldValues(EntityObject entityObject, Class entityType)
    {
        List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        
        for (Field field : entityType.getFields())
        {           
            try
            {
                FieldValue fieldValue = null;
                
                Boolean isCollection = IsClassCollection(field.getType());
                
                if (!isCollection)
                {
                    fieldValue = new FieldValue(field.getName(), field.get(entityObject), isCollection, IsReferencedObject(field.getType()));
                }
                else
                {
                    Object collection = field.get(entityObject);
                    
                    fieldValue = new FieldValue(field.getName(), collection , isCollection, false);
                }

                fieldValues.add(fieldValue);
            }
            catch (Exception exception)
            {

            }
        }
        
        return fieldValues;
    }
}