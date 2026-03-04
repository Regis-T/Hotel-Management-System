/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelmanagementsystem;

import java.util.regex.Pattern;
/**
 *
 *
 * 
 * @author Regis
 */
// Central place for validating all user inputs before creating bookings.
public class Validator {
    
    // Pattern used to validate basic email structure.
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]{3,20}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,10}$");
    
    // Pattern used to validate phone numbers like 123-456-7890.
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\d{3}-\\d{3}-\\d{4}$");
    
    // Pattern used to validate names that contain only letters.
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[A-Za-z]+$", Pattern.CASE_INSENSITIVE);
    
    // Allowed room number range used by the GUI when generating room numbers.
    private static final int MIN_ROOM = 100;
    private static final int MAX_ROOM = 150;
    
    // Returns true if the email matches the required format.
    public static boolean validEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    // Returns true if the phone matches XXX-XXX-XXXX.
    public static boolean validPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
     
    // Returns true if the name contains letters only.
    public static boolean validName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }
    
    // Returns true if the room number is within the allowed range.
    public static boolean validRoomNumber(int roomNumber) {
        return roomNumber >= MIN_ROOM && roomNumber <= MAX_ROOM;
    }
}