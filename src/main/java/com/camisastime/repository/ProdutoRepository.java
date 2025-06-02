package com.camisastime.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.camisastime.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByCodigo(String codigo);
}