/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelmanagementsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 *
 * 
 * @author Regis
 */
// Handles reading and writing bookings to the local data file.
public class FileUtility {
    
    // Ensures the data folder and file exist before reading/writing.
    public static File initFile(String dirName, String fileName) throws IOException {
        File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdir();
        }
        
        File file = new File(dirName, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
    
    // Writes all current bookings to the file using a simple CSV format.
    public static void saveAllBookings(File file, Booking[] list, int count) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        
        for (int i = 0; i < count; i++) {
            // Save with dates and status (Pending/Confirmed).
            String line = list[i].getGuest().getName() + "," + 
                         list[i].getGuest().getSurname() + "," +
                         list[i].getRoom().getRoomNumber() + "," + 
                         list[i].getNightsStr() + "," +
                         list[i].getCheckInDate() + "," +
                         list[i].getCheckOutDate() + "," +
                         list[i].getStatus();
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }
    
    // Loads bookings from the file and rebuilds objects in memory.
    public static void readAllBookings(File file, HotelManager manager) throws IOException {
        if (!file.exists()) {
            return;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");

            if (data.length >= 7) {
                Guest guest = new Guest(data[0], data[1], "N/A", "N/A");
                int roomNum = Integer.parseInt(data[2]);
                Room room = new StandardRoom(roomNum);

                String nights = data[3];
                String checkIn = data[4];
                String checkOut = data[5];
                String status = data[6];

                // System statuses used: "Pending" and "Confirmed" only.
                Booking booking;
                if (status.equals("Pending")) {
                    booking = new PendingBooking(guest, room, checkIn, checkOut, nights);
                } else {
                    booking = new ConfirmedBooking(guest, room, checkIn, checkOut, nights);
                    booking.setStatus("Confirmed"); // Force non-pending to Confirmed on reload.
                }

                manager.addBookingInternal(booking);
            }
        }
        reader.close();
    }
}