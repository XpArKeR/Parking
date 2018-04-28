/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage;

import Parking.Core.EntityObject;
import java.util.ArrayList;

/**
 *
 * @author noldi
 */
public class Manager extends Parking.Core.Manager
{
    public Manager()    
    {
        super();
        
        this.repositories = new ArrayList<>();
    }
    
    private ArrayList<StorageRepository> repositories;
    
    public void Add(StorageRepository storageRepository)
    {
        if (!this.contains(storageRepository))
        {
            this.repositories.add(storageRepository);
        }
    }
    
    public ArrayList<StorageRepository> GetRepositories(Class type)
    {
        ArrayList<StorageRepository> repositories = new ArrayList<>();
        
        //TODO: Filter related repositories
        for(StorageRepository repository : this.repositories)
        {
            repositories.add(repository);
        }
        
        return repositories;
    }
    
    public ArrayList<StorageRepository> GetRepositories()
    {
        ArrayList<StorageRepository> repositories = new ArrayList<>();
                
        for(StorageRepository repository : this.repositories)
        {
            repositories.add(repository);
        }
        
        return repositories;
    }
    
    private Boolean contains(StorageRepository repository)
    {
        return this.repositories.contains(repository);
    }
}
