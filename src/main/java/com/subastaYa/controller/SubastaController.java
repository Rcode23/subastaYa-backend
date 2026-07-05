package com.subastaYa.controller;

import com.subastaYa.dto.ApiResponse;
import com.subastaYa.dto.SubastaDTO;
import com.subastaYa.entity.EstadoSubasta;
import com.subastaYa.entity.Subasta;
import com.subastaYa.service.SubastaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/subastas")
@RequiredArgsConstructor
public class SubastaController {

    private final SubastaService subastaService;

    @GetMapping("/activas")
    public ResponseEntity<ApiResponse<?>> listarActivas() {
        return ResponseEntity.ok(ApiResponse.success(subastaService.listarActivas()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(subastaService.buscarPorId(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<?>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(subastaService.buscar(q)));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<ApiResponse<?>> porCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(ApiResponse.success(subastaService.listarPorCategoria(categoriaId)));
    }

    @GetMapping("/ganadas/{compradorId}")
    public ResponseEntity<ApiResponse<?>> subastaGanadas(@PathVariable Long compradorId) {
        return ResponseEntity.ok(ApiResponse.success(subastaService.listarGanadasPorComprador(compradorId)));
    }

    // Panel vendedor: mis subastas
    @GetMapping("/mis-subastas")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<?>> misSubastas(Principal principal) {
        var vendedor = subastaService.listarPorVendedor(
                Long.parseLong(principal.getName().replaceAll("\\D", ""))
        );
        // Mejor búsqueda por email directamente en el servicio
        return ResponseEntity.ok(ApiResponse.success(subastaService.listarActivas()));
    }

    @GetMapping("/buscar-avanzado")
    public ResponseEntity<ApiResponse<?>> buscarAvanzado(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String estado) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                subastaService.buscarConFiltros(keyword, categoriaId, estado)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<?>> topSubastas() {
        return ResponseEntity.ok(ApiResponse.success(
            subastaService.topSubastasPorPujas(5)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<?>> crear(@RequestBody SubastaDTO dto, Principal principal) {
        try {
            var subasta = subastaService.crear(dto, principal.getName());
            return ResponseEntity.ok(ApiResponse.success("Subasta creada", subasta));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<?>> actualizar(@PathVariable Long id,
                                                      @RequestBody SubastaDTO dto) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Subasta actualizada", subastaService.actualizar(id, dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<?>> cambiarEstado(@PathVariable Long id,
                                                         @RequestParam EstadoSubasta estado) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Estado actualizado", subastaService.cambiarEstado(id, estado)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<?>> cerrar(@PathVariable Long id,
                                                  @RequestParam(required = false) Long compradorGanadorId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Subasta cerrada", subastaService.cerrarSubasta(id, compradorGanadorId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/vendedor/{vendedorId}")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<?>> porVendedor(@PathVariable Long vendedorId) {
        return ResponseEntity.ok(ApiResponse.success(subastaService.listarPorVendedor(vendedorId)));
    }

    @PutMapping("/{id}/marcar-enviado")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<?>> marcarEnviado(
            @PathVariable Long id,
            @RequestParam String numeroGuia,
            Principal principal) {
        try {
            Subasta s = subastaService.buscarPorId(id);
            // Verificar que sea el vendedor de esa subasta o admin
            String rol = principal.getName();
            s.setEstado(EstadoSubasta.EN_CAMINO);
            s.setNumeroGuia(numeroGuia);
            return ResponseEntity.ok(ApiResponse.success("Marcado como enviado",
                subastaService.guardar(s)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/confirmar-recepcion")
    public ResponseEntity<ApiResponse<?>> confirmarRecepcion(@PathVariable Long id,
                                                            Principal principal) {
        try {
            Subasta s = subastaService.buscarPorId(id);
            String email = principal.getName();
            if (!s.getCompradorGanador().getEmail().equals(email)) {
                throw new RuntimeException("Solo el comprador puede confirmar la recepción");
            }
            s.setEstado(EstadoSubasta.ENTREGADA);
            return ResponseEntity.ok(ApiResponse.success("Recepción confirmada",
                subastaService.guardar(s)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    }
