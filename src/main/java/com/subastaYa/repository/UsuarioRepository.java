package com.subastaYa.repository;

import com.subastaYa.entity.Role;
import com.subastaYa.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol")
    List<Usuario> findByRol(Role rol);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.activo = true")
    Long countActivos();
}
