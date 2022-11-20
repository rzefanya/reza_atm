package com.reza.atm.command.service;

import com.reza.atm.exception.AtmException;
import com.reza.atm.model.Customer;
import com.reza.atm.response.History;

public interface AtmCommandService {
	public Customer login(String name);

	public History deposit(Customer customer, long amount) throws AtmException;

	public Customer withdraw(Customer customer, long amount) throws AtmException;

	public History transfer(Customer customer, String target, long amount) throws AtmException;
}
