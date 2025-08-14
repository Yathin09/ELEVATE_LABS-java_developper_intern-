import java.sql.*;
import java.util.Scanner;

public class EmployeeDatabaseApp {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/employee_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    
    // Connection object
    private static Connection connection = null;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Initialize database connection
            initializeDatabase();
            
            while (true) {
                showMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                switch (choice) {
                    case 1:
                        addEmployee(scanner);
                        break;
                    case 2:
                        viewAllEmployees();
                        break;
                    case 3:
                        viewEmployeeById(scanner);
                        break;
                    case 4:
                        updateEmployee(scanner);
                        break;
                    case 5:
                        deleteEmployee(scanner);
                        break;
                    case 6:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
            scanner.close();
        }
    }
    
    // Initialize database connection and create table if not exists
    private static void initializeDatabase() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection established successfully!");
            
            // Create database if not exists
            createDatabaseIfNotExists();
            
            // Create employees table if not exists
            createEmployeeTable();
            
            // Disable auto-commit for transaction management
            connection.setAutoCommit(false);
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    // Create database if it doesn't exist
    private static void createDatabaseIfNotExists() throws SQLException {
        String createDbQuery = "CREATE DATABASE IF NOT EXISTS employee_db";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createDbQuery);
            stmt.executeUpdate("USE employee_db");
        }
    }
    
    // Create employees table
    private static void createEmployeeTable() throws SQLException {
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS employees (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                department VARCHAR(50) NOT NULL,
                salary DECIMAL(10,2) NOT NULL,
                hire_date DATE NOT NULL
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableQuery);
            connection.commit(); // Commit the table creation
            System.out.println("Employee table created/verified successfully!");
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
    
    // Display menu options
    private static void showMenu() {
        System.out.println("\n=== Employee Database Management System ===");
        System.out.println("1. Add Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. View Employee by ID");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }
    
    // CREATE - Add new employee using PreparedStatement
    private static void addEmployee(Scanner scanner) {
        String insertQuery = "INSERT INTO employees (name, email, department, salary, hire_date) VALUES (?, ?, ?, ?, ?)";
        
        try {
            System.out.print("Enter employee name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter employee email: ");
            String email = scanner.nextLine();
            
            System.out.print("Enter department: ");
            String department = scanner.nextLine();
            
            System.out.print("Enter salary: ");
            double salary = scanner.nextDouble();
            scanner.nextLine(); // consume newline
            
            System.out.print("Enter hire date (YYYY-MM-DD): ");
            String hireDateStr = scanner.nextLine();
            Date hireDate = Date.valueOf(hireDateStr);
            
            // Using PreparedStatement to prevent SQL injection
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, department);
                pstmt.setDouble(4, salary);
                pstmt.setDate(5, hireDate);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Get generated employee ID
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int employeeId = generatedKeys.getInt(1);
                            System.out.println("Employee added successfully with ID: " + employeeId);
                        }
                    }
                    connection.commit(); // Commit the transaction
                } else {
                    System.out.println("Failed to add employee.");
                    connection.rollback();
                }
            }
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            handleSQLException(e);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }
    
    // READ - View all employees
    private static void viewAllEmployees() {
        String selectQuery = "SELECT * FROM employees ORDER BY id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery);
             ResultSet rs = pstmt.executeQuery()) {
            
            System.out.println("\n=== All Employees ===");
            System.out.printf("%-5s %-20s %-25s %-15s %-10s %-12s%n", 
                            "ID", "Name", "Email", "Department", "Salary", "Hire Date");
            System.out.println("=".repeat(95));
            
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                System.out.printf("%-5d %-20s %-25s %-15s %-10.2f %-12s%n",
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("department"),
                    rs.getDouble("salary"),
                    rs.getDate("hire_date")
                );
            }
            
            if (!hasResults) {
                System.out.println("No employees found in the database.");
            }
            
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
    
    // READ - View employee by ID
    private static void viewEmployeeById(Scanner scanner) {
        System.out.print("Enter employee ID: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        String selectQuery = "SELECT * FROM employees WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setInt(1, employeeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n=== Employee Details ===");
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Email: " + rs.getString("email"));
                    System.out.println("Department: " + rs.getString("department"));
                    System.out.println("Salary: $" + rs.getDouble("salary"));
                    System.out.println("Hire Date: " + rs.getDate("hire_date"));
                } else {
                    System.out.println("Employee with ID " + employeeId + " not found.");
                }
            }
            
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
    
    // UPDATE - Update employee information
    private static void updateEmployee(Scanner scanner) {
        System.out.print("Enter employee ID to update: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        // First check if employee exists
        if (!employeeExists(employeeId)) {
            System.out.println("Employee with ID " + employeeId + " not found.");
            return;
        }
        
        System.out.print("Enter new name (or press Enter to skip): ");
        String name = scanner.nextLine();
        
        System.out.print("Enter new email (or press Enter to skip): ");
        String email = scanner.nextLine();
        
        System.out.print("Enter new department (or press Enter to skip): ");
        String department = scanner.nextLine();
        
        System.out.print("Enter new salary (or 0 to skip): ");
        double salary = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        
        StringBuilder updateQuery = new StringBuilder("UPDATE employees SET ");
        boolean hasUpdates = false;
        
        if (!name.isEmpty()) {
            updateQuery.append("name = ?");
            hasUpdates = true;
        }
        
        if (!email.isEmpty()) {
            if (hasUpdates) updateQuery.append(", ");
            updateQuery.append("email = ?");
            hasUpdates = true;
        }
        
        if (!department.isEmpty()) {
            if (hasUpdates) updateQuery.append(", ");
            updateQuery.append("department = ?");
            hasUpdates = true;
        }
        
        if (salary > 0) {
            if (hasUpdates) updateQuery.append(", ");
            updateQuery.append("salary = ?");
            hasUpdates = true;
        }
        
        if (!hasUpdates) {
            System.out.println("No updates provided.");
            return;
        }
        
        updateQuery.append(" WHERE id = ?");
        
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery.toString())) {
            int paramIndex = 1;
            
            if (!name.isEmpty()) {
                pstmt.setString(paramIndex++, name);
            }
            if (!email.isEmpty()) {
                pstmt.setString(paramIndex++, email);
            }
            if (!department.isEmpty()) {
                pstmt.setString(paramIndex++, department);
            }
            if (salary > 0) {
                pstmt.setDouble(paramIndex++, salary);
            }
            
            pstmt.setInt(paramIndex, employeeId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Employee updated successfully!");
                connection.commit();
            } else {
                System.out.println("Failed to update employee.");
                connection.rollback();
            }
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            handleSQLException(e);
        }
    }
    
    // DELETE - Delete employee
    private static void deleteEmployee(Scanner scanner) {
        System.out.print("Enter employee ID to delete: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        // First check if employee exists
        if (!employeeExists(employeeId)) {
            System.out.println("Employee with ID " + employeeId + " not found.");
            return;
        }
        
        System.out.print("Are you sure you want to delete this employee? (y/n): ");
        String confirmation = scanner.nextLine();
        
        if (!confirmation.equalsIgnoreCase("y")) {
            System.out.println("Delete operation cancelled.");
            return;
        }
        
        String deleteQuery = "DELETE FROM employees WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, employeeId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Employee deleted successfully!");
                connection.commit();
            } else {
                System.out.println("Failed to delete employee.");
                connection.rollback();
            }
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            handleSQLException(e);
        }
    }
    
    // Helper method to check if employee exists
    private static boolean employeeExists(int employeeId) {
        String query = "SELECT 1 FROM employees WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, employeeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }
    }
    
    // Handle SQL exceptions with detailed error information
    private static void handleSQLException(SQLException e) {
        System.err.println("SQL Error occurred:");
        System.err.println("Error Code: " + e.getErrorCode());
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Message: " + e.getMessage());
        
        // Handle specific common errors
        switch (e.getErrorCode()) {
            case 1062: // Duplicate entry
                System.err.println("Error: Email already exists. Please use a different email.");
                break;
            case 1045: // Access denied
                System.err.println("Error: Access denied. Check your username and password.");
                break;
            case 1049: // Unknown database
                System.err.println("Error: Database does not exist.");
                break;
            default:
                System.err.println("An unexpected database error occurred.");
        }
    }
    
    // Close database connection properly
    private static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed successfully.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}

// Additional utility class for database connection management
class DatabaseConnectionManager {
    private static final String URL = "jdbc:mysql://localhost:3306/employee_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    
    // Get database connection with proper error handling
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            conn.setAutoCommit(false); // Enable transaction management
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    // Close connection safely
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    // Close ResultSet safely
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
    }
    
    // Close Statement safely
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing Statement: " + e.getMessage());
            }
        }
    }
}