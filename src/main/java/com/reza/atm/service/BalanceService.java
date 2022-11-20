package com.reza.atm.service;

import com.reza.atm.model.Balance;
import com.reza.atm.model.Customer;

public interface BalanceService {
	public Balance register(Customer customer);

	public Balance deposit(Customer customer, long amount);

	public Balance withdraw(Customer customer, long amount);

	public Balance transfer(Customer customer, long amount, Customer target);
}
