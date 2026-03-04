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
// Standard room that applies a discount based on stay length.
public class StandardRoom extends Room {
    
    // Constructor sets room number, type, and a fixed base rate.
    public StandardRoom(int roomNumber) {
        super(roomNumber, "Standard", 120.0);
    }
    
    // Uses recursion to give $20 discount for every 2 nights stayed.
    private double calculateDiscountRecursive(int nights) {
        if (nights < 2) {
            return 0; // Base case: no discount for less than 2 nights.
        }
        // Recursive step: apply $20 discount per two-night interval.
        return 20.0 + calculateDiscountRecursive(nights - 2);
    }
    
    // Calculates total price after applying the stay discount.
    @Override
    public double getFinalPrice(int nights) {
        double baseTotal = this.price * nights;
        double discount = calculateDiscountRecursive(nights);
        return baseTotal - discount;
    }
    
    // Prints a standard bill including the discount rule.
    @Override
    public void displayInfo(int nights) {
        double finalPrice = getFinalPrice(nights);
        
        StringBuilder sb = new StringBuilder();
        sb.append("--- STANDARD ROOM BILL ---\n")
          .append("Room Number: ").append(roomNumber).append("\n")
          .append("Stay: ").append(nights).append(" nights\n")
          .append("Benefit: $20 discount every 2 nights\n")
          .append("TOTAL BILL: $").append(finalPrice);
        
        System.out.println(sb.toString());
    }
}