// UserDashboard.java - Fixed Timing Repetition Issues
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

// Enhanced Turf class with additional fields
class Turf {
    private String id;
    private String name;
    private String description;
    private String city;
    private String address;
    private double price;
    private int capacity;
    private List<String> availableSlots;
    private List<String> facilities;
    private String contactPhone;
    private String contactEmail;
    private String ownerName;
    
    public Turf(String id, String name, String description, String city, String address, 
                double price, int capacity, List<String> availableSlots, 
                List<String> facilities, String contactPhone, String contactEmail, String ownerName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.city = city;
        this.address = address;
        this.price = price;
        this.capacity = capacity;
        this.availableSlots = availableSlots;
        this.facilities = facilities;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.ownerName = ownerName;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public double getPrice() { return price; }
    public int getCapacity() { return capacity; }
    public List<String> getAvailableSlots() { return availableSlots; }
    public List<String> getFacilities() { return facilities; }
    public String getContactPhone() { return contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public String getOwnerName() { return ownerName; }
}

// User class
class User {
    private String id;
    private String name;
    private String email;
    
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}

// Booking class with hours instead of end time
class Booking {
    private String id;
    private String userId;
    private String turfId;
    private String turfName;
    private String date;
    private String startTime;
    private int hours;
    private int playerCount;
    private double amount;
    private String status;
    private String username;
    private String email;
    
    public Booking(String id, String userId, String turfId, String turfName, String date, String startTime, int hours, int playerCount, double amount, String status) {
        this.id = id;
        this.userId = userId;
        this.turfId = turfId;
        this.turfName = turfName;
        this.date = date;
        this.startTime = startTime;
        this.hours = hours;
        this.playerCount = playerCount;
        this.amount = amount;
        this.status = status;
        this.username = "User " + userId;
        this.email = "user" + userId + "@example.com";
    }
    
    public Booking(String id, String userId, String turfId, String turfName, String date, String startTime, int hours, int playerCount, double amount, String status, String username, String email) {
        this.id = id;
        this.userId = userId;
        this.turfId = turfId;
        this.turfName = turfName;
        this.date = date;
        this.startTime = startTime;
        this.hours = hours;
        this.playerCount = playerCount;
        this.amount = amount;
        this.status = status;
        this.username = username;
        this.email = email;
    }
    
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTurfId() { return turfId; }
    public String getTurfName() { return turfName; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public int getHours() { return hours; }
    public int getPlayerCount() { return playerCount; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public void setStatus(String status) { this.status = status; }
}

// Calendar component for booking
class CalendarPanel extends JPanel {
    private JLabel monthLabel;
    private JPanel calendarPanel;
    private Calendar calendar;
    private JTextField selectedDateField;
    
    public CalendarPanel(JTextField dateField) {
        this.selectedDateField = dateField;
        this.calendar = Calendar.getInstance();
        setupCalendar();
    }
    
    private void setupCalendar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Select Date"));
        setBackground(Color.WHITE);
        
        // Month navigation
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBackground(Color.WHITE);
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        
        prevButton.addActionListener(e -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });
        
        nextButton.addActionListener(e -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });
        
        navigationPanel.add(prevButton, BorderLayout.WEST);
        navigationPanel.add(monthLabel, BorderLayout.CENTER);
        navigationPanel.add(nextButton, BorderLayout.EAST);
        
        // Calendar grid
        calendarPanel = new JPanel(new GridLayout(0, 7));
        calendarPanel.setBackground(Color.WHITE);
        
        add(navigationPanel, BorderLayout.NORTH);
        add(calendarPanel, BorderLayout.CENTER);
        
        updateCalendar();
    }
    
    private void updateCalendar() {
        calendarPanel.removeAll();
        
        // Set the month label
        monthLabel.setText(new SimpleDateFormat("MMMM yyyy").format(calendar.getTime()));
        
        // Add day headers
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            dayLabel.setBackground(new Color(240, 240, 240));
            dayLabel.setOpaque(true);
            calendarPanel.add(dayLabel);
        }
        
        // Get the first day of the month
        Calendar firstDay = (Calendar) calendar.clone();
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK);
        
        // Fill in blank days before the first day
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
        }
        
        // Get the number of days in the month
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Add day buttons
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        for (int day = 1; day <= daysInMonth; day++) {
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFont(new Font("Arial", Font.PLAIN, 12));
            
            Calendar buttonDate = (Calendar) calendar.clone();
            buttonDate.set(Calendar.DAY_OF_MONTH, day);
            buttonDate.set(Calendar.HOUR_OF_DAY, 0);
            buttonDate.set(Calendar.MINUTE, 0);
            buttonDate.set(Calendar.SECOND, 0);
            buttonDate.set(Calendar.MILLISECOND, 0);
            
            // Disable past dates
            if (buttonDate.before(today)) {
                dayButton.setEnabled(false);
                dayButton.setBackground(new Color(240, 240, 240));
                dayButton.setForeground(Color.GRAY);
            } else {
                dayButton.setBackground(Color.WHITE);
                dayButton.setForeground(Color.BLACK);
            }
            
            final int currentDay = day;
            dayButton.addActionListener(e -> {
                Calendar selected = (Calendar) calendar.clone();
                selected.set(Calendar.DAY_OF_MONTH, currentDay);
                String selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(selected.getTime());
                selectedDateField.setText(selectedDate);
                
                // Close the calendar dialog
                Window window = SwingUtilities.getWindowAncestor(CalendarPanel.this);
                if (window instanceof JDialog) {
                    window.dispose();
                }
            });
            
            calendarPanel.add(dayButton);
        }
        
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }
}

// Custom renderer for strikethrough text in combo box
class StrikethroughCellRenderer extends DefaultListCellRenderer {
    private Set<String> disabledItems;
    
    public StrikethroughCellRenderer(Set<String> disabledItems) {
        this.disabledItems = disabledItems;
    }
    
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value != null && disabledItems.contains(value.toString())) {
            // Create strikethrough font
            Font originalFont = label.getFont();
            Font strikethroughFont = new Font(originalFont.getName(), Font.PLAIN, originalFont.getSize());
            label.setFont(strikethroughFont);
            label.setText("<html><strike>" + value.toString() + "</strike></html>");
            label.setForeground(Color.GRAY);
            label.setEnabled(false);
        } else {
            label.setForeground(Color.BLACK);
            label.setEnabled(true);
        }
        
        return label;
    }
}

// Main User Dashboard class with sidebar layout
public class UserDashboard extends JFrame {
    private String normalizeId(String id) {
        return (id == null) ? "" : id.replaceAll("\\D", "");
    }
    
    // User information
    private User currentUser;
    private Login loginSystem;
    
    // Main panels
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Sidebar components
    private JPanel sidebarPanel;
    private JButton homeButton,  viewBookTurfButton, viewBookingButton, logoutButton;
    
    // Content panels
    private JPanel homepagePanel, viewBookTurfPanel, viewBookingPanel;
    
    // Data structures for storing application data - INITIALIZE ALL LISTS
    private List<Turf> turfs = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private Map<String, Integer> userPoints = new HashMap<>();
    
    // Data models - INITIALIZE TABLE MODELS
    private DefaultTableModel bookingTableModel;
    private DefaultTableModel recentBookingsTableModel; // NEW: For recent bookings on homepage
   
    
    // Search components for View Turf
    private JComboBox<String> cityFilterComboBox;
    private JTextField searchField;
    private JPanel turfsContainerPanel;
    
    // Current active menu button
    private JButton activeMenuButton = null;
    
    // Stat panel references for easy updating
    private JPanel totalBookingsStatPanel;
    private JPanel futureReservationsStatPanel;
    private JPanel favoriteTurfStatPanel;
    
    // FIXED: Proper booking dialog components with clear separation
    private JComboBox<String> startTimeComboBox;
    private JComboBox<Integer> playerCountComboBox;
    private JComboBox<Integer> hoursComboBox;
    private JTextField dateField;
    private JLabel capacityStatusLabel;
    private JButton confirmButton;
    private Turf currentBookingTurf;
    
    // Timer for automatic status updates
    private javax.swing.Timer autoCompleteTimer;
    
    // Colors matching AdminDashboard
    private final Color PRIMARY_COLOR = new Color(34, 139, 34); // Green
    private final Color SECONDARY_COLOR = new Color(240, 255, 240); // Light green
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color CHART_COLOR = new Color(34, 139, 34);
    private final Color CHART_GRID_COLOR = new Color(220, 220, 220);
    
    public UserDashboard(User user, Login loginSystem) {
        this.currentUser = user;
        this.loginSystem = loginSystem;
        
        // Initialize table models FIRST
        initializeTableModels();
        
        // ✅ Auto-update any past bookings to 'completed'
        loginSystem.autoUpdateCompletedBookings();

        initializeData();
        setupUI();
        showHomepage();
        
        // Start automatic booking completion timer
        startAutoCompletionTimer();
    }
    
    // ✅ NEW: Automatic booking completion timer
    private void startAutoCompletionTimer() {
        autoCompleteTimer = new javax.swing.Timer(60000, new ActionListener() { // Check every minute
            @Override
            public void actionPerformed(ActionEvent e) {
                autoCompletePastBookings();
            }
        });
        autoCompleteTimer.start();
    }
    
    // ✅ NEW: Automatically mark past bookings as completed
    private void autoCompletePastBookings() {
        try {
            Vector<Login.Booking> allBookings = loginSystem.getAllBookingsFromDatabase();
            if (allBookings == null) return;
            
            Date now = new Date();
            int updatedCount = 0;
            
            for (Login.Booking booking : allBookings) {
                // Only process confirmed bookings that haven't been completed yet
                if ("confirmed".equalsIgnoreCase(booking.getStatus())) {
                    try {
                        // Calculate booking end time
                        Date bookingStart = parseDateTime(booking.getDate(), booking.getStartTime());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(bookingStart);
                        cal.add(Calendar.HOUR, booking.getHours());
                        Date bookingEnd = cal.getTime();
                        
                        // If booking end time has passed, mark as completed
                        if (bookingEnd.before(now)) {
                            boolean updated = loginSystem.updateBookingStatusInDatabase(booking.getId(), "completed");
                            if (updated) {
                                updatedCount++;
                                System.out.println("[AUTO-COMPLETE] Booking " + booking.getId() + " marked as completed");
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("[ERROR] Failed to auto-complete booking " + booking.getId() + ": " + ex.getMessage());
                        // Continue with other bookings even if one fails
                    }
                }
            }
            
            if (updatedCount > 0) {
                System.out.println("[AUTO-COMPLETE] Successfully updated " + updatedCount + " bookings to completed status");
                // Refresh the bookings display if we're on the bookings page
                if ("VIEW_BOOKING".equals(((CardLayout) mainPanel.getLayout()).toString())) {
                    refreshBookingsFromDatabase();
                }
            }
            
        } catch (Exception ex) {
            System.out.println("[ERROR] Auto-completion timer error: " + ex.getMessage());
            // Don't stop the timer even if there's an error
        }
    }
    
    @Override
    public void dispose() {
        // Stop the timer when closing the application
        if (autoCompleteTimer != null && autoCompleteTimer.isRunning()) {
            autoCompleteTimer.stop();
        }
        super.dispose();
    }
    
    private void initializeTableModels() {
        // Initialize booking table model
        String[] bookingColumns = {"Booking ID", "Turf ID", "Turf Name", "Date", "Start Time", "Hours", "Players", "Amount", "Status"};
        bookingTableModel = new DefaultTableModel(bookingColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // NEW: Initialize recent bookings table model for homepage (similar to AdminDashboard)
        String[] recentBookingsColumns = {"Booking ID", "Turf Name", "Date", "Start Time", "Hours", "Status"};
        recentBookingsTableModel = new DefaultTableModel(recentBookingsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Initialize booking legends table model
       
    }
    
    private void initializeData() {
        // Load bookings from database
        refreshBookingsFromDatabase();
        
        // Initialize sample turfs with enhanced details
        turfs.add(new Turf("T001", "Green Field Football", 
            "Large grass field with professional goals and floodlights", 
            "Chennai", "123 Sports Complex, Anna Nagar, Chennai - 600040",
            1200.0, 20,
            new ArrayList<>(Arrays.asList("9:00", "11:00", "14:00", "16:00", "18:00")),
            new ArrayList<>(Arrays.asList("Floodlights", "Changing Rooms", "Parking", "Water Facility", "First Aid")),
            "+91-9876543210", "greenfield@example.com", "Rajesh"
        ));
        
        turfs.add(new Turf("T002", "Sports Arena Cricket", 
            "Professional cricket pitch with pavilion and practice nets", 
            "Coimbatore", "456 Cricket Ground, Race Course, Coimbatore - 641018",
            1500.0, 30,
            new ArrayList<>(Arrays.asList("8:00", "11:00", "14:00", "17:00")),
            new ArrayList<>(Arrays.asList("Practice Nets", "Pavilion", "Scoreboard", "Umpire Services", "Cafeteria")),
            "+91-9876543211", "sportsarena@example.com", "Suresh"
        ));
        
        turfs.add(new Turf("T003", "Elite Tennis Court", 
            "Hard court with nets and professional lighting system", 
            "Madurai", "789 Tennis Complex, KK Nagar, Madurai - 625020",
            800.0, 4,
            new ArrayList<>(Arrays.asList("9:00", "11:00", "13:00", "15:00", "17:00", "19:00")),
            new ArrayList<>(Arrays.asList("Air Conditioned", "Professional Nets", "Ball Machine", "Coach Available", "Pro Shop")),
            "+91-9876543212", "elitetennis@example.com", "Priya"
        ));
        
        turfs.add(new Turf("T004", "Basketball Arena", 
            "Indoor court with professional flooring and electronic scoreboard", 
            "Chennai", "321 Indoor Stadium, T Nagar, Chennai - 600017",
            900.0, 10,
            new ArrayList<>(Arrays.asList("8:00", "10:00", "12:00", "14:00", "16:00", "18:00")),
            new ArrayList<>(Arrays.asList("Air Conditioned", "Electronic Scoreboard", "Basketball Rental", "Coach Available", "Locker Rooms")),
            "+91-9876543213", "basketballarena@example.com", "Arun"
        ));
        
        turfs.add(new Turf("T005", "Rugby Ground", 
            "Full-size rugby field with changing rooms and medical facility", 
            "Coimbatore", "654 Rugby Park, Peelamedu, Coimbatore - 641004",
            1400.0, 25,
            new ArrayList<>(Arrays.asList("9:00", "12:00", "15:00", "18:00")),
            new ArrayList<>(Arrays.asList("Medical Room", "Changing Rooms", "Equipment Rental", "Showers", "Physiotherapist")),
            "+91-9876543214", "rugbyground@example.com", "Hari"
        ));
        
        turfs.add(new Turf("T006", "Badminton Court", 
            "Air-conditioned indoor court with professional flooring", 
            "Madurai", "987 Sports Center, Vilakkuthoon, Madurai - 625001",
            600.0, 6,
            new ArrayList<>(Arrays.asList("8:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00")),
            new ArrayList<>(Arrays.asList("Air Conditioned", "Professional Shuttles", "Racket Rental", "Coach Available", "Cafeteria")),
            "+91-9876543215", "badmintoncourt@example.com", "Surya"
        ));
        
        // Initialize user points
        userPoints.put("mega", 150);
        userPoints.put("shobi", 125);
        userPoints.put(currentUser.getName(), 100);
        userPoints.put("sanjith", 90);
        userPoints.put("sneka", 85);
        userPoints.put("rohini", 80);
        userPoints.put("sachika", 75);
        userPoints.put("sabari", 70);
        userPoints.put("gowtham", 65);
        userPoints.put("vijay", 60);
        
        // Update points based on actual bookings from database
        updateUserPointsFromBookings();
    }
    
    private void updateUserPointsFromBookings() {
        // Clear existing points
        userPoints.clear();
        
        // Get all bookings from database (only users, not admins)
        Vector<Login.Booking> allBookings = loginSystem.getAllBookingsFromDatabase();
        
        if (allBookings != null) {
            // Calculate points based on bookings
            for (Login.Booking booking : allBookings) {
                String username = booking.getUsername();
                if (username != null && !username.startsWith("admin")) {
                    int currentPoints = userPoints.getOrDefault(username, 0);
                    int pointsToAdd = (int) (booking.getAmount() / 100);
                    userPoints.put(username, currentPoints + pointsToAdd);
                }
            }
        }
        
        // Ensure current user has at least some points
        if (!userPoints.containsKey(currentUser.getName())) {
            userPoints.put(currentUser.getName(), 0);
        }
    }
    
    // ✅ SAFE DATE PARSING HELPER METHOD
    private Date parseDateTime(String date, String time) throws java.text.ParseException {
        if (date == null || time == null) {
            throw new java.text.ParseException("Date or time cannot be null", 0);
        }
        
        String cleanDate = date.trim();
        String cleanTime = time.trim();
        
        if (cleanDate.isEmpty() || cleanTime.isEmpty()) {
            throw new java.text.ParseException("Date or time cannot be empty", 0);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.parse(cleanDate + " " + cleanTime);
    }
    
    // ✅ CALCULATE END TIME HELPER METHOD
    private Date calculateEndTime(String date, String startTime, int hours) throws java.text.ParseException {
        Date start = parseDateTime(date, startTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.HOUR, hours);
        return cal.getTime();
    }
    
    // ✅ ENHANCED: Check if booking time is over
    private boolean isBookingTimeOver(Booking booking) {
        try {
            Date bookingEndTime = calculateEndTime(booking.getDate(), booking.getStartTime(), booking.getHours());
            Date now = new Date();
            return bookingEndTime.before(now);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ ENHANCED: Check if booking is active (not completed and time not over)
    private boolean isBookingActive(Booking booking) {
        return "confirmed".equalsIgnoreCase(booking.getStatus()) && !isBookingTimeOver(booking);
    }
    
    private void setupUI() {
        setTitle("BookMyTurf - User Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Create main layout with sidebar and content
        setLayout(new BorderLayout());
        
        // Create sidebar panel
        createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);
        
        // Create main content panel with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create all content panels
        createHomepagePanel();
        createViewBookTurfPanel();
        createViewBookingPanel();
        
        // Add panels to main panel
        mainPanel.add(homepagePanel, "HOME");
        mainPanel.add(viewBookTurfPanel, "VIEW_BOOK_TURF");
        mainPanel.add(viewBookingPanel, "VIEW_BOOKING");
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Add header with user info
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel welcomeLabel = new JLabel("Welcome to BookMyTurf, " + currentUser.getName() + "!");
        welcomeLabel.setForeground(new Color(33, 37, 41));
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setBorder(new EmptyBorder(15, 20, 15, 10));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, MMMM d, yyyy").format(new Date()));
        dateLabel.setForeground(new Color(108, 117, 125));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setBorder(new EmptyBorder(15, 10, 15, 20));
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createSidebarPanel() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBackground(BACKGROUND_COLOR);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(222, 226, 230)));
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        
        // Sidebar header
        JPanel sidebarHeader = new JPanel();
        sidebarHeader.setBackground(BACKGROUND_COLOR);
        sidebarHeader.setBorder(new EmptyBorder(20, 20, 20, 20));
        sidebarHeader.setLayout(new BorderLayout());
        
        JLabel sidebarTitle = new JLabel("BookMyTurf");
        sidebarTitle.setForeground(PRIMARY_COLOR);
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sidebarHeader.add(sidebarTitle, BorderLayout.CENTER);
        
        sidebarPanel.add(sidebarHeader, BorderLayout.NORTH);
        
        // Navigation menu
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(BACKGROUND_COLOR);
        navPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        // Create navigation buttons
        homeButton = createNavButton(" Homepage", "HOME", PRIMARY_COLOR);
        viewBookTurfButton = createNavButton(" View & Book Turf", "VIEW_BOOK_TURF", new Color(46, 139, 87));
        viewBookingButton = createNavButton(" My Bookings", "VIEW_BOOKING", new Color(186, 85, 211));
        logoutButton = createNavButton(" Logout", "LOGOUT", new Color(178, 34, 34));
        
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(homeButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(viewBookTurfButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(viewBookingButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(logoutButton);
        navPanel.add(Box.createVerticalStrut(10));
        
        sidebarPanel.add(navPanel, BorderLayout.CENTER);
    }
    
    private JButton createNavButton(String text, String action, Color color) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 50));
        button.setBackground(BACKGROUND_COLOR);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != activeMenuButton) {
                    button.setBackground(new Color(240, 240, 240));
                    button.setForeground(Color.BLACK);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != activeMenuButton) {
                    button.setBackground(BACKGROUND_COLOR);
                    button.setForeground(Color.BLACK);
                }
            }
        });
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("LOGOUT".equals(action)) {
                    int confirm = JOptionPane.showConfirmDialog(
                        UserDashboard.this, 
                        "Are you sure you want to logout?", 
                        "Confirm Logout", 
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        dispose();
                        new Login().setVisible(true);
                    }
                } else {
                    setActiveMenuButton(button);
                    cardLayout.show(mainPanel, action);
                    if ("VIEW_BOOK_TURF".equals(action)) {
                        refreshTurfDisplay();
                    } else if ("VIEW_BOOKING".equals(action)) {
                        refreshBookingsFromDatabase();
                    } else if ("HOME".equals(action)) {
                        refreshStats(); // Refresh stats and recent bookings when returning to homepage
                    }
                }
            }
        });
        
        return button;
    }
    
    private void setActiveMenuButton(JButton button) {
        if (activeMenuButton != null) {
            activeMenuButton.setBackground(BACKGROUND_COLOR);
            activeMenuButton.setForeground(Color.BLACK);
        }
        
        activeMenuButton = button;
        activeMenuButton.setBackground(PRIMARY_COLOR);
        activeMenuButton.setForeground(Color.WHITE);
    }
    
    // UPDATED: Homepage panel with recent bookings section (similar to AdminDashboard)
    private void createHomepagePanel() {
        homepagePanel = new JPanel(new BorderLayout());
        homepagePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        homepagePanel.setBackground(BACKGROUND_COLOR);
        
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)), "Dashboard Overview"));
        welcomePanel.setBackground(BACKGROUND_COLOR);
        welcomePanel.setForeground(Color.BLACK);
        
        JLabel welcomeTitle = new JLabel("BookMyTurf - User Dashboard");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeTitle.setForeground(PRIMARY_COLOR);
        welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel welcomeText = new JLabel("<html><div style='text-align: center;'>Welcome to your personal dashboard. "
                + "Here you can book sports turfs, view your bookings, and manage your reservations.</div></html>");
        welcomeText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeText.setForeground(new Color(108, 117, 125));
        welcomeText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(welcomeTitle);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(welcomeText);
        welcomePanel.add(Box.createVerticalStrut(30));
        
        // Quick stats with refresh button - SIMILAR TO ADMIN DASHBOARD
        JPanel statsHeaderPanel = new JPanel(new BorderLayout());
        statsHeaderPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel statsTitle = new JLabel("Your Booking Statistics");
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statsTitle.setForeground(new Color(33, 37, 41));
        statsHeaderPanel.add(statsTitle, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh Stats");
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshStatsWithPopup();
            }
        });
        statsHeaderPanel.add(refreshButton, BorderLayout.EAST);
        
        welcomePanel.add(statsHeaderPanel);
        welcomePanel.add(Box.createVerticalStrut(10));
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(BACKGROUND_COLOR);
        
        totalBookingsStatPanel = createStatPanel("Total Bookings", String.valueOf(bookings.size()), new Color(13, 110, 253));
        futureReservationsStatPanel = createStatPanel("Future Reservations", 
            String.valueOf(bookings.stream().filter(b -> "confirmed".equalsIgnoreCase(b.getStatus())).count()), new Color(25, 135, 84));
        favoriteTurfStatPanel = createStatPanel("Favorite Turf", getMostBookedTurf(), new Color(102, 16, 242));
        
        statsPanel.add(totalBookingsStatPanel);
        statsPanel.add(futureReservationsStatPanel);
        statsPanel.add(favoriteTurfStatPanel);
        
        welcomePanel.add(statsPanel);
        welcomePanel.add(Box.createVerticalStrut(30));
        
        // NEW: Recent Bookings Section - SIMILAR TO ADMIN DASHBOARD
        JPanel recentBookingsPanel = new JPanel(new BorderLayout());
        recentBookingsPanel.setBorder(BorderFactory.createTitledBorder("Quick view"));
        recentBookingsPanel.setBackground(BACKGROUND_COLOR);
        
        JTable recentBookingsTable = new JTable(recentBookingsTableModel);
        recentBookingsTable.setBackground(BACKGROUND_COLOR);
        recentBookingsTable.setForeground(Color.BLACK);
        recentBookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Color coding for status in recent bookings table - SIMILAR TO ADMIN DASHBOARD
        recentBookingsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Color coding for status column (column 5)
                if (column == 5 && value != null) {
                    String status = value.toString().toLowerCase();
                    switch (status) {
                        case "confirmed":
                            c.setBackground(new Color(144, 238, 144)); // Light green
                            c.setForeground(Color.BLACK);
                            break;
                        case "pending":
                            c.setBackground(new Color(255, 255, 224)); // Light yellow
                            c.setForeground(Color.BLACK);
                            break;
                        case "completed":
                            c.setBackground(new Color(173, 216, 230)); // Light blue
                            c.setForeground(Color.BLACK);
                            break;
                        case "cancelled":
                            c.setBackground(new Color(255, 182, 193)); // Light red
                            c.setForeground(Color.BLACK);
                            break;
                        default:
                            c.setBackground(BACKGROUND_COLOR);
                            c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setBackground(BACKGROUND_COLOR);
                    c.setForeground(Color.BLACK);
                }
                
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(recentBookingsTable);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 200));
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        recentBookingsPanel.add(scrollPane, BorderLayout.CENTER);
        
        welcomePanel.add(recentBookingsPanel);
        
        homepagePanel.add(welcomePanel, BorderLayout.CENTER);
        
        // Initial stats update
        refreshStats();
    }
    
    // NEW: Refresh stats with popup confirmation (similar to AdminDashboard)
    private void refreshStatsWithPopup() {
        refreshStats();
        
        // Calculate statistics for popup message
        int totalBookings = bookings.size();
        int futureReservations = (int) bookings.stream()
            .filter(b -> "confirmed".equalsIgnoreCase(b.getStatus()))
            .count();
        String favoriteTurf = getMostBookedTurf();
        
        JOptionPane.showMessageDialog(homepagePanel, 
            "Stats refreshed successfully!\n" +
            "Total Bookings: " + totalBookings + "\n" +
            "Future Reservations: " + futureReservations + "\n" +
            "Favorite Turf: " + favoriteTurf, 
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // UPDATED: Refresh stats to include recent bookings update
    private void refreshStats() {
        refreshBookingsFromDatabase();
        
        // Calculate statistics
        int totalBookings = bookings.size();
        int futureReservations = (int) bookings.stream()
            .filter(b -> "confirmed".equalsIgnoreCase(b.getStatus()))
            .count();
        String favoriteTurf = getMostBookedTurf();
        
        updateStatPanelValue(totalBookingsStatPanel, String.valueOf(totalBookings));
        updateStatPanelValue(futureReservationsStatPanel, String.valueOf(futureReservations));
        updateStatPanelValue(favoriteTurfStatPanel, favoriteTurf);
        
        // NEW: Update the recent bookings table
        refreshRecentBookingsTable();
    }
    
    // NEW: Refresh recent bookings table (similar to AdminDashboard)
    private void refreshRecentBookingsTable() {
        if (recentBookingsTableModel == null) return;
        
        recentBookingsTableModel.setRowCount(0);

        // Show only recent bookings (limit to 8 for homepage)
        int count = 0;
        for (Booking booking : bookings) {
            // Show all statuses in recent bookings
            recentBookingsTableModel.addRow(new Object[]{
                booking.getId(),
                booking.getTurfName(),
                booking.getDate(),
                booking.getStartTime(),
                booking.getHours(),
                booking.getStatus()
            });
            
            count++;
            if (count >= 8) break; // Limit to 8 recent bookings on homepage
        }
    }
    
    private void updateStatPanelValue(JPanel statPanel, String newValue) {
        for (Component comp : statPanel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getFont().getSize() >= 16 || label.getFont().isBold()) {
                    label.setText(newValue);
                    break;
                }
            }
        }
    }
    
    private String getMostBookedTurf() {
        Map<String, Integer> turfCount = new HashMap<>();
        for (Booking booking : bookings) {
            turfCount.put(booking.getTurfName(), turfCount.getOrDefault(booking.getTurfName(), 0) + 1);
        }
        
        return turfCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("No bookings yet");
    }
    
    private JPanel createStatPanel(String title, String value, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(color);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(valueLabel);
        
        return panel;
    }
    
    // FIXED: View & Book Turf Panel with proper timing display
    private void createViewBookTurfPanel() {
        viewBookTurfPanel = new JPanel(new BorderLayout());
        viewBookTurfPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        viewBookTurfPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel title = new JLabel("Available Turfs in Tamil Nadu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        viewBookTurfPanel.add(title, BorderLayout.NORTH);
        
        // Search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel cityLabel = new JLabel("Filter by City:");
        cityLabel.setForeground(new Color(33, 37, 41));
        searchPanel.add(cityLabel);
        
        cityFilterComboBox = new JComboBox<>();
        cityFilterComboBox.addItem("All Cities");
        cityFilterComboBox.setBackground(BACKGROUND_COLOR);
        
        Set<String> cities = new TreeSet<>();
        for (Turf turf : turfs) {
            cities.add(turf.getCity());
        }
        for (String city : cities) {
            cityFilterComboBox.addItem(city);
        }
        
        cityFilterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTurfDisplay();
            }
        });
        
        searchPanel.add(cityFilterComboBox);
        
        JLabel searchLabel = new JLabel("Search by Name:");
        searchLabel.setForeground(new Color(33, 37, 41));
        searchPanel.add(searchLabel);
        
        searchField = new JTextField(15);
        searchField.setBackground(BACKGROUND_COLOR);
        
        // FIXED: Add delayed document listener to prevent rapid firing
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private javax.swing.Timer timer;
            
            public void changedUpdate(DocumentEvent e) { 
                scheduleRefresh();
            }
            public void removeUpdate(DocumentEvent e) { 
                scheduleRefresh();
            }
            public void insertUpdate(DocumentEvent e) { 
                scheduleRefresh();
            }
            
            private void scheduleRefresh() {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                timer = new javax.swing.Timer(300, e -> refreshTurfDisplay()); // 300ms delay
                timer.setRepeats(false);
                timer.start();
            }
        });
        searchPanel.add(searchField);
        
        JButton clearFiltersButton = new JButton("Clear Filters");
        clearFiltersButton.setBackground(PRIMARY_COLOR);
        clearFiltersButton.setForeground(Color.WHITE);
        clearFiltersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cityFilterComboBox.setSelectedIndex(0);
                searchField.setText("");
                refreshTurfDisplay();
            }
        });
        searchPanel.add(clearFiltersButton);
        
        viewBookTurfPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Turfs container with scroll pane
        turfsContainerPanel = new JPanel();
        turfsContainerPanel.setLayout(new BoxLayout(turfsContainerPanel, BoxLayout.Y_AXIS));
        turfsContainerPanel.setBackground(BACKGROUND_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(turfsContainerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        viewBookTurfPanel.add(scrollPane, BorderLayout.CENTER);
        
        refreshTurfDisplay();
    }
    
    private void refreshTurfDisplay() {
        turfsContainerPanel.removeAll();
        
        String selectedCity = (String) cityFilterComboBox.getSelectedItem();
        String searchText = searchField.getText().toLowerCase();
        
        boolean hasResults = false;
        
        for (Turf turf : turfs) {
            if (!"All Cities".equals(selectedCity) && !turf.getCity().equals(selectedCity)) {
                continue;
            }
            
            if (!searchText.isEmpty() && !turf.getName().toLowerCase().contains(searchText)) {
                continue;
            }
            
            JPanel turfCard = createTurfCard(turf);
            turfsContainerPanel.add(turfCard);
            turfsContainerPanel.add(Box.createVerticalStrut(15));
            hasResults = true;
        }
        
        if (!hasResults) {
            JLabel noResultsLabel = new JLabel("No turfs found matching your criteria.");
            noResultsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noResultsLabel.setForeground(new Color(108, 117, 125));
            noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            turfsContainerPanel.add(noResultsLabel);
        }
        
        turfsContainerPanel.revalidate();
        turfsContainerPanel.repaint();
    }
    
    // FIXED: Turf card with proper timing information display
    private JPanel createTurfCard(Turf turf) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(800, 180));
        card.setBackground(BACKGROUND_COLOR);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel nameLabel = new JLabel(turf.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(33, 37, 41));
        
        JTextArea descArea = new JTextArea("ID: " + turf.getId() + " | " + turf.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(BACKGROUND_COLOR);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descArea.setForeground(new Color(108, 117, 125));
        
        leftPanel.add(nameLabel, BorderLayout.NORTH);
        leftPanel.add(descArea, BorderLayout.CENTER);
        
        JPanel detailsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        detailsPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel cityLabel = new JLabel("City: " + turf.getCity());
        cityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cityLabel.setForeground(PRIMARY_COLOR);
        
        JLabel priceLabel = new JLabel("₹" + String.format("%.0f", turf.getPrice()) + "/hour");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(new Color(25, 135, 84));
        
        JLabel capacityLabel = new JLabel("Capacity: " + turf.getCapacity() + " players");
        capacityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        capacityLabel.setForeground(new Color(108, 117, 125));
        
        // View Details button
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setBackground(new Color(30, 144, 255));
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTurfDetailsDialog(turf);
            }
        });
        
        // Book button for this turf
        JButton bookButton = new JButton("Book Now");
        bookButton.setBackground(PRIMARY_COLOR);
        bookButton.setForeground(Color.WHITE);
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBookingDialog(turf);
            }
        });
        
        detailsPanel.add(cityLabel);
        detailsPanel.add(priceLabel);
        detailsPanel.add(capacityLabel);
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(bookButton);
        detailsPanel.add(buttonPanel);
        
        card.add(leftPanel, BorderLayout.CENTER);
        card.add(detailsPanel, BorderLayout.EAST);
        
        return card;
    }
    
    // FIXED: Show detailed turf information with proper timing display
    private void showTurfDetailsDialog(Turf turf) {
        JDialog detailsDialog = new JDialog(this, "Turf Details - " + turf.getName(), true);
        detailsDialog.setLayout(new BorderLayout());
        detailsDialog.setSize(500, 600);
        detailsDialog.setLocationRelativeTo(this);
        
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(BACKGROUND_COLOR);
        
        // Header with turf name
        JLabel titleLabel = new JLabel(turf.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailsPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Basic Information
        JPanel basicInfoPanel = createDetailSection("Basic Information", Arrays.asList(
            "Turf ID: " + turf.getId(),
            "Description: " + turf.getDescription(),
            "Capacity: " + turf.getCapacity() + " players",
            "Price: ₹" + String.format("%.0f", turf.getPrice()) + " per hour"
        ));
        contentPanel.add(basicInfoPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Location Information
        JPanel locationPanel = createDetailSection("Location Information", Arrays.asList(
            "City: " + turf.getCity(),
            "Address: " + turf.getAddress()
        ));
        contentPanel.add(locationPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Available Slots - FIXED: Clear timing display
        JPanel slotsPanel = createDetailSection("Available Time Slots", turf.getAvailableSlots());
        contentPanel.add(slotsPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Facilities
        JPanel facilitiesPanel = createDetailSection("Facilities & Amenities", turf.getFacilities());
        contentPanel.add(facilitiesPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Contact Information
        JPanel contactPanel = createDetailSection("Contact Information", Arrays.asList(
            "Owner: " + turf.getOwnerName(),
            "Phone: " + turf.getContactPhone(),
            "Email: " + turf.getContactEmail()
        ));
        contentPanel.add(contactPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        detailsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton bookButton = new JButton("Book This Turf");
        bookButton.setBackground(PRIMARY_COLOR);
        bookButton.setForeground(Color.WHITE);
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookButton.addActionListener(e -> {
            detailsDialog.dispose();
            showBookingDialog(turf);
        });
        
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(108, 117, 125));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> detailsDialog.dispose());
        
        buttonPanel.add(bookButton);
        buttonPanel.add(closeButton);
        detailsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsDialog.add(detailsPanel);
        detailsDialog.setVisible(true);
    }
    
    // Helper method to create detail sections
    private JPanel createDetailSection(String title, List<String> items) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        for (String item : items) {
            JLabel itemLabel = new JLabel("• " + item);
            itemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            itemLabel.setForeground(new Color(73, 80, 87));
            itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(itemLabel);
            panel.add(Box.createVerticalStrut(3));
        }
        
        return panel;
    }
    
    private void showCalendarDialog(JTextField dateField) {
        JDialog calendarDialog = new JDialog(this, "Select Date", true);
        calendarDialog.setLayout(new BorderLayout());
        calendarDialog.setSize(400, 300);
        calendarDialog.setLocationRelativeTo(this);
        
        CalendarPanel calendarPanel = new CalendarPanel(dateField);
        calendarDialog.add(calendarPanel, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> calendarDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        calendarDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        calendarDialog.setVisible(true);
    }
    
    private boolean isPastDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date selectedDate = sdf.parse(dateString);
            Date today = new Date();
            
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);
            selectedCal.set(Calendar.HOUR_OF_DAY, 0);
            selectedCal.set(Calendar.MINUTE, 0);
            selectedCal.set(Calendar.SECOND, 0);
            selectedCal.set(Calendar.MILLISECOND, 0);
            
            Calendar todayCal = Calendar.getInstance();
            todayCal.setTime(today);
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);
            
            return selectedCal.before(todayCal);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ FIXED: Check if time slot is in the past for the selected date
    private boolean isPastTimeSlot(String date, String startTime) {
        try {
            Date slot = parseDateTime(date, startTime);
            return slot.before(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ FIXED: Validate booking date and time
    private String validateBookingDateTime(String date, String startTime, int hours) {
        try {
            Date bookingDateTime = parseDateTime(date, startTime);
            Date bookingEndTime = calculateEndTime(date, startTime, hours);
            Date now = new Date();
            
            // Check if booking is in the past
            if (bookingEndTime.before(now)) {
                return "You cannot book for past dates/times.\n" +
                       "Selected: " + date + " at " + startTime + " for " + hours + " hour(s)\n" +
                       "Booking would end at: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bookingEndTime) + "\n" +
                       "Current time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(now);
            }
            
            // Check if booking starts in the past
            if (bookingDateTime.before(now)) {
                return "Booking start time is in the past.\n" +
                       "Selected start: " + date + " at " + startTime + "\n" +
                       "Current time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(now);
            }
            
            return null; // Validation passed
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid date/time format. Please try again.";
        }
    }
    
   // ✅ FINAL FIX: Prevents double booking across all users
private boolean isTurfAvailableForAllUsers(String turfId, String date, String startTime, int hours) {
    try {
        // Always get fresh bookings directly from DB
        Vector<Login.Booking> allBookings = loginSystem.getAllBookingsFromDatabase();
        if (allBookings == null) return true;

        // Normalize turf ID (remove letters & leading zeros)
        String turfNorm = turfId.replaceAll("\\D", "");
        turfNorm = turfNorm.replaceFirst("^0+(?!$)", ""); // "001" → "1"

        Date newStart = parseDateTime(date.trim(), startTime.trim());
        Date newEnd   = calculateEndTime(date.trim(), startTime.trim(), hours);

        for (Login.Booking booking : allBookings) {
            if (booking == null) continue;

            // Skip cancelled or completed bookings
            String status = booking.getStatus();
            if (status == null) continue;
            if (status.equalsIgnoreCase("cancelled") || status.equalsIgnoreCase("completed"))
                continue;

            // Normalize existing turf ID
            String existingNorm = booking.getTurfId().replaceAll("\\D", "");
            existingNorm = existingNorm.replaceFirst("^0+(?!$)", "");

            // Must be same turf and same date
            if (!existingNorm.equals(turfNorm)) continue;
            if (!booking.getDate().trim().equals(date.trim())) continue;

            Date existingStart = parseDateTime(booking.getDate().trim(), booking.getStartTime().trim());
            Date existingEnd   = calculateEndTime(booking.getDate().trim(), booking.getStartTime().trim(), booking.getHours());

            // Overlap check (even touching times are blocked)
            boolean hasOverlap = newStart.before(existingEnd) && newEnd.after(existingStart);

            if (hasOverlap) {
               // System.out.println("[BLOCKED DOUBLE BOOKING] " + turfId + " " + date + " " + startTime);
                return false;
            }
        }
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    
    // ✅ ENHANCED: Get available time slots considering ALL user bookings
    private List<String> getAvailableTimeSlots(Turf turf, String date) {
        List<String> allSlots = new ArrayList<>(turf.getAvailableSlots());
        List<String> availableSlots = new ArrayList<>();
        
        try {
            // Check if date is in past
            if (isPastDate(date)) {
                return availableSlots; // Return empty list for past dates
            }
            
            for (String slot : allSlots) {
                boolean isSlotAvailable = true;
                
                // Check if time slot is in the past for the selected date
                if (isPastTimeSlot(date, slot)) {
                    continue; // Skip past time slots
                }
                
                // ENHANCED: Check if turf is available for ALL users (1 hour minimum check)
                if (!isTurfAvailableForAllUsers(turf.getId(), date, slot, 1)) {
                    isSlotAvailable = false;
                }
                
                // Check personal schedule conflict
                if (isSlotAvailable && hasPersonalScheduleConflict(date, slot, 1)) {
                    isSlotAvailable = false;
                }
                
                if (isSlotAvailable) {
                    availableSlots.add(slot);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return allSlots; // Return all slots if there's an error
        }
        
        return availableSlots;
    }
    
    // ✅ FIXED: Check if user has personal schedule conflict
    private boolean hasPersonalScheduleConflict(String date, String startTime, int hours) {
        try {
            Vector<Login.Booking> userBookings = loginSystem.getUserBookingsFromDatabase();
            if (userBookings == null) return false;

            Date newStart = parseDateTime(date, startTime);
            Date newEnd = calculateEndTime(date, startTime, hours);

            for (Login.Booking b : userBookings) {
                if (!"confirmed".equalsIgnoreCase(b.getStatus())) continue;
                if (!b.getDate().equals(date)) continue;

                Date existingStart = parseDateTime(b.getDate(), b.getStartTime());
                Date existingEnd = calculateEndTime(b.getDate(), b.getStartTime(), b.getHours());

                boolean overlap = newStart.before(existingEnd) && newEnd.after(existingStart);
                if (overlap) return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ FIXED: Update booking options with clear timing separation
    private void updateBookingOptions(Turf turf) {
        if (dateField == null || startTimeComboBox == null || playerCountComboBox == null || confirmButton == null) return;
        
        String date = dateField.getText();
        
        // Validate date format first
        if (date == null || date.trim().isEmpty()) {
            return;
        }
        
        // Validate date first
        if (isPastDate(date)) {
            // Disable all options for past dates
            startTimeComboBox.removeAllItems();
            playerCountComboBox.removeAllItems();
            capacityStatusLabel.setText("Cannot book past dates");
            capacityStatusLabel.setForeground(Color.RED);
            confirmButton.setEnabled(false);
            confirmButton.setToolTipText("Cannot book for past dates");
            return;
        }
        
        // Update available time slots
        List<String> availableSlots = getAvailableTimeSlots(turf, date);
        List<String> allSlots = new ArrayList<>(turf.getAvailableSlots());
        
        // Create sets for available and unavailable slots
        Set<String> availableSlotSet = new HashSet<>(availableSlots);
        Set<String> unavailableSlotSet = new HashSet<>(allSlots);
        unavailableSlotSet.removeAll(availableSlotSet);
        
        // Get current selection before clearing
        String currentSelection = (String) startTimeComboBox.getSelectedItem();
        
        // Clear and repopulate the combo box with all slots
        startTimeComboBox.removeAllItems();
        
        // Add all slots with strikethrough for unavailable ones
        for (String slot : allSlots) {
            startTimeComboBox.addItem(slot);
        }
        
        // Set custom renderer for strikethrough
        startTimeComboBox.setRenderer(new StrikethroughCellRenderer(unavailableSlotSet));
        
        // Try to preserve current selection if it's still available
        boolean currentTimeAvailable = false;
        if (currentSelection != null && availableSlotSet.contains(currentSelection)) {
            startTimeComboBox.setSelectedItem(currentSelection);
            currentTimeAvailable = true;
        } else if (!availableSlots.isEmpty()) {
            // Select first available slot if current selection is not available
            startTimeComboBox.setSelectedItem(availableSlots.get(0));
            currentSelection = availableSlots.get(0);
            currentTimeAvailable = true;
        } else {
            currentSelection = null;
        }
        
        // Update player count options based on selected time
        if (currentSelection != null && currentTimeAvailable) {
            int hours = hoursComboBox != null ? (Integer) hoursComboBox.getSelectedItem() : 1;
            
            // ENHANCED: Check global availability for the selected duration
            boolean slotAvailableForDuration = isTurfAvailableForAllUsers(turf.getId(), date, currentSelection, hours);
            
            playerCountComboBox.removeAllItems();
            if (slotAvailableForDuration) {
                // Add player options (1 to turf capacity)
                for (int i = 1; i <= turf.getCapacity(); i++) {
                    playerCountComboBox.addItem(i);
                }
                
                if (capacityStatusLabel != null) {
                    capacityStatusLabel.setText("Slot available - " + turf.getCapacity() + " players maximum");
                    capacityStatusLabel.setForeground(PRIMARY_COLOR); // Green
                }
                
                confirmButton.setEnabled(true);
                confirmButton.setToolTipText("Click to confirm booking");
            } else {
                capacityStatusLabel.setText("Time slot not available for selected duration");
                capacityStatusLabel.setForeground(Color.RED);
                confirmButton.setEnabled(false);
                confirmButton.setToolTipText("Time slot not available for selected duration");
            }
        } else {
            // No time slots available
            playerCountComboBox.removeAllItems();
            if (availableSlots.isEmpty()) {
                capacityStatusLabel.setText("No available time slots for selected date");
                capacityStatusLabel.setForeground(Color.RED);
            } else {
                capacityStatusLabel.setText("Please select a time slot");
                capacityStatusLabel.setForeground(new Color(108, 117, 125));
            }
            confirmButton.setEnabled(false);
            confirmButton.setToolTipText("No available time slots");
        }
    }
    
    // ✅ FIXED: Save booking with global availability check
    private boolean saveBookingWithGlobalCheck(String turfId, String bookingDate,
                                             String startTime, int hours, int playerCount,
                                             double totalAmount) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = loginSystem.connect();
            if (conn == null) {
                System.out.println("[DEBUG] Database connection failed.");
                return false;
            }

            // ✅ FINAL CHECK: Ensure turf is still available (prevent race condition)
            if (!isTurfAvailableForAllUsers(turfId, bookingDate, startTime, hours)) {
                JOptionPane.showMessageDialog(this,
                    "Sorry! This time slot was just booked by another user.\nPlease choose a different time.",
                    "Time Slot No Longer Available",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }

            System.out.println("[DEBUG] Inserting booking:");
            System.out.println(" user_id=" + currentUser.getId());
            System.out.println(" username=" + currentUser.getName());
            System.out.println(" turf_id=" + turfId);
            System.out.println(" date/time=" + bookingDate + " " + startTime);
            System.out.println(" hours=" + hours + ", players=" + playerCount + ", amount=" + totalAmount);

            String sql = "INSERT INTO bookings " +
                    "(user_id, username, turf_id, booking_date, start_time, hours, player_count, total_amount, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, datetime('now'))";

            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, Integer.parseInt(currentUser.getId().replaceAll("\\D", ""))); // user_id = INTEGER
            stmt.setString(2, currentUser.getName());           // username = TEXT
            stmt.setString(3, turfId.replaceAll("\\D", ""));    // turf_id = INTEGER (remove 'T' prefix)
            stmt.setString(4, bookingDate);                     // booking_date = TEXT
            stmt.setString(5, startTime);                       // start_time = TEXT
            stmt.setInt(6, hours);                              // hours = INTEGER
            stmt.setInt(7, playerCount);                        // player_count = INTEGER
            stmt.setDouble(8, totalAmount);                     // total_amount = REAL
            stmt.setString(9, "confirmed");                     // status = TEXT

            int rows = stmt.executeUpdate();
            System.out.println("[DEBUG] Booking inserted rows=" + rows);

            return rows > 0;

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to save booking:");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ✅ FIXED: showBookingDialog method with clear timing separation
    private void showBookingDialog(Turf turf) {
        currentBookingTurf = turf;
        
        JDialog bookingDialog = new JDialog(this, "Book " + turf.getName(), true);
        bookingDialog.setLayout(new BorderLayout());
        bookingDialog.setSize(500, 600);
        bookingDialog.setLocationRelativeTo(this);
        
        JPanel bookingPanel = new JPanel(new GridBagLayout());
        bookingPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        bookingPanel.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Turf Information Header
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel turfInfoLabel = new JLabel("Booking: " + turf.getName());
        turfInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        turfInfoLabel.setForeground(PRIMARY_COLOR);
        turfInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bookingPanel.add(turfInfoLabel, gbc);
        
        // Turf ID - CLEARLY SEPARATED
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel turfIdLabel = new JLabel("Turf ID:");
        turfIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        turfIdLabel.setForeground(new Color(33, 37, 41));
        bookingPanel.add(turfIdLabel, gbc);
        
        gbc.gridx = 1;
        JTextField turfIdField = new JTextField(turf.getId());
        turfIdField.setEditable(false);
        turfIdField.setBackground(new Color(248, 249, 250));
        turfIdField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingPanel.add(turfIdField, gbc);
        
        // Date selection - CLEARLY SEPARATED
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel dateLabel = new JLabel("Booking Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dateLabel.setForeground(new Color(33, 37, 41));
        bookingPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        JPanel datePanel = new JPanel(new BorderLayout());
        dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Document listener for date changes
        dateField.getDocument().addDocumentListener(new DocumentListener() {
            private javax.swing.Timer timer;
            
            public void changedUpdate(DocumentEvent e) { 
                scheduleUpdate();
            }
            public void removeUpdate(DocumentEvent e) { 
                scheduleUpdate();
            }
            public void insertUpdate(DocumentEvent e) { 
                scheduleUpdate();
            }
            
            private void scheduleUpdate() {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                timer = new javax.swing.Timer(500, e -> updateBookingOptions(turf)); // 500ms delay for date changes
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        JButton dateButton = new JButton("Calender");
        dateButton.setBackground(PRIMARY_COLOR);
        dateButton.setForeground(Color.WHITE);
        dateButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCalendarDialog(dateField);
            }
        });
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(dateButton, BorderLayout.EAST);
        bookingPanel.add(datePanel, gbc);
        
        // Available Time Slots Section - CLEARLY SEPARATED WITH HEADER
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel timeSlotsHeader = new JLabel("Available Time Slots");
        timeSlotsHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timeSlotsHeader.setForeground(PRIMARY_COLOR);
        timeSlotsHeader.setHorizontalAlignment(SwingConstants.CENTER);
        bookingPanel.add(timeSlotsHeader, gbc);
        
        // Start Time selection - PROPERLY LABELED
        gbc.gridwidth = 1;
        gbc.gridy = 4;
        gbc.gridx = 0;
        JLabel startTimeLabel = new JLabel("Start Time:");
        startTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        startTimeLabel.setForeground(new Color(33, 37, 41));
        bookingPanel.add(startTimeLabel, gbc);
        
        gbc.gridx = 1;
        startTimeComboBox = new JComboBox<>();
        startTimeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Action listener for time selection changes
        startTimeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTimeComboBox.getSelectedItem() != null && !startTimeComboBox.getSelectedItem().toString().trim().isEmpty()) {
                    updateBookingOptions(turf);
                }
            }
        });
        bookingPanel.add(startTimeComboBox, gbc);
        
        // Booking Details Section - CLEARLY SEPARATED WITH HEADER
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JLabel bookingDetailsHeader = new JLabel("Booking Details");
        bookingDetailsHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookingDetailsHeader.setForeground(PRIMARY_COLOR);
        bookingDetailsHeader.setHorizontalAlignment(SwingConstants.CENTER);
        bookingPanel.add(bookingDetailsHeader, gbc);
        
        // Hours selection - PROPERLY LABELED AS DURATION
        gbc.gridwidth = 1;
        gbc.gridy = 6;
        gbc.gridx = 0;
        JLabel hoursLabel = new JLabel("Duration (Hours):");
        hoursLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hoursLabel.setForeground(new Color(33, 37, 41));
        bookingPanel.add(hoursLabel, gbc);
        
        gbc.gridx = 1;
        hoursComboBox = new JComboBox<>();
        hoursComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        for (int i = 1; i <= 6; i++) {
            hoursComboBox.addItem(i);
        }
        
        // Create labels for duration and amount that we can reference directly
        JLabel durationLabel = new JLabel("1 hour");
        durationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        durationLabel.setForeground(new Color(33, 37, 41));
        
        JLabel amountLabel = new JLabel("₹" + String.format("%.0f", turf.getPrice()));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amountLabel.setForeground(new Color(25, 135, 84));
        
        // Action listener for hours changes
        hoursComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBookingAmount(turf, hoursComboBox, durationLabel, amountLabel);
                updateBookingOptions(turf);
            }
        });
        bookingPanel.add(hoursComboBox, gbc);
        
        // Player Count selection - PROPERLY LABELED
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel playerCountLabel = new JLabel("Number of Players:");
        playerCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        playerCountLabel.setForeground(new Color(33, 37, 41));
        bookingPanel.add(playerCountLabel, gbc);
        
        gbc.gridx = 1;
        playerCountComboBox = new JComboBox<>();
        playerCountComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingPanel.add(playerCountComboBox, gbc);
        
        // Capacity status label
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        capacityStatusLabel = new JLabel("Select date and time to see availability");
        capacityStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        capacityStatusLabel.setForeground(new Color(108, 117, 125));
        capacityStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bookingPanel.add(capacityStatusLabel, gbc);
        
        // Booking Summary Section - CLEARLY SEPARATED
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        JLabel durationTextLabel = new JLabel("Duration:");
        durationTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        durationTextLabel.setForeground(new Color(33, 37, 41));
        bookingPanel.add(durationTextLabel, gbc);
        
        gbc.gridx = 1;
        bookingPanel.add(durationLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 10;
        JLabel amountTextLabel = new JLabel("Total Amount:");
        amountTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        amountTextLabel.setForeground(new Color(33, 37, 41));
        bookingPanel.add(amountTextLabel, gbc);
        
        gbc.gridx = 1;
        bookingPanel.add(amountLabel, gbc);
        
        // Buttons Section
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        confirmButton = new JButton("Confirm Booking");
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmButton.setEnabled(false); // Initially disabled
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmBooking(turf, bookingDialog);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelButton.addActionListener(e -> bookingDialog.dispose());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        bookingPanel.add(buttonPanel, gbc);
        
        // Initial update of options
        SwingUtilities.invokeLater(() -> {
            updateBookingOptions(turf);
        });
        
        bookingDialog.add(bookingPanel, BorderLayout.CENTER);
        bookingDialog.setVisible(true);
    }
    
    // ✅ FIXED: confirmBooking method with clear timing validation
    private void confirmBooking(Turf turf, JDialog bookingDialog) {
        String startTime = (String) startTimeComboBox.getSelectedItem();
        int hours = (Integer) hoursComboBox.getSelectedItem();
        int playerCount = (Integer) playerCountComboBox.getSelectedItem();
        String date = dateField.getText();

        // Final validation
        String validationError = validateBookingDateTime(date, startTime, hours);
        if (validationError != null) {
            JOptionPane.showMessageDialog(bookingDialog,
                "Booking Error!\n\n" + validationError,
                "Invalid Date/Time Selection",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ENHANCED: Final global availability check
        if (!isTurfAvailableForAllUsers(turf.getId(), date, startTime, hours)) {
            JOptionPane.showMessageDialog(bookingDialog,
                "This time slot is no longer available!\n\n" +
                "Turf: " + turf.getName() + "\n" +
                "Date: " + date + "\n" +
                "Time: " + startTime + " for " + hours + " hour(s)\n\n" +
                "It was likely booked by another user. Please choose a different time slot.",
                "Time Slot No Longer Available",
                JOptionPane.WARNING_MESSAGE);
            
            // Refresh available slots
            updateBookingOptions(turf);
            return;
        }

        // Check for personal conflict
        if (hasPersonalScheduleConflict(date, startTime, hours)) {
            int result = JOptionPane.showConfirmDialog(bookingDialog,
                "You already have a booking at this time!\n\n" +
                "Date: " + date + "\n" +
                "Time: " + startTime + " for " + hours + " hour(s)\n\n" +
                "Do you want to proceed anyway?",
                "Schedule Conflict Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (result != JOptionPane.YES_OPTION) return;
        }

        String bookingId = "B" + String.format("%03d", bookings.size() + 1);
        double amount = turf.getPrice() * hours;

        // ✅ ENHANCED: Use global check method
        boolean saved = saveBookingWithGlobalCheck(
            turf.getId(), date, startTime, hours, playerCount, amount
        );

        if (saved) {
            refreshBookingsFromDatabase();

            Booking newBooking = new Booking(bookingId, currentUser.getId(), turf.getId(), turf.getName(),
                date, startTime, hours, playerCount, amount, "confirmed");
            bookings.add(newBooking);

            int pointsEarned = (int) (turf.getPrice() * hours / 100);
            userPoints.put(currentUser.getName(),
                userPoints.getOrDefault(currentUser.getName(), 0) + pointsEarned);

            JOptionPane.showMessageDialog(bookingDialog,
                "Booking Confirmed!\n\n" +
                "Booking ID: " + bookingId + "\n" +
                "Turf ID: " + turf.getId() + "\n" +
                "Turf: " + turf.getName() + "\n" +
                "Date: " + date + "\n" +
                "Time: " + startTime + " for " + hours + " hour(s)\n" +
                "Players: " + playerCount + "\n" +
                "Total Amount: ₹" + String.format("%.0f", amount) + "\n\n" +
                "Note: Booking status will automatically change to 'completed' when the time ends.",
                "Booking Confirmed",
                JOptionPane.INFORMATION_MESSAGE);

            updateBookingsTable();
            // NEW: Also update recent bookings on homepage
            refreshRecentBookingsTable();
            bookingDialog.dispose();
        } else {
            JOptionPane.showMessageDialog(bookingDialog,
                "Booking Failed!\n\n" +
                "Unable to complete booking. This may be due to:\n" +
                "- Time slot was just taken by another user\n" +
                "- Database error\n\n" +
                "Please try again with different options.",
                "Booking Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBookingAmount(Turf turf, JComboBox<Integer> hoursComboBox, 
                                   JLabel durationLabel, JLabel amountLabel) {
        int hours = (Integer) hoursComboBox.getSelectedItem();
        
        if (hours > 0) {
            durationLabel.setText(hours + " hour(s)");
            double amount = turf.getPrice() * hours;
            amountLabel.setText("₹" + String.format("%.0f", amount));
        }
    }
    
    private void createViewBookingPanel() {
        viewBookingPanel = new JPanel(new BorderLayout());
        viewBookingPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        viewBookingPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel title = new JLabel("My Bookings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        viewBookingPanel.add(title, BorderLayout.NORTH);
        
        JTable bookingsTable = new JTable(bookingTableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.setBackground(BACKGROUND_COLOR);
        bookingsTable.setForeground(Color.BLACK);
        bookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // ✅ ENHANCED: Color coding for booking status
        bookingsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Color coding for status column (column 8)
                if (column == 8 && value != null) {
                    String status = value.toString().toLowerCase();
                    switch (status) {
                        case "confirmed":
                            c.setBackground(new Color(144, 238, 144)); // Light green
                            c.setForeground(Color.BLACK);
                            break;
                        case "completed":
                            c.setBackground(new Color(173, 216, 230)); // Light blue
                            c.setForeground(Color.BLACK);
                            break;
                        case "cancelled":
                            c.setBackground(new Color(255, 182, 193)); // Light red
                            c.setForeground(Color.BLACK);
                            break;
                        default:
                            c.setBackground(BACKGROUND_COLOR);
                            c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setBackground(BACKGROUND_COLOR);
                    c.setForeground(Color.BLACK);
                }
                
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        viewBookingPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton("Cancel Selected Booking");
        cancelButton.setBackground(PRIMARY_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookingsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(viewBookingPanel, 
                        "Please select a booking to cancel.", 
                        "No Selection", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String bookingId = (String) bookingTableModel.getValueAt(selectedRow, 0);
                String status = (String) bookingTableModel.getValueAt(selectedRow, 8);
                
                // ✅ ENHANCED: Prevent cancellation of completed bookings
                if ("completed".equalsIgnoreCase(status)) {
                    JOptionPane.showMessageDialog(viewBookingPanel, 
                        "Cannot cancel a completed booking.\nThe booking time has already passed.", 
                        "Invalid Action", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // ✅ ENHANCED: Warn about time-elapsed bookings
                if ("confirmed".equalsIgnoreCase(status)) {
                    // Find the booking and check if time has passed
                    for (Booking booking : bookings) {
                        if (booking.getId().equals(bookingId) && isBookingTimeOver(booking)) {
                            JOptionPane.showMessageDialog(viewBookingPanel, 
                                "This booking time has already passed.\nIt will be automatically marked as completed soon.", 
                                "Booking Time Elapsed", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }
                
                int confirm = JOptionPane.showConfirmDialog(
                    viewBookingPanel, 
                    "Are you sure you want to cancel booking " + bookingId + "?", 
                    "Confirm Cancellation", 
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean updated = loginSystem.updateBookingStatusInDatabase(bookingId, "cancelled");
                    
                    if (updated) {
                        for (Booking booking : bookings) {
                            if (booking.getId().equals(bookingId)) {
                                booking.setStatus("cancelled");
                                break;
                            }
                        }
                        
                        updateBookingsTable();
                        // NEW: Also update recent bookings on homepage
                        refreshRecentBookingsTable();
                        
                        JOptionPane.showMessageDialog(viewBookingPanel, 
                            "Booking " + bookingId + " has been cancelled.\n" +
                            "This time slot is now available for other users.", 
                            "Booking Cancelled", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(viewBookingPanel, 
                            "Failed to cancel booking in database. Please try again.", 
                            "Database Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        buttonPanel.add(cancelButton);
        
        // ✅ NEW: Refresh button to update completed status
        JButton refreshButton = new JButton("Refresh Bookings");
        refreshButton.setBackground(new Color(30, 144, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshBookingsFromDatabase();
                autoCompletePastBookings(); // Force completion check
                // NEW: Also update recent bookings on homepage
                refreshRecentBookingsTable();
                JOptionPane.showMessageDialog(viewBookingPanel,
                    "Bookings refreshed!\nCompleted bookings are automatically updated.",
                    "Refresh Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(refreshButton);
        
        viewBookingPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void updateBookingsTable() {
        if (bookingTableModel == null) return;
        
        bookingTableModel.setRowCount(0);
        
        for (Booking booking : bookings) {
            // Include ALL bookings for the current user (confirmed, cancelled, completed)
            if (booking.getUserId().equals(currentUser.getId())) {
                bookingTableModel.addRow(new Object[]{
                    booking.getId(),
                    booking.getTurfId(),
                    booking.getTurfName(),
                    booking.getDate(),
                    booking.getStartTime(),
                    booking.getHours(),
                    booking.getPlayerCount(),
                    "₹" + String.format("%.0f", booking.getAmount()),
                    booking.getStatus()
                });
            }
        }
    }

    
    private void refreshBookingsFromDatabase() {
        bookings.clear();

        Vector<Login.Booking> dbBookings = loginSystem.getUserBookingsFromDatabase();
        if (dbBookings != null) {
            for (Login.Booking dbBooking : dbBookings) {
                String uiUserId = dbBooking.getUserId();
                if (!uiUserId.startsWith("U")) uiUserId = "U" + uiUserId;

                String uiTurfId = dbBooking.getTurfId();
                if (!uiTurfId.startsWith("T")) uiTurfId = "T" + String.format("%03d", Integer.parseInt(dbBooking.getTurfId()));

                bookings.add(new Booking(
                    dbBooking.getId(),
                    uiUserId,
                    uiTurfId,
                    dbBooking.getTurfName(),
                    dbBooking.getDate(),
                    dbBooking.getStartTime(),
                    dbBooking.getHours(),
                    dbBooking.getPlayerCount(),
                    dbBooking.getAmount(),
                    dbBooking.getStatus(),
                    dbBooking.getUsername(),
                    dbBooking.getEmail()
                ));
            }
        }
        updateBookingsTable();
        // NEW: Also update recent bookings table
        refreshRecentBookingsTable();
    }

    private void showHomepage() {
        cardLayout.show(mainPanel, "HOME");
        setActiveMenuButton(homeButton);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                User sampleUser = new User("U001", "mega", "mega@example.com");
                new UserDashboard(sampleUser, new Login()).setVisible(true);
            }
        });
    }
}