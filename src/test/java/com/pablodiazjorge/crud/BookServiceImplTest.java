package com.pablodiazjorge.crud;

import com.pablodiazjorge.crud.dto.BookWithImageDTO;
import com.pablodiazjorge.crud.entities.Book;
import com.pablodiazjorge.crud.entities.Image;
import com.pablodiazjorge.crud.repositories.BookRepository;
import com.pablodiazjorge.crud.services.BookServiceImpl;
import com.pablodiazjorge.crud.services.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Author");
        book.setPages(100);
        book.setPrice(10.0);
    }

    @Test
    void saveBook_success_noImage() throws IOException {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.saveBook(book, null);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getImage());
        verify(bookRepository).save(book);
        verify(imageService, never()).uploadImage(any());
    }

    @Test
    void saveBook_success_withImage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        Image image = new Image("test.jpg", "http://example.com/test.jpg", "test_id");
        when(imageService.uploadImage(file)).thenReturn(image);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.saveBook(book, file);

        assertNotNull(result);
        assertEquals(image, result.getImage());
        verify(imageService).uploadImage(file);
        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_success() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.updateBook(book);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookRepository).save(book);
    }

    @Test
    void updateBookImage_success_noPreviousImage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        Image newImage = new Image("new.jpg", "http://example.com/new.jpg", "new_id");
        when(imageService.uploadImage(file)).thenReturn(newImage);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.updateBookImage(file, book);

        assertNotNull(result);
        assertEquals(newImage, result.getImage());
        verify(imageService).uploadImage(file);
        verify(imageService, never()).deleteImage(any());
        verify(bookRepository).save(book);
    }

    @Test
    void updateBookImage_success_withPreviousImage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        Image oldImage = new Image("old.jpg", "http://example.com/old.jpg", "old_id");
        book.setImage(oldImage);
        Image newImage = new Image("new.jpg", "http://example.com/new.jpg", "new_id");
        when(imageService.uploadImage(file)).thenReturn(newImage);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.updateBookImage(file, book);

        assertNotNull(result);
        assertEquals(newImage, result.getImage());
        verify(imageService).deleteImage(oldImage);
        verify(imageService).uploadImage(file);
        verify(bookRepository).save(book);
    }

    @Test
    void getBooks_success() {
        BookWithImageDTO dto = new BookWithImageDTO(1L, "Test Book", "Author", 100, 10.0, null, null, null, null);
        Page<BookWithImageDTO> page = new PageImpl<>(Collections.singletonList(dto));
        when(bookRepository.findByTitleOrAuthorContainingIgnoreCase(eq(null), any(PageRequest.class))).thenReturn(page);

        Page<BookWithImageDTO> result = bookService.getBooks(PageRequest.of(0, 10, Sort.by("title")), null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
        verify(bookRepository).findByTitleOrAuthorContainingIgnoreCase(eq(null), any(PageRequest.class));
    }

    @Test
    void getBookById_success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookById_notFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookById(1L);

        assertFalse(result.isPresent());
        verify(bookRepository).findById(1L);
    }

    @Test
    void deleteBook_success_withImage() throws IOException {
        Image image = new Image("test.jpg", "http://example.com/test.jpg", "test_id");
        book.setImage(image);
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(book);

        verify(imageService).deleteImage(image);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_success_noImage() throws IOException {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(book);

        verify(imageService, never()).deleteImage(any());
        verify(bookRepository).deleteById(1L);
    }
}