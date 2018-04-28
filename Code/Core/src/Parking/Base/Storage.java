/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Base;

/**
 *
 * @author noldi
 */
public class Storage 
{
    private static Parking.Storage.Operator operator;
    public static Parking.Storage.Operator GetOperator()
    {
        if (operator == null)
        {
            operator = new Parking.Storage.Operator();
        }
        
        return operator;
    }
    
    private static Parking.Storage.Manager manager;
    public static Parking.Storage.Manager GetManager()
    {
        if (manager == null)
        {
            manager = new Parking.Storage.Manager();
        }
        
        return manager;
    }
}
