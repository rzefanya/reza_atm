package com.reza.atm.service;

import com.reza.atm.model.Customer;

public interface CustomerService {
	public Customer register(String name);

	public Customer findCustomer(String name);
}
