package com.camisastime.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camisastime.model.Estoque;
import com.camisastime.service.EstoqueService;

@RestController
@RequestMapping("/api/estoque")

public class EstoqueController {

    @Autowired
    private EstoqueService service;

    @GetMapping
    public List<Estoque> listar() {
        return service.listar();
    }

    @GetMapping("/produto/{produtoId}")
    public List<Estoque> listarPorProduto(@PathVariable Long produtoId) {
        return service.listarPorProduto(produtoId);
    }

    @GetMapping("/deposito/{depositoId}")
    public List<Estoque> listarPorDeposito(@PathVariable Long depositoId) {
        return service.listarPorDeposito(depositoId);
    }

    @GetMapping("/valor-total")
    public Double calcularValorTotal() {
        return service.calcularValorTotal();
    }

    @GetMapping("/valor-total/deposito/{depositoId}")
    public Double calcularValorTotalPorDeposito(@PathVariable Long depositoId) {
        return service.calcularValorTotalPorDeposito(depositoId);
    }
}