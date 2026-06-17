// AdminDashboard.java 
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

public class AdminDashboard extends JFrame {
    private Admin currentAdmin;
    private Login loginSystem;
    
    // Main panels
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Sidebar components
    private JPanel sidebarPanel;
    private JButton homeButton, bookingsButton, logoutButton;
    
    // Content panels
    private JPanel homepagePanel, bookingsPanel;
    
    // Data structures
    private List<Booking> allBookings = new ArrayList<>();
    
    // Data models
    private DefaultTableModel bookingsTableModel;
    
    // Current active menu button
    private JButton activeMenuButton = null;
    
    // Stat panel references
    private JPanel totalBookingsStatPanel;
    private JPanel futureReservationsStatPanel;
    
    // Colors matching UserDashboard
    private final Color PRIMARY_COLOR = new Color(34, 139, 34); // Green
    private final Color SECONDARY_COLOR = new Color(240, 255, 240); // Light green
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color CHART_COLOR = new Color(34, 139, 34);
    private final Color CHART_GRID_COLOR = new Color(220, 220, 220);

    public AdminDashboard(Admin admin, Login loginSystem) {
        this.currentAdmin = admin;
        this.loginSystem = loginSystem;
        
        // Initialize table models FIRST
        initializeTableModels();
        
        initializeData();
        setupUI();
        showHomepage();
    }
    
    private void initializeTableModels() {
        // Initialize bookings table model
        String[] bookingsColumns = {"Booking ID", "Customer Name", "Email", "Date", "Start Time", "Hours", "Players", "Amount", "Status"};
        bookingsTableModel = new DefaultTableModel(bookingsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
    
    private void initializeData() {
        refreshBookingsFromDatabase();
    }

    private void setupUI() {
        setTitle("BookMyTurf - Admin Dashboard");
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
        createBookingsPanel();
        
        // Add panels to main panel
        mainPanel.add(homepagePanel, "HOME");
        mainPanel.add(bookingsPanel, "BOOKINGS");
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Add header with admin info
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel welcomeLabel = new JLabel("Welcome to BookMyTurf Admin, " + currentAdmin.getName() + "!");
        welcomeLabel.setForeground(new Color(33, 37, 41));
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setBorder(new EmptyBorder(15, 20, 15, 10));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JLabel turfLabel = new JLabel("Managing: " + currentAdmin.getTurfName());
        turfLabel.setForeground(new Color(108, 117, 125));
        turfLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        turfLabel.setBorder(new EmptyBorder(15, 10, 15, 20));
        headerPanel.add(turfLabel, BorderLayout.EAST);
        
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
        homeButton = createNavButton(" Dashboard", "HOME", PRIMARY_COLOR);
        bookingsButton = createNavButton(" Manage Bookings", "BOOKINGS", new Color(46, 139, 87));
        logoutButton = createNavButton(" Logout", "LOGOUT", new Color(178, 34, 34));
        
        // Add buttons to navigation panel
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(homeButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(bookingsButton);
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
        
        // Add hover effects
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
                        AdminDashboard.this, 
                        "Are you sure you want to logout?", 
                        "Confirm Logout", 
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        dispose();
                        new Login().setVisible(true);
                    }
                } else {
                    // Highlight the active menu button
                    setActiveMenuButton(button);
                    
                    cardLayout.show(mainPanel, action);
                    if ("BOOKINGS".equals(action)) {
                        refreshBookingsTable();
                    } else if ("HOME".equals(action)) {
                        refreshStats();
                    } 
                }
            }
        });
        
        return button;
    }
    
    private void setActiveMenuButton(JButton button) {
        // Reset previous active button
        if (activeMenuButton != null) {
            activeMenuButton.setBackground(BACKGROUND_COLOR);
            activeMenuButton.setForeground(Color.BLACK);
        }
        
        // Set new active button
        activeMenuButton = button;
        activeMenuButton.setBackground(PRIMARY_COLOR);
        activeMenuButton.setForeground(Color.WHITE);
    }
    
    private void createHomepagePanel() {
        homepagePanel = new JPanel(new BorderLayout());
        homepagePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        homepagePanel.setBackground(BACKGROUND_COLOR);
        
        // Welcome section
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)), "Admin Dashboard Overview"));
        welcomePanel.setBackground(BACKGROUND_COLOR);
        welcomePanel.setForeground(Color.BLACK);
        
        JLabel welcomeTitle = new JLabel("BookMyTurf - Admin Dashboard");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeTitle.setForeground(PRIMARY_COLOR);
        welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel welcomeText = new JLabel("<html><div style='text-align: center;'>Welcome to your admin dashboard. "
                + "Here you can manage bookings, and oversee operations for " + currentAdmin.getTurfName() + ".</div></html>");
        welcomeText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeText.setForeground(new Color(108, 117, 125));
        welcomeText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(welcomeTitle);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(welcomeText);
        welcomePanel.add(Box.createVerticalStrut(30));
        
        // Quick stats with refresh button
        JPanel statsHeaderPanel = new JPanel(new BorderLayout());
        statsHeaderPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel statsTitle = new JLabel("Quick Stats");
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statsTitle.setForeground(new Color(33, 37, 41));
        statsHeaderPanel.add(statsTitle, BorderLayout.WEST);
        
        // Add refresh button
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
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setBackground(BACKGROUND_COLOR);
        
        // Create stats panels and store references - Only 2 panels now
        totalBookingsStatPanel = createStatPanel("Total Bookings", "0", new Color(13, 110, 253));
        futureReservationsStatPanel = createStatPanel("Future Reservations", "0", new Color(25, 135, 84));
        
        statsPanel.add(totalBookingsStatPanel);
        statsPanel.add(futureReservationsStatPanel);
        
        welcomePanel.add(statsPanel);
        welcomePanel.add(Box.createVerticalStrut(30));
        
        // Recent bookings
        JPanel recentBookingsPanel = new JPanel(new BorderLayout());
        recentBookingsPanel.setBorder(BorderFactory.createTitledBorder("Recent Bookings"));
        recentBookingsPanel.setBackground(BACKGROUND_COLOR);
        
        JTable recentBookingsTable = new JTable(bookingsTableModel);
        recentBookingsTable.setBackground(BACKGROUND_COLOR);
        recentBookingsTable.setForeground(Color.BLACK);
        recentBookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(recentBookingsTable);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        recentBookingsPanel.add(scrollPane, BorderLayout.CENTER);
        
        welcomePanel.add(recentBookingsPanel);
        
        homepagePanel.add(welcomePanel, BorderLayout.CENTER);
        
        // Initial stats update (without popup)
        refreshStats();
    }
     
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(color);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        
        return card;
    }
    
    private void refreshStatsWithPopup() {
        refreshStats();
        
        // Calculate statistics for popup message
        int totalBookings = 0;
        int futureReservations = 0;

        String currentTurfId = currentAdmin.getTurfId().replaceAll("[^0-9]", "");
        
        for (Booking booking : allBookings) {
            if (booking.getTurfId().equals(currentTurfId)) {
                totalBookings++;
                if ("confirmed".equalsIgnoreCase(booking.getStatus()) || "pending".equalsIgnoreCase(booking.getStatus())) {
                    futureReservations++;
                }
            }
        }
        
        JOptionPane.showMessageDialog(homepagePanel, 
            "Stats refreshed successfully!\n" +
            "Total Bookings: " + totalBookings + "\n" +
            "Future Reservations: " + futureReservations, 
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshStats() {
        refreshBookingsFromDatabase();
        
        // Calculate statistics
        int totalBookings = 0;
        int futureReservations = 0;

        String currentTurfId = currentAdmin.getTurfId().replaceAll("[^0-9]", "");
        
        for (Booking booking : allBookings) {
            if (booking.getTurfId().equals(currentTurfId)) {
                totalBookings++;
                if ("confirmed".equalsIgnoreCase(booking.getStatus()) || "pending".equalsIgnoreCase(booking.getStatus())) {
                    futureReservations++;
                }
            }
        }
        
        // Update the stat panels
        updateStatPanelValue(totalBookingsStatPanel, String.valueOf(totalBookings));
        updateStatPanelValue(futureReservationsStatPanel, String.valueOf(futureReservations));
        
        // Also update the recent bookings table
        refreshRecentBookingsTable();
    }
    
    private void refreshRecentBookingsTable() {
        if (bookingsTableModel == null) return;
        
        bookingsTableModel.setRowCount(0);
        String currentTurfId = currentAdmin.getTurfId().replaceAll("[^0-9]", "");

        // Show only recent confirmed and pending bookings (limit to 10)
        int count = 0;
        for (Booking booking : allBookings) {
            if (booking.getTurfId().equals(currentTurfId) && 
                ("confirmed".equalsIgnoreCase(booking.getStatus()) || "pending".equalsIgnoreCase(booking.getStatus()))) {
                
                bookingsTableModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getCustomerName(),
                    booking.getEmail(),
                    booking.getDate(),
                    booking.getStartTime(),
                    booking.getHours(),
                    booking.getPlayerCount(),
                    "₹" + String.format("%.0f", booking.getAmount()),
                    booking.getStatus()
                });
                
                count++;
                if (count >= 10) break; // Limit to 10 recent bookings
            }
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
    
    private void createBookingsPanel() {
        bookingsPanel = new JPanel(new BorderLayout());
        bookingsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        bookingsPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel title = new JLabel("Manage Bookings - " + currentAdmin.getTurfName());
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        bookingsPanel.add(title, BorderLayout.NORTH);
        
        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setBackground(BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Filter & Actions"));
        
        JLabel filterLabel = new JLabel("Show Bookings:");
        filterLabel.setForeground(new Color(33, 37, 41));
        controlsPanel.add(filterLabel);
        
        JComboBox<String> bookingFilter = new JComboBox<>(new String[]{"Current", "Past", "All"});
        bookingFilter.setBackground(BACKGROUND_COLOR);
        bookingFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) bookingFilter.getSelectedItem();
                refreshBookingsTableWithFilter(selected);
            }
        });
        controlsPanel.add(bookingFilter);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh Bookings");
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshBookingsFromDatabase();
                refreshBookingsTableWithFilter((String) bookingFilter.getSelectedItem());
            }
        });
        controlsPanel.add(refreshButton);
        
        bookingsPanel.add(controlsPanel, BorderLayout.NORTH);
        
        // Bookings table with enhanced rendering
        JTable bookingsTable = new JTable(bookingsTableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.setBackground(BACKGROUND_COLOR);
        bookingsTable.setForeground(Color.BLACK);
        bookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // ENHANCED: Color coding for booking status with completed booking protection
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
                        case "pending":
                            c.setBackground(new Color(255, 255, 224)); // Light yellow
                            c.setForeground(Color.BLACK);
                            break;
                        case "completed":
                            c.setBackground(new Color(173, 216, 230)); // Light blue
                            c.setForeground(Color.BLACK);
                            ((JLabel) c).setText(value.toString()); // FIXED: Added .toString()
                            break;
                        case "cancelled":
                            c.setBackground(new Color(255, 182, 193)); // Light red
                            c.setForeground(Color.BLACK);
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setBackground(Color.WHITE);
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
        bookingsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBackground(BACKGROUND_COLOR);
        
        JButton updateStatusButton = new JButton("Update Status");
        JButton viewDetailsButton = new JButton("View Details");
        JButton cancelBookingButton = new JButton("Cancel Booking");
        
        updateStatusButton.setBackground(new Color(255, 165, 0));
        viewDetailsButton.setBackground(new Color(30, 144, 255));
        cancelBookingButton.setBackground(new Color(220, 53, 69));
        
        updateStatusButton.setForeground(Color.WHITE);
        viewDetailsButton.setForeground(Color.WHITE);
        cancelBookingButton.setForeground(Color.WHITE);
        
        updateStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBookingStatus(bookingsTable);
            }
        });
        
        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewBookingDetails(bookingsTable);
            }
        });
        
        cancelBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBooking(bookingsTable);
            }
        });
        
        actionPanel.add(updateStatusButton);
        actionPanel.add(viewDetailsButton);
        actionPanel.add(cancelBookingButton);
        
        // ADD INFORMATION LABEL ABOUT COMPLETED BOOKINGS
        JLabel infoLabel = new JLabel("Note: Completed bookings cannot be modified or cancelled");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(108, 117, 125));
        actionPanel.add(infoLabel);
        
        bookingsPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // Initial load
        refreshBookingsTableWithFilter("Current");
    }
    
    private void refreshBookingsTable() {
        refreshBookingsTableWithFilter("Current");
    }
    
    private void refreshBookingsTableWithFilter(String filterType) {
        if (bookingsTableModel == null) return;
        
        bookingsTableModel.setRowCount(0);
        String currentTurfId = currentAdmin.getTurfId().replaceAll("[^0-9]", "");

        for (Booking booking : allBookings) {
            if (booking.getTurfId().equals(currentTurfId)) {
                boolean showBooking = false;
                String status = booking.getStatus().toLowerCase();

                switch (filterType) {
                    case "Current" -> showBooking = status.equals("confirmed") || status.equals("pending");
                    case "Past" -> showBooking = status.equals("completed") || status.equals("cancelled");
                    case "All" -> showBooking = true;
                }

                if (showBooking) {
                    bookingsTableModel.addRow(new Object[]{
                        booking.getBookingId(),
                        booking.getCustomerName(),
                        booking.getEmail(),
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
    }
    
    private void refreshBookingsFromDatabase() {
        allBookings.clear();

        // Normalize turf ID before querying
        String turfId = currentAdmin.getTurfId().replaceAll("[^0-9]", "");

        Vector<Login.Booking> dbBookings = loginSystem.getAdminBookingsFromDatabase(turfId);

        if (dbBookings != null && !dbBookings.isEmpty()) {
            for (Login.Booking dbBooking : dbBookings) {
                allBookings.add(new Booking(
                    dbBooking.getId(),
                    dbBooking.getTurfId(),
                    dbBooking.getUsername(),
                    dbBooking.getDate(),
                    dbBooking.getStartTime(),
                    dbBooking.getHours(),
                    dbBooking.getPlayerCount(),
                    dbBooking.getAmount(),
                    dbBooking.getStatus(),
                    dbBooking.getEmail()
                ));
            }
        } else {
            // If no database bookings, use sample data
            addSampleBookings();
        }
    }
    
    // Sample data method
    private void addSampleBookings() {
        String currentTurfId = currentAdmin.getTurfId().replaceAll("[^0-9]", "");
        
        // Add some sample bookings for demonstration
        allBookings.add(new Booking(
            "B001",
            currentTurfId,
            "John Doe",
            new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
            "14:00",
            2,
            8,
            2400.0,
            "confirmed",
            "john.doe@example.com"
        ));
        
        allBookings.add(new Booking(
            "B002",
            currentTurfId,
            "Jane Smith",
            new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
            "16:00",
            1,
            6,
            1200.0,
            "pending",
            "jane.smith@example.com"
        ));
        
        allBookings.add(new Booking(
            "B003",
            currentTurfId,
            "Mike Johnson",
            new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() - 86400000)), // Yesterday
            "18:00",
            3,
            10,
            3600.0,
            "completed",
            "mike.johnson@example.com"
        ));
    }
    
    private void updateBookingStatus(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to update.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String bookingId = (String) bookingsTableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) bookingsTableModel.getValueAt(selectedRow, 8);
        
        // PREVENT STATUS CHANGES FOR COMPLETED BOOKINGS
        if ("completed".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, 
                "Cannot modify status of completed bookings.\n\n" +
                "Booking ID: " + bookingId + "\n" +
                "Current Status: " + currentStatus + "\n\n" +
                "Completed bookings represent past events that have already occurred " +
                "and their status cannot be changed.",
                "Cannot Modify Completed Booking",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Find the booking
        Booking selectedBooking = null;
        for (Booking booking : allBookings) {
            if (booking.getBookingId().equals(bookingId)) {
                selectedBooking = booking;
                break;
            }
        }
        
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this, 
                "Booking not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create status update dialog
        String[] statusOptions = {"confirmed", "pending", "completed", "cancelled"};
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Update status for booking " + bookingId + ":\n" +
            "Current Status: " + currentStatus,
            "Update Booking Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus.toLowerCase()
        );
        
        if (newStatus != null && !newStatus.equals(currentStatus.toLowerCase())) {
            // Additional validation for status changes
            if ("completed".equalsIgnoreCase(newStatus)) {
                int confirmComplete = JOptionPane.showConfirmDialog(
                    this,
                    "Mark booking as completed?\n\n" +
                    "This indicates the booking time has passed and the event has occurred.\n" +
                    "Once marked as completed, this status cannot be changed.",
                    "Confirm Completion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (confirmComplete != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Update status in database
            boolean updated = loginSystem.updateBookingStatusInDatabase(bookingId, newStatus);
            
            if (updated) {
                selectedBooking.setStatus(newStatus);
                refreshBookingsTable();
                refreshStats();
                
                String message = "Booking status updated successfully!\n\n" +
                               "Booking ID: " + bookingId + "\n" +
                               "New Status: " + newStatus;
                
                if ("completed".equalsIgnoreCase(newStatus)) {
                    message += "\n\nNote: This booking is now marked as completed and cannot be modified further.";
                }
                
                JOptionPane.showMessageDialog(this, 
                    message,
                    "Status Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update booking status in database. Please try again.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewBookingDetails(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to view details.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String bookingId = (String) bookingsTableModel.getValueAt(selectedRow, 0);
        String customerName = (String) bookingsTableModel.getValueAt(selectedRow, 1);
        String email = (String) bookingsTableModel.getValueAt(selectedRow, 2);
        String date = (String) bookingsTableModel.getValueAt(selectedRow, 3);
        String startTime = (String) bookingsTableModel.getValueAt(selectedRow, 4);
        String hours = bookingsTableModel.getValueAt(selectedRow, 5).toString();
        String players = bookingsTableModel.getValueAt(selectedRow, 6).toString();
        String amount = (String) bookingsTableModel.getValueAt(selectedRow, 7);
        String status = (String) bookingsTableModel.getValueAt(selectedRow, 8);
        
        StringBuilder details = new StringBuilder();
        details.append("Booking Details\n\n");
        details.append("Booking ID: ").append(bookingId).append("\n");
        details.append("Customer: ").append(customerName).append("\n");
        details.append("Email: ").append(email).append("\n");
        details.append("Turf: ").append(currentAdmin.getTurfName()).append("\n");
        details.append("Date: ").append(date).append("\n");
        details.append("Start Time: ").append(startTime).append("\n");
        details.append("Duration: ").append(hours).append(" hour(s)\n");
        details.append("Players: ").append(players).append("\n");
        details.append("Amount: ").append(amount).append("\n");
        details.append("Status: ").append(status);
        
        // ADD WARNING FOR COMPLETED BOOKINGS
        if ("completed".equalsIgnoreCase(status)) {
            details.append("\n\nNote: This booking is completed and cannot be modified");
        }
        
        JOptionPane.showMessageDialog(this, 
            details.toString(),
            "Booking Details - " + bookingId,
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelBooking(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to cancel.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String bookingId = (String) bookingsTableModel.getValueAt(selectedRow, 0);
        String customerName = (String) bookingsTableModel.getValueAt(selectedRow, 1);
        String status = (String) bookingsTableModel.getValueAt(selectedRow, 8);
        
        // PREVENT CANCELLATION OF COMPLETED BOOKINGS
        if ("completed".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, 
                "Cannot cancel a completed booking.\n\n" +
                "Booking ID: " + bookingId + "\n" +
                "Customer: " + customerName + "\n" +
                "Status: " + status + "\n\n" +
                "Completed bookings represent past events that have already occurred " +
                "and cannot be cancelled.",
                "Cannot Cancel Completed Booking",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // PREVENT CANCELLATION OF CANCELLED BOOKINGS
        if ("cancelled".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, 
                "This booking is already cancelled.\n\n" +
                "Booking ID: " + bookingId + "\n" +
                "Customer: " + customerName + "\n" +
                "Status: " + status,
                "Booking Already Cancelled",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to cancel booking " + bookingId + " for " + customerName + "?\n\n" +
            "This action cannot be undone and will free up the time slot for other customers.",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Update booking status in database
            boolean updated = loginSystem.updateBookingStatusInDatabase(bookingId, "cancelled");
            
            if (updated) {
                // Find and update the local booking
                for (Booking booking : allBookings) {
                    if (booking.getBookingId().equals(bookingId)) {
                        booking.setStatus("cancelled");
                        break;
                    }
                }
                
                refreshBookingsTable();
                refreshStats();
                
                JOptionPane.showMessageDialog(this, 
                    "Booking successfully cancelled!\n\n" +
                    "Booking ID: " + bookingId + "\n" +
                    "Customer: " + customerName + "\n" +
                    "Status: Updated to 'cancelled'\n\n" +
                    "The time slot is now available for other bookings.",
                    "Booking Cancelled",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to cancel booking in database. Please try again.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showHomepage() {
        cardLayout.show(mainPanel, "HOME");
        setActiveMenuButton(homeButton);
    }
    
    // Booking class
    static class Booking {
        private String bookingId;
        private String turfId;
        private String customerName;
        private String date;
        private String startTime;
        private int hours;
        private int playerCount;
        private double amount;
        private String status;
        private String email;

        public Booking(String bookingId, String turfId, String customerName, 
                      String date, String startTime, int hours, int playerCount, double amount, String status) {
            this.bookingId = bookingId;
            this.turfId = turfId;
            this.customerName = customerName;
            this.date = date;
            this.startTime = startTime;
            this.hours = hours;
            this.playerCount = playerCount;
            this.amount = amount;
            this.status = status;
            this.email = customerName.toLowerCase().replace(" ", "") + "@example.com";
        }
        
        public Booking(String bookingId, String turfId, String customerName, 
                      String date, String startTime, int hours, int playerCount, double amount, String status,
                      String email) {
            this.bookingId = bookingId;
            this.turfId = turfId;
            this.customerName = customerName;
            this.date = date;
            this.startTime = startTime;
            this.hours = hours;
            this.playerCount = playerCount;
            this.amount = amount;
            this.status = status;
            this.email = email;
        }

        // Getters
        public String getBookingId() { return bookingId; }
        public String getTurfId() { return turfId; }
        public String getCustomerName() { return customerName; }
        public String getDate() { return date; }
        public String getStartTime() { return startTime; }
        public int getHours() { return hours; }
        public int getPlayerCount() { return playerCount; }
        public double getAmount() { return amount; }
        public String getStatus() { return status; }
        public String getEmail() { return email; }
        
        // Setters
        public void setStatus(String status) { this.status = status; }
    }

    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // For testing - create a sample admin
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Admin sampleAdmin = new Admin("A001", "Admin One", "T001", "Green Field Football");
                new AdminDashboard(sampleAdmin, new Login()).setVisible(true);
            }
        });
    }
}