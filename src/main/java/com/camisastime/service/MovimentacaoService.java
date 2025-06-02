package com.camisastime.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camisastime.model.Movimentacao;
import com.camisastime.model.Produto;
import com.camisastime.repository.MovimentacaoRepository;
import com.camisastime.repository.ProdutoRepository;

@Service
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public Movimentacao registrarMovimentacao(Movimentacao movimentacao) {
        Produto produto = produtoRepository.findById(movimentacao.getProduto().getId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        movimentacao.setDataHora(LocalDateTime.now());

        switch (movimentacao.getTipo().toUpperCase()) {
            case "ENTRADA":
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + movimentacao.getQuantidade());
                break;
            case "SAIDA":
                if (produto.getQuantidadeEstoque() < movimentacao.getQuantidade()) {
                    throw new RuntimeException("Estoque insuficiente");
                }
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - movimentacao.getQuantidade());
                break;
            case "TRANSFERENCIA":
                // Implementar se houver depósitos diferentes
                break;
            default:
                throw new RuntimeException("Tipo de movimentação inválido");
        }

        produtoRepository.save(produto);
        return movimentacaoRepository.save(movimentacao);
    }

    public List<Movimentacao> listar() {
        return movimentacaoRepository.findAll();
    }
}
