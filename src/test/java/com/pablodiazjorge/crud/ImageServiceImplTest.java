package com.pablodiazjorge.crud;

import com.pablodiazjorge.crud.entities.Image;
import com.pablodiazjorge.crud.repositories.ImageRepository;
import com.pablodiazjorge.crud.services.CloudinaryService;
import com.pablodiazjorge.crud.services.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageServiceImpl imageService;

    private Image image;

    @BeforeEach
    void setUp() {
        image = new Image("test.jpg", "http://example.com/test.jpg", "test_id");
        image.setId(1L);
    }

    @Test
    void uploadImage_success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(cloudinaryService.upload(file)).thenReturn(Map.of("url", "http://example.com/test.jpg", "public_id", "test_id"));
        when(imageRepository.save(any(Image.class))).thenReturn(image);

        Image result = imageService.uploadImage(file);

        assertNotNull(result);
        assertEquals("test.jpg", result.getName());
        assertEquals("http://example.com/test.jpg", result.getImageUrl());
        assertEquals("test_id", result.getImageId());
        verify(cloudinaryService).upload(file);
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void uploadImage_cloudinaryFailure() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(cloudinaryService.upload(file)).thenThrow(new IOException("Upload failed"));

        assertThrows(IOException.class, () -> imageService.uploadImage(file));
        verify(cloudinaryService).upload(file);
        verify(imageRepository, never()).save(any());
    }

    @Test
    void deleteImage_success() throws IOException {
        when(cloudinaryService.delete("test_id")).thenReturn(Map.of("result", "ok"));
        doNothing().when(imageRepository).deleteById(1L);

        imageService.deleteImage(image);

        verify(cloudinaryService).delete("test_id");
        verify(imageRepository).deleteById(1L);
    }

    @Test
    void deleteImage_cloudinaryFailure() throws IOException {
        when(cloudinaryService.delete("test_id")).thenThrow(new IOException("Delete failed"));

        assertThrows(IOException.class, () -> imageService.deleteImage(image));
        verify(cloudinaryService).delete("test_id");
        verify(imageRepository, never()).deleteById(any());
    }
}