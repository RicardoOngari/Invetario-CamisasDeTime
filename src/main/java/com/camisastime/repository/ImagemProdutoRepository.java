package com.camisastime.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.camisastime.model.ImagemProduto;

public interface ImagemProdutoRepository extends JpaRepository<ImagemProduto, Long> {
    
    List<ImagemProduto> findByProdutoId(Long produtoId);
    
    Optional<ImagemProduto> findByProdutoIdAndPrincipalTrue(Long produtoId);
    
    @Modifying
    @Query("UPDATE ImagemProduto i SET i.principal = false WHERE i.produto.id = :produtoId")
    void removerImagemPrincipalDoProduto(@Param("produtoId") Long produtoId);
    
    @Query("SELECT COUNT(i) FROM ImagemProduto i WHERE i.produto.id = :produtoId")
    long countByProdutoId(@Param("produtoId") Long produtoId);
    
    void deleteByProdutoId(Long produtoId);
}