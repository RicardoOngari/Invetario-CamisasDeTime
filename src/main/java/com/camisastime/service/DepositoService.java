package com.camisastime.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camisastime.model.Deposito;
import com.camisastime.repository.DepositoRepository;

@Service
public class DepositoService {

    @Autowired
    private DepositoRepository repository;

    public Deposito salvar(Deposito deposito) {
        return repository.save(deposito);
    }

    public List<Deposito> listar() {
        return repository.findAll();
    }

    public Optional<Deposito> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Deposito atualizar(Long id, Deposito depositoAtualizado) {
        Deposito deposito = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Depósito não encontrado"));
        
        deposito.setNome(depositoAtualizado.getNome());
        deposito.setEndereco(depositoAtualizado.getEndereco());
        
        return repository.save(deposito);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Depósito não encontrado");
        }
        repository.deleteById(id);
    }
}