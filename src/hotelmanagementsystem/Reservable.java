/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package hotelmanagementsystem;

/**
 *
 *
 * 
 * @author Regis
 */
// Defines actions for confirming and cancelling reservations.
public interface Reservable {

    // Marks room as occupied.
    void reserve(Booking booking);

    // Marks room as vacant.
    void cancel(Booking booking);
}