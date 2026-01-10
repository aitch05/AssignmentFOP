/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package storesystem;

/**
 *
 * @author Nur Hasna Nadirah
 */
import java.time.*;

public class Attendance {
    private String employeeId, outlet;
    private LocalDate date;
    private LocalTime clockIn, clockOut;

    public Attendance(String id, LocalDate date, LocalTime in, LocalTime out, String outlet) {
        this.employeeId = id;
        this.date = date;
        this.clockIn = in;
        this.clockOut = out;
        this.outlet = outlet;
    }

    public String getEmployeeId() { return employeeId; }
    public LocalDate getDate() { return date; }
    public LocalTime getClockIn() { return clockIn; }
    public LocalTime getClockOut() { return clockOut; }
    public String getOutlet() { return outlet; }

    public void setClockOut(LocalTime t) { clockOut = t; }

    public String toCSV() {
        return employeeId + "," + date + "," + clockIn + "," +
                (clockOut == null ? "" : clockOut) + "," + outlet;
    }
}
