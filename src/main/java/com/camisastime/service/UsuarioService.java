package com.camisastime.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.camisastime.model.Usuario;
import com.camisastime.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    /**
     * Salva um novo usuário com senha criptografada
     */
    public Usuario salvar(Usuario usuario) {
        try {
            // Validações básicas
            if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
                throw new RuntimeException("Username é obrigatório");
            }
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                throw new RuntimeException("Password é obrigatório");
            }

            // Verificar se username já existe
            if (existsByUsername(usuario.getUsername().trim())) {
                throw new RuntimeException("Username já existe: " + usuario.getUsername());
            }

            // Limpar espaços em branco e criptografar senha
            usuario.setUsername(usuario.getUsername().trim());
            usuario.setPassword(encoder.encode(usuario.getPassword()));
            
            return repository.save(usuario);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar usuário: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se já existe um usuário com o username informado
     */
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return repository.findByUsername(username.trim()).isPresent();
    }

    /**
     * Busca usuário por username
     */
    public Optional<Usuario> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return repository.findByUsername(username.trim());
    }

    /**
     * Implementação do UserDetailsService para autenticação
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Username não pode ser vazio");
        }

        Usuario usuario = repository.findByUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles("USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * Busca usuário por ID
     */
    public Optional<Usuario> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id);
    }

    /**
     * Atualiza senha do usuário
     */
    public Usuario atualizarSenha(Long id, String novaSenha) {
        if (id == null) {
            throw new RuntimeException("ID do usuário é obrigatório");
        }
        if (novaSenha == null || novaSenha.trim().isEmpty()) {
            throw new RuntimeException("Nova senha é obrigatória");
        }

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setPassword(encoder.encode(novaSenha));
        return repository.save(usuario);
    }

    /**
     * Valida se a senha informada confere com a senha do usuário
     */
    public boolean validarSenha(String username, String senhaInformada) {
        if (username == null || username.trim().isEmpty() || senhaInformada == null) {
            return false;
        }

        Optional<Usuario> usuario = findByUsername(username.trim());
        if (usuario.isPresent()) {
            return encoder.matches(senhaInformada, usuario.get().getPassword());
        }
        return false;
    }
}