package com.camisastime.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.camisastime.model.Deposito;

public interface DepositoRepository extends JpaRepository<Deposito, Long> {
}