package com.camisastime.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camisastime.model.Usuario;
import com.camisastime.security.JwtUtil;
import com.camisastime.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody Usuario usuario) {
        try {
            // Validação básica
            if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
                throw new RuntimeException("Username é obrigatório");
            }
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                throw new RuntimeException("Password é obrigatório");
            }

            // Autenticação
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            usuario.getUsername().trim(),
                            usuario.getPassword()
                    )
            );

            // Geração do token
            String token = jwtUtil.generateToken(usuario.getUsername().trim());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", usuario.getUsername().trim());
            response.put("message", "Login realizado com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Credenciais inválidas");
            errorResponse.put("message", "Username ou password incorretos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro interno");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody Usuario usuario) {
        try {
            // Validações adicionais
            if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
                throw new RuntimeException("Username é obrigatório");
            }
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                throw new RuntimeException("Password é obrigatório");
            }

            // Verificar se username já existe
            if (usuarioService.existsByUsername(usuario.getUsername().trim())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Username já existe");
                errorResponse.put("message", "Escolha outro username");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Limpar espaços em branco
            usuario.setUsername(usuario.getUsername().trim());
            
            // Salvar usuário
            Usuario usuarioSalvo = usuarioService.salvar(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", usuarioSalvo.getId());
            response.put("username", usuarioSalvo.getUsername());
            response.put("message", "Usuário registrado com sucesso");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao registrar usuário");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}