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
import java.util.List;
import java.util.Scanner;

public class EditInformation {
    public static void editStock(List<Stock> stocks, Scanner sc) {
        System.out.println("\n=== Edit Stock Information ===");
        System.out.print("Enter Model Name: ");
        String modelName = sc.nextLine();
        
        boolean found = false;
        
        for(Stock s : stocks) {
            if(s.getModel().equalsIgnoreCase(modelName)) {
                found = true;
                System.out.println("Current Stock: " + s.getQuantity());
                System.out.print("Enter New Stock Value: ");
                
                try {
                    int newStock = Integer.parseInt(sc.nextLine());
                    if(newStock < 0) {
                        System.out.println("New stock value cannot be negative");
                        return;
                    }
                    s.setQuantity(newStock);
                    
                    System.out.println("Stock information updated successfully.");
                    
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input");
                    return;
                }
                break;
            }
        }
        if(!found) {
            System.out.println("Model name not found");
        }
    }
    
    public static void EditSales(List<SaleRecord> sales, Scanner sc) {
        System.out.println("\n=== Edit Sale Information ===");
        System.out.print("Enter Transaction Date(YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(sc.nextLine());
        System.out.print("Enter Customer Name: ");
        String custName = sc.nextLine();
        
        SaleRecord foundRecord = null;
        for(SaleRecord record : sales) {
            if(record.date.equals(date.toString()) && record.customerName.equalsIgnoreCase(custName)){
                foundRecord = record;
                break;
            }
        }
        if(foundRecord == null) {
            System.out.println("No matching sales record found");
            return;
        }
        
        System.out.println("\nSales Record Found: ");
        System.out.println();
        System.out.println("Model: " + foundRecord.modelName + "Quantity: " + foundRecord.quantity);
        System.out.println("Total: RM" + foundRecord.total);
        System.out.println("Transaction Method: " + foundRecord.method);
        
        System.out.println("\nSelect number to edit:");
        System.out.println("1. Name 2. Model 3. Quantity 4. Total 5. Transaction Method");
        System.out.println(">");
        int choice = sc.nextInt();
        sc.nextLine();
        
        switch(choice) {
            case 1 -> {
                System.out.print("Enter New Name: ");
                foundRecord.customerName = sc.nextLine();
            }
            case 2 -> {
                System.out.println("Enter New Model: ");
                foundRecord.modelName = sc.nextLine();
            }
            case 3 -> {
                System.out.print("Enter New Quantity: ");
                foundRecord.quantity = sc.nextInt();
            }
            case 4 -> {
                System.out.print("Enter New Total: ");
                foundRecord.total = sc.nextInt();
                sc.nextLine();
            }
            case 5 -> {
                System.out.print("Enter New Transaction Method: ");
                foundRecord.method = sc.nextLine();
            }
            default -> {
                System.out.println("Invalid choice");
                return;
            }
        }
        
        System.out.print("Confirm Update? (Y/N): ");
        String ans = sc.nextLine();
        if(ans.equalsIgnoreCase("Y")) {
            System.out.println("Sales information updated successfully.");
        } else {
            System.out.println("Update cancelled");
        }
    }
}
