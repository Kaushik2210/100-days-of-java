package day18;

import day18.library.Book; // Book lives in a different package, so it must be imported

/*
 * Compile and run from the src/ directory:
 *   javac day18/PackagesDemo.java day18/library/Book.java
 *   java day18.PackagesDemo
 */
public class PackagesDemo {

    public static void main(String[] args) {
        Book book = new Book("Effective Java", 42.50, "978-0134685991", "A-14");

        System.out.println("title = " + book.getTitle());
        System.out.println("price = " + book.getPrice());
        System.out.println("inventory = " + book.inventoryLine());

        // book.title;                            // no -- private to Book
        // book.shelfCode;                        // no -- package-private to day18.library
        // book.isbn;                             // no -- protected, and we are not a subclass here
        // new day18.library.InventoryRecord(book); // no -- package-private class, invisible outside its package

        SignedBook signed = new SignedBook(
                "Java Concurrency in Practice", 39.99, "978-0321349606", "B-02", "Brian Goetz");
        System.out.println("signed = " + signed.describe());
        signed.compareIsbnWith(book, signed);

        // A fully-qualified name works with no import at all:
        java.util.List<String> shelf = new java.util.ArrayList<>();
        shelf.add(book.getTitle());
        shelf.add(signed.getTitle());
        System.out.println("shelf = " + shelf);
    }
}

// Package-private class in day18, subclassing a public class from day18.library.
class SignedBook extends Book {

    private final String signedBy;

    SignedBook(String title, double price, String isbn, String shelfCode, String signedBy) {
        super(title, price, isbn, shelfCode);
        this.signedBy = signedBy;
    }

    String describe() {
        // `isbn` is protected in Book. We are in a different package, but we are a
        // subclass reading our own inherited copy, so this is allowed.
        return getTitle() + " [" + isbn + "] signed by " + signedBy;
    }

    void compareIsbnWith(Book other, SignedBook sibling) {
        System.out.println("sibling.isbn = " + sibling.isbn); // OK -- reference typed as SignedBook

        // System.out.println(other.isbn);                    // will NOT compile: reference typed as Book.
        // Outside the declaring package, a subclass reaches a protected member only
        // through a reference of its own type -- not through the parent type in general.

        System.out.println("other, via public API = " + other.getTitle());
    }
}
