package com.reza.atm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reza.atm.model.Balance;
import com.reza.atm.model.Customer;

public interface BalanceRepository extends JpaRepository<Balance, Integer> {
	Balance findByCustomer(Customer c);
}
