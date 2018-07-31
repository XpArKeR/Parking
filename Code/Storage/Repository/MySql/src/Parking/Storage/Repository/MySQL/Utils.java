/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL;

import Parking.Core.BaseObject;
import Parking.Core.EntityObject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
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

    public static Boolean CreateTable(MySQLRepository repository, Class type, Class baseType) {
        Parking.Base.Debugger.Log("Creating Table for type '%s', base type '%s'", type.getName(), baseType.getName());
        String tableName = GetTableName(baseType);

        List<String> fields = new ArrayList<>();
        ArrayList<FieldInformation> fieldInformations = new ArrayList<>();

        fieldInformations.add(new FieldInformation("ID", String.class, false, false, true));
        fieldInformations.add(new FieldInformation("ModifiedOn", Date.class, false, false, false));

        if (EntityObject.class.isAssignableFrom(type)) {
            fieldInformations.add(new FieldInformation("Reference", String.class, false, true, false));
        }

        for (Field field : type.getFields()) {
            if (IsEligable(fields, field)) {
                FieldInformation fieldInformation = GetFieldInformation(field);

                fieldInformations.add(fieldInformation);
                fields.add(field.getName());
            }
        }

        return CreateTable(repository, tableName, fieldInformations);
    }

    public static Boolean CreateTable(MySQLRepository repository, BaseObject baseObject, Field field, Class fieldType) {
        String tableName = Utils.GetTableName(baseObject, field);

        List<String> fields = new ArrayList<>();
        ArrayList<FieldInformation> fieldInformations = new ArrayList<>();

        fieldInformations.add(new FieldInformation("ID", String.class, false, false, true));
        fieldInformations.add(new FieldInformation("ModifiedOn", Date.class, false, false, false));

        if (EntityObject.class.isAssignableFrom(fieldType)) {
            fieldInformations.add(new FieldInformation("Reference", String.class, false, true, false));
        }

        for (Field fieldProperty : fieldType.getFields()) {
            if (IsEligable(fields, fieldProperty)) {
                FieldInformation fieldInformation = GetFieldInformation(fieldProperty);

                fieldInformations.add(fieldInformation);
                fields.add(fieldProperty.getName());
            }
        }

        return CreateTable(repository, tableName, fieldInformations);
    }

    public static Boolean CreateTable(MySQLRepository repository, String tableName, ArrayList<FieldInformation> fieldInformations) {
        Boolean isSuccessful = false;

        StringBuilder commandBuilder = new StringBuilder();

        Parking.Base.Utils.AppendLine(commandBuilder, String.format("CREATE TABLE %s (", tableName));

        String prefix = "";
        for (FieldInformation fieldInformation : fieldInformations) {
            if (!fieldInformation.getIsCollection()) {
                Parking.Base.Utils.AppendLine(commandBuilder, String.format("%s%s", prefix, Utils.GetFieldDefinition(fieldInformation)));
                prefix = ",";
            }
        }

        commandBuilder.append(");");

        Connection connection = repository.GetConnection();

        try {
            Parking.Base.Debugger.Log("Creating Table with query '%s'", commandBuilder.toString());
            
            PreparedStatement statement = connection.prepareStatement(commandBuilder.toString());
            // Result set get the result of the SQL query
            statement.executeUpdate();

            isSuccessful = true;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return isSuccessful;
    }

    public static String GetFieldDefinition(FieldInformation field) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(field.getName());

        stringBuilder.append(String.format(" %s", GetPropertyType(field.getType())));

        if (!field.getIsNullable()) {
            stringBuilder.append(String.format(" NOT NULL"));
        }

        if (field.getIsPrimary()) {
            stringBuilder.append(String.format(" PRIMARY KEY"));
        }

        return stringBuilder.toString();
    }

    public static Boolean UpdateTable(MySQLRepository repository, Class type) {
        String tableName = Utils.GetTableName(type);

        List<FieldInformation> fieldInformations = new ArrayList<>();

        for (Field property : type.getFields()) {
            if (!IsClassCollection(property.getType())) {
                fieldInformations.add(GetFieldInformation(property));
            }
        }

        return UpdateTable(repository, tableName, fieldInformations);
    }

    public static Boolean UpdateTable(MySQLRepository repository, Class parentType, Field field) {
        Class fieldType = null;

        if ((field.getName().contains("Lines")) || (parentType.getName().contains("Lines")))
        {
            System.err.println(field.getName());
        }
        
        if (IsClassCollection(field.getType())) {
            fieldType = GetListType(field);
        } else {
            fieldType = field.getType();
        }
        
        String tableName = Utils.GetTableName(parentType, field);

        List<FieldInformation> fieldInformations = new ArrayList<>();
        List<String> fields = new ArrayList<>();

        FieldInformation parentIDFieldInformation = Utils.GetParentIDFieldInformation(parentType);

        for (Field property : fieldType.getFields()) {
            if ((!fields.contains(property.getName())) && (!IsClassCollection(property.getType()))) {
                FieldInformation fieldInformation = GetFieldInformation(property);

                fieldInformations.add(fieldInformation);
                fields.add(fieldInformation.getName());
            }
        }

        if (!fields.contains(parentIDFieldInformation.getName())) {
            fieldInformations.add(parentIDFieldInformation);
        }

        return UpdateTable(repository, tableName, fieldInformations);
    }

    public static Boolean UpdateTable(MySQLRepository repository, String tableName, List<FieldInformation> fieldInformations) {
        Boolean isSuccessful = false;

        List<String> tableFields = new ArrayList<>();

        try {
            DatabaseMetaData metaData = repository.GetConnection().getMetaData();

            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

            if ("Parking_Ontology_Products_ProductLine_Lines".equals(tableName))
            {
                System.err.println(tableName);
            }
            
            while (resultSet.next()) {
                tableFields.add(resultSet.getString("COLUMN_NAME"));
            }
            
            if (tableFields.isEmpty()) {
                System.err.println("Empty table Fields?");
            }
        } catch (SQLException exception) {
            System.out.println(exception);

            return isSuccessful;
        }

        Boolean alterTable = false;
        StringBuilder alterCommands = new StringBuilder();
        alterCommands.append(String.format("ALTER TABLE %s ", tableName));

        String prefix = "";
        for (FieldInformation fieldInformation : fieldInformations) {
            if (!tableFields.contains(fieldInformation.getName())) {
                alterTable = true;

                Parking.Base.Utils.AppendLine(alterCommands, String.format("%sADD COLUMN %s", prefix, GetFieldDefinition(fieldInformation)));
                prefix = ", ";
            }
        }

        if (alterTable) {

            Connection connection = repository.GetConnection();

            try {
                PreparedStatement statement = connection.prepareStatement(alterCommands.toString());
                // Result set get the result of the SQL query
                statement.executeUpdate();

                isSuccessful = true;
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } else {
            isSuccessful = true;
        }

        return isSuccessful;
    }

    public static Boolean IsEligable(String fieldName) {
        return ((!"ID".equals(fieldName))
                && (!"Reference".equals(fieldName))
                && (!"ModifiedOn".equals(fieldName)));
    }

    public static Boolean IsEligable(Field field) {
        if (field != null) {
            String fieldName = field.getName();

            return IsEligable(fieldName);
        }

        return false;
    }

    private static Boolean IsEligable(List<String> fields, Field field) {
        if (IsEligable(field)) {
            return !fields.contains(field.getName());
        }

        return false;
    }

    private static String GetPropertyType(Class type) {
        String propertyType = "";

        if (String.class.isAssignableFrom(type)) {
            propertyType = "varchar(255)";
        } else if ((Integer.class.isAssignableFrom(type)) || (int.class.isAssignableFrom(type))) {
            propertyType = "int";
        } else if (EntityObject.class.isAssignableFrom(type)) {
            propertyType = "varchar(255)";
        } else if (Date.class.isAssignableFrom(type)) {
            propertyType = "DateTime";
        } else {
            System.out.println(String.format("Unsupported Field type: %s", type.getName()));
        }

        return propertyType;
    }

    public static boolean IsClassCollection(Class c) {
        return Collection.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c);
    }

    public static boolean IsReferencedObject(Class c) {
        return EntityObject.class.isAssignableFrom(c);
    }

    public static String GetTableName(Class type) {
        Class tableType = type;

        while (tableType.getSuperclass() != EntityObject.class) {
            tableType = tableType.getSuperclass();
        }

        return tableType.getName().replace(".", "_");
    }

    public static String GetTableName(BaseObject baseObject, Field field) {
        return GetTableName(baseObject.getClass(), field);
    }

    public static String GetTableName(Class type, Field field) {
        return String.format("%s_%s", type.getName().replace(".", "_"), field.getName());
    }

    public static Object GetValue(EntityObject entityObject, Field field) {
        Object value = null;

        try {
            value = field.get(entityObject);

            Class fieldValueClass = field.getType();

            if (String.class.isAssignableFrom(fieldValueClass)) {
                value = String.format("\"%s\"", value);
            } else if (EntityObject.class.isAssignableFrom(fieldValueClass)) {
                if (value == null) {
                    value = "\"\"";
                } else {
                    value = String.format("\"%s\"", ((EntityObject) value).ID);
                }
            } else if ((Integer.class.isAssignableFrom(fieldValueClass)) || (int.class.isAssignableFrom(fieldValueClass))) {
                // skip this...
            } else if (java.util.Date.class.isAssignableFrom(fieldValueClass)) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                value = sdf.format(value);
            } else {
                System.out.println(String.format("Unsupported Field type: %s", fieldValueClass.getName()));
            }
        } catch (Exception exception) {
            System.out.println(exception);
        }

        return value;
    }

    public static java.sql.Types GetSqlType(int typeID) {
        java.sql.Types type = null;

        if (sqlTypesCache.containsKey(typeID)) {
            type = sqlTypesCache.get(typeID);
        } else {

        }

        return type;
    }

    public static List<FieldValue> GetFieldValues(EntityObject entityObject, Class entityType) {
        List<FieldValue> fieldValues = new ArrayList<FieldValue>();

        for (Field field : entityType.getFields()) {
            try {
                FieldValue fieldValue = null;

                Boolean isCollection = IsClassCollection(field.getType());

                if (!isCollection) {
                    String fieldName = field.getName();
                    
                    if (EntityObject.class.isAssignableFrom(field.getType()))
                    {
                        fieldName = String.format("%sID", field.getName());
                    }
                    
                    fieldValue = new FieldValue(fieldName, field.get(entityObject), isCollection, IsReferencedObject(field.getType()));
                } else {
                    Object collection = field.get(entityObject);

                    fieldValue = new FieldValue(field.getName(), collection, isCollection, false);
                }

                fieldValues.add(fieldValue);
            } catch (Exception exception) {

            }
        }

        return fieldValues;
    }

    public static Class GetListType(Field field) {
        ParameterizedType fieldType = (ParameterizedType) field.getGenericType();
        return (Class<?>) fieldType.getActualTypeArguments()[0];
    }

    public static FieldInformation GetParentIDFieldInformation(Class parentType) {
        String parentName = parentType.getName();

        String[] parentSplitName = parentName.split("\\.");

        parentName = String.format("%sID", parentSplitName[parentSplitName.length - 1]);

        FieldInformation parentIDFieldInformation = new FieldInformation();

        parentIDFieldInformation.setName(parentName);
        parentIDFieldInformation.setType(String.class);
        parentIDFieldInformation.setIsCollection(Boolean.FALSE);
        parentIDFieldInformation.setIsNullable(Boolean.TRUE);
        parentIDFieldInformation.setIsPrimary(false);

        return parentIDFieldInformation;
    }

    public static FieldInformation GetFieldInformation(Field field) {
        FieldInformation fieldInformation = new FieldInformation();

        if (EntityObject.class.isAssignableFrom(field.getType())) {
            fieldInformation.setName(String.format("%sID", field.getName()));
            fieldInformation.setType(String.class);
            fieldInformation.setIsCollection(false);
        } else {
            fieldInformation.setName(field.getName());
            fieldInformation.setType(field.getType());
            fieldInformation.setIsCollection(IsClassCollection(field.getType()));
        }

        fieldInformation.setIsNullable(false);
        fieldInformation.setIsPrimary(GetIsPrimaryKey(field));

        return fieldInformation;
    }

    public static Boolean GetIsPrimaryKey(Field field) {
        Boolean isPrimaryKey = false;

        if (!IsClassCollection(field.getType())) {
            if ("ID".equals(field.getName())) {
                isPrimaryKey = true;
            }
        }

        return isPrimaryKey;
    }
}
