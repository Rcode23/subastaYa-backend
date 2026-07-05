package com.subastaYa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subastaYa.config.TestSecurityConfig;
import com.subastaYa.dto.AuthDTO;
import com.subastaYa.entity.Role;
import com.subastaYa.entity.Usuario;
import com.subastaYa.repository.UsuarioRepository;
import com.subastaYa.security.JwtUtil;
import com.subastaYa.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    void register_DeberiaRetornarUsuarioCreado() throws Exception {
        AuthDTO.RegisterRequest req = new AuthDTO.RegisterRequest();
        req.setNombre("Rick");
        req.setApellido("Torres");
        req.setEmail("rick@test.com");
        req.setPassword("Test123!");
        req.setTelefono("999888777");
        req.setDireccion("Trujillo");
        req.setRol(Role.COMPRADOR);

        Usuario usuarioMock = Usuario.builder()
                .id(1L).nombre("Rick").apellido("Torres")
                .email("rick@test.com").password("hashed")
                .telefono("999888777").direccion("Trujillo")
                .rol(Role.COMPRADOR).activo(true)
                .fechaRegistro(LocalDateTime.now()).build();

        when(usuarioService.registrar(any())).thenReturn(usuarioMock);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.email").value("rick@test.com"));
    }

    @Test
    void login_ConCredencialesCorrectas_DeberiaRetornarToken() throws Exception {
        AuthDTO.LoginRequest req = new AuthDTO.LoginRequest();
        req.setEmail("rick@test.com");
        req.setPassword("Test123!");

        AuthDTO.AuthResponse responseMock = new AuthDTO.AuthResponse(
                "token.jwt.mock", "rick@test.com", "Rick Torres", "COMPRADOR");

        when(usuarioService.login(any())).thenReturn(responseMock);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.token").value("token.jwt.mock"));
    }

    @Test
    void login_ConCredencialesIncorrectas_DeberiaRetornarError() throws Exception {
        AuthDTO.LoginRequest req = new AuthDTO.LoginRequest();
        req.setEmail("rick@test.com");
        req.setPassword("wrong");

        when(usuarioService.login(any()))
                .thenThrow(new RuntimeException("Credenciales incorrectas"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Credenciales incorrectas"));
    }
}