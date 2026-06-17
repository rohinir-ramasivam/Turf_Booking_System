# BookMyTurf

A Java Swing application for booking sports turf facilities with separate admin and user dashboards.

## Project Structure

- `Login.java` - main entry point and authentication flow for admin and users.
- `AdminDashboard.java` - admin dashboard for managing bookings and statistics.
- `UserDashboard.java` - user dashboard for booking turf slots and viewing user bookings.
- `database_setup.sql` - SQLite database schema and initial seed data.
- `bookmyturf.db` - local SQLite database file used by the application.
- `sqlite-jdbc-3.45.1.0.jar` - SQLite JDBC driver.
- `slf4j-api-2.0.13.jar` and `slf4j-simple-2.0.13.jar` - logging dependencies.

## Requirements

- Java JDK 8 or newer
- SQLite JDBC driver is already included in the repository

## Run Instructions

From the project root, compile and run the application with the following commands:

```powershell
javac -cp ".;sqlite-jdbc-3.45.1.0.jar;slf4j-api-2.0.13.jar;slf4j-simple-2.0.13.jar" *.java
java -cp ".;sqlite-jdbc-3.45.1.0.jar;slf4j-api-2.0.13.jar;slf4j-simple-2.0.13.jar" Login
```

## Database

The app uses `bookmyturf.db` by default. If the database file is missing, the application will attempt to create and initialize the schema automatically.

If you want to reset the database manually, run the SQL in `database_setup.sql` using an SQLite client.

## Features

- Admin login and admin dashboard
- User login and user dashboard
- Turf booking management
- Booking status tracking and automatic completion updates
- SQLite-backed persistence

## Notes

- The main application class is `Login`.
- If the app cannot find the SQLite driver, ensure the JAR files are present in the project folder.
- The UI is implemented with Java Swing.
