/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Ontology.Products;

import Parking.Core.EntityObject;

public class ProductLine extends EntityObject {
    public Product Product;
    
    @Override
    public void SetProperty(String propertyName, Object value)
    {
        switch (propertyName)
        {
            case "Product":
            case "Parking.Ontology.Products.ProductLine.Product":
                
                this.Product = (Product)value;
                break;
                
            default:
                
                super.SetProperty(propertyName, value);
                break;
        }
    }
}
