package com.camisastime.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camisastime.model.Estoque;
import com.camisastime.model.Movimentacao;
import com.camisastime.service.EstoqueService;
import com.camisastime.service.MovimentacaoService;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @Autowired
    private EstoqueService estoqueService;

    @GetMapping("/movimentacoes")
    public List<Movimentacao> relatorioMovimentacoes() {
        return movimentacaoService.listar();
    }

    @GetMapping("/estoque")
    public List<Estoque> relatorioEstoque() {
        return estoqueService.listar();
    }

    @GetMapping("/estoque/deposito/{depositoId}")
    public List<Estoque> relatorioEstoquePorDeposito(@PathVariable Long depositoId) {
        return estoqueService.listarPorDeposito(depositoId);
    }

    @GetMapping("/estoque/produto/{produtoId}")
    public List<Estoque> relatorioEstoquePorProduto(@PathVariable Long produtoId) {
        return estoqueService.listarPorProduto(produtoId);
    }
}