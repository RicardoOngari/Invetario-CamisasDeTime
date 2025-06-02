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

        // Validações básicas
        if (movimentacao.getQuantidade() <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        if (movimentacao.getProduto() == null) {
            throw new RuntimeException("Produto é obrigatório");
        }

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
                throw new RuntimeException("Tipo de movimentação inválido. Use: ENTRADA, SAIDA ou TRANSFERENCIA");
        }

        return movimentacaoRepository.save(movimentacao);
    }

    private void processarEntrada(Movimentacao mov) {
        if (mov.getDepositoDestino() == null) {
            throw new RuntimeException("Depósito destino é obrigatório para entrada");
        }

        Estoque estoque = estoqueService.buscarPorProdutoEDeposito(
            mov.getProduto().getId(), 
            mov.getDepositoDestino().getId()
        ).orElse(new Estoque(mov.getProduto(), mov.getDepositoDestino(), 0));

        estoque.setQuantidade(estoque.getQuantidade() + mov.getQuantidade());
        estoqueService.salvar(estoque);
    }

    private void processarSaida(Movimentacao mov) {
        if (mov.getDepositoDestino() == null) {
            throw new RuntimeException("Depósito é obrigatório para saída");
        }

        Estoque estoque = estoqueService.buscarPorProdutoEDeposito(
            mov.getProduto().getId(), 
            mov.getDepositoDestino().getId()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado no estoque do depósito"));

        if (estoque.getQuantidade() < mov.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente. Disponível: " + estoque.getQuantidade());
        }

        estoque.setQuantidade(estoque.getQuantidade() - mov.getQuantidade());
        estoqueService.salvar(estoque);
    }

    private void processarTransferencia(Movimentacao mov) {
        if (mov.getDepositoOrigem() == null || mov.getDepositoDestino() == null) {
            throw new RuntimeException("Depósitos origem e destino são obrigatórios para transferência");
        }

        if (mov.getDepositoOrigem().getId().equals(mov.getDepositoDestino().getId())) {
            throw new RuntimeException("Depósito origem deve ser diferente do depósito destino");
        }

        // Saída do depósito origem
        Estoque estoqueOrigem = estoqueService.buscarPorProdutoEDeposito(
            mov.getProduto().getId(), 
            mov.getDepositoOrigem().getId()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado no depósito origem"));

        if (estoqueOrigem.getQuantidade() < mov.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente no depósito origem. Disponível: " + estoqueOrigem.getQuantidade());
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

    public List<Movimentacao> listarPorTipo(String tipo) {
        return movimentacaoRepository.findByTipo(tipo.toUpperCase());
    }

    public List<Movimentacao> listarPorProduto(Long produtoId) {
        return movimentacaoRepository.findByProdutoId(produtoId);
    }

    public List<Movimentacao> listarPorDeposito(Long depositoId) {
        return movimentacaoRepository.findByDepositoDestinoIdOrDepositoOrigemId(depositoId, depositoId);
    }
}