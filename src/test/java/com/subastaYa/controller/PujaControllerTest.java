package com.subastaYa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subastaYa.config.TestSecurityConfig;
import com.subastaYa.dto.PujaDTO;
import com.subastaYa.entity.*;
import com.subastaYa.repository.UsuarioRepository;
import com.subastaYa.security.JwtUtil;
import com.subastaYa.service.PujaService;
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

@WebMvcTest(PujaController.class)
@Import(TestSecurityConfig.class)
class PujaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PujaService pujaService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private Puja crearPujaMock() {
        Usuario comprador = Usuario.builder()
                .id(1L).nombre("Rick").apellido("Torres")
                .email("comprador@test.com").rol(Role.COMPRADOR)
                .activo(true).fechaRegistro(LocalDateTime.now()).build();

        Subasta subasta = Subasta.builder()
                .id(1L).titulo("iPhone 15")
                .precioActual(new BigDecimal("2000"))
                .estado(EstadoSubasta.ACTIVA)
                .fechaFin(LocalDateTime.now().plusDays(1)).build();

        return Puja.builder()
                .id(1L).monto(new BigDecimal("2100"))
                .fechaPuja(LocalDateTime.now())
                .subasta(subasta).comprador(comprador).build();
    }

    @Test
    void listarPujasPorSubasta_DeberiaRetornarLista() throws Exception {
        when(pujaService.listarPorSubasta(1L)).thenReturn(List.of(crearPujaMock()));

        mockMvc.perform(get("/api/pujas/subasta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].monto").value(2100));
    }

    @Test
    @WithMockUser(roles = "COMPRADOR")
    void pujar_ConRolComprador_DeberiaRegistrarPuja() throws Exception {
        PujaDTO dto = new PujaDTO();
        dto.setSubastaId(1L);
        dto.setMonto(new BigDecimal("2100"));

        when(pujaService.pujar(any(), anyString())).thenReturn(crearPujaMock());

        mockMvc.perform(post("/api/pujas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.monto").value(2100));
    }

    @Test
    void pujar_SinDatos_DeberiaRetornarError() throws Exception {
        when(pujaService.pujar(any(), anyString()))
                .thenThrow(new RuntimeException("Datos inválidos"));

        mockMvc.perform(post("/api/pujas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}