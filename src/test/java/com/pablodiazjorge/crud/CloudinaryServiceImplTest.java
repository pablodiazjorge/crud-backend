package com.pablodiazjorge.crud;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import com.pablodiazjorge.crud.services.CloudinaryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CloudinaryServiceImplTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryServiceImpl cloudinaryService;

    @Test
    void upload_success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "http://cloudinary.com/test.jpg");
        uploadResult.put("public_id", "test_id");
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(java.io.File.class), eq(ObjectUtils.emptyMap()))).thenReturn(uploadResult);

        Map result = cloudinaryService.upload(file);

        assertNotNull(result);
        assertEquals("http://cloudinary.com/test.jpg", result.get("url"));
        assertEquals("test_id", result.get("public_id"));
        verify(uploader).upload(any(java.io.File.class), eq(ObjectUtils.emptyMap()));
    }

    @Test
    void upload_emptyFile_throwsException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThrows(IOException.class, () -> cloudinaryService.upload(file));
        verify(cloudinary, never()).uploader();
    }

    @Test
    void upload_nullFile_throwsException() {
        assertThrows(IOException.class, () -> cloudinaryService.upload(null));
        verify(cloudinary, never()).uploader();
    }

    @Test
    void upload_cloudinaryFailure_throwsException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(java.io.File.class), eq(ObjectUtils.emptyMap())))
                .thenThrow(new IOException("Upload failed"));

        assertThrows(IOException.class, () -> cloudinaryService.upload(file));
        verify(uploader).upload(any(java.io.File.class), eq(ObjectUtils.emptyMap()));
    }

    @Test
    void delete_success() throws IOException {
        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("result", "ok");
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(eq("test_id"), eq(ObjectUtils.emptyMap()))).thenReturn(deleteResult);

        Map result = cloudinaryService.delete("test_id");

        assertNotNull(result);
        assertEquals("ok", result.get("result"));
        verify(uploader).destroy(eq("test_id"), eq(ObjectUtils.emptyMap()));
    }

    @Test
    void delete_cloudinaryFailure_throwsException() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(eq("test_id"), eq(ObjectUtils.emptyMap())))
                .thenThrow(new IOException("Delete failed"));

        assertThrows(IOException.class, () -> cloudinaryService.delete("test_id"));
        verify(uploader).destroy(eq("test_id"), eq(ObjectUtils.emptyMap()));
    }

    @Test
    void delete_nullPublicId_proceeds() throws IOException {
        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("result", "ok");
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(eq(null), eq(ObjectUtils.emptyMap()))).thenReturn(deleteResult);

        Map result = cloudinaryService.delete(null);

        assertNotNull(result);
        assertEquals("ok", result.get("result"));
        verify(uploader).destroy(eq(null), eq(ObjectUtils.emptyMap()));
    }

    @Test
    void delete_emptyPublicId_proceeds() throws IOException {
        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("result", "ok");
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(eq(""), eq(ObjectUtils.emptyMap()))).thenReturn(deleteResult);

        Map result = cloudinaryService.delete("");

        assertNotNull(result);
        assertEquals("ok", result.get("result"));
        verify(uploader).destroy(eq(""), eq(ObjectUtils.emptyMap()));
    }
}