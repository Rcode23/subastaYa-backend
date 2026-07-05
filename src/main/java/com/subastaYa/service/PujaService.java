package com.subastaYa.service;

import com.subastaYa.dto.PujaDTO;
import com.subastaYa.entity.*;
import com.subastaYa.repository.PujaRepository;
import com.subastaYa.repository.SubastaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PujaService {

    private final PujaRepository pujaRepository;
    private final SubastaService subastaService;
    private final UsuarioService usuarioService;
    private final SubastaRepository subastaRepository;

    // Cooldown: guarda último tiempo de puja por usuario+subasta
    private final Map<String, LocalDateTime> ultimaPuja = new ConcurrentHashMap<>();
    private static final int COOLDOWN_SEGUNDOS = 30;

    @Transactional
    public Puja pujar(PujaDTO dto, String emailComprador) {
        Subasta subasta = subastaService.buscarPorId(dto.getSubastaId());
        Usuario comprador = usuarioService.buscarPorEmail(emailComprador);

        if (subasta.getEstado() != EstadoSubasta.ACTIVA) {
            throw new RuntimeException("La subasta no está activa");
        }

        if (subasta.getFechaFin().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La subasta ya venció");
        }

        // Validar cooldown
        String key = emailComprador + "_" + dto.getSubastaId();
        LocalDateTime ultima = ultimaPuja.get(key);
        if (ultima != null) {
            long segundosTranscurridos = java.time.Duration.between(ultima, LocalDateTime.now()).getSeconds();
            if (segundosTranscurridos < COOLDOWN_SEGUNDOS) {
                long espera = COOLDOWN_SEGUNDOS - segundosTranscurridos;
                throw new RuntimeException("Debes esperar " + espera + " segundos antes de volver a pujar");
            }
        }

        if (dto.getMonto().compareTo(subasta.getPrecioActual().add(subasta.getPujaMinima())) < 0) {
            throw new RuntimeException("La puja debe ser al menos S/ " +
                    subasta.getPrecioActual().add(subasta.getPujaMinima()));
        }

        subasta.setPrecioActual(dto.getMonto());
        subastaRepository.save(subasta);

        // Registrar tiempo de puja
        ultimaPuja.put(key, LocalDateTime.now());

        Puja puja = Puja.builder()
                .monto(dto.getMonto())
                .subasta(subasta)
                .comprador(comprador)
                .build();

        return pujaRepository.save(puja);
    }

    public List<Puja> listarPorSubasta(Long subastaId) {
        return pujaRepository.findBySubastaId(subastaId);
    }

    public List<Puja> listarPorComprador(Long compradorId) {
        return pujaRepository.findByCompradorId(compradorId);
    }

    public Long contarPujasPorSubasta(Long subastaId) {
        return pujaRepository.countBySubastaId(subastaId);
    }
}