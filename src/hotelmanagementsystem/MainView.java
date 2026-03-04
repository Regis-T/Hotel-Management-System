/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package hotelmanagementsystem;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Main interface with 4 views: Dashboard, Add Bookings, View/Search, Check In/Out.
 *
 * 
 * @author Regis
 */
public class MainView extends javax.swing.JFrame {
    
    // Logger used mainly for UI initialization and look-and-feel errors.
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainView.class.getName());
    
    // Shared manager that stores bookings and handles file persistence.
    private final HotelManager manager;
    
    // Model used to populate the search results list.
    private DefaultListModel<String> listModel;
    
    // For recent Activity.
    private Employee currentEmployee;
    
    // Table model that stores the recent activity rows.
    private DefaultTableModel activityTableModel;

    
    // Constructor, sets up everything when window opens.
    public MainView(HotelManager manager,Employee employee) {
        this.manager = manager;
        this.currentEmployee = employee;
        this.listModel = new DefaultListModel<>();
        this.setTitle("Hotel Management System - Main View");
        
        initComponents();

        // Sidebar buttons switch tabs without allowing direct tab clicks.
        dashboardButton.addActionListener(e -> jTabbedPane1.setSelectedIndex(0));
        addBookingsButton.addActionListener(e -> jTabbedPane1.setSelectedIndex(1));
        viewSearchButton.addActionListener(e -> jTabbedPane1.setSelectedIndex(2));
        checkInOutButton.addActionListener(e -> jTabbedPane1.setSelectedIndex(3));

        this.setLocationRelativeTo(null);
        
        // Connect the list model to the JList used in the View/Search tab.
        searchResultsList.setModel(listModel);
        
        // Builds the activity table structure (columns + formatting).
        setupActivityTable();
        
        // Hide admin-only features for receptionists.
        hideAdminFeatures();
        
        // Recalculate the estimate whenever room type or nights change.
        roomTypeComboBox.addActionListener(e -> updateTotalPrice());
        nightsStayingSpinner.addChangeListener(e -> updateTotalPrice());
        
        // Load all data when window opens.
        updateComboBoxes();
        loadDashboardData();
        updateTotalPrice();
        loadSearchResults();
        
        // Records the login action as the first activity entry.
        showActivity("System", "Logged in to system");
    }
    
    // Hide features for non-admin users.
    private void hideAdminFeatures() {
        boolean isAdmin = currentEmployee.getRole().equals("Administrator");
        
        if (!isAdmin) {
            // Hide Total Revenue for receptionists.
            totalRevenueLabel.setVisible(false);
            jPanel4.setVisible(false);
        }
    }
    
    // Configures the activity table with headers and column widths.
    private void setupActivityTable() {
        activityTableModel = new DefaultTableModel(
            new String[]{"Employee", "Action", "Details", "Time"}, 0
        );
        recentActivity.setModel(activityTableModel);
        
        // Set column widths for better display.
        recentActivity.getColumnModel().getColumn(0).setPreferredWidth(100);
        recentActivity.getColumnModel().getColumn(1).setPreferredWidth(120);
        recentActivity.getColumnModel().getColumn(2).setPreferredWidth(200);
        recentActivity.getColumnModel().getColumn(3).setPreferredWidth(80);
    }
    
    // Inserts an activity row at the top and also prints the same info to the console.
    private void showActivity(String action, String details) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String time = timeFormat.format(new Date());

        // Add to table at top (most recent first).
        activityTableModel.insertRow(0, new Object[]{
            currentEmployee.getName() + " (" + currentEmployee.getRole() + ")",
            action,
            details,
            time
        });

        // Keep only last 20 activities.
        if (activityTableModel.getRowCount() > 20) {
            activityTableModel.removeRow(20);
        }

        // Console output.
        System.out.println("\n=== Activity ===");
        currentEmployee.displayInfo();
        System.out.println("Action: " + action);
        System.out.println("Details: " + details);
        System.out.println("Time: " + time);
    }
    
    // Updates dashboard stats like booking count, available rooms, and revenue estimate.
    private void loadDashboardData() {
        int totalBookings = manager.getBookingCount();
        int availableRooms = 50 - totalBookings;
        
        // Calculate revenue using the manager's recursive method.
        double revenue = 0;
        for (int i = 0; i < manager.getBookingCount(); i++) {
            Booking b = manager.getBookings()[i];
            try {
                int nights = Integer.parseInt(b.getNightsStr());
                double pricePerNight = b.getRoom().getFinalPrice(nights) / nights;
                revenue = revenue + manager.calculateTotalRecursive(pricePerNight, nights);
            } catch (Exception e) {
                // Skip if error.
            }
        }
        
        // Update dashboard labels.
        totalBookingsLabel.setText("Total Bookings: " + totalBookings);
        availableRoomsLabel.setText("Available Rooms: " + availableRooms);
        totalRevenueLabel.setText("Total Revenue: $" + String.format("%.2f", revenue));
        
         // Welcome text includes the logged-in user.
        welcomeBackLabel.setText("Welcome, " + currentEmployee.getName() + 
                                 " (" + currentEmployee.getRole() + "). Here's what's happening today.");
    }

    // Refreshes both combo boxes so they always match the current bookings list.
    private void updateComboBoxes() {
        guestComboBox.removeAllItems();
        CheckOutComboBox.removeAllItems();

        if (manager.getBookingCount() == 0) {
            guestComboBox.addItem("No guests available");
            CheckOutComboBox.addItem("No guests available");
            return;
        }

        for (int i = 0; i < manager.getBookingCount(); i++) {
            Booking b = manager.getBookings()[i];
            String info = b.getGuest().getName() + " " + b.getGuest().getSurname() + 
                         " (Room " + b.getRoom().getRoomNumber() + ")";
            guestComboBox.addItem(info);
            CheckOutComboBox.addItem(info);
        }
    }

    // Calculates the live total estimate based on current room type and nights.
     private void updateTotalPrice() {
        try {
            String roomType = (String) roomTypeComboBox.getSelectedItem();
            int nights = (Integer) nightsStayingSpinner.getValue();
            
            // Create a temporary room object to calculate the price without allocating a real booking.
            Room tempRoom;
            if (roomType.equals("Standard Room")) {
                tempRoom = new StandardRoom(100);
            } else {
                tempRoom = new DeluxeRoom(100);
            }
            
            double finalPrice = tempRoom.getFinalPrice(nights);
            totalPriceLabel.setText("Total Estimate: $" + String.format("%.2f", finalPrice));
            
            // Print bill to console.
            System.out.println("\n=== Price Calculation ===");
            tempRoom.displayInfo(nights);
            
        } catch (Exception e) {
            totalPriceLabel.setText("Total Estimate: $0.00");
        }
    }
    
    // Populates the list with all bookings after sorting by room number.
    private void loadSearchResults() {
        listModel.clear();

        if (manager.getBookingCount() == 0) {
            listModel.addElement("No bookings yet");
            resultLabel.setText("Results: 0 bookings");
            return;
        }

        // Sorting keeps the list ordered and easier to read.
        manager.sortByRoomNumber();

        for (int i = 0; i < manager.getBookingCount(); i++) {
            Booking b = manager.getBookings()[i];

            // Display status (statuses used: Pending / Confirmed).
            String statusDisplay = b.getStatus().equals("Pending") ? "PENDING" : "CONFIRMED";

            String display = b.getGuest().getName() + " " + b.getGuest().getSurname() +
                    " | Room #" + b.getRoom().getRoomNumber() +
                    " | " + b.getNightsStr() + " nights | " +
                    statusDisplay;

            listModel.addElement(display);
        }

        resultLabel.setText("Results: " + manager.getBookingCount() + " total");
    }


    // Resets the Add Booking form to default values after saving.
    private void clearBookingFields() {
        nameField.setText("");
        surnameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        roomTypeComboBox.setSelectedIndex(0);
        nightsStayingSpinner.setValue(1);
        updateTotalPrice();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sideBarMenu = new javax.swing.JPanel();
        hotelSystemLabel = new javax.swing.JLabel();
        managementConsoleLabel = new javax.swing.JLabel();
        logOutButton = new javax.swing.JButton();
        dashboardMenu = new javax.swing.JPanel();
        dashboardButton = new javax.swing.JButton();
        addBookings = new javax.swing.JPanel();
        addBookingsButton = new javax.swing.JButton();
        viewSearch = new javax.swing.JPanel();
        viewSearchButton = new javax.swing.JButton();
        checkInOut = new javax.swing.JPanel();
        checkInOutButton = new javax.swing.JButton();
        iconOfHotel = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        dashboardView = new javax.swing.JPanel();
        dashboardOverviewLabel = new javax.swing.JLabel();
        welcomeBackLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        totalBookingsLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        availableRoomsLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        totalRevenueLabel = new javax.swing.JLabel();
        recentActivityLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        recentActivity = new javax.swing.JTable();
        addBookingsView = new javax.swing.JPanel();
        emailField = new javax.swing.JTextField();
        phoneField = new javax.swing.JTextField();
        phoneLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        roomTypeComboBox = new javax.swing.JComboBox<>();
        surnameLabel = new javax.swing.JLabel();
        surnameField = new javax.swing.JTextField();
        durationLabel = new javax.swing.JLabel();
        nightsStayingSpinner = new javax.swing.JSpinner();
        addNewBookingLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        typeLabel = new javax.swing.JLabel();
        confirmBookingButton = new javax.swing.JButton();
        createReservationLabel = new javax.swing.JLabel();
        guestInformationLabel = new javax.swing.JLabel();
        roomDetailsLabel = new javax.swing.JLabel();
        totalPriceLabel = new javax.swing.JLabel();
        searchView = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        resultLabel = new javax.swing.JLabel();
        viewAllButton = new javax.swing.JButton();
        viewLabel = new javax.swing.JLabel();
        searchLabel = new javax.swing.JLabel();
        findManageReservationLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        searchResultsList = new javax.swing.JList<>();
        checkInOutView = new javax.swing.JPanel();
        checkInOutLabel = new javax.swing.JLabel();
        checkInLabel = new javax.swing.JLabel();
        confirmCheckInButton = new javax.swing.JButton();
        checkInSpinner = new javax.swing.JSpinner();
        checkOutSpinner = new javax.swing.JSpinner();
        checkOutLabel = new javax.swing.JLabel();
        guestComboBox = new javax.swing.JComboBox<>();
        manageGuestArrivalsLabel = new javax.swing.JLabel();
        selectGuestLabel = new javax.swing.JLabel();
        bookingReferenceLabel = new javax.swing.JLabel();
        setArrivalDatesLabel = new javax.swing.JLabel();
        arrivalDateLabel = new javax.swing.JLabel();
        departureDateLabel = new javax.swing.JLabel();
        finalizeStayFreeUpRoomLabel = new javax.swing.JLabel();
        CheckOutComboBox = new javax.swing.JComboBox<>();
        completeCheckOutButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        sideBarMenu.setBackground(new java.awt.Color(0, 0, 102));
        sideBarMenu.setForeground(new java.awt.Color(0, 0, 102));

        hotelSystemLabel.setBackground(new java.awt.Color(255, 255, 255));
        hotelSystemLabel.setFont(new java.awt.Font("Segoe UI Black", 1, 20)); // NOI18N
        hotelSystemLabel.setForeground(new java.awt.Color(255, 255, 255));
        hotelSystemLabel.setText("Hotel System");

        managementConsoleLabel.setBackground(new java.awt.Color(255, 255, 255));
        managementConsoleLabel.setForeground(new java.awt.Color(204, 204, 204));
        managementConsoleLabel.setText("Management Console");

        logOutButton.setBackground(new java.awt.Color(0, 0, 102));
        logOutButton.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        logOutButton.setForeground(new java.awt.Color(204, 51, 0));
        logOutButton.setText("Log Out");
        logOutButton.setFocusable(false);
        logOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutButtonActionPerformed(evt);
            }
        });

        dashboardMenu.setBackground(new java.awt.Color(0, 0, 102));
        dashboardMenu.setForeground(new java.awt.Color(0, 0, 102));

        dashboardButton.setBackground(new java.awt.Color(0, 0, 102));
        dashboardButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        dashboardButton.setForeground(new java.awt.Color(204, 204, 204));
        dashboardButton.setText("Dashboard");
        dashboardButton.setFocusable(false);

        javax.swing.GroupLayout dashboardMenuLayout = new javax.swing.GroupLayout(dashboardMenu);
        dashboardMenu.setLayout(dashboardMenuLayout);
        dashboardMenuLayout.setHorizontalGroup(
            dashboardMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dashboardButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dashboardMenuLayout.setVerticalGroup(
            dashboardMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dashboardButton, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        addBookings.setBackground(new java.awt.Color(0, 0, 102));
        addBookings.setForeground(new java.awt.Color(0, 0, 102));

        addBookingsButton.setBackground(new java.awt.Color(0, 0, 102));
        addBookingsButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addBookingsButton.setForeground(new java.awt.Color(204, 204, 204));
        addBookingsButton.setText("Add Bookings");
        addBookingsButton.setFocusable(false);

        javax.swing.GroupLayout addBookingsLayout = new javax.swing.GroupLayout(addBookings);
        addBookings.setLayout(addBookingsLayout);
        addBookingsLayout.setHorizontalGroup(
            addBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(addBookingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        addBookingsLayout.setVerticalGroup(
            addBookingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(addBookingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        viewSearch.setBackground(new java.awt.Color(0, 0, 102));
        viewSearch.setForeground(new java.awt.Color(0, 0, 102));

        viewSearchButton.setBackground(new java.awt.Color(0, 0, 102));
        viewSearchButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        viewSearchButton.setForeground(new java.awt.Color(204, 204, 204));
        viewSearchButton.setText("View / Search");
        viewSearchButton.setFocusable(false);

        javax.swing.GroupLayout viewSearchLayout = new javax.swing.GroupLayout(viewSearch);
        viewSearch.setLayout(viewSearchLayout);
        viewSearchLayout.setHorizontalGroup(
            viewSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(viewSearchButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        viewSearchLayout.setVerticalGroup(
            viewSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(viewSearchButton, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        checkInOut.setBackground(new java.awt.Color(0, 0, 102));
        checkInOut.setForeground(new java.awt.Color(0, 0, 102));

        checkInOutButton.setBackground(new java.awt.Color(0, 0, 102));
        checkInOutButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        checkInOutButton.setForeground(new java.awt.Color(204, 204, 204));
        checkInOutButton.setText("Check In / Out");
        checkInOutButton.setFocusable(false);

        javax.swing.GroupLayout checkInOutLayout = new javax.swing.GroupLayout(checkInOut);
        checkInOut.setLayout(checkInOutLayout);
        checkInOutLayout.setHorizontalGroup(
            checkInOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(checkInOutButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        checkInOutLayout.setVerticalGroup(
            checkInOutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(checkInOutButton, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        iconOfHotel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Hotel-icon.png"))); // NOI18N

        javax.swing.GroupLayout sideBarMenuLayout = new javax.swing.GroupLayout(sideBarMenu);
        sideBarMenu.setLayout(sideBarMenuLayout);
        sideBarMenuLayout.setHorizontalGroup(
            sideBarMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideBarMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sideBarMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dashboardMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addBookings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(viewSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkInOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(sideBarMenuLayout.createSequentialGroup()
                        .addComponent(logOutButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sideBarMenuLayout.createSequentialGroup()
                        .addComponent(iconOfHotel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sideBarMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(managementConsoleLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(hotelSystemLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        sideBarMenuLayout.setVerticalGroup(
            sideBarMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideBarMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sideBarMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sideBarMenuLayout.createSequentialGroup()
                        .addComponent(hotelSystemLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(managementConsoleLabel))
                    .addComponent(iconOfHotel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(dashboardMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(addBookings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(viewSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(checkInOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logOutButton)
                .addGap(30, 30, 30))
        );

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setEnabled(false);

        dashboardView.setBackground(new java.awt.Color(255, 255, 255));
        dashboardView.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.black, java.awt.Color.black));

        dashboardOverviewLabel.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        dashboardOverviewLabel.setText("Dashboard Overview");
        dashboardOverviewLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        welcomeBackLabel.setText("Welcome back, Admin. Here's what's happening today.");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(new java.awt.Color(102, 102, 102));

        totalBookingsLabel.setBackground(new java.awt.Color(255, 255, 255));
        totalBookingsLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalBookingsLabel.setForeground(new java.awt.Color(102, 102, 102));
        totalBookingsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalBookingsLabel.setText("Total Bookings");
        totalBookingsLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.lightGray, java.awt.Color.lightGray));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(totalBookingsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(totalBookingsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setForeground(new java.awt.Color(102, 102, 102));

        availableRoomsLabel.setBackground(new java.awt.Color(255, 255, 255));
        availableRoomsLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        availableRoomsLabel.setForeground(new java.awt.Color(102, 102, 102));
        availableRoomsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        availableRoomsLabel.setText("Available Rooms");
        availableRoomsLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.lightGray, java.awt.Color.lightGray));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(availableRoomsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(availableRoomsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setForeground(new java.awt.Color(102, 102, 102));

        totalRevenueLabel.setBackground(new java.awt.Color(255, 255, 255));
        totalRevenueLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalRevenueLabel.setForeground(new java.awt.Color(102, 102, 102));
        totalRevenueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalRevenueLabel.setText("Total Revenue");
        totalRevenueLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.lightGray, java.awt.Color.lightGray));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(totalRevenueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(totalRevenueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 49, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        recentActivityLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        recentActivityLabel.setText("My Recent Activity");

        jScrollPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        recentActivity.setBackground(new java.awt.Color(204, 204, 204));
        recentActivity.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.lightGray, java.awt.Color.lightGray));
        recentActivity.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        recentActivity.setEnabled(false);
        jScrollPane1.setViewportView(recentActivity);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout dashboardViewLayout = new javax.swing.GroupLayout(dashboardView);
        dashboardView.setLayout(dashboardViewLayout);
        dashboardViewLayout.setHorizontalGroup(
            dashboardViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dashboardOverviewLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(welcomeBackLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(recentActivityLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(dashboardViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dashboardViewLayout.setVerticalGroup(
            dashboardViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardViewLayout.createSequentialGroup()
                .addComponent(dashboardOverviewLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(welcomeBackLabel)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(recentActivityLabel)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 50, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Dashboard", dashboardView);

        addBookingsView.setBackground(new java.awt.Color(255, 255, 255));
        addBookingsView.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.black, java.awt.Color.black));

        phoneLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        phoneLabel.setText("Phone");

        emailLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        emailLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        emailLabel.setText("Email Address");

        roomTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Standard Room", "Deluxe Room" }));

        surnameLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        surnameLabel.setText("Surname");

        durationLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        durationLabel.setText("Duration (Nights)");

        nightsStayingSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        addNewBookingLabel.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        addNewBookingLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addNewBookingLabel.setText("Add New Booking");
        addNewBookingLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        nameLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        nameLabel.setText("First Name");

        typeLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        typeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        typeLabel.setText("Room Type");

        confirmBookingButton.setBackground(new java.awt.Color(0, 51, 255));
        confirmBookingButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        confirmBookingButton.setForeground(new java.awt.Color(255, 255, 255));
        confirmBookingButton.setText("Confirm Booking");
        confirmBookingButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(0, 51, 255), new java.awt.Color(0, 51, 255), new java.awt.Color(0, 51, 255), new java.awt.Color(0, 51, 255)));
        confirmBookingButton.setFocusable(false);
        confirmBookingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmBookingButtonActionPerformed(evt);
            }
        });

        createReservationLabel.setText("Create a reservation for a new guest.");

        guestInformationLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        guestInformationLabel.setForeground(new java.awt.Color(0, 51, 255));
        guestInformationLabel.setText("Guest Information");

        roomDetailsLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        roomDetailsLabel.setForeground(new java.awt.Color(0, 51, 255));
        roomDetailsLabel.setText("Room Details");

        totalPriceLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalPriceLabel.setText("Total Price Estimate");

        javax.swing.GroupLayout addBookingsViewLayout = new javax.swing.GroupLayout(addBookingsView);
        addBookingsView.setLayout(addBookingsViewLayout);
        addBookingsViewLayout.setHorizontalGroup(
            addBookingsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(addNewBookingLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
            .addComponent(createReservationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(addBookingsViewLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(addBookingsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(addBookingsViewLayout.createSequentialGroup()
                        .addComponent(emailLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(phoneLabel)
                        .addGap(138, 138, 138))
                    .addGroup(addBookingsViewLayout.createSequentialGroup()
                        .addGroup(addBookingsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(guestInformationLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(addBookingsViewLayout.createSequentialGroup()
                                .addComponent(nameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(surnameLabel)
                                .addGap(88, 88, 88))
                            .addGroup(addBookingsViewLayout.createSequentialGroup()
                                .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(surnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(36, 36, 36))
                    .addGroup(addBookingsViewLayout.createSequentialGroup()
                        .addComponent(typeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(durationLabel)
                        .addGap(66, 66, 66))
                    .addGroup(addBookingsViewLayout.createSequentialGroup()
                        .addGroup(addBookingsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(addBookingsViewLayout.createSequentialGroup()
                                .addComponent(totalPriceLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(confirmBookingButton))
                            .addGroup(addBookingsViewLayout.createSequentialGroup()
                                .addComponent(roomTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(nightsStayingSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(roomDetailsLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, addBookingsViewLayout.createSequentialGroup()
                                .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(phoneField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(35, 35, 35))))
        );
        addBookingsViewLayout.setVerticalGroup(
            addBookingsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addBookingsViewLayout.createSequentialGroup()
                .addComponent(addNewBookingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createReservationLabel)
                .addGap(30, 30, 30)
                .addComponent(guestInformationLabel)
                .addGap(18, 18, 18)
                .addGroup(addBookingsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(addBookingsViewLayout.createSequentialGroup()
                        .addComponent(surnameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(surnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(addBookingsViewLayout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addBookingsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailLabel)
                    .addComponent(phoneLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addBookingsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phoneField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(roomDetailsLabel)
                .addGap(18, 18, 18)
                .addGroup(addBookingsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(durationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addBookingsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(roomTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nightsStayingSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(addBookingsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(confirmBookingButton)
                    .addComponent(totalPriceLabel))
                .addContainerGap(154, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Add Bookings", addBookingsView);

        searchView.setBackground(new java.awt.Color(255, 255, 255));
        searchView.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.black, java.awt.Color.black));
        searchView.setEnabled(false);

        searchButton.setBackground(new java.awt.Color(0, 51, 255));
        searchButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        searchButton.setForeground(new java.awt.Color(255, 255, 255));
        searchButton.setText("Search");
        searchButton.setFocusable(false);
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        resultLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        resultLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        resultLabel.setText("Results :");

        viewAllButton.setBackground(new java.awt.Color(0, 51, 255));
        viewAllButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        viewAllButton.setForeground(new java.awt.Color(255, 255, 255));
        viewAllButton.setText("View All");
        viewAllButton.setFocusable(false);
        viewAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAllButtonActionPerformed(evt);
            }
        });

        viewLabel.setBackground(new java.awt.Color(204, 204, 204));
        viewLabel.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        viewLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        viewLabel.setText("Search Bookings");
        viewLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        searchLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        searchLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        searchLabel.setText("Search by guest name or surname:");

        findManageReservationLabel.setText("Find and manage existing reservations.");

        jScrollPane2.setViewportView(searchResultsList);

        javax.swing.GroupLayout searchViewLayout = new javax.swing.GroupLayout(searchView);
        searchView.setLayout(searchViewLayout);
        searchViewLayout.setHorizontalGroup(
            searchViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(viewLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(searchViewLayout.createSequentialGroup()
                .addGroup(searchViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(findManageReservationLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(searchViewLayout.createSequentialGroup()
                        .addGroup(searchViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(searchViewLayout.createSequentialGroup()
                                .addComponent(resultLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2))
                            .addGroup(searchViewLayout.createSequentialGroup()
                                .addComponent(searchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchButton)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchViewLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(viewAllButton)))
                .addContainerGap())
        );
        searchViewLayout.setVerticalGroup(
            searchViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchViewLayout.createSequentialGroup()
                .addComponent(viewLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findManageReservationLabel)
                .addGap(30, 30, 30)
                .addGroup(searchViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchLabel)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addGap(18, 18, 18)
                .addGroup(searchViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resultLabel)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(viewAllButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("View / Search", searchView);

        checkInOutView.setBackground(new java.awt.Color(255, 255, 255));
        checkInOutView.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.black, java.awt.Color.black));

        checkInOutLabel.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        checkInOutLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        checkInOutLabel.setText("Check In / Check Out");
        checkInOutLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        checkInLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        checkInLabel.setForeground(new java.awt.Color(0, 51, 255));
        checkInLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        checkInLabel.setText("Check In");

        confirmCheckInButton.setBackground(new java.awt.Color(51, 204, 0));
        confirmCheckInButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        confirmCheckInButton.setForeground(new java.awt.Color(255, 255, 255));
        confirmCheckInButton.setText("Confirm Check-In");
        confirmCheckInButton.setFocusable(false);
        confirmCheckInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmCheckInButtonActionPerformed(evt);
            }
        });

        checkInSpinner.setModel(new javax.swing.SpinnerDateModel());

        checkOutSpinner.setModel(new javax.swing.SpinnerDateModel());

        checkOutLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        checkOutLabel.setForeground(new java.awt.Color(0, 51, 255));
        checkOutLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        checkOutLabel.setText("Check Out");

        manageGuestArrivalsLabel.setText("Manage guest arrivals and departures.");

        selectGuestLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        selectGuestLabel.setForeground(new java.awt.Color(0, 51, 255));
        selectGuestLabel.setText("Select Guest");

        bookingReferenceLabel.setText("Booking Reference");

        setArrivalDatesLabel.setText("Set arrival dates for confirmed bookings");

        arrivalDateLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        arrivalDateLabel.setText("ARRIVAL DATE");

        departureDateLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        departureDateLabel.setText("DEPARTURE DATE");

        finalizeStayFreeUpRoomLabel.setText("Finalize stay and free up room");

        completeCheckOutButton.setBackground(new java.awt.Color(204, 51, 0));
        completeCheckOutButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        completeCheckOutButton.setForeground(new java.awt.Color(255, 255, 255));
        completeCheckOutButton.setText("Complete Check-Out");
        completeCheckOutButton.setFocusable(false);
        completeCheckOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                completeCheckOutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout checkInOutViewLayout = new javax.swing.GroupLayout(checkInOutView);
        checkInOutView.setLayout(checkInOutViewLayout);
        checkInOutViewLayout.setHorizontalGroup(
            checkInOutViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkInOutViewLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(checkInOutViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(checkInOutViewLayout.createSequentialGroup()
                        .addComponent(selectGuestLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(checkInOutViewLayout.createSequentialGroup()
                        .addGroup(checkInOutViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(confirmCheckInButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(guestComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(CheckOutComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(completeCheckOutButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(checkInOutViewLayout.createSequentialGroup()
                                .addGroup(checkInOutViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bookingReferenceLabel)
                                    .addComponent(finalizeStayFreeUpRoomLabel)
                                    .addGroup(checkInOutViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(checkInOutViewLayout.createSequentialGroup()
                                            .addComponent(arrivalDateLabel)
                                            .addGap(107, 107, 107)
                                            .addComponent(departureDateLabel))
                                        .addGroup(checkInOutViewLayout.createSequentialGroup()
                                            .addGroup(checkInOutViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(checkOutLabel)
                                                .addComponent(setArrivalDatesLabel)
                                                .addComponent(checkInLabel)
                                                .addComponent(checkInSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(81, 81, 81)))
                                    .addGroup(checkInOutViewLayout.createSequentialGroup()
                                        .addGap(190, 190, 190)
                                        .addComponent(checkOutSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 46, Short.MAX_VALUE)))
                        .addGap(33, 33, 33))))
            .addComponent(checkInOutLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(manageGuestArrivalsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        checkInOutViewLayout.setVerticalGroup(
            checkInOutViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkInOutViewLayout.createSequentialGroup()
                .addComponent(checkInOutLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(manageGuestArrivalsLabel)
                .addGap(30, 30, 30)
                .addComponent(selectGuestLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bookingReferenceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(guestComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(checkInLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setArrivalDatesLabel)
                .addGap(18, 18, 18)
                .addGroup(checkInOutViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(arrivalDateLabel)
                    .addComponent(departureDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(checkInOutViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkOutSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkInSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(confirmCheckInButton)
                .addGap(18, 18, 18)
                .addComponent(checkOutLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(finalizeStayFreeUpRoomLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CheckOutComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(completeCheckOutButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Check In / Out", checkInOutView);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sideBarMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sideBarMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Creates a new pending booking after validating all form inputs.
    private void confirmBookingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmBookingButtonActionPerformed
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String type = (String) roomTypeComboBox.getSelectedItem();
        int nights = (Integer) nightsStayingSpinner.getValue();

        // Empty-field validation before running format checks.
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }

        // Validate name.
        if (!Validator.validName(name)) {
            JOptionPane.showMessageDialog(this, "ERROR: Name must contain only letters.");
            return;
        }

        // Validate surname.
        if (!Validator.validName(surname)) {
            JOptionPane.showMessageDialog(this, "ERROR: Surname must contain only letters.");
            return;
        }

        // Validate email.
        if (!Validator.validEmail(email)) {
            JOptionPane.showMessageDialog(this, "ERROR: '" + email + "' is not valid. Email must include '@' and a domain.");
            return;
        }

        // Validate phone.
        if (!Validator.validPhone(phone)) {
            JOptionPane.showMessageDialog(this, "ERROR: Phone must follow format XXX-XXX-XXXX (e.g., 123-456-7890).");
            return;
        }

        // Create room.
        Room room;
        int nextRoomNum = 101 + manager.getBookingCount();

        // Validate room number.
        if (!Validator.validRoomNumber(nextRoomNum)) {
            JOptionPane.showMessageDialog(this, "ERROR: Room #" + nextRoomNum + " is not available or outside valid range.");
            return;
        }

        if (type.equals("Standard Room")) {
            room = new StandardRoom(nextRoomNum);
        } else {
            room = new DeluxeRoom(nextRoomNum);
        }

        try {
            // Guest is created from validated input values.
            Guest guest = new Guest(name, surname, phone, email);

            // Display guest info.
            guest.displayInfo();

            // New bookings start as pending until check-in confirmation.
            Booking booking = new PendingBooking(guest, room, 
                                                   "TBD", "TBD", 
                                                   String.valueOf(nights));

            // Employee action is shown for traceability in the activity table.
            currentEmployee.reserve(booking);

            // Display booking info using StringBuilder.
            booking.displayInfo();

            // Booking is persisted through the manager into the data file.
            manager.addBooking(booking);

            // Total estimate is calculated using the room pricing rules.
            double total = room.getFinalPrice(nights);

            showActivity("Add Booking", name + " " + surname + " Room #" + nextRoomNum);

            JOptionPane.showMessageDialog(this, 
                "Booking Created!\n\n" +
                "Guest: " + name + " " + surname + "\n" +
                "Room: #" + nextRoomNum + " (" + type + ")\n" +
                "Nights: " + nights + "\n" +
                "Total: $" + String.format("%.2f", total) + "\n" +
                "Status: PENDING");

            // Clear fields.
            clearBookingFields();

            // Update dashboard and combo boxes.
            loadDashboardData();
            updateComboBoxes();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_confirmBookingButtonActionPerformed
    
    // Searches bookings by name/surname and prints matching bookings to the console.
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a surname to search.");
            return;
        }

        // Clear previous results.
        listModel.clear();

        // Results array comes from the manager's search method.
        Booking[] results = manager.findGuest(keyword);
        boolean found = false;

        System.out.println("\n=== Search Results for: " + keyword + " ===");

        for (int i = 0; i < results.length; i++) {
            if (results[i] != null) {
                Booking b = results[i];

                // Display booking info to console.
                b.displayInfo();

                // Build display string using StringBuilder.
                StringBuilder display = new StringBuilder();
                display.append(b.getGuest().getName()).append(" ").append(b.getGuest().getSurname())
                       .append(" | Room #").append(b.getRoom().getRoomNumber())
                       .append(" | ").append(b.getNightsStr()).append(" nights")
                       .append(" | ").append(b.getStatus());

                listModel.addElement(display.toString());
                found = true;
            }
        }

        if (!found) {
            listModel.addElement("No results for: " + keyword);
        }

        resultLabel.setText("Results: " + (found ? listModel.size() : 0) + " found");
        showActivity("Search", "Searched: " + keyword);
    }//GEN-LAST:event_searchButtonActionPerformed

    // Reloads the full booking list into the View/Search tab.
    private void viewAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAllButtonActionPerformed
        loadSearchResults();
        showActivity("View All", "Viewed all bookings");
    }//GEN-LAST:event_viewAllButtonActionPerformed

    // Confirms a pending booking by saving arrival/departure dates and switching it to confirmed.
    private void confirmCheckInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmCheckInButtonActionPerformed
        if (manager.getBookingCount() == 0) {
            JOptionPane.showMessageDialog(this, "No bookings!");
            return;
        }

        String guestName = (String) guestComboBox.getSelectedItem();
        if (guestName == null || guestName.equals("No guests available")) {
            JOptionPane.showMessageDialog(this, "Please select a guest!");
            return;
        }

        Date checkInDate = (Date) checkInSpinner.getValue();
        Date checkOutDate = (Date) checkOutSpinner.getValue();
        
        // A check-out date must always be after check-in.
        if (checkOutDate.before(checkInDate) || checkOutDate.equals(checkInDate)) {
            JOptionPane.showMessageDialog(this, 
                "Check-out must be AFTER check-in!",
                "Invalid Dates",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // This loop finds the selected guest booking by matching the displayed combo box text.
        for (int i = 0; i < manager.getBookingCount(); i++) {
            Booking b = manager.getBookings()[i];
            String fullName = b.getGuest().getName() + " " + b.getGuest().getSurname();

            if (guestName.contains(fullName)) {
                // Only pending bookings can be confirmed during check-in.
                if (!b.getStatus().equals("Pending")) {
                    JOptionPane.showMessageDialog(this,
                        "This guest is already confirmed!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Nights are read from the stored booking so the stay length cannot be changed here.
                int nights;
                try {
                    nights = Integer.parseInt(b.getNightsStr());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid nights value in booking!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Calculate expected check-out = check-in + nights.
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(checkInDate);
                cal.add(java.util.Calendar.DAY_OF_MONTH, nights);
                Date expectedOut = cal.getTime();

                // If the user selects a different date, the system corrects it to match the rule.
                if (!sdf.format(checkOutDate).equals(sdf.format(expectedOut))) {
                    checkOutSpinner.setValue(expectedOut);
                    JOptionPane.showMessageDialog(this,
                        "Departure date must be EXACTLY " + nights + " night(s) after arrival.\n" +
                        "We corrected the departure date automatically.",
                        "Departure Fixed",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String checkInStr = sdf.format(checkInDate);
                String checkOutStr = sdf.format(expectedOut);

                // A confirmed booking replaces the pending one while keeping the same guest/room.
                Booking confirmed = new ConfirmedBooking(
                    b.getGuest(),
                    b.getRoom(),
                    checkInStr,
                    checkOutStr,
                    b.getNightsStr()
                );

                // Replace the object in the same index.
                manager.getBookings()[i] = confirmed;

                // Reserve room + employee action.
                currentEmployee.reserve(confirmed);
                confirmed.getRoom().reserve(confirmed);

                manager.saveToFile();
                confirmed.displayInfo();

                showActivity("Confirm Booking", fullName + " Room #" + confirmed.getRoom().getRoomNumber());

                JOptionPane.showMessageDialog(this,
                    "Booking Confirmed!\n\n" +
                    "Guest: " + fullName + "\n" +
                    "Room: " + confirmed.getRoom().getRoomNumber() + "\n" +
                    "Check-in: " + checkInStr + "\n" +
                    "Check-out: " + checkOutStr + "\n" +
                    "Nights: " + nights + "\n" +
                    "Status: CONFIRMED");

                loadDashboardData();
                loadSearchResults();
                updateComboBoxes();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Guest not found!");
    }//GEN-LAST:event_confirmCheckInButtonActionPerformed
    
    // Performs check-out by marking the room available and clearing the booking entry.
    private void completeCheckOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_completeCheckOutButtonActionPerformed
        if (manager.getBookingCount() == 0) {
            JOptionPane.showMessageDialog(this, "No bookings!");
            return;
        }

        String guestName = (String) CheckOutComboBox.getSelectedItem();
        if (guestName == null || guestName.equals("No guests available")) {
            JOptionPane.showMessageDialog(this, "Select a guest!");
            return;
        }

        // This loop finds the selected confirmed booking and removes it from the system.
        for (int i = 0; i < manager.getBookingCount(); i++) {
            Booking b = manager.getBookings()[i];
            String fullName = b.getGuest().getName() + " " + b.getGuest().getSurname();

            if (guestName.contains(fullName)) {
                // Pending bookings must be confirmed before check-out is allowed.
                if (b.getStatus().equals("Pending")) {
                    JOptionPane.showMessageDialog(this,
                        "Cannot check-out PENDING booking!\nCheck-in first.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Complete check-out?", 
                    "Confirm", 
                    JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                int roomNum = b.getRoom().getRoomNumber();
                b.displayInfo();

                // Cancel actions update both employee activity and room availability.
                currentEmployee.cancel(b);
                b.getRoom().cancel(b);
                
                manager.removeBooking(i);
                showActivity("Check-Out", fullName + " Room #" + roomNum);

                JOptionPane.showMessageDialog(this, 
                    "Check-Out Complete!\n\n" +
                    "Guest: " + fullName + "\n" +
                    "Room #" + roomNum + " now available");

                // Update everything.
                updateComboBoxes();
                loadDashboardData();
                loadSearchResults();
                return;
            }
        }
    }//GEN-LAST:event_completeCheckOutButtonActionPerformed
    
    // Logs out of the system and returns to the login window using the same manager instance.
    private void logOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutButtonActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Log out?", 
            "Confirm", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            showActivity("Logout", "Logged out");
            this.dispose();
            new LoginView(manager).setVisible(true);
        }
    }//GEN-LAST:event_logOutButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginView(new HotelManager()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CheckOutComboBox;
    private javax.swing.JPanel addBookings;
    private javax.swing.JButton addBookingsButton;
    private javax.swing.JPanel addBookingsView;
    private javax.swing.JLabel addNewBookingLabel;
    private javax.swing.JLabel arrivalDateLabel;
    private javax.swing.JLabel availableRoomsLabel;
    private javax.swing.JLabel bookingReferenceLabel;
    private javax.swing.JLabel checkInLabel;
    private javax.swing.JPanel checkInOut;
    private javax.swing.JButton checkInOutButton;
    private javax.swing.JLabel checkInOutLabel;
    private javax.swing.JPanel checkInOutView;
    private javax.swing.JSpinner checkInSpinner;
    private javax.swing.JLabel checkOutLabel;
    private javax.swing.JSpinner checkOutSpinner;
    private javax.swing.JButton completeCheckOutButton;
    private javax.swing.JButton confirmBookingButton;
    private javax.swing.JButton confirmCheckInButton;
    private javax.swing.JLabel createReservationLabel;
    private javax.swing.JButton dashboardButton;
    private javax.swing.JPanel dashboardMenu;
    private javax.swing.JLabel dashboardOverviewLabel;
    private javax.swing.JPanel dashboardView;
    private javax.swing.JLabel departureDateLabel;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JLabel finalizeStayFreeUpRoomLabel;
    private javax.swing.JLabel findManageReservationLabel;
    private javax.swing.JComboBox<String> guestComboBox;
    private javax.swing.JLabel guestInformationLabel;
    private javax.swing.JLabel hotelSystemLabel;
    private javax.swing.JLabel iconOfHotel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton logOutButton;
    private javax.swing.JLabel manageGuestArrivalsLabel;
    private javax.swing.JLabel managementConsoleLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JSpinner nightsStayingSpinner;
    private javax.swing.JTextField phoneField;
    private javax.swing.JLabel phoneLabel;
    private javax.swing.JTable recentActivity;
    private javax.swing.JLabel recentActivityLabel;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JLabel roomDetailsLabel;
    private javax.swing.JComboBox<String> roomTypeComboBox;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JList<String> searchResultsList;
    private javax.swing.JPanel searchView;
    private javax.swing.JLabel selectGuestLabel;
    private javax.swing.JLabel setArrivalDatesLabel;
    private javax.swing.JPanel sideBarMenu;
    private javax.swing.JTextField surnameField;
    private javax.swing.JLabel surnameLabel;
    private javax.swing.JLabel totalBookingsLabel;
    private javax.swing.JLabel totalPriceLabel;
    private javax.swing.JLabel totalRevenueLabel;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JButton viewAllButton;
    private javax.swing.JLabel viewLabel;
    private javax.swing.JPanel viewSearch;
    private javax.swing.JButton viewSearchButton;
    private javax.swing.JLabel welcomeBackLabel;
    // End of variables declaration//GEN-END:variables
}