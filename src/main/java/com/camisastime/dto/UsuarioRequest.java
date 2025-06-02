package com.camisastime.dto;

public class UsuarioRequest {
    private String username;
    private String senha;
    private String nome;

    public UsuarioRequest() {}

    public UsuarioRequest(String username, String senha, String nome) {
        this.username = username;
        this.senha = senha;
        this.nome = nome;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}