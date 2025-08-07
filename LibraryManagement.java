// LibraryManagement.java - Complete Library Management System in Single File

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

// Book class
class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private boolean isAvailable;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private int issuedToUserId;

    // Constructor
    public Book(int bookId, String title, String author, String isbn) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = true;
        this.issuedToUserId = -1;
    }

    // Getters and Setters
    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isAvailable() { return isAvailable; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public int getIssuedToUserId() { return issuedToUserId; }

    public void setAvailable(boolean available) { isAvailable = available; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setIssuedToUserId(int issuedToUserId) { this.issuedToUserId = issuedToUserId; }

    @Override
    public String toString() {
        return "Book{" +
                "ID=" + bookId +
                ", Title='" + title + '\'' +
                ", Author='" + author + '\'' +
                ", ISBN='" + isbn + '\'' +
                ", Available=" + isAvailable +
                '}';
    }
}

// User class
class User {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private List<Integer> issuedBooks;

    // Constructor
    public User(int userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.issuedBooks = new ArrayList<>();
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public List<Integer> getIssuedBooks() { return issuedBooks; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    public void addIssuedBook(int bookId) {
        issuedBooks.add(bookId);
    }

    public void removeIssuedBook(int bookId) {
        issuedBooks.remove(Integer.valueOf(bookId));
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + userId +
                ", Name='" + name + '\'' +
                ", Email='" + email + '\'' +
                ", Phone='" + phone + '\'' +
                ", Issued Books=" + issuedBooks.size() +
                '}';
    }
}

// LibraryInterface (Interface for abstraction)
interface LibraryInterface {
    boolean addBook(Book book);
    boolean addUser(User user);
    boolean issueBook(int bookId, int userId);
    boolean returnBook(int bookId, int userId);
    List<Book> searchBooksByTitle(String title);
    List<Book> searchBooksByAuthor(String author);
    void displayAllBooks();
    void displayAllUsers();
}

// Library class (Main class implementing the interface)
class Library implements LibraryInterface {
    private List<Book> books;
    private List<User> users;
    private String libraryName;

    // Constructor
    public Library(String libraryName) {
        this.libraryName = libraryName;
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    @Override
    public boolean addBook(Book book) {
        if (book != null) {
            books.add(book);
            System.out.println("Book added successfully: " + book.getTitle());
            return true;
        }
        return false;
    }

    @Override
    public boolean addUser(User user) {
        if (user != null) {
            users.add(user);
            System.out.println("User added successfully: " + user.getName());
            return true;
        }
        return false;
    }

    @Override
    public boolean issueBook(int bookId, int userId) {
        Book book = findBookById(bookId);
        User user = findUserById(userId);

        if (book == null) {
            System.out.println("Book not found!");
            return false;
        }

        if (user == null) {
            System.out.println("User not found!");
            return false;
        }

        if (!book.isAvailable()) {
            System.out.println("Book is already issued!");
            return false;
        }

        // Issue the book
        book.setAvailable(false);
        book.setIssueDate(LocalDate.now());
        book.setReturnDate(LocalDate.now().plusDays(14)); // 14 days return period
        book.setIssuedToUserId(userId);
        user.addIssuedBook(bookId);

        System.out.println("Book '" + book.getTitle() + "' issued to " + user.getName());
        System.out.println("Return date: " + book.getReturnDate());
        return true;
    }

    @Override
    public boolean returnBook(int bookId, int userId) {
        Book book = findBookById(bookId);
        User user = findUserById(userId);

        if (book == null || user == null) {
            System.out.println("Book or User not found!");
            return false;
        }

        if (book.isAvailable()) {
            System.out.println("Book is not currently issued!");
            return false;
        }

        if (book.getIssuedToUserId() != userId) {
            System.out.println("Book was not issued to this user!");
            return false;
        }

        // Return the book
        book.setAvailable(true);
        book.setIssueDate(null);
        book.setReturnDate(null);
        book.setIssuedToUserId(-1);
        user.removeIssuedBook(bookId);

        System.out.println("Book '" + book.getTitle() + "' returned by " + user.getName());
        return true;
    }

    @Override
    public List<Book> searchBooksByTitle(String title) {
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchBooksByAuthor(String author) {
        return books.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public void displayAllBooks() {
        System.out.println("\n=== All Books in " + libraryName + " ===");
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }

        for (Book book : books) {
            System.out.println(book);
            if (!book.isAvailable()) {
                User user = findUserById(book.getIssuedToUserId());
                System.out.println("  -> Issued to: " + (user != null ? user.getName() : "Unknown"));
                System.out.println("  -> Return Date: " + book.getReturnDate());
            }
        }
    }

    @Override
    public void displayAllUsers() {
        System.out.println("\n=== All Users in " + libraryName + " ===");
        if (users.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }

        for (User user : users) {
            System.out.println(user);
            if (!user.getIssuedBooks().isEmpty()) {
                System.out.println("  -> Issued Books:");
                for (Integer bookId : user.getIssuedBooks()) {
                    Book book = findBookById(bookId);
                    if (book != null) {
                        System.out.println("    - " + book.getTitle());
                    }
                }
            }
        }
    }

    // Helper methods (Encapsulation)
    private Book findBookById(int bookId) {
        return books.stream()
                .filter(book -> book.getBookId() == bookId)
                .findFirst()
                .orElse(null);
    }

    private User findUserById(int userId) {
        return users.stream()
                .filter(user -> user.getUserId() == userId)
                .findFirst()
                .orElse(null);
    }

    // Additional utility methods
    public void displayAvailableBooks() {
        System.out.println("\n=== Available Books ===");
        books.stream()
                .filter(Book::isAvailable)
                .forEach(System.out::println);
    }

    public void displayIssuedBooks() {
        System.out.println("\n=== Issued Books ===");
        books.stream()
                .filter(book -> !book.isAvailable())
                .forEach(book -> {
                    System.out.println(book);
                    User user = findUserById(book.getIssuedToUserId());
                    System.out.println("  -> Issued to: " + (user != null ? user.getName() : "Unknown"));
                    System.out.println("  -> Return Date: " + book.getReturnDate());
                });
    }
}

// Main class with demonstration
public class LibraryManagement {
    public static void main(String[] args) {
        // Create library instance
        Library library = new Library("Central Library");
        Scanner scanner = new Scanner(System.in);

        // Add sample data
        initializeSampleData(library);

        // Display menu
        while (true) {
            displayMenu();
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addNewBook(library, scanner);
                    break;
                case 2:
                    addNewUser(library, scanner);
                    break;
                case 3:
                    issueBookToUser(library, scanner);
                    break;
                case 4:
                    returnBookFromUser(library, scanner);
                    break;
                case 5:
                    searchBooks(library, scanner);
                    break;
                case 6:
                    library.displayAllBooks();
                    break;
                case 7:
                    library.displayAllUsers();
                    break;
                case 8:
                    library.displayAvailableBooks();
                    break;
                case 9:
                    library.displayIssuedBooks();
                    break;
                case 0:
                    System.out.println("Thank you for using Library Management System!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Library Management System ===");
        System.out.println("1. Add Book");
        System.out.println("2. Add User");
        System.out.println("3. Issue Book");
        System.out.println("4. Return Book");
        System.out.println("5. Search Books");
        System.out.println("6. Display All Books");
        System.out.println("7. Display All Users");
        System.out.println("8. Display Available Books");
        System.out.println("9. Display Issued Books");
        System.out.println("0. Exit");
        System.out.println("================================");
    }

    private static void initializeSampleData(Library library) {
        // Add sample books
        library.addBook(new Book(1, "Java: The Complete Reference", "Herbert Schildt", "978-1260440232"));
        library.addBook(new Book(2, "Clean Code", "Robert C. Martin", "978-0132350884"));
        library.addBook(new Book(3, "Design Patterns", "Gang of Four", "978-0201633612"));
        library.addBook(new Book(4, "Effective Java", "Joshua Bloch", "978-0134685991"));
        library.addBook(new Book(5, "Spring in Action", "Craig Walls", "978-1617294945"));

        // Add sample users
        library.addUser(new User(1, "Alice Johnson", "alice@email.com", "123-456-7890"));
        library.addUser(new User(2, "Bob Smith", "bob@email.com", "234-567-8901"));
        library.addUser(new User(3, "Charlie Brown", "charlie@email.com", "345-678-9012"));

        System.out.println("\n=== Sample data initialized successfully! ===");
    }

    private static void addNewBook(Library library, Scanner scanner) {
        System.out.print("Enter Book ID: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();

        Book book = new Book(bookId, title, author, isbn);
        library.addBook(book);
    }

    private static void addNewUser(Library library, Scanner scanner) {
        System.out.print("Enter User ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();

        User user = new User(userId, name, email, phone);
        library.addUser(user);
    }

    private static void issueBookToUser(Library library, Scanner scanner) {
        System.out.print("Enter Book ID to issue: ");
        int bookId = scanner.nextInt();
        System.out.print("Enter User ID: ");
        int userId = scanner.nextInt();

        library.issueBook(bookId, userId);
    }

    private static void returnBookFromUser(Library library, Scanner scanner) {
        System.out.print("Enter Book ID to return: ");
        int bookId = scanner.nextInt();
        System.out.print("Enter User ID: ");
        int userId = scanner.nextInt();

        library.returnBook(bookId, userId);
    }

    private static void searchBooks(Library library, Scanner scanner) {
        System.out.println("Search by: 1. Title  2. Author");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        List<Book> results;
        if (choice == 1) {
            System.out.print("Enter title to search: ");
            String title = scanner.nextLine();
            results = library.searchBooksByTitle(title);
        } else if (choice == 2) {
            System.out.print("Enter author to search: ");
            String author = scanner.nextLine();
            results = library.searchBooksByAuthor(author);
        } else {
            System.out.println("Invalid choice!");
            return;
        }

        System.out.println("\n=== Search Results ===");
        if (results.isEmpty()) {
            System.out.println("No books found!");
        } else {
            results.forEach(System.out::println);
        }
    }
}