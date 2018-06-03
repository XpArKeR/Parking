/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage;

import Parking.Core.BaseObject;
import Parking.Core.EntityObject;
import java.util.ArrayList;

/**
 *
 * @author noldi
 */
public abstract class StorageRepository extends BaseObject 
{
    public abstract Boolean Open();
    public abstract Boolean Close();
    public abstract <T extends EntityObject> T Get(String reference, Class type);
    public abstract <T extends EntityObject> T GetByID(String id, Class type);
    public abstract <T extends EntityObject> ArrayList<T> Search(Class type);
    public abstract Boolean Save(EntityObject baseObject);
    public abstract Boolean Save(EntityObject baseObject, TransactionParameters transactionParameters);
}
