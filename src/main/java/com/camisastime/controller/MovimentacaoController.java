package com.camisastime.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camisastime.model.Movimentacao;
import com.camisastime.service.MovimentacaoService;

@RestController
@RequestMapping("/api/movimentacoes")
@CrossOrigin(origins = "*")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService service;

    @PostMapping
    public Movimentacao registrar(@RequestBody Movimentacao movimentacao) {
        return service.registrarMovimentacao(movimentacao);
    }

    @GetMapping
    public List<Movimentacao> listar() {
        return service.listar();
    }
}
