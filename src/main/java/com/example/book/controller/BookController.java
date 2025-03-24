package com.example.book.controller;

import com.example.book.model.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Book entities.
 * Provides endpoints for creating, reading, updating, and deleting books.
 */
@RestController
@RequestMapping("/books")
public class BookController {

    /**
     * In-memory data store for books.
     */
    private final Map<Integer, Book> bookMap = new HashMap<>();

    /**
     * Auto-increment counter to generate unique IDs for books.
     */
    private int nextId = 1;

    /**
     * Creates a new book.
     *
     * @param book The book details sent in the request body.
     * @return A ResponseEntity containing the created book with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        book.setId(nextId++);
        bookMap.put(book.getId(), book);
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    /**
     * Retrieves all books.
     *
     * @return A ResponseEntity containing a list of all books with HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Book>> getBooks() {
        return new ResponseEntity<>(new ArrayList<>(bookMap.values()), HttpStatus.OK);
    }

    /**
     * Retrieves a single book by its ID.
     *
     * @param id The ID of the book to retrieve.
     * @return A ResponseEntity containing the book if found,
     * or an error message with HTTP status 404 (Not Found) if not.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable int id) {
        Book book = bookMap.get(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }

    /**
     * Updates an existing book.
     *
     * @param id          The ID of the book to update.
     * @param updatedBook The updated book details sent in the request body.
     * @return A ResponseEntity containing the updated book with HTTP status 200 (OK),
     * or an error message with HTTP status 404 (Not Found) if the book does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable int id, @RequestBody Book updatedBook) {
        Book book = bookMap.get(id);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setContent(updatedBook.getContent());
        bookMap.put(id, book);
        return ResponseEntity.ok(book);
    }

    /**
     * Deletes a book by its ID.
     *
     * @param id The ID of the book to delete.
     * @return A ResponseEntity with a success message and HTTP status 200 (OK)
     * if the book was deleted, or an error message with HTTP status 404 (Not Found) if it was not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable int id) {
        Book book = bookMap.remove(id);
        if (book != null) {
            return ResponseEntity.ok("Book deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }
}
