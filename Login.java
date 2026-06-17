// Login.java - Complete Fixed Version with Database Schema Updates
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Login extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Admin login fields
    private JTextField adminUsernameField;
    private JPasswordField adminPasswordField;

    // User login fields  
    private JTextField userUsernameField;
    private JPasswordField userPasswordField;

    // Registration fields
    private JTextField regUsernameField, regEmailField;
    private JPasswordField regPasswordField, regConfirmPasswordField;

    // Database connection
    private Connection connection;
    private String currentUser;
    private String currentUserRole;

    public Login() {
        initializeDatabase();
        initializeUI();
    }
    
    // Database Connection Class
    class DBConnection {
        private static final String URL = "jdbc:sqlite:bookmyturf.db";
        private static Connection connection = null;
        
        public static Connection getConnection() {
            if (connection == null) {
                try {
                    // Load SQLite JDBC driver
                    Class.forName("org.sqlite.JDBC");
                    // Create database connection
                    connection = DriverManager.getConnection(URL);
                    System.out.println("Database connected successfully!");
                } catch (ClassNotFoundException e) {
                    System.err.println("SQLite JDBC Driver not found!");
                    JOptionPane.showMessageDialog(null, 
                        "SQLite Driver not found!\n" +
                        "Please make sure 'sqlite-jdbc-3.45.1.0.jar' is in your project folder.\n" +
                        "Running in demo mode without database.", 
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException e) {
                    System.err.println("Database connection failed: " + e.getMessage());
                    JOptionPane.showMessageDialog(null, 
                        "Database connection failed!\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "Running in demo mode without database.", 
                        "Database Error", JOptionPane.WARNING_MESSAGE);
                }
            }
            return connection;
        }
        
        public static void closeConnection() {
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    // Database connection helper - used by dashboards
    public Connection connect() {
        try {
            String url = "jdbc:sqlite:bookmyturf.db";
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // ✅ AUTO: Mark all past bookings as 'completed'
    public void autoUpdateCompletedBookings() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = connect();
            if (conn == null) {
                System.out.println("[DEBUG] Database connection failed for auto update.");
                return;
            }

            // 🕒 Convert date + time + hours into actual end time and mark completed
            String sql = 
                "UPDATE bookings " +
                "SET status = 'completed' " +
                "WHERE status = 'confirmed' AND " +
                "datetime(booking_date || ' ' || start_time, '+' || hours || ' hours') < datetime('now')";

            stmt = conn.prepareStatement(sql);
            int rows = stmt.executeUpdate();

           // System.out.println("[DEBUG] Auto-updated " + rows + " booking(s) to 'completed'.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeDatabase() {
        try {
            // Get database connection
            connection = DBConnection.getConnection();
            
            if (connection != null && !connection.isClosed()) {
                createTables();
                updateDatabaseSchema(); // ADD THIS LINE - FIXES THE MISSING COLUMN ISSUE
                insertSampleData();
                System.out.println("Database initialized successfully!");
            } else {
                System.out.println("Running in demo mode without database");
            }
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Running in demo mode without database.\n" +
                "Some features may be limited.", 
                "Demo Mode", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // NEW METHOD: Update database schema to add missing columns
    private void updateDatabaseSchema() {
        if (connection == null) return;
        
        try (Statement stmt = connection.createStatement()) {
            // Check if username column exists in bookings table
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(bookings)");
            boolean hasUsernameColumn = false;
            while (rs.next()) {
                if ("username".equals(rs.getString("name"))) {
                    hasUsernameColumn = true;
                    break;
                }
            }
            
            // Add username column if it doesn't exist
            if (!hasUsernameColumn) {
                stmt.execute("ALTER TABLE bookings ADD COLUMN username TEXT");
                System.out.println("Added username column to bookings table");
                
                // Update existing records with usernames
                stmt.execute("UPDATE bookings SET username = 'user' || user_id WHERE username IS NULL");
                System.out.println("Updated existing booking records with usernames");
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating database schema: " + e.getMessage());
        }
    }
    
    private void createTables() {
        if (connection == null) return;
        
        try (Statement stmt = connection.createStatement()) {
            // Enable foreign keys
            stmt.execute("PRAGMA foreign_keys = ON");
            
            // Users table
            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "role TEXT NOT NULL CHECK(role IN ('admin', 'user')), " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
            
            // Turfs table with enhanced fields
            String turfsTable = "CREATE TABLE IF NOT EXISTS turfs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT UNIQUE NOT NULL, " +
                "location TEXT NOT NULL, " +
                "sport_type TEXT NOT NULL, " +
                "price_per_hour REAL NOT NULL, " +
                "capacity INTEGER NOT NULL, " +
                "admin_id INTEGER, " +
                "description TEXT, " +
                "address TEXT, " +
                "contact_phone TEXT, " +
                "contact_email TEXT, " +
                "owner_name TEXT, " +
                "available_slots TEXT, " +
                "facilities TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (admin_id) REFERENCES users(id))";
            
            // Bookings table with username column - UPDATED TO REMOVE TURF_NAME
            String bookingsTable = "CREATE TABLE IF NOT EXISTS bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "username TEXT NOT NULL, " + // Store username directly
                "turf_id INTEGER NOT NULL, " +
                "booking_date DATE NOT NULL, " +
                "start_time TIME NOT NULL, " +
                "hours INTEGER NOT NULL, " +
                "player_count INTEGER NOT NULL, " +
                "total_amount REAL NOT NULL, " +
                "status TEXT DEFAULT 'confirmed', " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (turf_id) REFERENCES turfs(id))";
            
            stmt.execute(usersTable);
            stmt.execute(turfsTable);
            stmt.execute(bookingsTable);
            
            System.out.println("Database tables created successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
    
    private void insertSampleData() {
        if (connection == null) return;
        
        try (Statement stmt = connection.createStatement()) {
            // Check if sample data already exists
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            if (rs.getInt(1) == 0) {
                // Insert admin users
                stmt.execute("INSERT INTO users (username, password, email, role) VALUES " +
                    "('rajesh', 'admin123', 'rajesh@bookmyturf.com', 'admin'), " +
                    "('suresh', 'admin123', 'suresh@bookmyturf.com', 'admin'), " +
                    "('priya', 'admin123', 'priya@bookmyturf.com', 'admin'), " +
                    "('arun', 'admin123', 'arun@bookmyturf.com', 'admin'), " +
                    "('hari', 'admin123', 'hari@bookmyturf.com', 'admin'), " +
                    "('surya', 'admin123', 'surya@bookmyturf.com', 'admin')");
                
                // Insert user accounts
                stmt.execute("INSERT INTO users (username, password, email, role) VALUES " +
                    "('mega', 'mega123', 'mega@example.com', 'user'), " +
                    "('shobi', 'shobi123', 'shobi@example.com', 'user'), " +
                    "('sanjith', 'sanjith123', 'sanjith@example.com', 'user'), " +
                    "('sneka', 'sneka123', 'sneka@example.com', 'user'), " +
                    "('rohini', 'rohini123', 'rohini@example.com', 'user'), " +
                    "('sachika', 'sachika123', 'sachika@example.com', 'user'), " +
                    "('sabari', 'sabari123', 'sabari@example.com', 'user'), " +
                    "('gowtham', 'gowtham123', 'gowtham@example.com', 'user'), " +
                    "('vijay', 'vijay123', 'vijay@example.com', 'user')");
                
                // Insert turfs with enhanced details
                stmt.execute("INSERT INTO turfs (name, location, sport_type, price_per_hour, capacity, admin_id, description, address, contact_phone, contact_email, owner_name, available_slots, facilities) VALUES " +
                    "('Green Field Football', 'Chennai', 'Football', 1200.0, 20, (SELECT id FROM users WHERE username = 'rajesh'), " +
                    "'Large grass field with professional goals and floodlights', " +
                    "'123 Sports Complex, Anna Nagar, Chennai - 600040', " +
                    "'+91-9876543210', 'greenfield@example.com', 'Rajesh', " +
                    "'[\"9:00\", \"11:00\", \"14:00\", \"16:00\", \"18:00\"]', " +
                    "'[\"Floodlights\", \"Changing Rooms\", \"Parking\", \"Water Facility\", \"First Aid\"]'), " +
                    
                    "('Sports Arena Cricket', 'Coimbatore', 'Cricket', 1500.0, 30, (SELECT id FROM users WHERE username = 'suresh'), " +
                    "'Professional cricket pitch with pavilion and practice nets', " +
                    "'456 Cricket Ground, Race Course, Coimbatore - 641018', " +
                    "'+91-9876543211', 'sportsarena@example.com', 'Suresh', " +
                    "'[\"8:00\", \"11:00\", \"14:00\", \"17:00\"]', " +
                    "'[\"Practice Nets\", \"Pavilion\", \"Scoreboard\", \"Umpire Services\", \"Cafeteria\"]'), " +
                    
                    "('Elite Tennis Court', 'Madurai', 'Tennis', 800.0, 4, (SELECT id FROM users WHERE username = 'priya'), " +
                    "'Hard court with nets and professional lighting system', " +
                    "'789 Tennis Complex, KK Nagar, Madurai - 625020', " +
                    "'+91-9876543212', 'elitetennis@example.com', 'Priya', " +
                    "'[\"9:00\", \"11:00\", \"13:00\", \"15:00\", \"17:00\", \"19:00\"]', " +
                    "'[\"Air Conditioned\", \"Professional Nets\", \"Ball Machine\", \"Coach Available\", \"Pro Shop\"]'), " +
                    
                    "('Basketball Arena', 'Chennai', 'Basketball', 900.0, 10, (SELECT id FROM users WHERE username = 'arun'), " +
                    "'Indoor court with professional flooring and electronic scoreboard', " +
                    "'321 Indoor Stadium, T Nagar, Chennai - 600017', " +
                    "'+91-9876543213', 'basketballarena@example.com', 'Arun', " +
                    "'[\"8:00\", \"10:00\", \"12:00\", \"14:00\", \"16:00\", \"18:00\"]', " +
                    "'[\"Air Conditioned\", \"Electronic Scoreboard\", \"Basketball Rental\", \"Coach Available\", \"Locker Rooms\"]'), " +
                    
                    "('Rugby Ground', 'Coimbatore', 'Rugby', 1400.0, 25, (SELECT id FROM users WHERE username = 'hari'), " +
                    "'Full-size rugby field with changing rooms and medical facility', " +
                    "'654 Rugby Park, Peelamedu, Coimbatore - 641004', " +
                    "'+91-9876543214', 'rugbyground@example.com', 'Hari', " +
                    "'[\"9:00\", \"12:00\", \"15:00\", \"18:00\"]', " +
                    "'[\"Medical Room\", \"Changing Rooms\", \"Equipment Rental\", \"Showers\", \"Physiotherapist\"]'), " +
                    
                    "('Badminton Court', 'Madurai', 'Badminton', 600.0, 6, (SELECT id FROM users WHERE username = 'surya'), " +
                    "'Air-conditioned indoor court with professional flooring', " +
                    "'987 Sports Center, Vilakkuthoon, Madurai - 625001', " +
                    "'+91-9876543215', 'badmintoncourt@example.com', 'Surya', " +
                    "'[\"8:00\", \"10:00\", \"12:00\", \"14:00\", \"16:00\", \"18:00\", \"20:00\"]', " +
                    "'[\"Air Conditioned\", \"Professional Shuttles\", \"Racket Rental\", \"Coach Available\", \"Cafeteria\"]')");
                
                // Insert sample bookings with username - UPDATED TO REMOVE TURF_NAME
                stmt.execute("INSERT INTO bookings (user_id, username, turf_id, booking_date, start_time, hours, player_count, total_amount, status) VALUES " +
                    "((SELECT id FROM users WHERE username = 'mega'), 'mega', (SELECT id FROM turfs WHERE name = 'Green Field Football'), '2024-01-20', '10:00', 2, 18, 2400.0, 'confirmed'), " +
                    "((SELECT id FROM users WHERE username = 'mega'), 'mega', (SELECT id FROM turfs WHERE name = 'Sports Arena Cricket'), '2024-01-21', '14:00', 1, 15, 1500.0, 'confirmed'), " +
                    "((SELECT id FROM users WHERE username = 'shobi'), 'shobi', (SELECT id FROM turfs WHERE name = 'Basketball Arena'), '2024-01-20', '11:00', 2, 8, 1800.0, 'confirmed'), " +
                    "((SELECT id FROM users WHERE username = 'shobi'), 'shobi', (SELECT id FROM turfs WHERE name = 'Rugby Ground'), '2024-01-21', '16:00', 1, 20, 1400.0, 'confirmed'), " +
                    "((SELECT id FROM users WHERE username = 'sanjith'), 'sanjith', (SELECT id FROM turfs WHERE name = 'Green Field Football'), '2024-01-23', '11:00', 1, 16, 1200.0, 'confirmed'), " +
                    "((SELECT id FROM users WHERE username = 'sneka'), 'sneka', (SELECT id FROM turfs WHERE name = 'Elite Tennis Court'), '2024-01-25', '17:00', 1, 4, 800.0, 'confirmed'), " +
                    "((SELECT id FROM users WHERE username = 'rohini'), 'rohini', (SELECT id FROM turfs WHERE name = 'Rugby Ground'), '2024-01-27', '15:00', 2, 22, 2800.0, 'confirmed'), " +
                    "((SELECT id FROM users WHERE username = 'sachika'), 'sachika', (SELECT id FROM turfs WHERE name = 'Green Field Football'), '2024-01-29', '09:00', 2, 16, 2400.0, 'confirmed'), " +
                    "((SELECT id FROM users WHERE username = 'sabari'), 'sabari', (SELECT id FROM turfs WHERE name = 'Elite Tennis Court'), '2024-02-01', '13:00', 1, 4, 800.0, 'confirmed'), " +
                    "((SELECT id FROM users WHERE username = 'gowtham'), 'gowtham', (SELECT id FROM turfs WHERE name = 'Rugby Ground'), '2024-02-03', '12:00', 1, 20, 1400.0, 'confirmed'), " +
                    "((SELECT id FROM users WHERE username = 'vijay'), 'vijay', (SELECT id FROM turfs WHERE name = 'Green Field Football'), '2024-02-05', '14:00', 1, 18, 1200.0, 'confirmed')");
                
                System.out.println("Sample data inserted successfully!");
            } else {
                System.out.println("Sample data already exists!");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
        }
    }
    
    private boolean authenticateUser(String username, String password) {
        // If database is not available, use demo authentication
        if (connection == null) {
            return demoAuthenticateUser(username, password);
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                currentUser = username;
                currentUserRole = rs.getString("role");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            // Fall back to demo authentication
            return demoAuthenticateUser(username, password);
        }
        return false;
    }
    
    // Demo authentication for when database is not available
    private boolean demoAuthenticateUser(String username, String password) {
        // Demo users - admins and regular users
        Map<String, String[]> demoUsers = new HashMap<>();
        demoUsers.put("rajesh", new String[]{"admin123", "admin"});
        demoUsers.put("suresh", new String[]{"admin123", "admin"});
        demoUsers.put("priya", new String[]{"admin123", "admin"});
        demoUsers.put("arun", new String[]{"admin123", "admin"});
        demoUsers.put("hari", new String[]{"admin123", "admin"});
        demoUsers.put("surya", new String[]{"admin123", "admin"});
        demoUsers.put("mega", new String[]{"mega123", "user"});
        demoUsers.put("shobi", new String[]{"shobi123", "user"});
        demoUsers.put("sanjith", new String[]{"sanjith123", "user"});
        demoUsers.put("sneka", new String[]{"sneka123", "user"});
        demoUsers.put("rohini", new String[]{"rohini123", "user"});
        demoUsers.put("sachika", new String[]{"sachika123", "user"});
        demoUsers.put("sabari", new String[]{"sabari123", "user"});
        demoUsers.put("gowtham", new String[]{"gowtham123", "user"});
        demoUsers.put("vijay", new String[]{"vijay123", "user"});
        
        if (demoUsers.containsKey(username) && demoUsers.get(username)[0].equals(password)) {
            currentUser = username;
            currentUserRole = demoUsers.get(username)[1];
            return true;
        }
        return false;
    }
    
    private boolean registerUser(String username, String email, String password) {
        // If database is not available, use demo registration
        if (connection == null) {
            JOptionPane.showMessageDialog(this, 
                "Database not available. Registration not persistent.\n" +
                "Use demo accounts: mega/mega123 or shobi/shobi123", 
                "Demo Mode", JOptionPane.INFORMATION_MESSAGE);
            return true; // Accept registration in demo mode
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'user')")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Registration error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }
    
    // Get user ID from username
    private int getUserId(String username) {
        if (connection == null) return -1;
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT id FROM users WHERE username = ?")) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user ID: " + e.getMessage());
        }
        return -1;
    }
    
    // Get user name from user ID
    public String getUserNameFromId(String userId) {
        if (connection == null) return "Customer " + userId;
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT username FROM users WHERE id = ?")) {
            pstmt.setString(1, userId.replace("U", "")); // Remove 'U' prefix if exists
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user name: " + e.getMessage());
        }
        return "Customer " + userId;
    }
    
    // Save booking to database - UPDATED TO REMOVE TURF_NAME
    public boolean saveBookingToDatabase(String turfId, String date, String startTime, 
                                       int hours, int playerCount, double amount) {
        if (connection == null) {
            System.out.println("Database not available - booking not saved");
            return false;
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO bookings (user_id, username, turf_id, booking_date, start_time, hours, player_count, total_amount, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'confirmed')")) {
            
            int userId = getUserId(currentUser);
            if (userId == -1) {
                JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, currentUser); // Store username directly
            pstmt.setString(3, turfId.replace("T", "")); // Remove 'T' prefix
            pstmt.setString(4, date);
            pstmt.setString(5, startTime);
            pstmt.setInt(6, hours);
            pstmt.setInt(7, playerCount);
            pstmt.setDouble(8, amount);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Booking saved to database: " + turfId + " for " + currentUser);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error saving booking: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error saving booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    // Get bookings for user with proper user info - FIXED TO SHOW BOOKINGS
    public Vector<Booking> getUserBookingsFromDatabase() {
        Vector<Booking> bookings = new Vector<>();
        
        if (connection == null) {
            System.out.println("Database not available - returning empty bookings");
            return bookings;
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT b.*, t.name as turf_name, u.username, u.email " +
                "FROM bookings b " +
                "JOIN turfs t ON b.turf_id = t.id " +
                "JOIN users u ON b.user_id = u.id " +
                "WHERE u.username = ? ORDER BY b.booking_date DESC, b.start_time DESC")) {
            
            pstmt.setString(1, currentUser);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = new Booking(
                    "B" + rs.getInt("id"),
                    rs.getString("user_id"),
                    rs.getString("turf_id"),
                    rs.getString("turf_name"),
                    rs.getString("booking_date"),
                    rs.getString("start_time"),
                    rs.getInt("hours"),
                    rs.getInt("player_count"),
                    rs.getDouble("total_amount"),
                    rs.getString("status"),
                    rs.getString("username"),
                    rs.getString("email")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }
        return bookings;
    }
    
    // ✅ FIXED: Corrected type mismatches in getAdminBookingsFromDatabase
    public Vector<Booking> getAdminBookingsFromDatabase(String turfId) {
        Vector<Booking> bookings = new Vector<>();
        
        if (connection == null) {
            System.out.println("Database not available - returning empty bookings");
            return bookings;
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT b.*, t.name as turf_name, u.username, u.email " +
                "FROM bookings b " +
                "JOIN turfs t ON b.turf_id = t.id " +
                "JOIN users u ON b.user_id = u.id " +
                "WHERE t.id = ? ORDER BY b.booking_date DESC, b.start_time DESC")) {
            
            // Remove 'T' prefix if exists and parse as integer
            String cleanTurfId = turfId.replaceAll("[^0-9]", "");
            pstmt.setInt(1, Integer.parseInt(cleanTurfId));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = new Booking(
                    "B" + rs.getInt("id"),
                    rs.getString("user_id"),
                    rs.getString("turf_id"),
                    rs.getString("turf_name"),
                    rs.getString("booking_date"),
                    rs.getString("start_time"),
                    rs.getInt("hours"),
                    rs.getInt("player_count"),
                    rs.getDouble("total_amount"),
                    rs.getString("status"),
                    rs.getString("username"),
                    rs.getString("email")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error loading admin bookings: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid turf ID format: " + turfId);
        }
        return bookings;
    }
    
   // ✅ FINAL FIX: Return only active bookings for all users (used in double booking check)
public Vector<Booking> getAllBookingsFromDatabase() {
    Vector<Booking> allBookings = new Vector<>();

    if (connection == null) {
        System.out.println("Database connection not available.");
        return allBookings;
    }

    try (PreparedStatement stmt = connection.prepareStatement(
            "SELECT b.*, t.name AS turf_name, u.username, u.email " +
            "FROM bookings b " +
            "JOIN turfs t ON b.turf_id = t.id " +
            "JOIN users u ON b.user_id = u.id " +
            "WHERE b.status IN ('confirmed', 'pending')")) {

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            allBookings.add(new Booking(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("turf_id"),
                rs.getString("turf_name"),
                rs.getString("booking_date"),
                rs.getString("start_time"),
                rs.getInt("hours"),
                rs.getInt("player_count"),
                rs.getDouble("total_amount"),
                rs.getString("status"),
                rs.getString("username"),
                rs.getString("email")
            ));
        }

    } catch (Exception e) {
        System.err.println("Error fetching bookings: " + e.getMessage());
        e.printStackTrace();
    }

    return allBookings;
}

    
    // Update booking status in database
    // ✅ FIXED: Correct column names, safe connection handling, and debug logging
    public boolean updateBookingStatusInDatabase(String bookingId, String newStatus) {
    Connection conn = null;
    PreparedStatement stmt = null;
    
    try {
        conn = connect();
        if (conn == null) return false;

        // ✅ FIX: Remove 'updated_at' column from the query
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, newStatus);
        
        // Extract numeric ID from booking ID (e.g., "B001" -> "001")
        String numericId = bookingId.replaceAll("\\D", "");
        stmt.setInt(2, Integer.parseInt(numericId));
        
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (Exception e) {
        System.out.println("Error updating booking status: " + e.getMessage());
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

    private Vector<Turf> getTurfsForAdmin() {
        Vector<Turf> turfs = new Vector<>();
        
        // If database is not available, return demo turfs
        if (connection == null) {
            turfs.add(new Turf(1, "Green Field Football", "Chennai", "Football", 1200.0, 20));
            turfs.add(new Turf(2, "Sports Arena Cricket", "Coimbatore", "Cricket", 1500.0, 30));
            turfs.add(new Turf(3, "Elite Tennis Court", "Madurai", "Tennis", 800.0, 4));
            return turfs;
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT t.* FROM turfs t " +
                "JOIN users u ON t.admin_id = u.id " +
                "WHERE u.username = ?")) {
            pstmt.setString(1, currentUser);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                turfs.add(new Turf(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("location"),
                    rs.getString("sport_type"),
                    rs.getDouble("price_per_hour"),
                    rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading turfs: " + e.getMessage());
        }
        return turfs;
    }
    
    private Vector<Turf> getAllTurfs() {
        Vector<Turf> turfs = new Vector<>();
        
        // If database is not available, return demo turfs
        if (connection == null) {
            turfs.add(new Turf(1, "Green Field Football", "Chennai", "Football", 1200.0, 20));
            turfs.add(new Turf(2, "Sports Arena Cricket", "Coimbatore", "Cricket", 1500.0, 30));
            turfs.add(new Turf(3, "Elite Tennis Court", "Madurai", "Tennis", 800.0, 4));
            turfs.add(new Turf(4, "Basketball Arena", "Chennai", "Basketball", 900.0, 10));
            turfs.add(new Turf(5, "Rugby Ground", "Coimbatore", "Rugby", 1400.0, 25));
            turfs.add(new Turf(6, "Badminton Court", "Madurai", "Badminton", 600.0, 6));
            return turfs;
        }
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM turfs");
            
            while (rs.next()) {
                turfs.add(new Turf(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("location"),
                    rs.getString("sport_type"),
                    rs.getDouble("price_per_hour"),
                    rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading turfs: " + e.getMessage());
        }
        return turfs;
    }
    
    private boolean updateTurf(Turf turf) {
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database not available. Changes not saved.", "Demo Mode", JOptionPane.WARNING_MESSAGE);
            return true; // Accept changes in demo mode
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE turfs SET name = ?, location = ?, sport_type = ?, price_per_hour = ?, capacity = ? WHERE id = ?")) {
            pstmt.setString(1, turf.getName());
            pstmt.setString(2, turf.getLocation());
            pstmt.setString(3, turf.getSportType());
            pstmt.setDouble(4, turf.getPricePerHour());
            pstmt.setInt(5, turf.getCapacity());
            pstmt.setInt(6, turf.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating turf: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean addTurf(Turf turf) {
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database not available. Turf not saved.", "Demo Mode", JOptionPane.WARNING_MESSAGE);
            return true; // Accept addition in demo mode
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO turfs (name, location, sport_type, price_per_hour, capacity, admin_id) VALUES (?, ?, ?, ?, ?, " +
                "(SELECT id FROM users WHERE username = ?))")) {
            pstmt.setString(1, turf.getName());
            pstmt.setString(2, turf.getLocation());
            pstmt.setString(3, turf.getSportType());
            pstmt.setDouble(4, turf.getPricePerHour());
            pstmt.setInt(5, turf.getCapacity());
            pstmt.setString(6, currentUser);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding turf: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean deleteTurf(int turfId) {
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database not available. Delete not performed.", "Demo Mode", JOptionPane.WARNING_MESSAGE);
            return true; // Accept deletion in demo mode
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM turfs WHERE id = ?")) {
            pstmt.setInt(1, turfId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting turf: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ... (UI initialization methods remain the same as your original code)
    private void initializeUI() {
        setTitle("BookMyTurf - Turf Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create different screens - SEPARATE LOGIN SCREENS
        mainPanel.add(createRoleSelectionPanel(), "ROLE_SELECTION");
        mainPanel.add(createAdminLoginPanel(), "ADMIN_LOGIN");
        mainPanel.add(createUserLoginPanel(), "USER_LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");

        add(mainPanel);
        showRoleSelection();
    }

    private JPanel createRoleSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Title with BookMyTurf branding
        JLabel titleLabel = new JLabel("BOOKMYTURF");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 100, 0));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Your Sports, Your Turf, Your Way");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));

        // Tagline
        JLabel taglineLabel = new JLabel("Book Your Perfect Playground");
        taglineLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        taglineLabel.setForeground(new Color(0, 120, 0));

        // Admin Button
        JButton adminButton = new JButton("ADMIN LOGIN");
        adminButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        adminButton.setBackground(new Color(0, 120, 0));
        adminButton.setForeground(Color.WHITE);
        adminButton.setPreferredSize(new Dimension(200, 50));
        adminButton.setFocusPainted(false);

        // User Button
        JButton userButton = new JButton("USER LOGIN");
        userButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userButton.setBackground(new Color(0, 120, 0));
        userButton.setForeground(Color.WHITE);
        userButton.setPreferredSize(new Dimension(200, 50));
        userButton.setFocusPainted(false);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 20, 0);
        panel.add(subtitleLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 40, 0);
        panel.add(taglineLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(adminButton, gbc);

        gbc.gridx = 1;
        panel.add(userButton, gbc);

        // Button actions - SEPARATE LOGIN SCREENS
        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "ADMIN_LOGIN");
            }
        });

        userButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "USER_LOGIN");
            }
        });

        return panel;
    }

    // ✅ FIXED: SEPARATE ADMIN LOGIN PANEL - PROPERLY INITIALIZED
    private JPanel createAdminLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 245, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Main container
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        // Title with BookMyTurf branding
        JLabel titleLabel = new JLabel("BOOKMYTURF");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 100, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel loginLabel = new JLabel("Admin Login");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginLabel.setForeground(new Color(100, 100, 100));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Username - FIXED: Using adminUsernameField
        JLabel userLabel = new JLabel("Admin Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        adminUsernameField = new JTextField(20);
        adminUsernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adminUsernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Password - FIXED: Using adminPasswordField
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        adminPasswordField = new JPasswordField(20);
        adminPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Buttons
        JButton loginButton = new JButton("ADMIN LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 120, 0));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton backButton = new JButton("BACK TO HOME");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backButton.setBackground(new Color(200, 200, 200));
        backButton.setFocusPainted(false);

        // Layout
        GridBagConstraints containerGbc = new GridBagConstraints();
        containerGbc.insets = new Insets(5, 5, 5, 5);
        containerGbc.fill = GridBagConstraints.HORIZONTAL;
        containerGbc.gridwidth = 2;

        // Title
        containerGbc.gridx = 0; containerGbc.gridy = 0;
        containerGbc.insets = new Insets(0, 0, 10, 0);
        mainContainer.add(titleLabel, containerGbc);

        containerGbc.gridy = 1;
        containerGbc.insets = new Insets(0, 0, 30, 0);
        mainContainer.add(loginLabel, containerGbc);

        containerGbc.insets = new Insets(5, 5, 5, 5);

        // Username
        containerGbc.gridy = 2;
        containerGbc.insets = new Insets(10, 0, 5, 0);
        mainContainer.add(userLabel, containerGbc);

        containerGbc.gridy = 3;
        containerGbc.insets = new Insets(0, 0, 15, 0);
        mainContainer.add(adminUsernameField, containerGbc);

        // Password
        containerGbc.gridy = 4;
        containerGbc.insets = new Insets(10, 0, 5, 0);
        mainContainer.add(passLabel, containerGbc);

        containerGbc.gridy = 5;
        containerGbc.insets = new Insets(0, 0, 20, 0);
        mainContainer.add(adminPasswordField, containerGbc);

        // Login Button
        containerGbc.gridy = 6;
        containerGbc.insets = new Insets(0, 0, 15, 0);
        mainContainer.add(loginButton, containerGbc);

        // Back Button
        containerGbc.gridy = 7;
        mainContainer.add(backButton, containerGbc);

        // Add main container to panel
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(mainContainer, gbc);

        // Event listeners - FIXED: Call performAdminLogin
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performAdminLogin();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRoleSelection();
            }
        });

        return loginPanel;
    }

    // ✅ FIXED: SEPARATE USER LOGIN PANEL - PROPERLY INITIALIZED
    private JPanel createUserLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 245, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Main container
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        // Title with BookMyTurf branding
        JLabel titleLabel = new JLabel("BOOKMYTURF");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 100, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel loginLabel = new JLabel("User Login");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginLabel.setForeground(new Color(100, 100, 100));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Username - FIXED: Using userUsernameField
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        userUsernameField = new JTextField(20);
        userUsernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userUsernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Password - FIXED: Using userPasswordField
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        userPasswordField = new JPasswordField(20);
        userPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Buttons
        JButton loginButton = new JButton("USER LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 120, 0));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton backButton = new JButton("BACK TO HOME");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backButton.setBackground(new Color(200, 200, 200));
        backButton.setFocusPainted(false);

        JButton registerButton = new JButton("New to BookMyTurf? Create Account");
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(new Color(0, 100, 200));

        // Layout
        GridBagConstraints containerGbc = new GridBagConstraints();
        containerGbc.insets = new Insets(5, 5, 5, 5);
        containerGbc.fill = GridBagConstraints.HORIZONTAL;
        containerGbc.gridwidth = 2;

        // Title
        containerGbc.gridx = 0; containerGbc.gridy = 0;
        containerGbc.insets = new Insets(0, 0, 10, 0);
        mainContainer.add(titleLabel, containerGbc);

        containerGbc.gridy = 1;
        containerGbc.insets = new Insets(0, 0, 30, 0);
        mainContainer.add(loginLabel, containerGbc);

        containerGbc.insets = new Insets(5, 5, 5, 5);

        // Username
        containerGbc.gridy = 2;
        containerGbc.insets = new Insets(10, 0, 5, 0);
        mainContainer.add(userLabel, containerGbc);

        containerGbc.gridy = 3;
        containerGbc.insets = new Insets(0, 0, 15, 0);
        mainContainer.add(userUsernameField, containerGbc);

        // Password
        containerGbc.gridy = 4;
        containerGbc.insets = new Insets(10, 0, 5, 0);
        mainContainer.add(passLabel, containerGbc);

        containerGbc.gridy = 5;
        containerGbc.insets = new Insets(0, 0, 20, 0);
        mainContainer.add(userPasswordField, containerGbc);

        // Login Button
        containerGbc.gridy = 6;
        containerGbc.insets = new Insets(0, 0, 10, 0);
        mainContainer.add(loginButton, containerGbc);

        // Register Button
        containerGbc.gridy = 7;
        containerGbc.insets = new Insets(0, 0, 10, 0);
        mainContainer.add(registerButton, containerGbc);

        // Back Button
        containerGbc.gridy = 8;
        mainContainer.add(backButton, containerGbc);

        // Add main container to panel
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(mainContainer, gbc);

        // Event listeners - FIXED: Call performUserLogin
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performUserLogin();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRoleSelection();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "REGISTER");
            }
        });

        return loginPanel;
    }

    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBackground(new Color(240, 245, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Main container
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        // Title with BookMyTurf branding
        JLabel titleLabel = new JLabel("BOOKMYTURF");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 100, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel registerLabel = new JLabel("Create Your BookMyTurf Account");
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        registerLabel.setForeground(new Color(100, 100, 100));
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        regUsernameField = new JTextField(20);
        regUsernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        regUsernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        regEmailField = new JTextField(20);
        regEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        regEmailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        regPasswordField = new JPasswordField(20);
        regPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        regPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Confirm Password
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        confirmPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        regConfirmPasswordField = new JPasswordField(20);
        regConfirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        regConfirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Buttons
        JButton registerButton = new JButton("CREATE BOOKMYTURF ACCOUNT");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBackground(new Color(0, 120, 0));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton backButton = new JButton("BACK TO USER LOGIN");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backButton.setBackground(new Color(200, 200, 200));
        backButton.setFocusPainted(false);

        // Layout
        GridBagConstraints containerGbc = new GridBagConstraints();
        containerGbc.insets = new Insets(5, 5, 5, 5);
        containerGbc.fill = GridBagConstraints.HORIZONTAL;
        containerGbc.gridwidth = 2;

        // Title
        containerGbc.gridx = 0; containerGbc.gridy = 0;
        containerGbc.insets = new Insets(0, 0, 10, 0);
        mainContainer.add(titleLabel, containerGbc);

        containerGbc.gridy = 1;
        containerGbc.insets = new Insets(0, 0, 30, 0);
        mainContainer.add(registerLabel, containerGbc);

        containerGbc.insets = new Insets(5, 5, 5, 5);

        // Username
        containerGbc.gridy = 2;
        containerGbc.insets = new Insets(10, 0, 5, 0);
        mainContainer.add(userLabel, containerGbc);

        containerGbc.gridy = 3;
        containerGbc.insets = new Insets(0, 0, 15, 0);
        mainContainer.add(regUsernameField, containerGbc);

        // Email
        containerGbc.gridy = 4;
        containerGbc.insets = new Insets(10, 0, 5, 0);
        mainContainer.add(emailLabel, containerGbc);

        containerGbc.gridy = 5;
        containerGbc.insets = new Insets(0, 0, 15, 0);
        mainContainer.add(regEmailField, containerGbc);

        // Password
        containerGbc.gridy = 6;
        containerGbc.insets = new Insets(10, 0, 5, 0);
        mainContainer.add(passLabel, containerGbc);

        containerGbc.gridy = 7;
        containerGbc.insets = new Insets(0, 0, 15, 0);
        mainContainer.add(regPasswordField, containerGbc);

        // Confirm Password
        containerGbc.gridy = 8;
        containerGbc.insets = new Insets(10, 0, 5, 0);
        mainContainer.add(confirmPassLabel, containerGbc);

        containerGbc.gridy = 9;
        containerGbc.insets = new Insets(0, 0, 20, 0);
        mainContainer.add(regConfirmPasswordField, containerGbc);

        // Register Button
        containerGbc.gridy = 10;
        containerGbc.insets = new Insets(0, 0, 10, 0);
        mainContainer.add(registerButton, containerGbc);

        // Back Button
        containerGbc.gridy = 11;
        mainContainer.add(backButton, containerGbc);

        // Add main container to panel
        gbc.gridx = 0; gbc.gridy = 0;
        registerPanel.add(mainContainer, gbc);

        // Event listeners
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "USER_LOGIN");
                clearRegistrationFields();
            }
        });

        return registerPanel;
    }

    // ✅ FIXED: ADMIN LOGIN METHOD
    private void performAdminLogin() {
        String username = adminUsernameField.getText().trim();
        String password = new String(adminPasswordField.getPassword()).trim();

        System.out.println("Admin login attempt - Username: " + username + ", Password: " + password);

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authenticateUser(username, password)) {
            if (currentUserRole != null && currentUserRole.equals("admin")) {
                Admin authenticatedAdmin = getAdminInfo(username);
                if (authenticatedAdmin != null) {
                    AdminDashboard adminDashboard = new AdminDashboard(authenticatedAdmin, this);
                    adminDashboard.setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Admin information not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "You are not authorized as an admin", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid admin username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }

        clearLoginFields();
    }

    // ✅ FIXED: USER LOGIN METHOD  
    private void performUserLogin() {
        String username = userUsernameField.getText().trim();
        String password = new String(userPasswordField.getPassword()).trim();

        System.out.println("User login attempt - Username: " + username + ", Password: " + password);

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authenticateUser(username, password)) {
            if (currentUserRole != null && currentUserRole.equals("user")) {
                User authenticatedUser = getUserInfo(username);
                UserDashboard userDashboard = new UserDashboard(authenticatedUser, this);
                userDashboard.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                   "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }

        clearLoginFields();
    }

    private void performRegistration() {
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = new String(regPasswordField.getPassword()).trim();
        String confirmPassword = new String(regConfirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (registerUser(username, email, password)) {
            JOptionPane.showMessageDialog(this, "Welcome to BookMyTurf! Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainPanel, "USER_LOGIN");
            clearRegistrationFields();
        }
    }

    // Helper method to get admin information
    private Admin getAdminInfo(String username) {
        // If database is available, get admin info from database
        if (connection != null) {
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT u.id, u.username, t.id as turf_id, t.name as turf_name " +
                    "FROM users u " +
                    "JOIN turfs t ON u.id = t.admin_id " +
                    "WHERE u.username = ? AND u.role = 'admin'")) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return new Admin(
                        "A" + rs.getInt("id"),
                        rs.getString("username"),
                        "T" + rs.getInt("turf_id"),
                        rs.getString("turf_name")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting admin info: " + e.getMessage());
            }
        }
        
        // Fallback: create demo admin based on username
        if (username.equals("rajesh")) {
            return new Admin("A001", "Rajesh", "T001", "Green Field Football");
        } else if (username.equals("suresh")) {
            return new Admin("A002", "Suresh", "T002", "Sports Arena Cricket");
        } else if (username.equals("priya")) {
            return new Admin("A003", "Priya", "T003", "Elite Tennis Court");
        } else if (username.equals("arun")) {
            return new Admin("A004", "Arun", "T004", "Basketball Arena");
        } else if (username.equals("hari")) {
            return new Admin("A005", "Hari", "T005", "Rugby Ground");
        } else if (username.equals("surya")) {
            return new Admin("A006", "Surya", "T006", "Badminton Court");
        }
        
        return null;
    }

    // Helper method to get user information
    private User getUserInfo(String username) {
        // If database is available, get user info from database
        if (connection != null) {
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT id, username, email FROM users WHERE username = ?")) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return new User(
                        "U" + rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting user info: " + e.getMessage());
            }
        }
        
        // Fallback: create demo user
        return new User(
            "U" + String.format("%03d", (int)(Math.random() * 1000)),
            username,
            username + "@bookmyturf.com"
        );
    }

    // ✅ FIXED: Clear login fields method
    private void clearLoginFields() {
        if (adminUsernameField != null) adminUsernameField.setText("");
        if (adminPasswordField != null) adminPasswordField.setText("");
        if (userUsernameField != null) userUsernameField.setText("");
        if (userPasswordField != null) userPasswordField.setText("");
    }

    private void clearRegistrationFields() {
        regUsernameField.setText("");
        regEmailField.setText("");
        regPasswordField.setText("");
        regConfirmPasswordField.setText("");
    }

    private void showRoleSelection() {
        cardLayout.show(mainPanel, "ROLE_SELECTION");
        clearLoginFields();
        clearRegistrationFields();
        currentUser = null;
        currentUserRole = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Turf class
    class Turf {
        private int id;
        private String name;
        private String location;
        private String sportType;
        private double pricePerHour;
        private int capacity;

        public Turf(int id, String name, String location, String sportType, double pricePerHour, int capacity) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.sportType = sportType;
            this.pricePerHour = pricePerHour;
            this.capacity = capacity;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getLocation() { return location; }
        public String getSportType() { return sportType; }
        public double getPricePerHour() { return pricePerHour; }
        public int getCapacity() { return capacity; }

        public void setName(String name) { this.name = name; }
        public void setLocation(String location) { this.location = location; }
        public void setSportType(String sportType) { this.sportType = sportType; }
        public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
    }
    
    // Updated Booking class with username and email
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
        
        public Booking(String id, String userId, String turfId, String turfName, String date, 
                      String startTime, int hours, int playerCount, double amount, String status) {
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
        
        public Booking(String id, String userId, String turfId, String turfName, String date, 
                      String startTime, int hours, int playerCount, double amount, String status,
                      String username, String email) {
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
}