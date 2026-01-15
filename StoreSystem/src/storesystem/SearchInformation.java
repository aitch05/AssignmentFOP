/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package storesystem;

/**
 *
 * @author Nur Hasna Nadirah
 */
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SearchInformation {
    public static void searchStock(String modelName, ArrayList<Stock> stocks, Map<String, String> outletMap) {
        boolean found = false;
        double price = 0;

    // Get price from loaded stock (current outlet)
    for (Stock s : stocks) {
        if (s.getModel().equalsIgnoreCase(modelName)) {
            price = s.getPrice();
            found = true;
            break;
        }
    }

    if (!found) {
        System.out.println("No stock record found.");
        return;
    }
    
    System.out.println("Model: " + modelName);
    System.out.println("Unit Price: RM" + price);
    System.out.println("Stock by Outlet: ");
    
    try(Scanner sc = new Scanner(new File("model.csv"))) {
        String[]  header = sc.nextLine().split(",");
        
        while(sc.hasNextLine()) {
            String[] data = sc.nextLine().split(",");
            
            if(!data[0].equalsIgnoreCase(modelName)) continue;
            
            List<String> nonZeroOutlets = new ArrayList<>();
            for(int i = 2; i < data.length; i++) {
                int qty = Integer.parseInt(data[i].trim());
                if(qty > 0) {
                    String outletName = outletMap.getOrDefault(header[i].trim(), header[i].trim());
                    nonZeroOutlets.add(outletName + ": " + qty);
                }
            }
            int half = (int) Math.ceil(nonZeroOutlets.size() / 2.0);
            for(int i = 0; i < half; i++) System.out.print(nonZeroOutlets.get(i) + "   ");
            System.out.println();
            for(int i = half; i < nonZeroOutlets.size(); i++) System.out.print(nonZeroOutlets.get(i) + "   ");
            System.out.println();
            
            return;
        }
    } catch (Exception e) {
        System.out.println("Error reading stock file");
    }
 } 
    
    public static void searchSales(String keyword, ArrayList<SaleRecord> sales) {
            boolean found = false;
            for(SaleRecord s : sales){
                if(s.getCustomerName().toLowerCase().contains(keyword.toLowerCase()) || s.getModelName().equalsIgnoreCase(keyword) || s.getDate().equals(keyword)) {
                    System.out.println("Sales Record found:");
                    System.out.println("Date: " + s.getDate() + "\tTime: " + s.getTime());
                    System.out.println("Customer: " + s.getCustomerName());
                    System.out.println("Item(s): " + s.getModelName() + "\tQuantity: " + s.getQuantity());
                    System.out.println("Total: RM" + s.getTotal());
                    System.out.println("Transaction Mathod: " + s.getMethod());
                    System.out.println("Employee: " + s.getEmployee());
                    System.out.println("Status: Transaction verified.");
                    
                    found = true;
                }
            }
            if(!found) {
                System.out.println("No matching sales record found ");
            }
    }
    
    public static void showAnalytics(ArrayList<SaleRecord> sales) {
        if(sales.isEmpty()){
            System.out.println("No sales data available for analytics.");
            return;
        }
        double totalRevenue = 0;
        Map<String, Integer> modelCount = new HashMap<>();
        
        for(SaleRecord s : sales){
            totalRevenue += s.getTotal();
            modelCount.put(s.getModelName(), modelCount.getOrDefault(s.getModelName(),0) + s.getQuantity());
        }
        String bestModel = Collections.max(modelCount.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("\n=== Sales Analytics ===");
        System.out.println("Total Revenue recorded: RM" + totalRevenue);
        System.out.println("Total Transaction: " + sales.size());
        System.out.println("Best Selling Model: " + bestModel + "( " + modelCount.get(bestModel) + "units)");
        System.out.println("Average Sale Valur: RM" + (totalRevenue / sales.size()));
    }
    public static void filterAndSortSales(ArrayList<SaleRecord> sales, Scanner sc) {
        System.out.print("Enter Start Date (YYYY-MM-DD): ");
        LocalDate start = LocalDate.parse(sc.nextLine());
        System.out.print("Enter End Date (YYYY-MM-DD): ");
        LocalDate end = LocalDate.parse(sc.nextLine());
        
        List<SaleRecord> filtered = new ArrayList<>();
        double cumulativeTotal = 0;
        for(SaleRecord s : sales) {
            LocalDate saleDate = LocalDate.parse(s.getDate());
            if(!saleDate.isBefore(start) && !saleDate.isAfter(end)) {
                filtered.add(s);
                cumulativeTotal = s.getTotal();
            }
        }
        if(filtered.isEmpty()){
            System.out.println("No records found for this period");
        }
        System.out.println("Sort by (1/2/3): 1.Date 2. Ammount 3. Customer Name");
        int choice = sc.nextInt();
        
        for(int i = 0; i < filtered.size(); i++){
            for(int j = 0; j < filtered.size() - 1; j++){
                boolean swap = false;
                SaleRecord r1 = filtered.get(j);
                SaleRecord r2 = filtered.get(j + 1);
                
                switch(choice){
                    case 1 -> {
                        if(r1.getDate().compareTo(r2.getDate()) > 0)
                            swap = true;
                    }
                    case 2 -> {
                        if(r1.getTotal() < r2.getTotal())
                            swap = true;
                    }
                    case 3 -> {
                        if(r1.getCustomerName().compareToIgnoreCase(r2.getCustomerName()) > 0)
                            swap = true;
                    }
                }
                if(swap) {
                    filtered.set(j, r2);
                    filtered.set(j + 1, r1);
                }
            }
        }
        System.out.println("\n" + "=".repeat(80));
        System.out.printf("%-12s | %-15s | %-12s | %-5s | %-10s%n", "Date", "Customer", "Model", "Qty", "Total (RM)");
        System.out.println("-".repeat(80));
        
        for(SaleRecord s : filtered) {
            System.out.printf("%-12s | %-15s | %-12s | %-5s | %-10s%n", s.getDate(), s.getCustomerName(), s.getModelName(), s.getQuantity(), s.getTotal());
        }
        System.out.println("-".repeat(80));
        System.out.printf("Cumulative Sales: RM %.2f%n", cumulativeTotal);
        System.out.println("=".repeat(80));
    }
    
    public static void employeePerformance(ArrayList<SaleRecord> sales) {
        // Map to store: EmployeeName -> [Total Sales, Transaction Count]
        Map<String, double[]> performanceMap = new HashMap<>();
        
        for(SaleRecord s : sales) {
            String emp = s.getEmployee();
            performanceMap.putIfAbsent(emp, new double[2]);
            performanceMap.get(emp)[0] += s.getTotal();
            performanceMap.get(emp)[1] += 1;
        }
        // 2. Convert Map to an ArrayList of a temporary helper class or Object array for sorting
        List<Map.Entry<String, double[]>> list = new ArrayList<>(performanceMap.entrySet());
        for(int i = 0; i < list.size() - 1; i++){
            for(int j = 0; j < list.size() - i - 1; j++){
                if(list.get(j).getValue()[0] < list.get(j + 1).getValue()[0]){
                    Map.Entry<String, double[]> temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        System.out.println("\n" + "=".repeat(60));
        System.out.println("\n=== Employee Performance Report(Manager only) ===");
        System.out.println("=".repeat(60));
        System.out.printf("%-20s | %-15s | %-12s%n", "Employee Name", "Total Sales", "Transaction");
        System.out.println("-".repeat(60));
        
        for(Map.Entry<String, double[]> entry : list) {
            System.out.printf("%-20s | RM %-12.2f | %-12.0f%n", entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
        }
        System.out.println("=".repeat(60));
    }
}
