/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelmanagementsystem;

/**
 *
 *
 *
 * 
 * @author Regis
 */
// Represents a hotel guest and their contact details.
public class Guest extends Person {
    private String email;
    
    // Constructor initializes guest with name, surname, contact, and email.
    public Guest(String name, String surname, String contact, String email) {
        super(name, surname, contact); // Call parent class constructor.
        this.email = email;
    }
    
    // Prints guest info in one readable line.
    @Override
    public void displayInfo() {
        StringBuilder info = new StringBuilder();
        info.append("\nGuest: ").append(name).append(" ").append(surname)
            .append(" | Contact: ").append(contact)
            .append(" | Email: ").append(email);
        
        System.out.println(info.toString());
    }
    
    // Returns the guest email.
    public String getEmail() { 
        return email; 
    }
}