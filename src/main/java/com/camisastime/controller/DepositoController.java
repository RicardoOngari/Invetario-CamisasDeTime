package com.camisastime.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camisastime.model.Deposito;
import com.camisastime.repository.DepositoRepository;

@RestController
@RequestMapping("/api/depositos")
@CrossOrigin(origins = "*")
public class DepositoController {

    @Autowired
    private DepositoRepository repository;

    @PostMapping
    public Deposito criar(@RequestBody Deposito deposito) {
        return repository.save(deposito);
    }

    @GetMapping
    public List<Deposito> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Deposito> buscar(@PathVariable Long id) {
        return repository.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id);
    }
}