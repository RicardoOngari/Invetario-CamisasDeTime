package com.camisastime.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camisastime.model.Estoque;
import com.camisastime.repository.EstoqueRepository;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository repository;

    public List<Estoque> listar() {
        return repository.findAll();
    }

    public List<Estoque> listarPorProduto(Long produtoId) {
        return repository.findByProdutoId(produtoId);
    }

    public List<Estoque> listarPorDeposito(Long depositoId) {
        return repository.findByDepositoId(depositoId);
    }

    public Optional<Estoque> buscarPorProdutoEDeposito(Long produtoId, Long depositoId) {
        return repository.findByProdutoIdAndDepositoId(produtoId, depositoId);
    }

    public Double calcularValorTotal() {
        return repository.calcularValorTotalGeral();
    }

    public Double calcularValorTotalPorDeposito(Long depositoId) {
        return repository.calcularValorTotalPorDeposito(depositoId);
    }

    public Estoque salvar(Estoque estoque) {
        return repository.save(estoque);
    }
}
