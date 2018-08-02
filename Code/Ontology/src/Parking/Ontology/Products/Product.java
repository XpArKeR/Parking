/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Ontology.Products;

import Parking.Ontology.Categories.Category;
import Parking.Ontology.Optics.Barcode;
import Parking.Core.EntityObject;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author noldi
 */
public class Product extends EntityObject {
    public String Name;
    public String Description;
    public Category Category;
    public List<ProductLine> Lines;
    public List<Barcode> Codes;
    public int Amount;
    
    @Override
    public Boolean GenerateReference()
    {
        this.Reference = UUID.randomUUID().toString();
        
        return true;
    }
    
    @Override
    public void SetProperty(String propertyName, Object value)
    {
        switch (propertyName)
        {
            case "Name":
            case "Parking.Model.Products.Product.Name":
                
                this.Name = (String)value;
                break;              
                
            case "Description":
            case "Parking.Model.Products.Product.Description":
                this.Description = (String)value;                
                break;
            
            case "Category":
            case "Parking.Model.Products.Product.Category":
                this.Category = (Category)value;                
                break;
                
            case "Lines":
            case "Parking.Model.Products.Product.Lines":
                this.Lines = (List<ProductLine>)value;                
                break;
                
            case "Codes":
            case "Parking.Model.Products.Product.Codes":
                this.Codes = (List<Barcode>)value;                
                break;
                
            case "Amount":
            case "Parking.Model.Products.Product.Amount":
                this.Amount = (int)value;                
                break;
                
            default:
                super.SetProperty(propertyName, value);
        }
    }
}
