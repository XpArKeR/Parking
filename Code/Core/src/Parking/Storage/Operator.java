package Parking.Storage;

import Parking.Base.Debugger;
import Parking.Core.EntityObject;
import java.util.ArrayList;

/**
 *
 * @author noldi
 */
public class Operator extends Parking.Core.Operator
{
    @Override
    public Boolean Load()
    {
        Boolean isSuccessful = super.Load();
        Parking.Storage.Manager manager = Parking.Base.Storage.GetManager();
        
        if (manager != null)
        {
            for (Parking.Storage.StorageRepository repository : manager.GetRepositories()) 
            {
                if (!repository.Open())
                {
                    isSuccessful = false;
                    break;
                }
            }
        }
        
        return isSuccessful;
    }
            
    public Boolean Save(EntityObject entityObject)
    {
        return this.Save(entityObject, null);
    }
    
    public Boolean Save(EntityObject entityObject, TransactionParameters transactionParameters)
    {
        Debugger.Log("Saving entityObject '%s'", entityObject.toString());
        
        if ((entityObject.Reference == null) || (entityObject.Reference.isEmpty()))
        {
            if (!entityObject.GenerateReference())
            {
                throw new IllegalArgumentException("Reference may not be empty");
            }            
        }
        
        if (transactionParameters == null)
        {
            transactionParameters = new TransactionParameters();
        }
        
        Boolean saveSuccessful = true;
        
        for(StorageRepository repository : Parking.Base.Storage.GetManager().GetRepositories(entityObject.getClass()))
        {
            if (!repository.Save(entityObject, transactionParameters))
            {
                saveSuccessful = false;
            }
        }
        
        return saveSuccessful;
    }
    
    public <T extends EntityObject> T Get(String reference, Class type)
    {
        T returnValue = null;
        
        Parking.Storage.Manager manager = Parking.Base.Storage.GetManager();
        
        if (manager != null)
        {
            for (Parking.Storage.StorageRepository repository : manager.GetRepositories(type)) 
            {
                returnValue = repository.Get(reference, type);
                
                if (returnValue != null)
                {
                    break;
                }
            }
        }
        
        return returnValue;
    }
    
    public <T extends EntityObject> ArrayList<T> Search(Class type)
    {
        ArrayList<T> returnValue = new ArrayList<>();
        
        Parking.Storage.Manager manager = Parking.Base.Storage.GetManager();
        
        if (manager != null)
        {
            for (Parking.Storage.StorageRepository repository : manager.GetRepositories(type)) 
            {
                returnValue.addAll(repository.Search(type));
            }
        }
        
        return returnValue;
    }
}
