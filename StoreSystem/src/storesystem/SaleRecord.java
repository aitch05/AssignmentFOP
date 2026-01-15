/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package storesystem;

/**
 *
 * @author Nur Hasna Nadirah
 */
import java.time.LocalDate;
import java.time.LocalTime;
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
    
    public String getDate() {return date;}
    public String getTime() {return time;}
    public String getCustomerName() {return customerName;}
    public String getModelName() {return modelName;}
    public int getQuantity() {return quantity;}
    public double getTotal() {return total;}
    public String getMethod() {return method;}
    public void setMethod(String method) {this.method = method;}
    public String getEmployee() {return employee;}
    
    @Override
    public String toString() {
        return date + "," + time + "," + customerName + "," +
               modelName + "," + quantity + "," + total + "," +
               method + "," + employee;
    }
}
