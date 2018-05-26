/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Ontology.Categories;

import Parking.Core.EntityObject;

/**
 *
 * @author noldi
 */
public class Category extends EntityObject 
{
    public String Name;
    
    @Override
    public void SetProperty(String propertyName, Object value)
    {
        switch (propertyName)
        {
            case "Name":
            case "Parking.Ontology.Categories.Category.Name":
                
                this.Name = (String)value;
                break;       
                
            default:
                super.SetProperty(propertyName, value);
        }
    }    
}
