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
// Deluxe room that adds a nightly services fee on top of the base price.
public class DeluxeRoom extends Room {
    
    // Constructor sets room number, type, and a fixed base rate.
    public DeluxeRoom(int roomNumber) {
        super(roomNumber, "Deluxe", 220.0);
    }
    
    // Calculates total price including the nightly service fee.
    @Override
    public double getFinalPrice(int nights) {
        double baseTotal = this.price * nights;
        double serviceFee = 30.0 * nights; // $30 per night for services.
        return baseTotal + serviceFee;
    }
    
    // Prints a detailed deluxe bill for the selected nights.
    @Override
    public void displayInfo(int nights) {
        double finalPrice = getFinalPrice(nights);
        
        StringBuilder sb = new StringBuilder();
        sb.append("--- DELUXE ROOM BILL ---\n")
          .append("Room Number: ").append(roomNumber).append("\n")
          .append("Stay: ").append(nights).append(" nights\n")
          .append("Includes: Spa, Gym, and Pool Access\n")
          .append("Daily Service Fee: $30.00\n")
          .append("TOTAL BILL: $").append(finalPrice);
        
        System.out.println(sb.toString());
    }
}