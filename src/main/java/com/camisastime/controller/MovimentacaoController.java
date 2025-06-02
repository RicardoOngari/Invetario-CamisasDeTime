package com.camisastime.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camisastime.model.Movimentacao;
import com.camisastime.service.MovimentacaoService;

@RestController
@RequestMapping("/api/movimentacoes")

public class MovimentacaoController {

    @Autowired
    private MovimentacaoService service;

    @PostMapping
    public Movimentacao registrar(@RequestBody Movimentacao movimentacao) {
        return service.registrar(movimentacao);
    }

    @GetMapping
    public List<Movimentacao> listar() {
        return service.listar();
    }

    @GetMapping("/tipo/{tipo}")
    public List<Movimentacao> listarPorTipo(@PathVariable String tipo) {
        return service.listarPorTipo(tipo);
    }

    @GetMapping("/produto/{produtoId}")
    public List<Movimentacao> listarPorProduto(@PathVariable Long produtoId) {
        return service.listarPorProduto(produtoId);
    }

    @GetMapping("/deposito/{depositoId}")
    public List<Movimentacao> listarPorDeposito(@PathVariable Long depositoId) {
        return service.listarPorDeposito(depositoId);
    }
}