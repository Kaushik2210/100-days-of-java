package day18;

import day18.library.Book; // Book lives in a different package, so it must be imported

/*
 * Compile and run from the src/ directory:
 *   javac day18/PackagesDemo.java day18/library/Book.java
 *   java day18.PackagesDemo
 */
public class PackagesDemo {

    public static void main(String[] args) {
        Book book = new Book("Effective Java", 42.50);

        System.out.println("title = " + book.getTitle());
        System.out.println("price = " + book.getPrice());

        // book.title;  // will not compile -- title is private to Book

        // A fully-qualified name works with no import at all:
        java.util.List<String> shelf = new java.util.ArrayList<>();
        shelf.add(book.getTitle());
        System.out.println("shelf = " + shelf);
    }
}
