package com.subastaYa.dto;

import com.subastaYa.entity.Role;
import lombok.Data;

public class AuthDTO {

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class RegisterRequest {
        private String nombre;
        private String apellido;
        private String email;
        private String password;
        private String telefono;
        private String direccion;
        private Role rol;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String email;
        private String nombre;
        private String rol;

        public AuthResponse(String token, String email, String nombre, String rol) {
            this.token = token;
            this.email = email;
            this.nombre = nombre;
            this.rol = rol;
        }
    }
}
