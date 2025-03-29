package com.pablodiazjorge.crud;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pablodiazjorge.crud.controllers.BookController;
import com.pablodiazjorge.crud.dto.BookWithImageDTO;
import com.pablodiazjorge.crud.entities.Book;
import com.pablodiazjorge.crud.exceptions.GlobalExceptionHandler;
import com.pablodiazjorge.crud.services.BookServiceImpl;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookServiceImpl bookService;

    @InjectMocks
    private BookController bookController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Book book;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Author");
        book.setPages(100);
        book.setPrice(10.0);

        file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());

        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void saveBook_success() throws Exception {
        when(bookService.saveBook(any(Book.class), any())).thenReturn(book);

        mockMvc.perform(multipart("/book")
                        .file(file)
                        .file(new MockMultipartFile("book", "", "application/json", objectMapper.writeValueAsBytes(book))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"));

        verify(bookService).saveBook(any(Book.class), any());
    }

    @Test
    void updateBookImage_success() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));
        when(bookService.updateBookImage(any(), any(Book.class))).thenReturn(book);

        MockHttpServletRequestBuilder builder = multipart("/book/1/image")
                .file(file)
                .with(request -> { request.setMethod("PUT"); return request; });

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookService).updateBookImage(any(), eq(book));
    }

    @Test
    void updateBookImage_notFound() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder builder = multipart("/book/1/image")
                .file(file)
                .with(request -> { request.setMethod("PUT"); return request; });

        mockMvc.perform(builder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(bookService, never()).updateBookImage(any(), any());
    }

    @Test
    void updateBook_success() throws Exception {
        when(bookService.updateBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(put("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookService).updateBook(any(Book.class));
    }

    @Test
    void updateBook_invalidData() throws Exception {
        Book invalidBook = new Book(); // Missing required fields

        mockMvc.perform(put("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verify(bookService, never()).updateBook(any());
    }

    @Test
    void getAllBooks_success() throws Exception {
        BookWithImageDTO dto = new BookWithImageDTO(1L, "Test Book", "Author", 100, 10.0, null, null, null, null);
        Page<BookWithImageDTO> page = new PageImpl<>(Collections.singletonList(dto), PageRequest.of(0, 10, Sort.by("title")), 1);
        when(bookService.getBooks(eq(PageRequest.of(0, 10, Sort.by("title"))), eq(null))).thenReturn(page);

        mockMvc.perform(get("/book?page=0&size=10&sortBy=title&sortDirection=ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Test Book"));

        verify(bookService).getBooks(eq(PageRequest.of(0, 10, Sort.by("title"))), eq(null));
    }

    @Test
    void getBookById_success() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookService).getBookById(1L);
    }

    @Test
    void getBookById_notFound() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/book/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(bookService).getBookById(1L);
    }

    @Test
    void deleteBook_success() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookService).deleteBook(book);

        mockMvc.perform(delete("/book/1"))
                .andExpect(status().isOk());

        verify(bookService).deleteBook(book);
    }

    @Test
    void deleteBook_notFound() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/book/1"))
                .andExpect(status().isNotFound());

        verify(bookService, never()).deleteBook(any());
    }
}