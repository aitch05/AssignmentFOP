import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import storesystem.Attendance;
import storesystem.Employee;
import storesystem.SaleRecord;
import storesystem.Stock;
import storesystem.EditInformation;

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
        loadInitialData();

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
        }

        System.out.println("4. Stock Management");
        System.out.println("5. Sales & Search Information");
        System.out.println("6. Edit Stock Information");
        System.out.println("7. Edit Sales Information");
        System.out.println("8. Logout");
        System.out.print("Select: ");

        switch (sc.nextLine()) {
            case "1": clockIn(); break;
            case "2": clockOut(); break;
            case "3":
                if (currentUser.getRole().equalsIgnoreCase("Manager")) registerEmployee();
                else System.out.println("Unauthorized.");
                break;
            case "4": stockMenu(); break;
            case "5": runSalesModule(); break;
            case "6": EditInformation.editStock(stocks, sc); saveStock(); break;
            case "7": EditInformation.EditSales(salesHistory, sc); break;
            case "8": logout(); break;
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

        System.out.print("Role (Manager / Full-time / Part-time): ");
        String role = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        employees.add(new Employee(id, name, role, pass, outlet));
        saveEmployees();

        System.out.println("Employee registered successfully.");
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

        Attendance a = new Attendance(
                currentUser.getId(),
                today,
                LocalTime.now(),
                null,
                currentUser.getOutlet()
        );

        attendanceLogs.add(a);
        saveAttendance();
        System.out.println("Clock-in successful.");
    }

    private static void clockOut() {
        LocalDate today = LocalDate.now();

        for (Attendance a : attendanceLogs) {
            if (a.getEmployeeId().equals(currentUser.getId())
                    && a.getDate().equals(today)
                    && a.getClockOut() == null) {

                a.setClockOut(LocalTime.now());
                saveAttendance();

                double hours = Duration.between(a.getClockIn(), a.getClockOut())
                        .toMinutes() / 60.0;

                System.out.printf("Clock-out successful. Hours worked: %.1f%n", hours);
                return;
            }
        }
        System.out.println("No active clock-in found.");
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
        }
    }

    private static void stockCount(String type) {
        System.out.println("\n=== " + type + " Stock Count ===");

        for (Stock s : stocks) {
            System.out.print("Model " + s.getModel() + " (record " + s.getQuantity() + ") counted: ");
            int counted = Integer.parseInt(sc.nextLine());

            if (counted != s.getQuantity()) {
                System.out.println("Mismatch detected.");
            }
        }
    }

    private static void stockIn() {
        System.out.print("Model: ");
        String model = sc.nextLine();
        System.out.print("Quantity: ");
        int qty = Integer.parseInt(sc.nextLine());

        for (Stock s : stocks) {
            if (s.getModel().equalsIgnoreCase(model)) {
                s.setQuantity(s.getQuantity() + qty);
                saveStock();
                System.out.println("Stock updated.");
                return;
            }
        }
        System.out.println("Model not found.");
    }

    private static void stockOut() {
        System.out.print("Model: ");
        String model = sc.nextLine();
        System.out.print("Quantity: ");
        int qty = Integer.parseInt(sc.nextLine());

        for (Stock s : stocks) {
            if (s.getModel().equalsIgnoreCase(model)) {
                if (s.getQuantity() < qty) {
                    System.out.println("Insufficient stock.");
                    return;
                }
                s.setQuantity(s.getQuantity() - qty);
                saveStock();
                System.out.println("Stock updated.");
                return;
            }
        }
        System.out.println("Model not found.");
    }

    // ================= FILE HANDLING =================
    private static void loadInitialData() {
        loadOutlets();
        loadEmployees();
    }

    private static void loadOutlets() {
        try (Scanner fs = new Scanner(new File(OUTLET_FILE))) {
            while (fs.hasNextLine()) {
                String[] p = fs.nextLine().split(",");
                outletMap.put(p[0].trim(), p[1].trim());
            }
        } catch (Exception e) {
            System.out.println("Error loading outlets.");
        }
    }

    private static void loadEmployees() {
        try (Scanner fs = new Scanner(new File(EMPLOYEE_FILE))) {
            while (fs.hasNextLine()) {
                String[] p = fs.nextLine().split(",");
                String code = p[0].substring(0, 3);
                String outlet = code + " (" + outletMap.get(code) + ")";
                employees.add(new Employee(p[0], p[1], p[2], p[3], outlet));
            }
        } catch (Exception e) {
            System.out.println("Error loading employees.");
        }
    }

    private static void loadStock() {
        stocks.clear();
        if (currentUser == null) return;

        try (Scanner fs = new Scanner(new File(STOCK_FILE))) {
            String[] headers = fs.nextLine().split(",");
            String outletCode = currentUser.getOutlet().split(" ")[0];

            int col = Arrays.asList(headers).indexOf(outletCode);

            while (fs.hasNextLine()) {
                String[] p = fs.nextLine().split(",");
                stocks.add(new Stock(p[0], Integer.parseInt(p[col]), outletCode));
            }
        } catch (Exception e) {
            System.out.println("Error loading stock.");
        }
    }

    private static void saveStock() {
        // same logic you already implemented earlier (kept short here)
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
    System.out.print("Customer Name: ");
    String customer = sc.nextLine();
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

    if (target != null && target.getQuantity() > 0) {
        System.out.print("Quantity: ");
        int qty = Integer.parseInt(sc.nextLine());
        
        if (target.getQuantity() >= qty) {
            target.setQuantity(target.getQuantity() - qty);
            double total = target.getPrice() * qty;
            
            System.out.print("Payment Method: ");
            String method = sc.nextLine();

            // Auto-capture data from the logged-in session!
            SaleRecord s = new SaleRecord(
                LocalDate.now().toString(), 
                formatTime(LocalTime.now()), 
                customer, model, qty, total, method, 
                currentUser.getName() // Captures current employee name
            );
            
            salesHistory.add(s);
            saveStock(); // Save update to model.csv
            generateSalesReceipt(s);
            System.out.println("Sale Recorded Successfully!");
        } else {
            System.out.println("Insufficient stock!");
        }
    } else {
        System.out.println("Product not found in this outlet.");
    }
}

// --- FEATURE: SEARCH STOCK ---
private static void performStockSearch() {
    System.out.print("Search Model Name: ");
    String keyword = sc.nextLine();
    boolean found = false;

    // Use the 'stocks' list that was loaded during login
    for (Stock s : stocks) {
        if (s.getModel().equalsIgnoreCase(keyword)) {
            System.out.println("\n--- Product Found ---");
            System.out.println("Model: " + s.getModel());
            System.out.println("Price: RM" + s.getPrice());
            System.out.println("Available Stock (" + s.getOutlet() + "): " + s.getQuantity());
            found = true;
            break;
        }
    }
    if (!found) System.out.println("Product not found in current outlet.");
}

// --- FEATURE: SEARCH SALES ---
private static void performSalesSearch() {
    boolean found = false;
    System.out.print("Search keyword (Date/Customer/Model): ");
    String key = sc.nextLine().toLowerCase();

    for (SaleRecord s : salesHistory) {
        if (s.customerName.toLowerCase().contains(key) || s.getmodelName.toLowerCase().contains(key) || 
            s.getdate.contains(key)) {
            
            System.out.println("Found Record: [" + s.date + "] " + s.customerName + 
                               " purchased " + s.modelName + " (RM" + s.total + ")");
            found = true;
        }
    }
    if (!found) System.out.println("No records match '" + key + "'.");
}

// --- HELPER: GENERATE SALES RECEIPT FILE ---
private static void generateSalesReceipt(SaleRecord s) {
    String fileName = "sales_receipt_" + LocalDate.now() + ".txt";
    try (PrintWriter out = new PrintWriter(new FileWriter(fileName, true))) {
        out.println("=== OFFICIAL RECEIPT ===");
        out.println("Date: " + s.date + " | Time: " + s.time);
        out.println("Customer: " + s.customerName);
        out.println("Item: " + s.modelName + " x" + s.quantity);
        out.println("Total: RM" + s.total);
        out.println("Payment: " + s.method);
        out.println("Staff: " + s.employee);
        out.println("---------------------------");
    } catch (IOException e) {
        System.out.println("Error saving receipt file.");
    }
  }

}