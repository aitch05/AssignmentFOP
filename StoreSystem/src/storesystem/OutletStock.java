/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package storesystem;

/**
 *
 * @author Nur Hasna Nadirah
 */
public class OutletStock {
    private String outletName;
    private int quantity;
    
    public OutletStock(String outletName, int quantity) {
        this.outletName = outletName;
        this.quantity = quantity;
    }
    public String getOutletName() {return outletName;}
    public int getQuantity() {return quantity;}
}
