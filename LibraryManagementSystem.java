import java.io.*;
import java.util.*;

// Book details ko represent karne ke liye class
class Book implements Serializable {
    /*Iska matlab hai ki is class ke objects ko hum bytes mein convert karke file (.txt) mein save kar sakte hain,
     aur baad mein wapas object bana sakte hain.*/

    private static final long serialVersionUID = 1L;
    /* Yeh ek tarah ka version control hai. Jab file se data load hota hai,
     toh Java check karta hai ki class ka version aur saved data ka version same hai ya nahi.
     */
    private String id;
    private String title;
    private String author;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }

    @Override
    public String toString() {
        return "Book ID: " + id + " | Title: " + title + " | Author: " + author;
    }
}

public class LibraryManagementSystem {
    private static final String FILE_NAME = "library_books.txt"; // Jahan hamara saara data permanently save hoga.
    private static List<Book> library = new ArrayList<>(); // Yeh hamari temporary RAM memory hai. Jab tak program chal raha hai, books is list mein rahengi.
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadBooksFromFile(); // Program start hote hi purana data load karega

        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Search Book");
            System.out.println("4. View All Books");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");

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
                    System.out.println("Thank you for using Library Management System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice! Please enter a number between 1 and 5.");
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

        library.add(new Book(id, title, author));
        /* new Book(id, title, author): Diye gaye data se Book class ka ek naya object memory mein banta hai. */
        // library.add(...): Wo object hamari temporary ArrayList mein add ho jata hai.
        saveBooksToFile();
        /*
        Yeh turant poori updated list ko library_books.txt file mein write kar deta hai taaki agar computer band bhi ho jaye, toh aapka data safe rahe.
        */
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

        boolean removed = library.removeIf(book -> book.getId().equalsIgnoreCase(id));
        // Yeh line poori library list mein se us book ko dhoondh kar ek jhatke mein delete kar deta hai jiska ID user ke diye gaye ID se match karta hai
        // aur delete hone par true return karta hai.
        if (removed) {
            saveBooksToFile();
            System.out.println(" Book removed successfully!");
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

    // File Handling - Save Data
    private static void saveBooksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(library);
        } catch (IOException e) {
            System.out.println("Error saving data to file: " + e.getMessage());
        }
    }

    // File Handling - Load Data
    @SuppressWarnings("unchecked")
    private static void loadBooksFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return; // Initial run me agar file nahi hai toh load nahi karega

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            library = (List<Book>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Notice: Starting a fresh database (could not read file).");
        }
    }
}
