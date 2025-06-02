package com.camisastime.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        