package com.subastaYa.service;

import com.subastaYa.dto.AuthDTO;
import com.subastaYa.entity.Role;
import com.subastaYa.entity.Usuario;
import com.subastaYa.repository.UsuarioRepository;
import com.subastaYa.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public Usuario registrar(AuthDTO.RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        Usuario usuario = Usuario.builder()
                .nombre(req.getNombre())
                .apellido(req.getApellido())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .telefono(req.getTelefono())
                .direccion(req.getDireccion())
                .rol(req.getRol() != null ? req.getRol() : Role.COMPRADOR)
                .activo(true)
                .build();
        return usuarioRepository.save(usuario);
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest req) {
        Usuario usuario = usuarioRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(req.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }
        if (!usuario.isActivo()) {
            throw new RuntimeException("Usuario desactivado");
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().name());
        return new AuthDTO.AuthResponse(token, usuario.getEmail(),
                usuario.getNombre() + " " + usuario.getApellido(),
                usuario.getRol().name());
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarPorRol(Role rol) {
        return usuarioRepository.findByRol(rol);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional
    public Usuario toggleActivo(Long id) {
        Usuario u = buscarPorId(id);
        u.setActivo(!u.isActivo());
        return usuarioRepository.save(u);
    }

    public Long contarActivos() {
        return usuarioRepository.countActivos();
    }
}
