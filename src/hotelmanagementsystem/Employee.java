/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelmanagementsystem;

/**
 *
 *
 * 
 * @author Regis
 */
// Represents a staff member who can confirm or cancel bookings.
public class Employee extends Person implements Reservable {
    private String role;
    
    // Constructor initializes employee with name, surname, contact, and role.
    public Employee(String name, String surname, String contact, String role) {
        super(name, surname, contact); // Call parent constructor.
        this.role = role;
    }
    
    // Prints staff info.
    @Override
    public void displayInfo() {
        System.out.println("Hotel Staff: " + name + " " + surname + " (Role: " + role + ")");
    }
    
    // Shows a reservation confirmation action performed by this employee.
    @Override
    public void reserve(Booking booking) {
        System.out.println("Action: Booking confirmed by " + name + " (" + role + ")\n");
    }
    
    // Shows a reservation cancellation action performed by this employee.
    @Override
    public void cancel(Booking booking) {
        System.out.println("Action: Booking cancelled by " + name + " (" + role + ")\n");
    }
    
    // Returns the employee role.
    public String getRole() {
        return role;
    }
}