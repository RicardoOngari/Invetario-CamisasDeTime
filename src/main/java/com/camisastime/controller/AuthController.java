package com.camisastime.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camisastime.dto.LoginRequest;
import com.camisastime.dto.LoginResponse;
import com.camisastime.dto.UsuarioRequest;
import com.camisastime.model.Usuario;
import com.camisastime.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService service;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return service.login(request);
    }

    @PostMapping("/cadastrar")
    public Usuario cadastrar(@RequestBody UsuarioRequest request) {
        return service.criarUsuario(request);
    }

    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return service.listar();
    }

    @GetMapping("/usuarios/{id}")
    public Optional<Usuario> buscarUsuario(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/usuarios/{id}/desativar")
    public void desativarUsuario(@PathVariable Long id) {
        service.desativar(id);
    }

    @PutMapping("/usuarios/{id}/alterar-senha")
    public Usuario alterarSenha(@PathVariable Long id, @RequestBody String novaSenha) {
        return service.alterarSenha(id, novaSenha);
    }
}