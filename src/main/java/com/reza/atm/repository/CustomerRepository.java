package com.reza.atm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reza.atm.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	Customer findByName(String name);
}
