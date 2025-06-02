package com.camisastime.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.camisastime.dto.LoginRequest;
import com.camisastime.dto.LoginResponse;
import com.camisastime.dto.UsuarioRequest;
import com.camisastime.model.Usuario;
import com.camisastime.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Usuario criarUsuario(UsuarioRequest request) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username já existe");
        }

        String senhaCriptografada = encoder.encode(request.getSenha());
        Usuario usuario = new Usuario(request.getUsername(), senhaCriptografada, request.getNome());
        
        return repository.save(usuario);
    }

    public LoginResponse login(LoginRequest request) {
        Optional<Usuario> usuarioOpt = repository.findByUsername(request.getUsername());
        
        if (usuarioOpt.isEmpty()) {
            return new LoginResponse(false, "Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        
        if (!usuario.isAtivo()) {
            return new LoginResponse(false, "Usuário inativo");
        }

        if (encoder.matches(request.getSenha(), usuario.getSenha())) {
            return new LoginResponse(true, "Login realizado com sucesso", usuario.getId(), usuario.getNome());
        } else {
            return new LoginResponse(false, "Senha incorreta");
        }
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void desativar(Long id) {
        Usuario usuario = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setAtivo(false);
        repository.save(usuario);
    }

    public Usuario alterarSenha(Long id, String novaSenha) {
        Usuario usuario = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        String senhaCriptografada = encoder.encode(novaSenha);
        usuario.setSenha(senhaCriptografada);
        
        return repository.save(usuario);
    }
}