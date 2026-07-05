package com.subastaYa.repository;

import com.subastaYa.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    @Query("SELECT p FROM Pago p WHERE p.comprador.id = :compradorId ORDER BY p.fechaCreacion DESC")
    List<Pago> findByCompradorId(@Param("compradorId") Long compradorId);

    @Query("SELECT p FROM Pago p WHERE p.subasta.id = :subastaId ORDER BY p.fechaCreacion DESC")
    List<Pago> findBySubastaIdOrderByFechaDesc(@Param("subastaId") Long subastaId);

    @Query("SELECT p FROM Pago p ORDER BY p.fechaCreacion DESC")
    List<Pago> findAllOrderByFecha();
}