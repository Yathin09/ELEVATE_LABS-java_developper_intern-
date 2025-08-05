import java.util.*;

// Student class with encapsulation
class Student {
    private int id;
    private String name;
    private double marks;
    
    // Constructor overloading
    public Student() {
        this.id = 0;
        this.name = "";
        this.marks = 0.0;
    }
    
    public Student(int id, String name, double marks) {
        this.id = id;
        this.name = name;
        this.marks = marks;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getMarks() {
        return marks;
    }
    
    public void setMarks(double marks) {
        this.marks = marks;
    }
    
    @Override
    public String toString() {
        return "Student{ID=" + id + ", Name='" + name + "', Marks=" + marks + "}";
    }
}

// Main class for Student Record Management System
public class StudentRecordManagement {
    private static ArrayList<Student> students = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static int nextId = 1;
    
    public static void main(String[] args) {
        System.out.println("=== STUDENT RECORD MANAGEMENT SYSTEM ===");
        
        while (true) {
            displayMenu();
            int choice = getChoice();
            
            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewAllStudents();
                    break;
                case 3:
                    updateStudent();
                    break;
                case 4:
                    deleteStudent();
                    break;
                case 5:
                    searchStudent();
                    break;
                case 6:
                    sortStudents();
                    break;
                case 7:
                    System.out.println("Thank you for using Student Record Management System!");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
    
    private static void displayMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Update Student");
        System.out.println("4. Delete Student");
        System.out.println("5. Search Student");
        System.out.println("6. Sort Students by Marks");
        System.out.println("7. Exit");
        System.out.print("Enter your choice (1-7): ");
    }
    
    private static int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    // CREATE operation
    private static void addStudent() {
        System.out.println("\n--- ADD STUDENT ---");
        
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter student marks: ");
        double marks;
        try {
            marks = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid marks format!");
            return;
        }
        
        Student student = new Student(nextId++, name, marks);
        students.add(student);
        System.out.println("Student added successfully! ID: " + student.getId());
    }
    
    // READ operation
    private static void viewAllStudents() {
        System.out.println("\n--- ALL STUDENTS ---");
        
        if (students.isEmpty()) {
            System.out.println("No students found!");
            return;
        }
        
        System.out.println("Total Students: " + students.size());
        System.out.println("----------------------------------------");
        for (Student student : students) {
            System.out.println(student);
        }
    }
    
    // UPDATE operation
    private static void updateStudent() {
        System.out.println("\n--- UPDATE STUDENT ---");
        
        if (students.isEmpty()) {
            System.out.println("No students to update!");
            return;
        }
        
        System.out.print("Enter student ID to update: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
            return;
        }
        
        Student student = findStudentById(id);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }
        
        System.out.println("Current details: " + student);
        System.out.print("Enter new name (or press Enter to keep current): ");
        String newName = scanner.nextLine();
        if (!newName.trim().isEmpty()) {
            student.setName(newName);
        }
        
        System.out.print("Enter new marks (or press Enter to keep current): ");
        String marksInput = scanner.nextLine();
        if (!marksInput.trim().isEmpty()) {
            try {
                double newMarks = Double.parseDouble(marksInput);
                student.setMarks(newMarks);
            } catch (NumberFormatException e) {
                System.out.println("Invalid marks format! Keeping current marks.");
            }
        }
        
        System.out.println("Student updated successfully!");
        System.out.println("Updated details: " + student);
    }
    
    // DELETE operation
    private static void deleteStudent() {
        System.out.println("\n--- DELETE STUDENT ---");
        
        if (students.isEmpty()) {
            System.out.println("No students to delete!");
            return;
        }
        
        System.out.print("Enter student ID to delete: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
            return;
        }
        
        Student student = findStudentById(id);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }
        
        System.out.println("Student to delete: " + student);
        System.out.print("Are you sure? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.toLowerCase().equals("y") || confirm.toLowerCase().equals("yes")) {
            students.remove(student);
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    private static void searchStudent() {
        System.out.println("\n--- SEARCH STUDENT ---");
        
        if (students.isEmpty()) {
            System.out.println("No students to search!");
            return;
        }
        
        System.out.print("Enter student ID or name to search: ");
        String searchTerm = scanner.nextLine();
        
        // Try to search by ID first
        try {
            int id = Integer.parseInt(searchTerm);
            Student student = findStudentById(id);
            if (student != null) {
                System.out.println("Found: " + student);
                return;
            }
        } catch (NumberFormatException e) {
            // Not a number, search by name
        }
        
        // Search by name
        boolean found = false;
        for (Student student : students) {
            if (student.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                System.out.println("Found: " + student);
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No student found with that ID or name!");
        }
    }
    
    private static void sortStudents() {
        System.out.println("\n--- SORT STUDENTS ---");
        
        if (students.isEmpty()) {
            System.out.println("No students to sort!");
            return;
        }
        
        // Sort ArrayList by marks (descending order)
        students.sort((s1, s2) -> Double.compare(s2.getMarks(), s1.getMarks()));
        
        System.out.println("Students sorted by marks (highest to lowest):");
        viewAllStudents();
    }
    
    private static Student findStudentById(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }
}