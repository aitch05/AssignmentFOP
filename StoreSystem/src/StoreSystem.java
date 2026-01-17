
import java.io.*;
import java.time.*;
import static java.time.LocalDate.now;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalTime;
import storesystem.Attendance;
import storesystem.Employee;
import storesystem.SaleRecord;
import storesystem.Stock;
import storesystem.EditInformation;
import storesystem.SearchInformation;

// ================= MAIN SYSTEM =================
public class StoreSystem {

    // CSV Files
    private static final String EMPLOYEE_FILE = "employee.csv";
    private static final String OUTLET_FILE = "outlet.csv";
    private static final String ATTENDANCE_FILE = "attendance.csv";
    private static final String STOCK_FILE = "model.csv";

    // In-memory storage
    private static List<Employee> employees = new ArrayList<>();
    private static List<Attendance> attendanceLogs = new ArrayList<>();
    private static Map<String, String> outletMap = new HashMap<>();
    private static List<Stock> stocks = new ArrayList<>();
    private static List<SaleRecord> salesHistory = new ArrayList<>();

    private static Scanner sc = new Scanner(System.in);
    private static Employee currentUser = null;

    // ================= MAIN =================
    public static void main(String[] args) {
        loadData();

        while (true) {
            if (currentUser == null) {
                loginMenu();
            } else {
                mainMenu();
            }
        }
    }

    // ================= LOGIN =================
    private static void loginMenu() {
        System.out.println("\n=== GOLDENHOUR STORE SYSTEM ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Select: ");

        switch (sc.nextLine()) {
            case "1": login(); break;
            case "2": System.exit(0);
            default: System.out.println("Invalid option.");
        }
    }

    private static void login() {
        System.out.print("User ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Password: ");
        String pass = sc.nextLine().trim();

        for (Employee e : employees) {
            if (e.getId().equalsIgnoreCase(id) && e.getPassword().equals(pass)) {
                currentUser = e;
                System.out.println("\nLogin Successful!");
                System.out.println("Welcome, " + e.getName());
                loadStock();
                return;
            }
        }
        System.out.println("Invalid login.");
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out.");
    }

    // ================= MAIN MENU =================
    private static void mainMenu() {
        System.out.println("\n=== Main Menu (" + currentUser.getName() + ") ===");
        System.out.println("1. Clock In");
        System.out.println("2. Clock Out");

        if (currentUser.getRole().equalsIgnoreCase("Manager")) {
            System.out.println("3. Register Employee");
            System.out.println("4. View Employee Performance Metrics");
            System.out.println("5. Close Store(Send Daily Report)");
        }
        System.out.println("6. Stock Management");
        System.out.println("7. Sales & Search Information");
        System.out.println("8. Edit Stock Information");
        System.out.println("9. Edit Sales Information");
        System.out.println("10. View Data Analytics");
        System.out.println("11. Filter and Sort Sales History");
        System.out.println("12. Logout");
        System.out.print("Select: ");

        switch (sc.nextLine()) {
            case "1": clockIn(); break;
            case "2": clockOut(); break;
            case "3":
                if (currentUser.getRole().equalsIgnoreCase("Manager")) registerEmployee();
                else System.out.println("Unauthorized.");
                break;
            case "4":
                if (currentUser.getRole().equalsIgnoreCase("Manager")) {
                    SearchInformation.employeePerformance((ArrayList<SaleRecord>) salesHistory);
                } else {
                    System.out.println("Unauthorized.");
                }
                break;
            case "5":
                if (currentUser.getRole().equalsIgnoreCase("Manager")) {
                    sendDailyReport();
                    System.out.println("Store records finalized. Logging out...");
                    logout();
                } else {
                    System.out.println("Unauthorized.");
                }
                break;
            case "6": stockMenu(); break;
            case "7": runSalesModule(); break;
            case "8": EditInformation.editStock(stocks, sc); saveStock(); break;
            case "9": EditInformation.EditSales(salesHistory, sc); break;
            case "10": SearchInformation.showAnalytics((ArrayList<SaleRecord>) salesHistory); break;
            case "11": SearchInformation.filterAndSortSales((ArrayList<SaleRecord>) salesHistory, sc); break;
            case "12": logout(); break;
            default: System.out.println("Invalid option.");
        }
    }

    // ================= EMPLOYEE =================
    private static void registerEmployee() {
        System.out.print("Name: ");
        String name = sc.nextLine();

        String id;
        String outlet;

        while (true) {
            System.out.print("Employee ID (e.g. C6013): ");
            id = sc.nextLine().trim();

            boolean exists = false;
            for (Employee e : employees) {
                if (e.getId().equalsIgnoreCase(id)) {
                    exists = true;break; // Stop searching once found
                }
            }

            if (exists) {
                System.out.println("ID already exists.");
                continue;
            }

            String code = id.substring(0, 3);
            if (outletMap.containsKey(code)) {
                outlet = code + " (" + outletMap.get(code) + ")";
                break;
            }
            System.out.println("Invalid outlet code.");
        }

        System.out.print("Set Role (Part-time/Full-time): ");
        String role = sc.nextLine();

        System.out.print("Set Password: ");
        String pass = sc.nextLine();

        employees.add(new Employee(id, name, role, pass, outlet));
        saveEmployees();

        System.out.println("Employee successfully registered!");
        System.out.println("Assigned to: " + outlet);
    }

    // ================= ATTENDANCE =================
    private static void clockIn() {
        LocalDate today = LocalDate.now();

        for (Attendance a : attendanceLogs) {
            if (a.getEmployeeId().equals(currentUser.getId())
                    && a.getDate().equals(today)
                    && a.getClockOut() == null) {
                System.out.println("Already clocked in.");
                return;
            }
        }

        LocalTime now = LocalTime.now();
        Attendance log = new Attendance(currentUser.getId(), today, LocalTime.now(), null, currentUser.getOutlet());
        attendanceLogs.add(log);
        saveAttendance();
        System.out.println("\nClock In Successful!");
        displayAttendance(log);
    }

    private static void clockOut() {
        LocalDate today = LocalDate.now();

        for (Attendance a : attendanceLogs) {
            if (a.getEmployeeId().equals(currentUser.getId())
                    && a.getDate().equals(today)
                    && a.getClockOut() == null) {

                a.setClockOut(LocalTime.now());
                saveAttendance();

                Duration d = Duration.between(a.getClockIn(), a.getClockOut());
                double hours = d.toMinutes() / 60.0;

                System.out.println("\nClock Out Successful!");
                displayAttendance(a);
                System.out.printf("Total Hours Worked: %.1f hours%n", hours);
                return;
            }
        }
        System.out.println("No active clock-in found.");
    }
    private static void displayAttendance(Attendance a) {
        System.out.println("Employee ID: " + currentUser.getId());
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Outlet: " + a.getOutlet());
        System.out.println("Date: " + a.getDate());
        System.out.println("Time: " + formatTime(
                a.getClockOut() == null ? a.getClockIn() : a.getClockOut())); 
    }
    
    // ================= STOCK =================
    private static void stockMenu() {
        loadStock();

        System.out.println("\n=== STOCK MANAGEMENT ===");
        System.out.println("1. Morning Count");
        System.out.println("2. Night Count");
        System.out.println("3. Stock In");
        System.out.println("4. Stock Out");
        System.out.println("5. Back");
        System.out.print("Select: ");

        switch (sc.nextLine()) {
            case "1": stockCount("Morning"); break;
            case "2": stockCount("Night"); break;
            case "3": stockIn(); break;
            case "4": stockOut(); break;
            case "5": return;
        }
    }

    private static void stockCount(String type) {
        System.out.println("\n=== " + type + " Stock Count ===");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Time: " + formatTime(LocalTime.now()));
        
        int correct = 0;
        int mismatch = 0;
        int totalModels = 0;
        
        if (stocks.isEmpty()) {
        System.out.println("Error: No models found for outlet " + currentUser.getOutlet());
        return;
    }
        for (Stock s : stocks) {
            totalModels++;
            System.out.print("Model: " + s.getModel() + " – Counted: ");
            int counted = Integer.parseInt(sc.nextLine());
            System.out.println("Store Record: " + s.getQuantity());

            if (counted == s.getQuantity()) {
            System.out.println("Stock tally correct.\n");
            correct++;
            } else {
            int diff = Math.abs(counted - s.getQuantity());
            System.out.println("! Mismatch detected (" + diff + " unit difference)\n");
            mismatch++;
            } 
        }
        // ===== SUMMARY =====
        System.out.println("Total Models Checked: " + totalModels);
        System.out.println("Tally Correct: " + correct);
        System.out.println("Mismatches: " + mismatch);
        
        if (mismatch > 0) {
        System.out.println(type + " stock count completed. Warning: Please verify stock.");
        } else {
            System.out.println(type + " stock count completed.");
        }
    }

    private static void stockIn() {
        loadStock();
        System.out.println("\n=== Stock In ===");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Time: " + formatTime(LocalTime.now()));
        System.out.print("From (HQ / Other Outlet): ");
        String from = sc.nextLine();
        String currentOutlet = currentUser.getOutlet().split(" ")[0];
        int totalQty = 0;
        
        Map<String, Integer> receivedModels = new LinkedHashMap<>();
        while (true) {
            System.out.print("Enter Model Name: ");
            String model = sc.nextLine();
            System.out.print("Quantity: ");
            int qty = Integer.parseInt(sc.nextLine());

            boolean found = false;
            for (Stock s : stocks) {
                if (s.getModel().equalsIgnoreCase(model) && s.getOutlet().equals(currentOutlet)) {
                    s.setQuantity(s.getQuantity() + qty);
                    found = true;
                    break;
                }
            }
           
            receivedModels.put(model, receivedModels.getOrDefault(model, 0) + qty);
            
            totalQty += qty;

            System.out.print("Add more models? (Y/N): ");
            if (!sc.nextLine().equalsIgnoreCase("Y")) break;
        }

        saveStock();
        // Display output
        System.out.println("\nFrom: " + from);
        System.out.println("To: " + currentOutlet);
        System.out.println("Models Received:");
        
        for (Map.Entry<String, Integer> entry : receivedModels.entrySet()) {
            System.out.println("- " + entry.getKey() + " (Quantity: " + entry.getValue() + ")");
        }
        System.out.println("Total Quantity: " + totalQty);
        System.out.println("Model quantities updated successfully.");
        System.out.println("Stock In recorded.");
        
        saveStock();
        generateReceipt("Stock In", from, currentOutlet, totalQty, currentUser.getName());
    }

    private static void stockOut() {
        loadStock();
        System.out.println("\n=== Stock Out ===");
        System.out.println("Date: " + LocalDate.now());
        System.out.println("Time: " + formatTime(LocalTime.now()));
        System.out.print("To (Outlet / Customer): ");
        String to = sc.nextLine();
        String currentOutlet = currentUser.getOutlet().split(" ")[0];
        int totalQty = 0;
        
        while (true) {
            System.out.print("Enter Model Name: ");
            String model = sc.nextLine();
            System.out.print("Quantity: ");
            int qty = Integer.parseInt(sc.nextLine());

            boolean modelFound = false;
            for (Stock s : stocks) {
                if (s.getModel().equalsIgnoreCase(model) && s.getOutlet().equals(currentOutlet)) {
                    if (s.getQuantity() < qty) {
                        System.out.println("Error: Insufficient stock.");
                        modelFound = true;
                         break;
                    }

                    s.setQuantity(s.getQuantity() - qty);
                    totalQty += qty;
                    modelFound = true;
                    break;
                }
            }

            if (!modelFound) {
                System.out.println("Error: Model not found in this outlet.");
            }

            System.out.print("Add more models? (Y/N): ");
            if (!sc.nextLine().equalsIgnoreCase("Y")) break;
        }

        saveStock();
        
        generateReceipt("Stock Out", currentOutlet, to, totalQty, currentUser.getName());
    }
    
    private static void generateReceipt(String type, String from, String to, int totalQty, String employee) {
        String fileName = "stock_receipt_" + LocalDate.now() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName, true))) {
            pw.println("=== " + type.toUpperCase() + " ===");
            pw.println("Date: " + LocalDate.now());
            pw.println("Time: " + formatTime(LocalTime.now()));
            pw.println("From: " + from);
            pw.println("To: " + to);
            pw.println("Total Quantity: " + totalQty);
            pw.println("Handled by: " + employee);
            pw.println("----------------------------");
            
            System.out.println("Stock updated successfully.");
            System.out.println("Receipt generated: " + fileName);
        } catch (IOException e) {
            System.out.println("Error generating receipt file.");
        }
    }
    // ================= FILE HANDLING =================
    private static void loadData() {
        try {
           //Load outlets
            Scanner oSc = new Scanner(new File(OUTLET_FILE));
            while (oSc.hasNextLine()) {
                String[] p = oSc.nextLine().split(",");
                outletMap.put(p[0].trim(), p[1].trim());
            }
            oSc.close();

            //Load employees
            Scanner eSc = new Scanner(new File(EMPLOYEE_FILE));
            while (eSc.hasNextLine()) {
                String[] p = eSc.nextLine().split(",");

                String id = p[0].trim();
                String name = p[1].trim();
                String role = p[2].trim();
                String pass = p[3].trim();

                String code = id.substring(0, 3);
                String outlet = code + " (" + outletMap.get(code) + ")";
               employees.add(new Employee(id, name, role, pass, outlet));
            }            eSc.close();
        } catch (Exception e) {
            System.out.println("Error loading data.");
            }
        loadStock();
    }

    private static void loadStock() {
        stocks.clear(); // Clear existing list to prevent duplicates
        if (currentUser == null) return;

        File file = new File("model.csv");
        if (!file.exists()) return;

        try (Scanner fs = new Scanner(file)) {
            if (!fs.hasNextLine()) return;

            // 1. Find which column belongs to this user's outlet
            String headerLine = fs.nextLine();
            String[] headers = headerLine.split(",");
            int outletColumnIndex = -1;
            String userOutletCode = currentUser.getOutlet().split(" ")[0];

            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(userOutletCode)) {
                    outletColumnIndex = i;
                    break;
                }
            }

            if (outletColumnIndex == -1) {
                System.out.println("Error: Column for " + userOutletCode + " not found in model.csv");
                return;
            }

            // 2. Load the models and the quantities from that specific column
            while (fs.hasNextLine()) {
                String line = fs.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                String modelName = parts[0];
                double price = Double.parseDouble(parts[1].trim());
                int qty = Integer.parseInt(parts[outletColumnIndex].trim());
                
                Stock s = new Stock(modelName, qty, userOutletCode, price);
                stocks.add(s);
            }
        } catch (Exception e) {
            System.out.println("Error loading stock: " + e.getMessage());
        }
    }

    private static void saveStock() {

    File file = new File("model.csv");
    List<String[]> rows = new ArrayList<>();

    try (Scanner sc = new Scanner(file)) {
        while (sc.hasNextLine()) {
            rows.add(sc.nextLine().split(","));
        }
    } catch (Exception e) {
        System.out.println("Error reading model.csv");
        return;
    }

    String userOutletCode = currentUser.getOutlet().split(" ")[0];

    // Find outlet column index
    String[] header = rows.get(0);
    int outletIndex = -1;

    for (int i = 0; i < header.length; i++) {
        if (header[i].equalsIgnoreCase(userOutletCode)) {
            outletIndex = i;
            break;
        }
    }

    if (outletIndex == -1) return;

    // Update quantities
    for (int i = 1; i < rows.size(); i++) {
        String modelName = rows.get(i)[0];

        for (Stock s : stocks) {
            if (s.getModel().equalsIgnoreCase(modelName)) {
                rows.get(i)[outletIndex] = String.valueOf(s.getQuantity());
            }
        }
    }

    // Write back
    try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
        for (String[] row : rows) {
            pw.println(String.join(",", row));
        }
    } catch (Exception e) {
        System.out.println("Error saving stock");
    }
}

    private static void saveEmployees() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(EMPLOYEE_FILE))) {
            for (Employee e : employees) {
                pw.println(e.getId() + "," + e.getName() + "," + e.getRole() + "," + e.getPassword());
            }
        } catch (IOException e) {
            System.out.println("Error saving employees.");
        }
    }

    private static void saveAttendance() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ATTENDANCE_FILE, true))) {
            for (Attendance a : attendanceLogs) pw.println(a.toCSV());
        } catch (IOException e) {
            System.out.println("Error saving attendance.");
        }
    }
    
    private static void runSalesModule() {
    boolean back = false;
    while (!back) {
        System.out.println("\n=== SALES & SEARCH ===");
        System.out.println("1. Record New Sale");
        System.out.println("2. Search Stock (This Outlet)");
        System.out.println("3. Search Sales History");
        System.out.println("4. Back");
        System.out.print("> ");
        String choice = sc.nextLine();

        switch (choice) {
            case "1": performNewSale(); break;
            case "2": performStockSearch(); break;
            case "3": performSalesSearch(); break;
            case "4": back = true; break;
        }
    }
}

private static void performNewSale() {
    System.out.println("=== Record New Sale ===");
    System.out.println("Date: " + LocalDate.now());
    System.out.println("Time: " + formatTime(LocalTime.now()));
    String date = LocalDate.now().toString();
    String time = formatTime(LocalTime.now());
    System.out.print("Customer Name: ");
    String customer = sc.nextLine();
    
    double subtotal = 0;
    String choice = null;
    
    System.out.println("Item(s) Purchased:");
    do {
    System.out.print("Model Name: ");
    String model = sc.nextLine();
    
    // Find model in the 'stocks' list already loaded by the system
    Stock target = null;
    for (Stock s : stocks) {
        if (s.getModel().equalsIgnoreCase(model)) {
            target = s;
            break;
        }
    }

    if (target == null) {
        System.out.println("Product not found in this outlet.");
        continue;
    }
    
    System.out.print("Enter Quantity: ");
    int qty = Integer.parseInt(sc.nextLine());
    
    if (qty <= 0 || qty > target.getQuantity()) {
        System.out.println("Insufficient stock!");
        continue;
    }
    target.setQuantity(target.getQuantity() - qty);
    double itemTotal = target.getPrice() * qty;
    subtotal += itemTotal;
    
    SaleRecord s = new SaleRecord(
        LocalDate.now().toString(), 
        formatTime(LocalTime.now()), 
        customer, model, qty, itemTotal, "", 
        currentUser.getName() // Captures current employee name
    );
    salesHistory.add(s);
    
    System.out.println("Unit Price: RM" + target.getPrice());
    System.out.print("\nAre there more items purchased? (Y/N): ");
    choice = sc.nextLine();
    } while(choice.equalsIgnoreCase("Y"));
    
    System.out.print("Enter transaction method: ");
    String method = sc.nextLine();
    System.out.println("Subtotal: RM" + subtotal);
    System.out.println();

// Update all the items from this transaction with method
    for (SaleRecord s : salesHistory) {
    if (s.getCustomerName().equals(customer) &&
        s.getDate().equals(date) &&
        s.getMethod().isEmpty()) {
        s.setMethod(method);
        generateSalesReceipt(s);
    }
}
    
    saveStock(); // Save update to model.csv
    System.out.println("\nTransaction" + " successful.");
    System.out.println("Sale Recorded Successfully!");
    System.out.println("Model quantities updated successfully.");
    System.out.println("Receipt generated: sales_" + LocalDate.now() + ".txt");
} 

// --- FEATURE: SEARCH STOCK ---
private static void performStockSearch() {
    System.out.println("\n=== Search Stock Information ===");
    System.out.print("Search Model Name: ");
    String modelName = sc.nextLine();
    System.out.print("Searching...\n");
    System.out.println();
    SearchInformation.searchStock(modelName, (ArrayList<Stock>) stocks, outletMap);
}

// --- FEATURE: SEARCH SALES ---
private static void performSalesSearch() {
    System.out.println("\n=== Search Sales Information ===");
    System.out.print("Search keyword (date/ customer name/ model name): ");
    String keyword = sc.nextLine();
    System.out.print("Searching...\n");

    SearchInformation.searchSales(keyword, (ArrayList<SaleRecord>) salesHistory);
}

// --- HELPER: GENERATE SALES RECEIPT FILE ---
 private static void generateSalesReceipt(SaleRecord s) {
    String fileName = "sales_receipt_" + s.getDate() + ".txt";
    try (PrintWriter out = new PrintWriter(new FileWriter(fileName, true))) {
        out.println("=== OFFICIAL RECEIPT ===");
        out.println("Date: " + s.getDate() + "\tTime: " + s.getTime());
        out.println("Customer: " + s.getCustomerName());
        out.println("Item: " + s.getModelName() + "\tQuantity: " + s.getQuantity());
        out.println("Total: RM" + s.getTotal());
        out.println("Payment Method: " + s.getMethod());
        out.println("Employee: " + s.getEmployee());
        out.println("---------------------------");
    } catch (IOException e) {
        System.out.println("Error saving receipt file.");
    }
  }

private static String formatTime(LocalTime t) {
        return t.format(DateTimeFormatter.ofPattern("hh:mm a"))
                .toLowerCase()
                .replace("am", "a.m.")
                .replace("pm", "p.m.");
    }

private static void sendDailyReport() {
    LocalDate today = LocalDate.now();
    LocalTime now = LocalTime.now();
    LocalTime end = LocalTime.of(22, 0);
    
    if(now.isAfter(end)) {
        System.out.println("Warning: It is past 10.00 pm. Report should have been sent");
    }
    
    double dailyTotal = 0;
    for(SaleRecord s : salesHistory) {
        if(s.getDate().equals(today.toString())){
            dailyTotal += s.getTotal();
        }
    }
    String fileName = "sales_receipt_" + today + ".txt";
    System.out.println("\n=== Automated Email System ===");
    System.out.println("Recipient: 24001391@siswa.um.edu.my");
    System.out.println("Subject: Daily Sales Report - " + today);
    System.out.println("Body: Hello HQ, the total sales for " + today + " is RM" + String.format("%.2f", dailyTotal));
    System.out.println("Attach: [" + fileName + "]");
    System.out.println("Status: Email sent successfully at" + now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    System.out.println("-------------------------------------\n");
}

}