/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelmanagementsystem;

import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * Core manager for hotel bookings with data structure operations.
 *
 * 
 * @author Regis
 */
public class HotelManager {
    private Booking[] bookings;
    private int bookingCount;
    private File dataFile;
    
    // Constructor initializes the system and loads existing data.
    public HotelManager() {
        this.bookings = new Booking[50]; // Fixed-size array.
        this.bookingCount = 0;
        
        try {
            this.dataFile = FileUtility.initFile("data", "hotel_records.txt");
            loadFromFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "File system error: " + e.getMessage());
        }
    }

    // Searches for bookings by guest name or surname using linear search.
    public Booking[] findGuest(String keyword) {
        Booking[] results = new Booking[50];
        int count = 0;

        for (int i = 0; i < bookingCount; i++) {
            // Check both name and surname (case-insensitive).
            boolean matchesName = bookings[i].getGuest().getName().equalsIgnoreCase(keyword);
            boolean matchesSurname = bookings[i].getGuest().getSurname().equalsIgnoreCase(keyword);

            if (matchesName || matchesSurname) {
                results[count] = bookings[i];
                count = count + 1;
            }
        }
        return results;
    }

    // Sorts bookings by room number in ascending order using Bubble sort algorithm.
    public void sortByRoomNumber() {
        for (int i = 0; i < bookingCount - 1; i++) {
            for (int j = 0; j < bookingCount - i - 1; j++) {
                // Compare adjacent elements.
                if (bookings[j].getRoom().getRoomNumber() > 
                    bookings[j + 1].getRoom().getRoomNumber()) {
                    // Swap if they are in wrong order.
                    Booking temp = bookings[j];
                    bookings[j] = bookings[j + 1];
                    bookings[j + 1] = temp;
                }
            }
        }
    }

    // Calculates total price recursively.
    public double calculateTotalRecursive(double pricePerNight, int nights) {
        if (nights <= 1) {
            return pricePerNight; // Base case.
        }
        // Recursive call: add current night price to remaining nights.
        return pricePerNight + calculateTotalRecursive(pricePerNight, nights - 1);
    }

    // Removes a booking and shifts remaining elements.
    public void removeBooking(int index) {
        if (index >= 0 && index < bookingCount) {
            // Shift all elements after index to the left.
            for (int i = index; i < bookingCount - 1; i++) {
                bookings[i] = bookings[i + 1];
            }
            bookingCount = bookingCount - 1;
            saveToFile();
        }
    }

    // Loads bookings from file.
    public void loadFromFile() {
        try {
            bookingCount = 0;
            FileUtility.readAllBookings(dataFile, this);
        } catch (IOException e) {
            System.out.println("Load error: " + e.getMessage());
        }
    }

    // Saves bookings to file.
    public void saveToFile() {
        try {
            FileUtility.saveAllBookings(dataFile, bookings, bookingCount);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Save error: " + e.getMessage());
        }
    }

    // Adds a booking and saves to file.
    public void addBooking(Booking b) {
        if (bookingCount < bookings.length) {
            bookings[bookingCount] = b;
            bookingCount = bookingCount + 1;
            saveToFile();
        } else {
            JOptionPane.showMessageDialog(null, "Error: Storage is full.");
        }
    }
    
    // Overloaded method to add booking from strings.
    public void addBooking(String guestName, String surname, String roomNum, String nights) {
        try {
            Guest guest = new Guest(guestName, surname, "N/A", "N/A");
            int roomNumber = Integer.parseInt(roomNum);
            Room room = new StandardRoom(roomNumber);
            
            Booking booking = new ConfirmedBooking(guest, room, 
                                                   "N/A", "N/A", nights);
            addBooking(booking);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid room number format.");
        }
    }

    // Adds booking without saving (used during file loading).
    public void addBookingInternal(Booking b) {
        if (bookingCount < bookings.length) {
            bookings[bookingCount] = b;
            bookingCount = bookingCount + 1;
        }
    }

    // Getters for accessing data.
    public Booking[] getBookings() { 
        return bookings; 
    }
    
    public int getBookingCount() { 
        return bookingCount; 
    }
}