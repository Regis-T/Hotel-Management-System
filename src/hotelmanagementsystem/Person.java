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
// Common base type for people in the system (Guest and Employee).
public abstract class Person {
    // Basic identity and contact details.
    protected String name;
    protected String surname;
    protected String contact;
    
    // Constructor to initialize person details.
    public Person(String name, String surname, String contact) {
        this.name = name;
        this.surname = surname;
        this.contact = contact;
    }
    
    // Prints this person's details in a readable format.
    public abstract void displayInfo();
    
    public String getName() { 
        return name; 
    }
    
    public String getSurname() { 
        return surname; 
    }
    
    public String getContact() { 
        return contact; 
    }
}