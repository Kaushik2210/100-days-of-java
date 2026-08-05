package day18.library;

public class Book {

    private final String title;    // private          -- only this class can touch the field
    private final double price;
    protected final String isbn;   // protected        -- this package, plus subclasses anywhere
    final String shelfCode;        // package-private  -- only classes in day18.library

    public Book(String title, double price, String isbn, String shelfCode) {
        this.title = title;
        this.price = price;
        this.isbn = isbn;
        this.shelfCode = shelfCode;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    // Public API that happens to be implemented with a package-private class.
    // Callers outside day18.library can use this without ever seeing InventoryRecord.
    public String inventoryLine() {
        return new InventoryRecord(this).describe();
    }
}

// No modifier on a top-level class means package-private: this type cannot be
// imported, named, or instantiated from outside day18.library.
class InventoryRecord {

    private final Book book;

    InventoryRecord(Book book) {
        this.book = book;
    }

    String describe() {
        // Same package, so both the package-private field and the protected one are reachable.
        return book.shelfCode + " / " + book.isbn;
    }
}
