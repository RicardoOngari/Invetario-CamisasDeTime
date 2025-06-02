package com.camisastime.dto;

public class LoginResponse {
    private boolean sucesso;
    private String mensagem;
    private Long userId;
    private String nome;

    public LoginResponse() {}

    public LoginResponse(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    public LoginResponse(boolean sucesso, String mensagem, Long userId, String nome) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.userId = userId;
        this.nome = nome;
    }

    public boolean isSucesso() { return sucesso; }
    public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
