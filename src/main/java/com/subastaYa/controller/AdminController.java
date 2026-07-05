package com.subastaYa.controller;

import com.subastaYa.dto.ApiResponse;
import com.subastaYa.entity.Role;
import com.subastaYa.service.SubastaService;
import com.subastaYa.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioService usuarioService;
    private final SubastaService subastaService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<?>> dashboard() {
        var stats = Map.of(
                "totalUsuarios", usuarioService.contarActivos(),
                "subastaActivas", subastaService.contarActivas(),
                "totalSubastas", subastaService.listarTodas().size(),
                "vendedores", usuarioService.listarPorRol(Role.VENDEDOR).size(),
                "compradores", usuarioService.listarPorRol(Role.COMPRADOR).size()
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/usuarios")
    public ResponseEntity<ApiResponse<?>> listarUsuarios() {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.listarTodos()));
    }

    @PutMapping("/usuarios/{id}/toggle")
    public ResponseEntity<ApiResponse<?>> toggleUsuario(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Estado actualizado", usuarioService.toggleActivo(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/subastas")
    public ResponseEntity<ApiResponse<?>> listarTodasSubastas() {
        return ResponseEntity.ok(ApiResponse.success(subastaService.listarTodas()));
    }
}
