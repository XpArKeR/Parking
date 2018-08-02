/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Base;

/**
 *
 * @author RGE
 */
public class Debugger {
    private static Boolean IsEnabled = true;
    
    public static void Log(String message, Object... arguments)
    {
        if (IsEnabled)
        {
            if (arguments!= null && arguments.length > 0) {
                System.out.println(String.format(message, arguments));
            } else {
                System.out.println(message);
            }
        }
    }
}
