package com.camisastime.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camisastime.model.Estoque;
import com.camisastime.model.Movimentacao;
import com.camisastime.repository.MovimentacaoRepository;

@Service
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private EstoqueService estoqueService;

    public Movimentacao registrar(Movimentacao movimentacao) {
        movimentacao.setDataHora(LocalDateTime.now());

        switch (movimentacao.getTipo().toUpperCase()) {
            case "ENTRADA":
                processarEntrada(movimentacao);
                break;
            case "SAIDA":
                processarSaida(movimentacao);
                break;
            case "TRANSFERENCIA":
                processarTransferencia(movimentacao);
                break;
            default:
                throw new RuntimeException("Tipo inválido");
        }

        return movimentacaoRepository.save(movimentacao);
    }

    private void processarEntrada(Movimentacao mov) {
        Estoque estoque = estoqueService.buscarPorProdutoEDeposito(
            mov.getProduto().getId(), 
            mov.getDepositoDestino().getId()
        ).orElse(new Estoque(mov.getProduto(), mov.getDepositoDestino(), 0));

        estoque.setQuantidade(estoque.getQuantidade() + mov.getQuantidade());
        estoqueService.salvar(estoque);
    }

    private void processarSaida(Movimentacao mov) {
        Estoque estoque = estoqueService.buscarPorProdutoEDeposito(
            mov.getProduto().getId(), 
            mov.getDepositoDestino().getId()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado no estoque"));

        if (estoque.getQuantidade() < mov.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente");
        }

        estoque.setQuantidade(estoque.getQuantidade() - mov.getQuantidade());
        estoqueService.salvar(estoque);
    }

    private void processarTransferencia(Movimentacao mov) {
        // Saída do depósito origem
        Estoque estoqueOrigem = estoqueService.buscarPorProdutoEDeposito(
            mov.getProduto().getId(), 
            mov.getDepositoOrigem().getId()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado no depósito origem"));

        if (estoqueOrigem.getQuantidade() < mov.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente no depósito origem");
        }

        estoqueOrigem.setQuantidade(estoqueOrigem.getQuantidade() - mov.getQuantidade());
        estoqueService.salvar(estoqueOrigem);

        // Entrada no depósito destino
        Estoque estoqueDestino = estoqueService.buscarPorProdutoEDeposito(
            mov.getProduto().getId(), 
            mov.getDepositoDestino().getId()
        ).orElse(new Estoque(mov.getProduto(), mov.getDepositoDestino(), 0));

        estoqueDestino.setQuantidade(estoqueDestino.getQuantidade() + mov.getQuantidade());
        estoqueService.salvar(estoqueDestino);
    }

    public List<Movimentacao> listar() {
        return movimentacaoRepository.findAll();
    }
}