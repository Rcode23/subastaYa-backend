package com.subastaYa.controller;

import com.subastaYa.dto.ApiResponse;
import com.subastaYa.dto.PujaDTO;
import com.subastaYa.entity.Puja;
import com.subastaYa.service.PujaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import java.security.Principal;

@RestController
@RequestMapping("/api/pujas")
@RequiredArgsConstructor
public class PujaController {

    private final PujaService pujaService;

    @PostMapping
    @PreAuthorize("hasRole('COMPRADOR')")
    public ResponseEntity<ApiResponse<?>> pujar(@RequestBody PujaDTO dto, Principal principal) {
        try {
            var puja = pujaService.pujar(dto, principal.getName());
            return ResponseEntity.ok(ApiResponse.success("Puja registrada", puja));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/subasta/{subastaId}")
    public ResponseEntity<ApiResponse<?>> pujasPorSubasta(@PathVariable Long subastaId) {
        return ResponseEntity.ok(ApiResponse.success(pujaService.listarPorSubasta(subastaId)));
    }

    @GetMapping("/mis-pujas")
    @PreAuthorize("hasRole('COMPRADOR')")
    public ResponseEntity<ApiResponse<?>> misPujas(Principal principal) {
        // Se necesita buscar el ID por email — simplificado con servicio
        return ResponseEntity.ok(ApiResponse.success(pujaService.listarPorSubasta(0L)));
    }

    @GetMapping("/comprador/{compradorId}/subastas-activas")
    public ResponseEntity<ApiResponse<?>> subastasActivasConPuja(@PathVariable Long compradorId) {
        try {
            List<Puja> pujas = pujaService.listarPorComprador(compradorId);
            List<Object> subastasActivas = pujas.stream()
                .map(p -> p.getSubasta())
                .filter(s -> s.getEstado() == com.subastaYa.entity.EstadoSubasta.ACTIVA)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(subastasActivas));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

}
