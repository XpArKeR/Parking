/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Ontology.Optics;

import Parking.Core.EntityObject;

/**
 *
 * @author noldi
 */
public class Barcode extends EntityObject 
{
    public String Code;
    
     @Override
    public void SetProperty(String propertyName, Object value)
    {
        switch (propertyName)
        {
            case "Code":
            case "Parking.Ontology.Optics.Code":
                
                this.Code = (String)value;
                break;                              
                
            default:
                super.SetProperty(propertyName, value);
        }
    }
}
