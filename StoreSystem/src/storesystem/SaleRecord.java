/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package storesystem;

/**
 *
 * @author Nur Hasna Nadirah
 */
public class SaleRecord {
    
    String date, time, customerName, modelName, method, employee;
    int quantity;
    double total;

    public SaleRecord(String date, String time, String customer, String model, int qty, double total, String method, String employee) {
        this.date = date;
        this.time = time;
        this.customerName = customer;
        this.modelName = model;
        this.quantity = qty;
        this.total = total;
        this.method = method;
        this.employee = employee;
    }
}
