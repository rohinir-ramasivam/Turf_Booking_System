// Admin.java - Complete Updated Version
import java.util.*;

public class Admin {
    private String adminId;
    private String name;
    private String turfId;
    private String turfName;

    public Admin(String adminId, String name, String turfId, String turfName) {
        this.adminId = adminId;
        this.name = name;
        this.turfId = turfId;
        this.turfName = turfName;
    }

    // Getters
    public String getAdminId() { return adminId; }
    public String getName() { return name; }
    public String getTurfId() { return turfId; }
    public String getTurfName() { return turfName; }
    
    // Setters
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public void setName(String name) { this.name = name; }
    public void setTurfId(String turfId) { this.turfId = turfId; }
    public void setTurfName(String turfName) { this.turfName = turfName; }
    
    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", name='" + name + '\'' +
                ", turfId='" + turfId + '\'' +
                ", turfName='" + turfName + '\'' +
                '}';
    }
    
    // Method to get numeric turf ID for database operations
    public String getNumericTurfId() {
        if (turfId != null && turfId.startsWith("T")) {
            return turfId.substring(1); // Remove 'T' prefix
        }
        return turfId;
    }
    
    // Method to get numeric admin ID for database operations
    public String getNumericAdminId() {
        if (adminId != null && adminId.startsWith("A")) {
            return adminId.substring(1); // Remove 'A' prefix
        }
        return adminId;
    }
    
    // Static method to get admin by username (for database integration)
    public static Admin getAdminByUsername(String username) {
        // This would typically query the database
        // For now, return demo admins based on username
        switch (username) {
            case "admin1":
                return new Admin("A001", "Admin One", "T001", "Green Field Football");
            case "admin2":
                return new Admin("A002", "Admin Two", "T002", "Sports Arena Cricket");
            case "admin3":
                return new Admin("A003", "Admin Three", "T003", "Elite Tennis Court");
            default:
                return null;
        }
    }
    
    // Static method to get all admins (for demonstration)
    public static java.util.List<Admin> getAllAdmins() {
        java.util.List<Admin> admins = new java.util.ArrayList<>();
        admins.add(new Admin("A001", "Admin One", "T001", "Green Field Football"));
        admins.add(new Admin("A002", "Admin Two", "T002", "Sports Arena Cricket"));
        admins.add(new Admin("A003", "Admin Three", "T003", "Elite Tennis Court"));
        return admins;
    }
    
    // Method to check if admin manages a specific turf
    public boolean managesTurf(String turfId) {
        return this.turfId.equals(turfId);
    }
    
    // Method to get admin statistics
    public java.util.Map<String, Object> getStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("adminId", adminId);
        stats.put("name", name);
        stats.put("turfName", turfName);
        stats.put("totalBookings", 0); // Would be calculated from database
        stats.put("revenue", 0.0); // Would be calculated from database
        return stats;
    }
    
    // Method to validate admin data
    public boolean isValid() {
        return adminId != null && !adminId.isEmpty() && 
               name != null && !name.isEmpty() && 
               turfId != null && !turfId.isEmpty() && 
               turfName != null && !turfName.isEmpty();
    }
    
    // Method to get display information
    public String getDisplayInfo() {
        return name + " (" + adminId + ") - Manages: " + turfName + " (" + turfId + ")";
    }
    
    // Method to get admin by turf ID
    public static Admin getAdminByTurfId(String turfId) {
        for (Admin admin : getAllAdmins()) {
            if (admin.getTurfId().equals(turfId)) {
                return admin;
            }
        }
        return null;
    }
    
    // Method to check if user is admin
    public static boolean isAdmin(String username) {
        return username.equals("admin1") || username.equals("admin2") || username.equals("admin3");
    }
    
    // Method to get admin turf information
    public String getTurfInfo() {
        return turfName + " (" + turfId + ")";
    }
    
    // Method to get formatted admin info for display
    public String getFormattedInfo() {
        return String.format("Admin ID: %s\nName: %s\nTurf: %s\nTurf ID: %s", 
                           adminId, name, turfName, turfId);
    }
}