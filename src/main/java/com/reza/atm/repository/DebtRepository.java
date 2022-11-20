package com.reza.atm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reza.atm.model.Debt;

public interface DebtRepository extends JpaRepository<Debt, Integer> {

}
