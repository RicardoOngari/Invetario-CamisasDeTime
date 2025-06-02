package com.camisastime.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camisastime.model.Deposito;
import com.camisastime.service.DepositoService;

@RestController
@RequestMapping("/api/depositos")

public class DepositoController {

    @Autowired
    private DepositoService service;

    @PostMapping
    public Deposito criar(@RequestBody Deposito deposito) {
        return service.salvar(deposito);
    }

    @GetMapping
    public List<Deposito> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Optional<Deposito> buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Deposito atualizar(@PathVariable Long id, @RequestBody Deposito deposito) {
        return service.atualizar(id, deposito);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}