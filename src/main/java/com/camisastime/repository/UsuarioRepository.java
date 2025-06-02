package com.camisastime.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.camisastime.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
}