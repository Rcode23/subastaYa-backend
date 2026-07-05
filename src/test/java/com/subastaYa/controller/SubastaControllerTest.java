package com.subastaYa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subastaYa.config.TestSecurityConfig;
import com.subastaYa.dto.SubastaDTO;
import com.subastaYa.entity.*;
import com.subastaYa.repository.UsuarioRepository;
import com.subastaYa.security.JwtUtil;
import com.subastaYa.service.SubastaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubastaController.class)
@Import(TestSecurityConfig.class)
class SubastaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubastaService subastaService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private Subasta crearSubastaMock() {
        Usuario vendedor = Usuario.builder()
                .id(1L).nombre("Carlos").apellido("Lopez")
                .email("vendedor@test.com").rol(Role.VENDEDOR)
                .activo(true).fechaRegistro(LocalDateTime.now()).build();

        Categoria categoria = Categoria.builder()
                .id(1L).nombre("Electrónica").build();

        return Subasta.builder()
                .id(1L).titulo("iPhone 15 Pro")
                .descripcion("Teléfono en perfecto estado")
                .precioBase(new BigDecimal("2000"))
                .precioActual(new BigDecimal("2000"))
                .pujaMinima(new BigDecimal("50"))
                .estado(EstadoSubasta.ACTIVA)
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusDays(1))
                .fechaCreacion(LocalDateTime.now())
                .vendedor(vendedor).categoria(categoria).build();
    }

    @Test
    void listarActivas_DeberiaRetornarListaDeSubastas() throws Exception {
        when(subastaService.listarActivas()).thenReturn(List.of(crearSubastaMock()));

        mockMvc.perform(get("/api/subastas/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].titulo").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.data[0].estado").value("ACTIVA"));
    }

    @Test
    void buscarPorId_SubastaExistente_DeberiaRetornarSubasta() throws Exception {
        when(subastaService.buscarPorId(1L)).thenReturn(crearSubastaMock());

        mockMvc.perform(get("/api/subastas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.titulo").value("iPhone 15 Pro"));
    }

    @Test
    void buscarPorId_SubastaNoExistente_DeberiaRetornarError() throws Exception {
        when(subastaService.buscarPorId(999L))
                .thenThrow(new RuntimeException("Subasta no encontrada"));

        mockMvc.perform(get("/api/subastas/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Subasta no encontrada"));
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void crear_ConRolVendedor_DeberiaCrearSubasta() throws Exception {
        SubastaDTO dto = new SubastaDTO();
        dto.setTitulo("iPhone 15 Pro");
        dto.setPrecioBase(new BigDecimal("2000"));
        dto.setPujaMinima(new BigDecimal("50"));
        dto.setCategoriaId(1L);
        dto.setFechaInicio(LocalDateTime.now());
        dto.setFechaFin(LocalDateTime.now().plusDays(1));

        when(subastaService.crear(any(), anyString())).thenReturn(crearSubastaMock());

        mockMvc.perform(post("/api/subastas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.titulo").value("iPhone 15 Pro"));
    }

    @Test
    void crear_ConDatosVacios_DeberiaRetornarError() throws Exception {
        when(subastaService.crear(any(), anyString()))
                .thenThrow(new RuntimeException("Datos inválidos"));

        mockMvc.perform(post("/api/subastas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}