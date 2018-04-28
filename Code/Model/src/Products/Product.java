/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Products;

import Categories.Category;
import Optics.Barcode;
import Parking.Core.EntityObject;
import java.util.List;

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
}
