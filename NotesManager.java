import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A text-based notes manager application with file I/O operations
 * Demonstrates FileReader/FileWriter, BufferedReader, and exception handling
 */
public class NotesManager {
    private static final String NOTES_FILE = "notes.txt";
    private static final String SEPARATOR = "==================================================";
    private Scanner scanner;

    public NotesManager() {
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        NotesManager notesManager = new NotesManager();
        notesManager.run();
    }

    public void run() {
        System.out.println("Welcome to Java Notes Manager!");
        System.out.println(SEPARATOR);

        while (true) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    addNote();
                    break;
                case 2:
                    viewAllNotes();
                    break;
                case 3:
                    searchNotes();
                    break;
                case 4:
                    deleteNote();
                    break;
                case 5:
                    clearAllNotes();
                    break;
                case 6:
                    System.out.println("Thank you for using Notes Manager!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n--- Notes Manager Menu ---");
        System.out.println("1. Add New Note");
        System.out.println("2. View All Notes");
        System.out.println("3. Search Notes");
        System.out.println("4. Delete Note");
        System.out.println("5. Clear All Notes");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; // Invalid choice
        }
    }

    /**
     * Adds a new note using FileWriter (append mode)
     * Demonstrates exception handling with try-with-resources
     */
    private void addNote() {
        System.out.print("Enter note title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter note content: ");
        String content = scanner.nextLine();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String noteEntry = String.format("[%s] %s%n%s%n%s%n%n", timestamp, title, content, SEPARATOR);

        // Using try-with-resources for automatic resource management
        try (FileWriter writer = new FileWriter(NOTES_FILE, true)) { // append mode
            writer.write(noteEntry);
            System.out.println("Note added successfully!");
        } catch (IOException e) {
            System.err.println("Error writing note: " + e.getMessage());
            logException(e);
        }
    }

    /**
     * Views all notes using BufferedReader for efficient reading
     * Demonstrates the difference between FileReader and BufferedReader
     */
    private void viewAllNotes() {
        File file = new File(NOTES_FILE);
        if (!file.exists()) {
            System.out.println("No notes found. Create your first note!");
            return;
        }

        System.out.println("\n--- All Notes ---");
        // Using BufferedReader for efficient line-by-line reading
        try (BufferedReader reader = new BufferedReader(new FileReader(NOTES_FILE))) {
            String line;
            boolean hasNotes = false;
            
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                hasNotes = true;
            }
            
            if (!hasNotes) {
                System.out.println("No notes available.");
            }
        } catch (IOException e) {
            System.err.println("Error reading notes: " + e.getMessage());
            logException(e);
        }
    }

    /**
     * Searches for notes containing a specific keyword
     * Demonstrates file reading and string searching
     */
    private void searchNotes() {
        File file = new File(NOTES_FILE);
        if (!file.exists()) {
            System.out.println("No notes found to search.");
            return;
        }

        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine().toLowerCase();

        System.out.println("\n--- Search Results ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(NOTES_FILE))) {
            String line;
            boolean found = false;
            StringBuilder currentNote = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                currentNote.append(line).append("\n");
                
                if (line.equals(SEPARATOR)) {
                    String note = currentNote.toString();
                    if (note.toLowerCase().contains(keyword)) {
                        System.out.println(note);
                        found = true;
                    }
                    currentNote.setLength(0); // Clear for next note
                }
            }
            
            if (!found) {
                System.out.println("No notes found containing: " + keyword);
            }
        } catch (IOException e) {
            System.err.println("Error searching notes: " + e.getMessage());
            logException(e);
        }
    }

    /**
     * Deletes a specific note by title
     * Demonstrates file reading, processing, and overwrite mode
     */
    private void deleteNote() {
        File file = new File(NOTES_FILE);
        if (!file.exists()) {
            System.out.println("No notes found to delete.");
            return;
        }

        System.out.print("Enter the title of the note to delete: ");
        String titleToDelete = scanner.nextLine();

        List<String> notes = new ArrayList<>();
        StringBuilder currentNote = new StringBuilder();
        boolean noteFound = false;

        // Read all notes
        try (BufferedReader reader = new BufferedReader(new FileReader(NOTES_FILE))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                currentNote.append(line).append("\n");
                
                if (line.equals(SEPARATOR)) {
                    String note = currentNote.toString();
                    if (!note.contains("] " + titleToDelete + "\n")) {
                        notes.add(note);
                    } else {
                        noteFound = true;
                    }
                    currentNote.setLength(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading notes for deletion: " + e.getMessage());
            logException(e);
            return;
        }

        if (!noteFound) {
            System.out.println("Note with title '" + titleToDelete + "' not found.");
            return;
        }

        // Write back remaining notes (overwrite mode)
        try (FileWriter writer = new FileWriter(NOTES_FILE, false)) { // overwrite mode
            for (String note : notes) {
                writer.write(note);
            }
            System.out.println("Note deleted successfully!");
        } catch (IOException e) {
            System.err.println("Error deleting note: " + e.getMessage());
            logException(e);
        }
    }

    /**
     * Clears all notes (demonstrates file deletion and creation)
     */
    private void clearAllNotes() {
        System.out.print("Are you sure you want to delete all notes? (y/N): ");
        String confirmation = scanner.nextLine();
        
        if (confirmation.toLowerCase().equals("y")) {
            File file = new File(NOTES_FILE);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("All notes cleared successfully!");
                } else {
                    System.err.println("Failed to clear notes file.");
                }
            } else {
                System.out.println("No notes file exists to clear.");
            }
        } else {
            System.out.println("Operation cancelled.");
        }
    }

    /**
     * Logs exceptions to a separate error log file
     * Demonstrates exception handling and logging
     */
    private void logException(Exception e) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String errorEntry = String.format("[%s] Exception: %s%n", timestamp, e.toString());
        
        try (FileWriter errorWriter = new FileWriter("error.log", true)) {
            errorWriter.write(errorEntry);
            
            // Write stack trace
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            errorWriter.write(sw.toString() + "\n" + SEPARATOR + "\n");
            
        } catch (IOException logError) {
            System.err.println("Failed to log error: " + logError.getMessage());
        }
    }

    /**
     * Demonstrates finally block usage
     */
    private void demonstrateFinallyBlock() {
        FileWriter writer = null;
        try {
            writer = new FileWriter("temp.txt");
            writer.write("Temporary data");
            // Simulate an exception
            if (Math.random() > 0.5) {
                throw new IOException("Simulated exception");
            }
        } catch (IOException e) {
            System.err.println("Exception in finally demo: " + e.getMessage());
        } finally {
            // This block always executes
            if (writer != null) {
                try {
                    writer.close();
                    System.out.println("FileWriter closed in finally block");
                } catch (IOException e) {
                    System.err.println("Error closing writer in finally: " + e.getMessage());
                }
            }
        }
    }

    // Cleanup resources
    public void cleanup() {
        if (scanner != null) {
            scanner.close();
        }
    }
}