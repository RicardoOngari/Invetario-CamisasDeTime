package com.camisastime.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.camisastime.model.Deposito;
import com.camisastime.model.Estoque;
import com.camisastime.model.Movimentacao;
import com.camisastime.model.Produto;
import com.camisastime.repository.MovimentacaoRepository;

@Service
@Transactional
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private DepositoService depositoService;

    public Movimentacao registrar(Movimentacao movimentacao) {
        // Validações básicas
        validarMovimentacaoBasica(movimentacao);

        // Buscar entidades completas
        Produto produto = produtoService.buscarPorId(movimentacao.getProduto().getId())
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + movimentacao.getProduto().getId()));
        
        movimentacao.setProduto(produto);
        movimentacao.setDataHora(LocalDateTime.now());

        String tipo = movimentacao.getTipo().toUpperCase();
        movimentacao.setTipo(tipo);

        switch (tipo) {
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

    private void validarMovimentacaoBasica(Movimentacao movimentacao) {
        if (movimentacao == null) {
            throw new RuntimeException("Movimentação não pode ser nula");
        }
        
        if (movimentacao.getQuantidade() <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        if (movimentacao.getProduto() == null || movimentacao.getProduto().getId() == null) {
            throw new RuntimeException("Produto é obrigatório e deve ter um ID válido");
        }

        if (movimentacao.getTipo() == null || movimentacao.getTipo().trim().isEmpty()) {
            throw new RuntimeException("Tipo de movimentação é obrigatório");
        }
    }

    private void processarEntrada(Movimentacao mov) {
        if (mov.getDepositoDestino() == null || mov.getDepositoDestino().getId() == null) {
            throw new RuntimeException("Depósito destino é obrigatório para entrada");
        }

        // Buscar depósito completo
        Deposito depositoDestino = depositoService.buscarPorId(mov.getDepositoDestino().getId())
            .orElseThrow(() -> new RuntimeException("Depósito destino não encontrado com ID: " + mov.getDepositoDestino().getId()));
        
        mov.setDepositoDestino(depositoDestino);

        // Buscar ou criar estoque
        Estoque estoque = estoqueService.buscarPorProdutoEDeposito(
            mov.getProduto().getId(), 
            mov.getDepositoDestino().getId()
        ).orElse(new Estoque(mov.getProduto(), mov.getDepositoDestino(), 0));

        estoque.setQuantidade(estoque.getQuantidade() + mov.getQuantidade());
        estoqueService.salvar(estoque);
    }

    private void processarSaida(Movimentacao mov) {
        if (mov.getDepositoDestino() == null || mov.getDepositoDestino().getId() == null) {
            throw new RuntimeException("Depósito é obrigatório para saída");
        }

        // Buscar depósito completo
        Deposito depositoDestino = depositoService.buscarPorId(mov.getDepositoDestino().getId())
            .orElseThrow(() -> new RuntimeException("Depósito não encontrado com ID: " + mov.getDepositoDestino().getId()));
        
        mov.setDepositoDestino(depositoDestino);

        // Verificar estoque
        Estoque estoque = estoqueService.buscarPorProdutoEDeposito(
            mov.getProduto().getId(), 
            mov.getDepositoDestino().getId()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado no estoque do depósito especificado"));

        if (estoque.getQuantidade() < mov.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente. Disponível: " + estoque.getQuantidade() + 
                ", Solicitado: " + mov.getQuantidade());
        }

        estoque.setQuantidade(estoque.getQuantidade() - mov.getQuantidade());
        estoqueService.salvar(estoque);
    }

    private void processarTransferencia(Movimentacao mov) {
        if (mov.getDepositoOrigem() == null || mov.getDepositoOrigem().getId() == null) {
            throw new RuntimeException("Depósito origem é obrigatório para transferência");
        }
        
        if (mov.getDepositoDestino() == null || mov.getDepositoDestino().getId() == null) {
            throw new RuntimeException("Depósito destino é obrigatório para transferência");
        }

        if (mov.getDepositoOrigem().getId().equals(mov.getDepositoDestino().getId())) {
            throw new RuntimeException("Depósito origem deve ser diferente do depósito destino");
        }

        // Buscar depósitos completos
        Deposito depositoOrigem = depositoService.buscarPorId(mov.getDepositoOrigem().getId())
            .orElseThrow(() -> new RuntimeException("Depósito origem não encontrado com ID: " + mov.getDepositoOrigem().getId()));
        
        Deposito depositoDestino = depositoService.buscarPorId(mov.getDepositoDestino().getId())
            .orElseThrow(() -> new RuntimeException("Depósito destino não encontrado com ID: " + mov.getDepositoDestino().getId()));
        
        mov.setDepositoOrigem(depositoOrigem);
        mov.setDepositoDestino(depositoDestino);

        // Saída do depósito origem
        Estoque estoqueOrigem = estoqueService.buscarPorProdutoEDeposito(
            mov.getProduto().getId(), 
            mov.getDepositoOrigem().getId()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado no depósito origem"));

        if (estoqueOrigem.getQuantidade() < mov.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente no depósito origem. Disponível: " + 
                estoqueOrigem.getQuantidade() + ", Solicitado: " + mov.getQuantidade());
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
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new RuntimeException("Tipo não pode ser vazio");
        }
        return movimentacaoRepository.findByTipo(tipo.toUpperCase());
    }

    public List<Movimentacao> listarPorProduto(Long produtoId) {
        if (produtoId == null) {
            throw new RuntimeException("ID do produto não pode ser nulo");
        }
        return movimentacaoRepository.findByProdutoId(produtoId);
    }

    public List<Movimentacao> listarPorDeposito(Long depositoId) {
        if (depositoId == null) {
            throw new RuntimeException("ID do depósito não pode ser nulo");
        }
        return movimentacaoRepository.findByDepositoDestinoIdOrDepositoOrigemId(depositoId, depositoId);
    }
}