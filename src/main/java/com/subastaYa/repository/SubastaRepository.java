package com.subastaYa.repository;

import com.subastaYa.entity.EstadoSubasta;
import com.subastaYa.entity.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubastaRepository extends JpaRepository<Subasta, Long>, SubastaRepositoryCustom {

    @Query("SELECT s FROM Subasta s WHERE s.estado = :estado ORDER BY s.fechaFin ASC")
    List<Subasta> findByEstado(@Param("estado") EstadoSubasta estado);

    @Query("SELECT s FROM Subasta s WHERE s.vendedor.id = :vendedorId ORDER BY s.fechaCreacion DESC")
    List<Subasta> findByVendedorId(@Param("vendedorId") Long vendedorId);

    @Query("SELECT s FROM Subasta s WHERE s.categoria.id = :categoriaId AND s.estado = 'ACTIVA'")
    List<Subasta> findActivasByCategoria(@Param("categoriaId") Long categoriaId);

    @Query("SELECT s FROM Subasta s WHERE s.compradorGanador.id = :compradorId")
    List<Subasta> findGanadasByComprador(@Param("compradorId") Long compradorId);

    @Query("SELECT COUNT(s) FROM Subasta s WHERE s.estado = 'ACTIVA'")
    Long countActivas();

    @Query("SELECT s FROM Subasta s WHERE LOWER(s.titulo) LIKE LOWER(CONCAT('%', :keyword, '%')) AND s.estado = 'ACTIVA'")
    List<Subasta> buscarPorTitulo(@Param("keyword") String keyword);
}