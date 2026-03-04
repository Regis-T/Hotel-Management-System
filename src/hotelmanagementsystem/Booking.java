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
// Base abstract type for all reservations (stores guest, room, dates, nights, and status).
public abstract class Booking {

    // Core reservation data shared by all booking types.
    protected Guest guest;
    protected Room room;
    protected String status;
    protected String checkInDate;
    protected String checkOutDate;
    protected String nightsStr;
    
    // Constructor to initialize all booking information.
    public Booking(Guest guest, Room room, String checkInDate, 
                   String checkOutDate, String nightsStr, String status) {
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.nightsStr = nightsStr;
        this.status = status;
    }
    
    // Prints a readable summary of the booking (implemented by each booking type).
    public abstract void displayInfo();
    
    // Getters and setters for accessing booking details.
    public Guest getGuest() { 
        return guest; 
    }
    
    public Room getRoom() { 
        return room; 
    }
    
    public String getCheckInDate() { 
        return checkInDate; 
    }
    
    public String getCheckOutDate() { 
        return checkOutDate; 
    }
    
    public String getNightsStr() { 
        return nightsStr; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public void setCheckInDate(String date) { 
        this.checkInDate = date; 
    }
    
    public void setCheckOutDate(String date) { 
        this.checkOutDate = date; 
    }
    
    public void setNightsStr(String nights) { 
        this.nightsStr = nights; 
    }
}