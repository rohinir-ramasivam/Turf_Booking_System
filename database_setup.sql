-- database_setup_focused.sql
-- BookMyTurf Database Setup - Focused Version with Only 6 Turfs and Separate Tables
-- Run this script to create and initialize the database

-- Enable foreign key support
PRAGMA foreign_keys = ON;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS turfs;
DROP TABLE IF EXISTS users;

-- Drop individual turf booking tables
DROP TABLE IF EXISTS green_field_football_bookings;
DROP TABLE IF EXISTS sports_arena_cricket_bookings;
DROP TABLE IF EXISTS elite_tennis_court_bookings;
DROP TABLE IF EXISTS basketball_arena_bookings;
DROP TABLE IF EXISTS rugby_ground_bookings;
DROP TABLE IF EXISTS badminton_court_bookings;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    email TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('admin', 'user')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Turfs table with enhanced fields
CREATE TABLE IF NOT EXISTS turfs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL,
    location TEXT NOT NULL,
    sport_type TEXT NOT NULL,
    price_per_hour REAL NOT NULL,
    capacity INTEGER NOT NULL,
    admin_id INTEGER,
    description TEXT,
    address TEXT,
    contact_phone TEXT,
    contact_email TEXT,
    owner_name TEXT,
    available_slots TEXT,
    facilities TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Main bookings table (for general queries)
CREATE TABLE IF NOT EXISTS bookings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    turf_id INTEGER NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    hours INTEGER NOT NULL CHECK(hours > 0 AND hours <= 8),
    player_count INTEGER NOT NULL CHECK(player_count > 0),
    total_amount REAL NOT NULL CHECK(total_amount >= 0),
    status TEXT DEFAULT 'confirmed' CHECK(status IN ('confirmed', 'pending', 'completed', 'cancelled')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (turf_id) REFERENCES turfs(id) ON DELETE CASCADE,
    UNIQUE(turf_id, booking_date, start_time)
);

-- Individual turf booking tables with user details
CREATE TABLE IF NOT EXISTS green_field_football_bookings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    username TEXT NOT NULL,
    email TEXT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    hours INTEGER NOT NULL CHECK(hours > 0 AND hours <= 8),
    player_count INTEGER NOT NULL CHECK(player_count > 0),
    total_amount REAL NOT NULL CHECK(total_amount >= 0),
    status TEXT DEFAULT 'confirmed' CHECK(status IN ('confirmed', 'pending', 'completed', 'cancelled')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(booking_date, start_time)
);

CREATE TABLE IF NOT EXISTS sports_arena_cricket_bookings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    username TEXT NOT NULL,
    email TEXT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    hours INTEGER NOT NULL CHECK(hours > 0 AND hours <= 8),
    player_count INTEGER NOT NULL CHECK(player_count > 0),
    total_amount REAL NOT NULL CHECK(total_amount >= 0),
    status TEXT DEFAULT 'confirmed' CHECK(status IN ('confirmed', 'pending', 'completed', 'cancelled')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(booking_date, start_time)
);

CREATE TABLE IF NOT EXISTS elite_tennis_court_bookings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    username TEXT NOT NULL,
    email TEXT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    hours INTEGER NOT NULL CHECK(hours > 0 AND hours <= 8),
    player_count INTEGER NOT NULL CHECK(player_count > 0),
    total_amount REAL NOT NULL CHECK(total_amount >= 0),
    status TEXT DEFAULT 'confirmed' CHECK(status IN ('confirmed', 'pending', 'completed', 'cancelled')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(booking_date, start_time)
);

CREATE TABLE IF NOT EXISTS basketball_arena_bookings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    username TEXT NOT NULL,
    email TEXT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    hours INTEGER NOT NULL CHECK(hours > 0 AND hours <= 8),
    player_count INTEGER NOT NULL CHECK(player_count > 0),
    total_amount REAL NOT NULL CHECK(total_amount >= 0),
    status TEXT DEFAULT 'confirmed' CHECK(status IN ('confirmed', 'pending', 'completed', 'cancelled')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(booking_date, start_time)
);

CREATE TABLE IF NOT EXISTS rugby_ground_bookings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    username TEXT NOT NULL,
    email TEXT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    hours INTEGER NOT NULL CHECK(hours > 0 AND hours <= 8),
    player_count INTEGER NOT NULL CHECK(player_count > 0),
    total_amount REAL NOT NULL CHECK(total_amount >= 0),
    status TEXT DEFAULT 'confirmed' CHECK(status IN ('confirmed', 'pending', 'completed', 'cancelled')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(booking_date, start_time)
);

CREATE TABLE IF NOT EXISTS badminton_court_bookings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    username TEXT NOT NULL,
    email TEXT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    hours INTEGER NOT NULL CHECK(hours > 0 AND hours <= 8),
    player_count INTEGER NOT NULL CHECK(player_count > 0),
    total_amount REAL NOT NULL CHECK(total_amount >= 0),
    status TEXT DEFAULT 'confirmed' CHECK(status IN ('confirmed', 'pending', 'completed', 'cancelled')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(booking_date, start_time)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_bookings_user_id ON bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_turf_id ON bookings(turf_id);
CREATE INDEX IF NOT EXISTS idx_bookings_date ON bookings(booking_date);
CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings(status);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_turfs_admin_id ON turfs(admin_id);

-- Create indexes for individual turf tables
CREATE INDEX IF NOT EXISTS idx_gff_user_id ON green_field_football_bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_sac_user_id ON sports_arena_cricket_bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_etc_user_id ON elite_tennis_court_bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_ba_user_id ON basketball_arena_bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_rg_user_id ON rugby_ground_bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_bc_user_id ON badminton_court_bookings(user_id);

-- Insert admin users (using names from your code: Rajesh, Suresh, Priya, Arun, Hari, Surya)
INSERT OR IGNORE INTO users (username, password, email, role) VALUES 
('rajesh', 'admin123', 'rajesh@bookmyturf.com', 'admin'),
('suresh', 'admin123', 'suresh@bookmyturf.com', 'admin'),
('priya', 'admin123', 'priya@bookmyturf.com', 'admin'),
('arun', 'admin123', 'arun@bookmyturf.com', 'admin'),
('hari', 'admin123', 'hari@bookmyturf.com', 'admin'),
('surya', 'admin123', 'surya@bookmyturf.com', 'admin');

-- Insert ONLY the requested user accounts
INSERT OR IGNORE INTO users (username, password, email, role) VALUES 
('mega', 'mega123', 'mega@example.com', 'user'),
('shobi', 'shobi123', 'shobi@example.com', 'user'),
('sanjith', 'sanjith123', 'sanjith@example.com', 'user'),
('sneka', 'sneka123', 'sneka@example.com', 'user'),
('rohini', 'rohini123', 'rohini@example.com', 'user'),
('sachika', 'sachika123', 'sachika@example.com', 'user'),
('sabari', 'sabari123', 'sabari@example.com', 'user'),
('gowtham', 'gowtham123', 'gowtham@example.com', 'user'),
('vijay', 'vijay123', 'vijay@example.com', 'user');

-- Insert ONLY the 6 turfs from your Java code with matching admin assignments
INSERT OR IGNORE INTO turfs (name, location, sport_type, price_per_hour, capacity, admin_id, description, address, contact_phone, contact_email, owner_name, available_slots, facilities) VALUES 
('Green Field Football', 'Chennai', 'Football', 1200.0, 20, (SELECT id FROM users WHERE username = 'rajesh'),
 'Large grass field with professional goals and floodlights',
 '123 Sports Complex, Anna Nagar, Chennai - 600040',
 '+91-9876543210',
 'greenfield@example.com',
 'Rajesh',
 '["9:00", "11:00", "14:00", "16:00", "18:00"]',
 '["Floodlights", "Changing Rooms", "Parking", "Water Facility", "First Aid"]'),

('Sports Arena Cricket', 'Coimbatore', 'Cricket', 1500.0, 30, (SELECT id FROM users WHERE username = 'suresh'),
 'Professional cricket pitch with pavilion and practice nets',
 '456 Cricket Ground, Race Course, Coimbatore - 641018',
 '+91-9876543211',
 'sportsarena@example.com',
 'Suresh',
 '["8:00", "11:00", "14:00", "17:00"]',
 '["Practice Nets", "Pavilion", "Scoreboard", "Umpire Services", "Cafeteria"]'),

('Elite Tennis Court', 'Madurai', 'Tennis', 800.0, 4, (SELECT id FROM users WHERE username = 'priya'),
 'Hard court with nets and professional lighting system',
 '789 Tennis Complex, KK Nagar, Madurai - 625020',
 '+91-9876543212',
 'elitetennis@example.com',
 'Priya',
 '["9:00", "11:00", "13:00", "15:00", "17:00", "19:00"]',
 '["Air Conditioned", "Professional Nets", "Ball Machine", "Coach Available", "Pro Shop"]'),

('Basketball Arena', 'Chennai', 'Basketball', 900.0, 10, (SELECT id FROM users WHERE username = 'arun'),
 'Indoor court with professional flooring and electronic scoreboard',
 '321 Indoor Stadium, T Nagar, Chennai - 600017',
 '+91-9876543213',
 'basketballarena@example.com',
 'Arun',
 '["8:00", "10:00", "12:00", "14:00", "16:00", "18:00"]',
 '["Air Conditioned", "Electronic Scoreboard", "Basketball Rental", "Coach Available", "Locker Rooms"]'),

('Rugby Ground', 'Coimbatore', 'Rugby', 1400.0, 25, (SELECT id FROM users WHERE username = 'hari'),
 'Full-size rugby field with changing rooms and medical facility',
 '654 Rugby Park, Peelamedu, Coimbatore - 641004',
 '+91-9876543214',
 'rugbyground@example.com',
 'Hari',
 '["9:00", "12:00", "15:00", "18:00"]',
 '["Medical Room", "Changing Rooms", "Equipment Rental", "Showers", "Physiotherapist"]'),

('Badminton Court', 'Madurai', 'Badminton', 600.0, 6, (SELECT id FROM users WHERE username = 'surya'),
 'Air-conditioned indoor court with professional flooring',
 '987 Sports Center, Vilakkuthoon, Madurai - 625001',
 '+91-9876543215',
 'badmintoncourt@example.com',
 'Surya',
 '["8:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00"]',
 '["Air Conditioned", "Professional Shuttles", "Racket Rental", "Coach Available", "Cafeteria"]');

-- Insert sample bookings into main bookings table
INSERT OR IGNORE INTO bookings (user_id, turf_id, booking_date, start_time, hours, player_count, total_amount, status) VALUES 
-- Bookings for mega
((SELECT id FROM users WHERE username = 'mega'), (SELECT id FROM turfs WHERE name = 'Green Field Football'), '2024-01-20', '10:00', 2, 18, 2400.0, 'confirmed'),
((SELECT id FROM users WHERE username = 'mega'), (SELECT id FROM turfs WHERE name = 'Sports Arena Cricket'), '2024-01-21', '14:00', 1, 15, 1500.0, 'confirmed'),
((SELECT id FROM users WHERE username = 'mega'), (SELECT id FROM turfs WHERE name = 'Elite Tennis Court'), '2024-01-22', '15:00', 1, 4, 800.0, 'confirmed'),

-- Bookings for shobi
((SELECT id FROM users WHERE username = 'shobi'), (SELECT id FROM turfs WHERE name = 'Basketball Arena'), '2024-01-20', '11:00', 2, 8, 1800.0, 'confirmed'),
((SELECT id FROM users WHERE username = 'shobi'), (SELECT id FROM turfs WHERE name = 'Rugby Ground'), '2024-01-21', '16:00', 1, 20, 1400.0, 'confirmed'),
((SELECT id FROM users WHERE username = 'shobi'), (SELECT id FROM turfs WHERE name = 'Badminton Court'), '2024-01-22', '18:00', 2, 4, 1200.0, 'confirmed'),

-- Bookings for sanjith
((SELECT id FROM users WHERE username = 'sanjith'), (SELECT id FROM turfs WHERE name = 'Green Field Football'), '2024-01-23', '11:00', 1, 16, 1200.0, 'confirmed'),
((SELECT id FROM users WHERE username = 'sanjith'), (SELECT id FROM turfs WHERE name = 'Sports Arena Cricket'), '2024-01-24', '16:00', 1, 20, 1500.0, 'confirmed'),

-- Bookings for sneka
((SELECT id FROM users WHERE username = 'sneka'), (SELECT id FROM turfs WHERE name = 'Elite Tennis Court'), '2024-01-25', '17:00', 1, 4, 800.0, 'confirmed'),
((SELECT id FROM users WHERE username = 'sneka'), (SELECT id FROM turfs WHERE name = 'Basketball Arena'), '2024-01-26', '14:00', 2, 8, 1800.0, 'confirmed'),

-- Bookings for rohini
((SELECT id FROM users WHERE username = 'rohini'), (SELECT id FROM turfs WHERE name = 'Rugby Ground'), '2024-01-27', '15:00', 2, 22, 2800.0, 'confirmed'),
((SELECT id FROM users WHERE username = 'rohini'), (SELECT id FROM turfs WHERE name = 'Badminton Court'), '2024-01-28', '20:00', 1, 4, 600.0, 'confirmed'),

-- Bookings for sachika
((SELECT id FROM users WHERE username = 'sachika'), (SELECT id FROM turfs WHERE name = 'Green Field Football'), '2024-01-29', '09:00', 2, 16, 2400.0, 'confirmed'),
((SELECT id FROM users WHERE username = 'sachika'), (SELECT id FROM turfs WHERE name = 'Sports Arena Cricket'), '2024-01-30', '11:00', 3, 24, 4500.0, 'confirmed'),

-- Bookings for sabari
((SELECT id FROM users WHERE username = 'sabari'), (SELECT id FROM turfs WHERE name = 'Elite Tennis Court'), '2024-02-01', '13:00', 1, 4, 800.0, 'confirmed'),
((SELECT id FROM users WHERE username = 'sabari'), (SELECT id FROM turfs WHERE name = 'Basketball Arena'), '2024-02-02', '16:00', 1, 8, 900.0, 'confirmed'),

-- Bookings for gowtham
((SELECT id FROM users WHERE username = 'gowtham'), (SELECT id FROM turfs WHERE name = 'Rugby Ground'), '2024-02-03', '12:00', 1, 20, 1400.0, 'confirmed'),
((SELECT id FROM users WHERE username = 'gowtham'), (SELECT id FROM turfs WHERE name = 'Badminton Court'), '2024-02-04', '18:00', 2, 4, 1200.0, 'confirmed'),

-- Bookings for vijay
((SELECT id FROM users WHERE username = 'vijay'), (SELECT id FROM turfs WHERE name = 'Green Field Football'), '2024-02-05', '14:00', 1, 18, 1200.0, 'confirmed'),
((SELECT id FROM users WHERE username = 'vijay'), (SELECT id FROM turfs WHERE name = 'Sports Arena Cricket'), '2024-02-06', '17:00', 1, 25, 1500.0, 'confirmed');

-- Copy data to individual turf booking tables with user details
INSERT OR IGNORE INTO green_field_football_bookings (user_id, username, email, booking_date, start_time, hours, player_count, total_amount, status)
SELECT b.user_id, u.username, u.email, b.booking_date, b.start_time, b.hours, b.player_count, b.total_amount, b.status
FROM bookings b
JOIN users u ON b.user_id = u.id
WHERE b.turf_id = (SELECT id FROM turfs WHERE name = 'Green Field Football');

INSERT OR IGNORE INTO sports_arena_cricket_bookings (user_id, username, email, booking_date, start_time, hours, player_count, total_amount, status)
SELECT b.user_id, u.username, u.email, b.booking_date, b.start_time, b.hours, b.player_count, b.total_amount, b.status
FROM bookings b
JOIN users u ON b.user_id = u.id
WHERE b.turf_id = (SELECT id FROM turfs WHERE name = 'Sports Arena Cricket');

INSERT OR IGNORE INTO elite_tennis_court_bookings (user_id, username, email, booking_date, start_time, hours, player_count, total_amount, status)
SELECT b.user_id, u.username, u.email, b.booking_date, b.start_time, b.hours, b.player_count, b.total_amount, b.status
FROM bookings b
JOIN users u ON b.user_id = u.id
WHERE b.turf_id = (SELECT id FROM turfs WHERE name = 'Elite Tennis Court');

INSERT OR IGNORE INTO basketball_arena_bookings (user_id, username, email, booking_date, start_time, hours, player_count, total_amount, status)
SELECT b.user_id, u.username, u.email, b.booking_date, b.start_time, b.hours, b.player_count, b.total_amount, b.status
FROM bookings b
JOIN users u ON b.user_id = u.id
WHERE b.turf_id = (SELECT id FROM turfs WHERE name = 'Basketball Arena');

INSERT OR IGNORE INTO rugby_ground_bookings (user_id, username, email, booking_date, start_time, hours, player_count, total_amount, status)
SELECT b.user_id, u.username, u.email, b.booking_date, b.start_time, b.hours, b.player_count, b.total_amount, b.status
FROM bookings b
JOIN users u ON b.user_id = u.id
WHERE b.turf_id = (SELECT id FROM turfs WHERE name = 'Rugby Ground');

INSERT OR IGNORE INTO badminton_court_bookings (user_id, username, email, booking_date, start_time, hours, player_count, total_amount, status)
SELECT b.user_id, u.username, u.email, b.booking_date, b.start_time, b.hours, b.player_count, b.total_amount, b.status
FROM bookings b
JOIN users u ON b.user_id = u.id
WHERE b.turf_id = (SELECT id FROM turfs WHERE name = 'Badminton Court');

-- Display success message and data counts
SELECT 'Focused database setup completed successfully!' as message;

SELECT 'Users count: ' || COUNT(*) as user_count FROM users;
SELECT 'Turfs count: ' || COUNT(*) as turf_count FROM turfs;
SELECT 'Bookings count: ' || COUNT(*) as booking_count FROM bookings;

-- Display individual turf booking counts
SELECT '=== INDIVIDUAL TURF BOOKING COUNTS ===' as info;
SELECT 
    'Green Field Football' as turf_name,
    COUNT(*) as booking_count 
FROM green_field_football_bookings
UNION ALL SELECT 'Sports Arena Cricket', COUNT(*) FROM sports_arena_cricket_bookings
UNION ALL SELECT 'Elite Tennis Court', COUNT(*) FROM elite_tennis_court_bookings
UNION ALL SELECT 'Basketball Arena', COUNT(*) FROM basketball_arena_bookings
UNION ALL SELECT 'Rugby Ground', COUNT(*) FROM rugby_ground_bookings
UNION ALL SELECT 'Badminton Court', COUNT(*) FROM badminton_court_bookings;

-- Display user information
SELECT '=== USER INFORMATION ===' as info;
SELECT id, username, email, role, created_at FROM users ORDER BY role, username;

-- Display turf information with enhanced details
SELECT '=== TURF INFORMATION ===' as info;
SELECT 
    t.id, 
    t.name, 
    t.location, 
    t.sport_type, 
    t.price_per_hour, 
    t.capacity,
    t.owner_name,
    t.contact_phone,
    u.username as admin_name 
FROM turfs t 
LEFT JOIN users u ON t.admin_id = u.id 
ORDER BY t.name;

-- Display booking information with user details
SELECT '=== BOOKING INFORMATION ===' as info;
SELECT 
    b.id as booking_id,
    u.username as customer_name,
    u.email as customer_email,
    t.name as turf_name,
    b.booking_date,
    b.start_time,
    b.hours,
    b.player_count,
    b.total_amount,
    b.status,
    b.created_at
FROM bookings b
JOIN users u ON b.user_id = u.id
JOIN turfs t ON b.turf_id = t.id
ORDER BY b.booking_date DESC, b.start_time DESC;

-- Display leaderboard information (only regular users)
SELECT '=== LEADERBOARD INFORMATION ===' as info;
SELECT 
    u.username,
    u.email,
    COUNT(b.id) as total_bookings,
    SUM(b.total_amount) as total_spent,
    ROUND(SUM(b.total_amount) / 100) as points -- 1 point per 100 rupees spent
FROM users u
LEFT JOIN bookings b ON u.id = b.user_id AND b.status IN ('confirmed', 'completed')
WHERE u.role = 'user'  -- Only include regular users, not admins
GROUP BY u.id, u.username, u.email
ORDER BY points DESC, total_bookings DESC;

-- Display admin turf assignments
SELECT '=== ADMIN TURF ASSIGNMENTS ===' as info;
SELECT 
    u.username as admin_name,
    u.email as admin_email,
    COUNT(t.id) as turfs_managed,
    GROUP_CONCAT(t.name, ', ') as managed_turfs
FROM users u
LEFT JOIN turfs t ON u.id = t.admin_id
WHERE u.role = 'admin'
GROUP BY u.id, u.username, u.email
ORDER BY u.username;

-- Display individual turf table information
SELECT '=== INDIVIDUAL TURF TABLES CREATED ===' as info;
SELECT name FROM sqlite_master WHERE type='table' AND name LIKE '%_bookings' ORDER BY name;

-- Display sample data from individual turf tables
SELECT '=== GREEN FIELD FOOTBALL BOOKINGS ===' as info;
SELECT * FROM green_field_football_bookings ORDER BY booking_date DESC, start_time DESC;

SELECT '=== SPORTS ARENA CRICKET BOOKINGS ===' as info;
SELECT * FROM sports_arena_cricket_bookings ORDER BY booking_date DESC, start_time DESC;

SELECT '=== ELITE TENNIS COURT BOOKINGS ===' as info;
SELECT * FROM elite_tennis_court_bookings ORDER BY booking_date DESC, start_time DESC;