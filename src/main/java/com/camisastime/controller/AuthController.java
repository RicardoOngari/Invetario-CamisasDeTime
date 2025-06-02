package com.camisastime.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camisastime.model.Usuario;
import com.camisastime.security.JwtUtil;
import com.camisastime.service.UsuarioService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Usuario usuario) {
        try {
            
            if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Username é obrigatório");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Password é obrigatório");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            usuario.getUsername().trim(),
                            usuario.getPassword()
                    )
            );

           
            String token = jwtUtil.generateToken(usuario.getUsername().trim());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", usuario.getUsername().trim());
            response.put("message", "Login realizado com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Credenciais inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Usuario usuario) {
        try {
           
            if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Username é obrigatório");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Password é obrigatório");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            
            if (usuarioService.existsByUsername(usuario.getUsername().trim())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Username já existe");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            
            usuario.setUsername(usuario.getUsername().trim());
            
          
            Usuario usuarioSalvo = usuarioService.salvar(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", usuarioSalvo.getId());
            response.put("username", usuarioSalvo.getUsername());
            response.put("message", "Usuário registrado com sucesso");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao registrar: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}