package com.subastaYa.service;

import com.subastaYa.dto.SubastaDTO;
import com.subastaYa.entity.*;
import com.subastaYa.repository.PujaRepository;
import com.subastaYa.repository.SubastaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubastaService {

    private final SubastaRepository subastaRepository;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;
    private final PujaRepository pujaRepository;

    public List<Subasta> listarActivas() {
        return subastaRepository.findByEstado(EstadoSubasta.ACTIVA);
    }

    public List<Subasta> listarTodas() {
        return subastaRepository.findAll();
    }

    public Subasta buscarPorId(Long id) {
        return subastaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subasta no encontrada"));
    }

    public List<Subasta> listarPorVendedor(Long vendedorId) {
        return subastaRepository.findByVendedorId(vendedorId);
    }

    public List<Subasta> listarGanadasPorComprador(Long compradorId) {
        return subastaRepository.findGanadasByComprador(compradorId);
    }

    public List<Subasta> buscar(String keyword) {
        return subastaRepository.buscarPorTitulo(keyword);
    }

    public List<Subasta> listarPorCategoria(Long categoriaId) {
        return subastaRepository.findActivasByCategoria(categoriaId);
    }

    public List<Subasta> buscarConFiltros(String keyword, Long categoriaId, String estado) {
        return subastaRepository.buscarConFiltros(keyword, categoriaId, estado);
    }

    public List<Subasta> topSubastasPorPujas(int limit) {
        return subastaRepository.findTopSubastasPorPujas(limit);
    }

    @Transactional
    public Subasta crear(SubastaDTO dto, String emailVendedor) {
        Usuario vendedor = usuarioService.buscarPorEmail(emailVendedor);
        Categoria categoria = categoriaService.buscarPorId(dto.getCategoriaId());

        Subasta subasta = Subasta.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .precioBase(dto.getPrecioBase())
                .precioActual(dto.getPrecioBase())
                .pujaMinima(dto.getPujaMinima())
                .imagenUrl(dto.getImagenUrl())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .vendedor(vendedor)
                .categoria(categoria)
                .estado(EstadoSubasta.ACTIVA)
                .build();

        return subastaRepository.save(subasta);
    }

    @Transactional
    public Subasta actualizar(Long id, SubastaDTO dto) {
        Subasta s = buscarPorId(id);
        Categoria categoria = categoriaService.buscarPorId(dto.getCategoriaId());
        s.setTitulo(dto.getTitulo());
        s.setDescripcion(dto.getDescripcion());
        s.setPujaMinima(dto.getPujaMinima());
        s.setImagenUrl(dto.getImagenUrl());
        s.setFechaFin(dto.getFechaFin());
        s.setCategoria(categoria);
        return subastaRepository.save(s);
    }

    @Transactional
    public Subasta cambiarEstado(Long id, EstadoSubasta nuevoEstado) {
        Subasta s = buscarPorId(id);
        s.setEstado(nuevoEstado);
        return subastaRepository.save(s);
    }

    @Transactional
    public Subasta cerrarSubasta(Long id, Long compradorGanadorId) {
        Subasta s = buscarPorId(id);
        if (compradorGanadorId != null) {
            Usuario ganador = usuarioService.buscarPorId(compradorGanadorId);
            s.setCompradorGanador(ganador);
        }
        s.setEstado(EstadoSubasta.CERRADA);
        return subastaRepository.save(s);
    }

    // Cierre automático cada minuto
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cerrarSubastasVencidas() {
        List<Subasta> activas = subastaRepository.findByEstado(EstadoSubasta.ACTIVA);
        LocalDateTime ahora = LocalDateTime.now();

        for (Subasta s : activas) {
            if (s.getFechaFin().isBefore(ahora)) {
                // Buscar puja más alta
                List<Puja> pujas = pujaRepository.findBySubastaId(s.getId());
                if (!pujas.isEmpty()) {
                    // La primera es la más alta (ordenadas DESC por monto)
                    Usuario ganador = pujas.get(0).getComprador();
                    s.setCompradorGanador(ganador);
                    s.setEstado(EstadoSubasta.CERRADA);
                } else {
                    s.setEstado(EstadoSubasta.CANCELADA);
                }
                subastaRepository.save(s);
            }
        }
    }

    public Long contarActivas() {
        return subastaRepository.countActivas();
    }

    @Transactional
    public Subasta guardar(Subasta s) {
        return subastaRepository.save(s);
    }
}