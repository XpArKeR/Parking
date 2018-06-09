package Parking.Storage;

import Parking.Core.BaseObject;
import Parking.Core.EntityObject;
import java.util.ArrayList;

public abstract class StorageRepository extends BaseObject 
{
    public abstract Boolean Open();
    public abstract Boolean Close();
    public abstract <T extends EntityObject> T Get(String reference, Class type);
    public abstract <T extends EntityObject> T GetByID(String id, Class type);
    public abstract <T extends EntityObject> ArrayList<T> Search(Class type);
    public abstract <T extends EntityObject> ArrayList<T> Search(Class type, QueryParameters queryParameters);
    public abstract Boolean Save(EntityObject baseObject);
    public abstract Boolean Save(EntityObject baseObject, TransactionParameters transactionParameters);
}
