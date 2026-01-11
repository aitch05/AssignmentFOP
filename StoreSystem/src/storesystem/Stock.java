/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package storesystem;

/**
 *
 * @author Nur Hasna Nadirah
 */
import java.util.ArrayList;
public class Stock {
    private String model;
    private int quantity;
    private String outlet;
    private ArrayList<OutletStock> outletStocks;
    private double price; // Added for sales calculation

    public Stock(String model, int quantity, String outlet, double price) {
        this.model = model;
        this.quantity = quantity;
        this.outlet = outlet;
        this.price = price;
        this.outletStocks = new ArrayList();
    }
    public void addOutletStock(String outlet, int qty) {
        outletStocks.add(new OutletStock(outlet,qty));
    }
    public ArrayList<OutletStock> getOutletStock() {return outletStocks;}
    public String getModel() { return model; }
    public int getQuantity() { return quantity; }
    public String getOutlet() { return outlet; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getPrice() { return price; }

    public String toCSV() {
        return model + "," + quantity + "," + outlet;
    }
}
