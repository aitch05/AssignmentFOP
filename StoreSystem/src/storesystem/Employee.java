/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package storesystem;

/**
 *
 * @author Nur Hasna Nadirah
 */
public class Employee {
    private String id, name, role, password, outlet;

    public Employee(String id, String name, String role, String password, String outlet) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.password = password;
        this.outlet = outlet;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
    public String getOutlet() { return outlet; }
}
