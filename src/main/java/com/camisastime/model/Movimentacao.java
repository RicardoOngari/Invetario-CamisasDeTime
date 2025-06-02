package com.camisastime.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataHora;
    private String tipo; // ENTRADA, SAIDA, TRANSFERENCIA
    private int quantidade;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "deposito_origem_id")
    private Deposito depositoOrigem;

    @ManyToOne
    @JoinColumn(name = "deposito_destino_id")
    private Deposito depositoDestino;

    public Movimentacao() {}

    public Movimentacao(String tipo, int quantidade, Produto produto, Deposito deposito) {
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.produto = produto;
        if ("ENTRADA".equals(tipo) || "SAIDA".equals(tipo)) {
            this.depositoDestino = deposito;
        }
    }

    public Long getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public Deposito getDepositoOrigem() { return depositoOrigem; }
    public void setDepositoOrigem(Deposito depositoOrigem) { this.depositoOrigem = depositoOrigem; }
    public Deposito getDepositoDestino() { return depositoDestino; }
    public void setDepositoDestino(Deposito depositoDestino) { this.depositoDestino = depositoDestino; }
}