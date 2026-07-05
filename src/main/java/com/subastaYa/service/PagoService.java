package com.subastaYa.service;

import com.subastaYa.dto.PagoDTO;
import com.subastaYa.entity.*;
import com.subastaYa.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final SubastaService subastaService;
    private final UsuarioService usuarioService;

    @Transactional
    public Pago procesarPago(PagoDTO dto, String emailComprador) {
        Subasta subasta = subastaService.buscarPorId(dto.getSubastaId());
        Usuario comprador = usuarioService.buscarPorEmail(emailComprador);

        // Validaciones
        if (subasta.getEstado() != EstadoSubasta.CERRADA) {
            throw new RuntimeException("Solo se puede pagar subastas cerradas");
        }
        if (subasta.getCompradorGanador() == null ||
            !subasta.getCompradorGanador().getId().equals(comprador.getId())) {
            throw new RuntimeException("Solo el comprador ganador puede realizar el pago");
        }

        // Reemplaza el bloque de verificación de pago existente
        List<Pago> pagosExistentes = pagoRepository.findBySubastaIdOrderByFechaDesc(dto.getSubastaId());
        if (!pagosExistentes.isEmpty()) {
            Pago ultimoPago = pagosExistentes.get(0);
            if (ultimoPago.getEstado() != EstadoPago.FALLIDO) {
                throw new RuntimeException("Esta subasta ya tiene un pago registrado");
            }
        }

        Pago pago = Pago.builder()
            .subasta(subasta)
            .comprador(comprador)
            .monto(subasta.getPrecioActual())
            .metodoPago(dto.getMetodoPago())
            .codigoTransaccion(dto.getCodigoTransaccion())
            .urlComprobante(dto.getUrlComprobante())
            .estado(EstadoPago.COMPLETADO)
            .fechaPago(LocalDateTime.now())
            .build();

        Pago pagoGuardado = pagoRepository.save(pago);

        return pagoGuardado;
    }

    public List<Pago> listarPorComprador(Long compradorId) {
        return pagoRepository.findByCompradorId(compradorId);
    }

    public Pago buscarPorSubasta(Long subastaId) {
        List<Pago> pagos = pagoRepository.findBySubastaIdOrderByFechaDesc(subastaId);
        if (pagos.isEmpty()) throw new RuntimeException("No hay pago para esta subasta");
        return pagos.get(0);
    }
}