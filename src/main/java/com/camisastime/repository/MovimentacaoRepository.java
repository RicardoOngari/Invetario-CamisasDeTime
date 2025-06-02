package com.camisastime.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.camisastime.model.Movimentacao;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    
    List<Movimentacao> findByTipo(String tipo);
    
    List<Movimentacao> findByProdutoId(Long produtoId);
    
    List<Movimentacao> findByDepositoDestinoIdOrDepositoOrigemId(Long depositoDestinoId, Long depositoOrigemId);
    
    List<Movimentacao> findByDepositoDestinoId(Long depositoId);
    
    List<Movimentacao> findByDepositoOrigemId(Long depositoId);
}