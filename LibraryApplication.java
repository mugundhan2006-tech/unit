import java.util.List;
import java.util.Scanner;

/**
 * Menu-driven command-line interface for managing the library inventory.
 */
public final class LibraryApplication {
    private final LibraryInventory inventory = new LibraryInventory();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new LibraryApplication().run();
    }

    /** Reads menu choices and dispatches each request until the user exits. */
    private void run() {
        System.out.println("Library Book Inventory");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine("Choose an option: ").trim();
            try {
                switch (choice) {
                    case "1":
                        addBook();
                        break;
                    case "2":
                        listBooks();
                        break;
                    case "3":
                        updateBook();
                        break;
                    case "4":
                        deleteBook();
                        break;
                    case "0":
                        running = false;
                        System.out.println("Goodbye.");
                        break;
                    default:
                        System.out.println("Please enter 0, 1, 2, 3, or 4.");
                }
            } catch (IllegalArgumentException exception) {
                // Report invalid data without terminating the menu loop.
                System.out.println("Error: " + exception.getMessage());
            }
            System.out.println();
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("1. Add a book");
        System.out.println("2. List all books");
        System.out.println("3. Update a book");
        System.out.println("4. Delete a book");
        System.out.println("0. Exit");
    }

    private void addBook() {
        String title = readRequired("Title: ");
        String author = readRequired("Author: ");
        String isbn = readRequired("ISBN: ");
        int year = readYear("Publication year:", false, 0);
        inventory.addBook(new Book(title, author, isbn, year));
        System.out.println("Book added.");
    }

    private void listBooks() {
        List<Book> books = inventory.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("The inventory is empty.");
            return;
        }

        System.out.printf("%-32s | %-24s | %-18s | %s%n", "TITLE", "AUTHOR", "ISBN", "YEAR");
        System.out.println("--------------------------------------------------------------------------------------");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    private void updateBook() {
        String currentIsbn = readRequired("ISBN of the book to update: ");
        Book currentBook = inventory.findBook(currentIsbn);
        if (currentBook == null) {
            System.out.println("No book was found with that ISBN.");
            return;
        }

        System.out.println("Press Enter to keep a current value.");
        String title = readOptional("Title", currentBook.getTitle());
        String author = readOptional("Author", currentBook.getAuthor());
        String isbn = readOptional("ISBN", currentBook.getIsbn());
        int year = readYear("Publication year", true, currentBook.getPublicationYear());
        inventory.updateBook(currentIsbn, new Book(title, author, isbn, year));
        System.out.println("Book updated.");
    }

    private void deleteBook() {
        String isbn = readRequired("ISBN of the book to delete: ");
        if (inventory.deleteBook(isbn)) {
            System.out.println("Book deleted.");
        } else {
            System.out.println("No book was found with that ISBN.");
        }
    }

    private String readRequired(String prompt) {
        while (true) {
            String value = readLine(prompt).trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    private String readOptional(String field, String currentValue) {
        String value = readLine(field + " [" + currentValue + "]: ").trim();
        return value.isEmpty() ? currentValue : value;
    }

    private int readYear(String prompt, boolean allowCurrentValue, int currentValue) {
        while (true) {
            String value = readLine(prompt + (allowCurrentValue ? " [" + currentValue + "]: " : " ")).trim();
            if (allowCurrentValue && value.isEmpty()) {
                return currentValue;
            }
            try {
                int year = Integer.parseInt(value);
                if (year >= 1 && year <= java.time.Year.now().getValue()) {
                    return year;
                }
            } catch (NumberFormatException exception) {
                // The message below covers both non-numeric and out-of-range input.
            }
            System.out.println("Enter a whole year between 1 and " + java.time.Year.now().getValue() + ".");
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}