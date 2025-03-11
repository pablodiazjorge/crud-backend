package com.pablodiazjorge.crud.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pablodiazjorge.crud.exceptions.BookNotFoundException;
import com.pablodiazjorge.crud.exceptions.BadRequestException;
import com.pablodiazjorge.crud.entities.Book;
import com.pablodiazjorge.crud.dto.BookWithImageDTO;
import com.pablodiazjorge.crud.services.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.Optional;


@RestController
@RequestMapping("/book")
@CrossOrigin("http://localhost:4200/")
public class BookController {

    @Autowired
    BookServiceImpl bookServiceImpl;

    /**
     * Saves a new book with an associated file.
     *
     * @param book   The book object containing details to be saved.
     * @param file   The multipart file associated with the book.
     * @return ResponseEntity<Book> - The saved book object with HTTP status OK on success,
     *                                or BAD_REQUEST if an exception occurs.
     */
    @PostMapping
    public ResponseEntity<Book> saveBook(@RequestPart("book") Book book, @RequestPart("file") MultipartFile file) {
        try {
            Book savedBook = bookServiceImpl.saveBook(book, file);
            return new ResponseEntity<>(savedBook, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid book JSON format");
        } catch (IOException e) {
            throw new BadRequestException("Error processing file");
        }
    }


    /**
     * Updates the image of a book by its ID.
     *
     * @param id     The ID of the book to update.
     * @param file   The multipart file representing the new image.
     * @return ResponseEntity<Book> - The updated book object with HTTP status OK on success,
     *                                or NOT_FOUND if the book does not exist.
     * @throws IOException If an I/O error occurs while processing the file.
     */
    @PutMapping("/{id}/image")
    public ResponseEntity<Book> updateBookImage(@PathVariable Long id, @RequestPart("file") MultipartFile file) throws IOException {
        Book book = bookServiceImpl.getBookById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));

        Book updatedBook = bookServiceImpl.updateBookImage(file, book);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    /**
     * Updates an existing book.
     *
     * @param book The book object with updated details.
     * @return ResponseEntity<Book> - The updated book object with HTTP status OK on success,
     *                                or BAD_REQUEST if an exception occurs.
     */
    @PutMapping
    public ResponseEntity<Book> updateBook(@RequestBody Book book){
        if (book.getId() == null || book.getTitle() == null || book.getAuthor() == null || book.getPages() < 0 || book.getPrice() < 0) {
            throw new BadRequestException("Invalid book data provided for update");
        }
        Book updatedBook = bookServiceImpl.updateBook(book);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    /**
     * Retrieves a paginated list of all books.
     *
     * @param page The page number (default is 0).
     * @param size The number of items per page (default is 10).
     * @return ResponseEntity<Page<Book>> - A paginated list of books with HTTP status OK.
     */
    @GetMapping
    public ResponseEntity<Page<BookWithImageDTO>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BookWithImageDTO> bookPage = bookServiceImpl.getBooks(pageable, query);
            return new ResponseEntity<>(bookPage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid page or size parameters: " + e.getMessage());
        }
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param id The ID of the book to retrieve.
     * @return ResponseEntity<Book> - The book object with HTTP status OK if found,
     *                                or NOT_FOUND if the book does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookServiceImpl.getBookById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    /**
     * Deletes a book by its ID.
     *
     * @param id The ID of the book to delete.
     * @return ResponseEntity<Void> - HTTP status OK if the book is deleted successfully,
     *                                or NOT_FOUND if the book does not exist.
     * @throws IOException If an I/O error occurs during deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) throws IOException {
        Optional<Book> book = bookServiceImpl.getBookById(id);
        if (book.isPresent()){
            bookServiceImpl.deleteBook(book.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}