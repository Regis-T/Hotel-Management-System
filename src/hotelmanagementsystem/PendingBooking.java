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
// Represents a reservation request that is waiting to be confirmed.
public class PendingBooking extends Booking implements Searchable {
    
    // Constructor initializes pending booking.
    public PendingBooking(Guest guest, Room room, String checkInDate, 
                         String checkOutDate, String nightsStr) {
        super(guest, room, checkInDate, checkOutDate, nightsStr, "Pending");
    }
    
    // Builds a readable booking summary using StringBuilder and prints it to the console.
    @Override
    public void displayInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- PENDING RESERVATION ---\n")
          .append("Guest: ").append(guest.getName()).append(" ").append(guest.getSurname()).append("\n")
          .append("Room: #").append(room.getRoomNumber()).append(" (").append(room.getRoomType()).append(")\n")
          .append("Period: ").append(checkInDate).append(" to ").append(checkOutDate).append("\n")
          .append("Stay Duration: ").append(nightsStr).append(" nights\n")
          .append("Status: ").append(status);
        
        System.out.println(sb.toString());
    }
    
    // Returns a combined text key used by the search feature.
    @Override
    public String getSearchKey() {
        return guest.getName() + " " + guest.getSurname() + " " + room.getRoomType() + " " + status;
    }
}