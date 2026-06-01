import java.io.*;
import java.util.*;

// Book details ko represent karne ke liye class
class Book implements Serializable {
    /* Iska matlab hai ki is class ke objects ko hum bytes mein convert karke file (.txt) mein save kar sakte hain, aur baad mein wapas object bana sakte hain. */
    private static final long serialVersionUID = 1L;
    /* Yeh ek tarah ka version control hai. Jab file se data load hota hai, toh Java check karta hai ki class ka version aur saved data ka version same hai ya nahi. */
    private String id;
    private String title;
    private String author;
    private boolean isAvailable; // TRUE matlab library me hai, FALSE matlab kisi ne borrow ki hai
    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = true; // Nayi book default me library me available hogi
    }
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }

    @Override
    public String toString() {
        String status = isAvailable ? "Available" : "Borrowed";
        return "Book ID: " + id + " | Title: " + title + " | Author: " + author + " | Status: [" + status + "]";
    }
}
public class LibraryManagementSystem {
    // Yeh hamari temporary RAM memory hai. Jab tak program chal raha hai, books is list mein rahengi.
    private static List<Book> library = new ArrayList<>(); 
    private static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        loadBooksFromFile(); // Program start hote hi purana data load karega
        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Search Book");
            System.out.println("4. View All Books");
            System.out.println("5. Borrow Book"); 
            System.out.println("6. Return Book"); 
            System.out.println("7. Exit");
            System.out.print("Enter your choice (1-7): ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    addBook();
                    break;
                case "2":
                    removeBook();
                    break;
                case "3":
                    searchBook();
                    break;
                case "4":
                    viewAllBooks();
                    break;
                case "5":
                    borrowBook();
                    break;
                case "6":
                    returnBook();
                    break;
                case "7":
                    System.out.println("Thank you for using Library Management System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice! Please enter a number between 1 and 7.");
            }
        }
    }
    // 1. New Book Add karne ke liye
    private static void addBook() {
        System.out.print("Enter Book ID: ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Book ID cannot be empty!");
            return;
        }
        // Check duplicate ID
        for (Book b : library) {
            if (b.getId().equalsIgnoreCase(id)) {
                System.out.println("Error: A book with this ID already exists!");
                return;
            }
        }
        System.out.print("Enter Book Title: ");
        String title = sc.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Title cannot be empty!");
            return;
        }
        System.out.print("Enter Author Name: ");
        String author = sc.nextLine().trim();
        if (author.isEmpty()) {
            System.out.println("Author cannot be empty!");
            return;
        }
        /* new Book(id, title, author): Diye gaye data se Book class ka ek naya object memory mein banta hai. */
        // library.add(...): Wo object hamari temporary ArrayList mein add ho jata hai.
        library.add(new Book(id, title, author));
        /* Yeh turant poori updated list ko library_books.txt file mein write kar deta hai taaki agar computer band bhi ho jaye, toh aapka data safe rahe.*/
        saveBooksToFile();
        System.out.println("Book added successfully!");
    }
    // 2. Book Remove karne ke liye
    private static void removeBook() {
        if (library.isEmpty()) {
            System.out.println("Library is empty. No books to remove.");
            return;
        }
        System.out.print("Enter Book ID to remove: ");
        String id = sc.nextLine().trim();
        /* Yeh line poori library list mein se us book ko dhoondh kar ek jhatke mein delete kar deta hai jiska ID user ke diye gaye ID se match karta hai aur delete hone par true return karta hai. */
        boolean removed = library.removeIf(book -> book.getId().equalsIgnoreCase(id));
        if (removed) {
            saveBooksToFile();
            System.out.println("Book removed successfully!");
        } else {
            System.out.println("Book not found with ID: " + id);
        }
    }
    // 3. Book Search karne ke liye (Title ya ID se)
    private static void searchBook() {
        if (library.isEmpty()) {
            System.out.println("Library is empty.");
            return;
        }
        System.out.print("Search by (1) ID or (2) Title? Enter choice: ");
        String searchChoice = sc.nextLine().trim();
        if (searchChoice.equals("1")) {
            System.out.print("Enter Book ID: ");
            String id = sc.nextLine().trim();
            boolean found = false;
            for (Book b : library) {
                if (b.getId().equalsIgnoreCase(id)) {
                    System.out.println("Found: " + b);
                    found = true;
                    break;
                }
            }
            if (!found) System.out.println("No book found with ID: " + id);
        } else if (searchChoice.equals("2")) {
            System.out.print("Enter Book Title or Keyword: ");
            String keyword = sc.nextLine().toLowerCase().trim();
            boolean found = false;
            for (Book b : library) {
                if (b.getTitle().toLowerCase().contains(keyword)) {
                    System.out.println("Found: " + b);
                    found = true;
                }
            }
            if (!found) System.out.println("No book found matching title: " + keyword);
        } else {
            System.out.println("Invalid choice!");
        }
    }
    // 4. Saari Books display karne ke liye
    private static void viewAllBooks() {
        if (library.isEmpty()) {
            System.out.println("No books available in the library.");
            return;
        }
        System.out.println("\n--- Current Library Collection ---");
        for (Book b : library) {
            System.out.println(b);
        }
    }
    // 5. Book Borrow karne ke liye (Naya Feature)
    private static void borrowBook() {
        if (library.isEmpty()) {
            System.out.println("Library is empty. No books to borrow.");
            return;
        }
        System.out.print("Enter Book ID to borrow: ");
        String id = sc.nextLine().trim();
        for (Book b : library) {
            if (b.getId().equalsIgnoreCase(id)) {
                // Check kar rahe hain ki book available HAI ya NAHI
                if (!b.isAvailable()) { 
                    System.out.println("Sorry, this book is currently not available (Already borrowed).");
                } else {
                    b.setAvailable(false); // Ab book library me available nahi rahi
                    saveBooksToFile();     // File update karke save karo
                    System.out.println("Book '" + b.getTitle() + "' has been successfully borrowed!");
                }
                return;
            }
        }
        System.out.println("Book not found with ID: " + id);
    }
    // 6. Book Return karne ke liye (Naya Feature)
    private static void returnBook() {
        if (library.isEmpty()) {
            System.out.println("Library is empty.");
            return;
        }
        System.out.print("Enter Book ID to return: ");
        String id = sc.nextLine().trim();
        for (Book b : library) {
            if (b.getId().equalsIgnoreCase(id)) {
                // Check kar rahe hain agar book pehle se hi library me available hai
                if (b.isAvailable()) { 
                    System.out.println("This book is already in the library. No need to return.");
                } else {
                    b.setAvailable(true); // Ab book wapas available ho gayi hai
                    saveBooksToFile();    // File update karke save karo
                    System.out.println("Book '" + b.getTitle() + "' has been successfully returned!");
                }
                return;
            }
        }
        System.out.println("Book not found with ID: " + id);
    }
    // File Handling - Save Data (Simple Version)
    private static void saveBooksToFile() {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            // Direct file ka naam use kiya hai jahan data permanently save hoga.
            fos = new FileOutputStream("library_books.txt");
            oos = new ObjectOutputStream(fos);
            
            // Poori updated library list ko file mein write kar dete hain
            oos.writeObject(library);
            
        } catch (IOException e) {
            System.out.println("Error saving data to file: " + e.getMessage());
        } finally {
            // Streams ko manually close karna zaroori hai taaki memory free ho jaye
            try {
                if (oos != null) oos.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                System.out.println("Error closing save streams.");
            }
        }
    }
    // File Handling - Load Data (Simple Version)
    @SuppressWarnings("unchecked")
    private static void loadBooksFromFile() {
        File file = new File("library_books.txt");
        // Initial run me agar file nahi hai toh load nahi karega
        if (!file.exists()) {
            return; 
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            // File se bytes read karne ke liye stream open ki
            fis = new FileInputStream("library_books.txt");
            ois = new ObjectInputStream(fis);
            
            // File se data read karke wapas list me load kar rahe hain
            library = (List<Book>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Notice: Starting a fresh database (could not read file).");
        } finally {
            // Streams ko manually close karo
            try {
                if (ois != null) ois.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                System.out.println("Error closing load streams.");
            }
        }
    }
}
