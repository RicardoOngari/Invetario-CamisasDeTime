package com.camisastime.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.camisastime.model.Estoque;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    
    Optional<Estoque> findByProdutoIdAndDepositoId(Long produtoId, Long depositoId);
    
    List<Estoque> findByProdutoId(Long produtoId);
    
    List<Estoque> findByDepositoId(Long depositoId);
    
    @Query("SELECT SUM(e.quantidade * e.produto.precoUnitario) FROM Estoque e WHERE e.deposito.id = ?1")
    Double calcularValorTotalPorDeposito(Long depositoId);
    
    @Query("SELECT SUM(e.quantidade * e.produto.precoUnitario) FROM Estoque e")
    Double calcularValorTotalGeral();
}