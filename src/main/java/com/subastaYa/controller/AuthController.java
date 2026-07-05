package com.subastaYa.controller;

import java.security.Principal;
import com.subastaYa.dto.ApiResponse;
import com.subastaYa.dto.AuthDTO;
import com.subastaYa.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody AuthDTO.RegisterRequest req) {
        try {
            var usuario = usuarioService.registrar(req);
            return ResponseEntity.ok(ApiResponse.success("Usuario registrado exitosamente", usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody AuthDTO.LoginRequest req) {
        try {
            var response = usuarioService.login(req);
            return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(Principal principal) {
        try {
            var usuario = usuarioService.buscarPorEmail(principal.getName());
            return ResponseEntity.ok(ApiResponse.success(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
