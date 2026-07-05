package com.subastaYa.controller;

import com.subastaYa.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final String uploadDir = "uploads/";
    private static final List<String> TIPOS_PERMITIDOS = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final List<String> EXTENSIONES_PERMITIDAS = List.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    @PostMapping("/imagen")
    public ResponseEntity<ApiResponse<?>> subirImagen(@RequestParam("file") MultipartFile file) {
        // Validar que no esté vacío
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("El archivo está vacío"));
        }

        // Validar tipo MIME
        String contentType = file.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType)) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Tipo de archivo no permitido. Solo se aceptan: JPG, PNG, GIF, WEBP"));
        }

        // Validar extensión
        String nombreOriginal = file.getOriginalFilename();
        if (nombreOriginal == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Nombre de archivo inválido"));
        }
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".")).toLowerCase();
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Extensión no permitida. Solo: .jpg, .jpeg, .png, .gif, .webp"));
        }

        // Validar tamaño (máx 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(ApiResponse.error("El archivo supera el límite de 10MB"));
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String nombreArchivo = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(nombreArchivo);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String urlImagen = "http://localhost:8080/uploads/" + nombreArchivo;
            return ResponseEntity.ok(ApiResponse.success("Imagen subida", urlImagen));

        } catch (IOException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error al subir imagen"));
        }
    }
}