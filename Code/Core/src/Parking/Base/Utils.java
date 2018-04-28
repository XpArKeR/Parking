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
public class Utils {
    public static void AppendLine(StringBuilder stringBuilder, String line)
    {
        if (stringBuilder != null) {
            stringBuilder.append(line).append(System.lineSeparator());
        }
    }
}
