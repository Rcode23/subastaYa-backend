package com.subastaYa.repository;

import com.subastaYa.entity.Puja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PujaRepository extends JpaRepository<Puja, Long> {

    @Query("SELECT p FROM Puja p WHERE p.subasta.id = :subastaId ORDER BY p.monto DESC")
    List<Puja> findBySubastaId(@Param("subastaId") Long subastaId);

    @Query("SELECT p FROM Puja p WHERE p.comprador.id = :compradorId ORDER BY p.fechaPuja DESC")
    List<Puja> findByCompradorId(@Param("compradorId") Long compradorId);

    @Query("SELECT p FROM Puja p WHERE p.subasta.id = :subastaId ORDER BY p.monto DESC")
    Optional<Puja> findTopBySubastaId(@Param("subastaId") Long subastaId);

    @Query("SELECT COUNT(p) FROM Puja p WHERE p.subasta.id = :subastaId")
    Long countBySubastaId(@Param("subastaId") Long subastaId);
}
