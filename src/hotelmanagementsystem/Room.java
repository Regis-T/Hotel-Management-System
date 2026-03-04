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
// Base room type that stores pricing and availability logic shared by all rooms.
public abstract class Room implements Reservable {
    protected int roomNumber;
    protected String roomType;
    protected double price;
    protected boolean available;
    
    // Constructor to initialize room details.
    public Room(int roomNumber, String roomType, double price) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.available = true;
    }
    
    // Returns the final stay price for a given number of nights.
    public abstract double getFinalPrice(int nights);
    
    // Prints a simple bill/summary for the given stay duration.
    public abstract void displayInfo(int nights);
    
    // Getters for room properties.
    public int getRoomNumber() { 
        return roomNumber; 
    }
    
    public String getRoomType() { 
        return roomType; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    public boolean isAvailable() { 
        return available; 
    }
    
    // Marks this room as occupied when a reservation is confirmed.
    @Override
    public void reserve(Booking b) {
        this.available = false;
        System.out.println("Console: Room #" + roomNumber + " is now Occupied.");
    }
    
    // Marks this room as vacant when a reservation is cancelled or removed.
    @Override
    public void cancel(Booking b) {
        this.available = true;
        System.out.println("Console: Room #" + roomNumber + " is now Vacant.");
    }
}