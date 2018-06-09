/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL;

import Parking.Core.BaseObject;
import Parking.Core.EntityObject;
import Parking.Storage.QueryParameters;
import Parking.Storage.StorageRepository;
import Parking.Storage.TransactionParameters;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author noldi
 */
public class MySQLRepository extends StorageRepository
{
    private String server;
    private String database;
    private String username;
    private String password;
    private ArrayList<String> initializedTypes;
    
    public MySQLRepository(String server, String database, String username, String password)
    {
        super();
        
        this.server = server;
        this.database = database;
        this.username = username;
        this.password = password;
        
        this.initializedTypes = new ArrayList<>();
    }
            
    private MySQLLoader loader;
    protected MySQLLoader GetLoader()
    {
        if (this.loader == null)
        {
            this.loader = new MySQLLoader(this);
        }
        
        return this.loader;
    }
    
//    private String GetConnectionString()
//    {
//        return "jdbc:mysql://" + this.server + "/" + this.database + "?allowMultiQueries=true";
//    }
    
    private Boolean LoadClass()
    {
        try 
        {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
	} 
        catch (ClassNotFoundException e) 
        {
            System.out.println("Where is your MySQL JDBC Driver?");
            System.out.println(e.getMessage());
            return false;
	}
    }
    
    private Boolean TestConnection()
    {
        Boolean success = false;
        
        Connection connection = this.GetConnection();
                	 
	if (connection == null) 
        {
            System.out.println("Failed to make connection!");
            success = false;
	}
        else
        {
            try 
            {
                connection.close();
                
                success = true;
            } 
            catch (SQLException e) 
            {
            }
        }
        
        return success;
    }

    Connection GetConnection()
    {
        Connection newConnection = null;
        
        try 
        {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(this.server);
            dataSource.setDatabaseName(this.database);
            dataSource.setUser(this.username);
            dataSource.setPassword(this.password);      
            
            newConnection = dataSource.getConnection();
        } 
        catch (SQLException e) 
        {
            System.out.println("Connection Failed! Check output console");
            System.out.println(e.getMessage());            
	}
        
        return newConnection;
    }
    
    @Override
    public Boolean Open()
    {
        Boolean isSuccessful = false;
        
        if ((this.LoadClass()) && (this.TestConnection())){            
            isSuccessful = true;
        }
        
        return isSuccessful;
    }
    
    @Override
    public Boolean Close()
    {        
        return true;
    }

    @Override
    public <T extends EntityObject> T Get(String reference, Class type) 
    {             
        if ((reference == null) || (reference.trim().isEmpty()) || type == null) {
            throw new IllegalArgumentException("Argument is null!");
        }
                
        return this.GetLoader().Get(reference, type);
    }
    
    @Override
    public <T extends EntityObject> T GetByID(String id, Class type) 
    {
        if ((id == null) || (id.trim().isEmpty()) || type == null) {
            throw new IllegalArgumentException("Argument is null!");
        }
        
        return this.GetLoader().GetByID(id, type);
    }

    @Override
    public <T extends EntityObject> ArrayList<T> Search(Class type) 
    {
        return this.Search(type, null);
    }
    
    @Override
    public <T extends EntityObject> ArrayList<T> Search(Class type, QueryParameters queryParameters)
    {
        if (type == null) {
            throw new IllegalArgumentException("Argument is null!");
        }
        
        if (queryParameters == null)
        {
            queryParameters = new QueryParameters();
        }
        
        return GetLoader().Search(type, queryParameters);
    }

    @Override
    public Boolean Save(EntityObject entityObject) 
    {
        return this.Save(entityObject, null);
    }    
    
    @Override
    public Boolean Save(EntityObject entityObject, TransactionParameters transactionParameters)
    {
        if (entityObject == null) 
        {
            throw new IllegalArgumentException("Argument is null!");
        }
        else if ((entityObject.Reference != null) && (entityObject.Reference.isEmpty())) 
        {
            throw new IllegalArgumentException("Referenc emay not be empty!");
        }
        
        if (transactionParameters == null)
        {
            transactionParameters = new TransactionParameters();
        }

        return this.GetLoader().Save(entityObject, transactionParameters);
    }
    
    Boolean CheckType(Class type)
    {        
        Boolean isSuccessful = false;
        
        if (!this.initializedTypes.contains(type.getName())) 
        {
            if (this.initializeType(type)) 
            {
                this.initializedTypes.add(type.getName());                
                isSuccessful = true;
            }            
        }
        
        return isSuccessful;
    }
    
    Boolean CheckType(BaseObject baseObject, Field field)
    {
        Boolean isSuccessful = true;
        
        String fieldKey = String.format("%s_%s", Utils.GetTableName(baseObject.getClass()), field.getName());
        
        if (!this.initializedTypes.contains(fieldKey)) 
        {
            if (this.initializeType(baseObject, field)) 
            {
                this.initializedTypes.add(fieldKey);                
                isSuccessful = true;
            }            
        }
        
        return isSuccessful;
    }
    
    private Boolean initializeType(Class type)
    {   
        Boolean isSuccessful = false;
        
        Class baseClass = type;
        
        while (baseClass.getSuperclass() != EntityObject.class) {
            baseClass = baseClass.getSuperclass();
        }
        
        if (!this.CheckTableExists(baseClass)) 
        {
            isSuccessful = Utils.CreateTable(this, type, baseClass);
        } 
        else if (!Utils.UpdateTable(this, baseClass))
        {
            isSuccessful = false;
        } 
        else 
        {
            isSuccessful = true;
        }
        
        return isSuccessful;
    }
    
    private Boolean initializeType(BaseObject baseObject, Field field)
    {   
        Boolean isSuccessful = false;
        
        String tableName = Utils.GetTableName(baseObject, field);
        
//        if (!this.CheckTableExists(tableName)) 
//        {
//            isSuccessful = Utils.CreateTable(this, baseObject.class, baseClass);
//        } 
//        else if (!Utils.UpdateTable(this, baseClass))
//        {
//            isSuccessful = false;
//        } 
//        else 
//        {
//            isSuccessful = true;
//        }
        
        return isSuccessful;
    }
            
    private Boolean CheckTableExists(Class type)     
    {
        String tableName = Utils.GetTableName(type);
        
        return this.CheckTableExists(tableName);
    }
    
    private Boolean CheckTableExists(String tableName)     
    {
        Boolean tableExists = false;
        
        String command = String.format("SELECT * FROM information_schema.tables WHERE table_schema = '%s' AND table_name = '%s' LIMIT 1;", this.database, tableName);
        
        Connection connection = this.GetConnection();
        
        try
        {
            Statement statement = connection.createStatement();
            // Result set get the result of the SQL query
            ResultSet resultSet = statement.executeQuery(command);
            
            if (resultSet.first())
            {
                tableExists = true;
            }
        }
        catch (SQLException exception)
        {            
        }
                               
        return tableExists;
    }
}