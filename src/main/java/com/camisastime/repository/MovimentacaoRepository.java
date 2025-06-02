package com.camisastime.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.camisastime.model.Movimentacao;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
}

