/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hotelmanagementsystem;

/**
 *
 *
 * 
 * @author Regis
 */
public class HotelManagementSystem {
    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("           HOTEL MANAGEMENT SYSTEM");
        System.out.println("=============================================\n");
        
        // Initialize the hotel manager.
        HotelManager manager = new HotelManager();
        
        System.out.println("System initialized successfully.");
        System.out.println("Loaded " + manager.getBookingCount() + " existing booking(s).");
        System.out.println("\nLaunching login interface...\n");
        
        // Launch the GUI.
        new LoginView(manager).setVisible(true);
    }
}