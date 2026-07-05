package com.subastaYa.controller;

import com.subastaYa.dto.ApiResponse;
import com.subastaYa.dto.PagoDTO;
import com.subastaYa.entity.EstadoPago;
import com.subastaYa.entity.EstadoSubasta;
import com.subastaYa.entity.Pago;
import com.subastaYa.repository.PagoRepository;
import com.subastaYa.security.JwtUtil;
import com.subastaYa.service.PagoService;
import com.subastaYa.service.SubastaService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final PagoRepository pagoRepository;
    private final JwtUtil jwtUtil;
    private final SubastaService subastaService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> pagar(@RequestBody PagoDTO dto,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(token);
            var pago = pagoService.procesarPago(dto, email);
            return ResponseEntity.ok(ApiResponse.success("Pago procesado exitosamente", pago));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/subasta/{subastaId}")
    public ResponseEntity<ApiResponse<?>> porSubasta(@PathVariable Long subastaId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(pagoService.buscarPorSubasta(subastaId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> todos() {
        List<Pago> pagos = pagoRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(pagos));
    }

    @GetMapping("/comprador/{compradorId}")
    public ResponseEntity<ApiResponse<?>> porComprador(@PathVariable Long compradorId) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.listarPorComprador(compradorId)));
    }

    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> aprobar(@PathVariable Long id) {
        try {
            Pago pago = pagoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
            pago.setEstado(EstadoPago.COMPLETADO);
            pagoRepository.save(pago);
            // Actualizar subasta a PAGADA
            subastaService.cambiarEstado(pago.getSubasta().getId(), EstadoSubasta.PAGADA);
            return ResponseEntity.ok(ApiResponse.success("Pago aprobado", pago));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> rechazar(@PathVariable Long id) {
        try {
            Pago pago = pagoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
            pago.setEstado(EstadoPago.FALLIDO);
            pagoRepository.save(pago);
            return ResponseEntity.ok(ApiResponse.success("Pago rechazado", pago));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}