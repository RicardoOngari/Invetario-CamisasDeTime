package com.camisastime.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camisastime.model.Produto;
import com.camisastime.repository.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    public List<Produto> listar() {
        return repository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<Produto> buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo);
    }

    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produto = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        
        produto.setCodigo(produtoAtualizado.getCodigo());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setPrecoUnitario(produtoAtualizado.getPrecoUnitario());
        produto.setUnidadeMedida(produtoAtualizado.getUnidadeMedida());
        
        return repository.save(produto);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado");
        }
        repository.deleteById(id);
    }
}