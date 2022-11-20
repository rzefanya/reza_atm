package com.reza.atm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.reza.atm.model.Customer;
import com.reza.atm.repository.CustomerRepository;

@Service
public class CustomerService {
	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private CustomerRepository repository;

	public CustomerService(CustomerRepository repository) {
		this.repository = repository;
	}

	public Customer register(String name) {
		log.info("register name={}", name);
		Customer customer = new Customer();
		customer.setName(name);
		customer = repository.save(customer);
		log.info("register result customer={}", customer);
		return customer;
	}

	public Customer findCustomer(String name) {
		log.info("findCustomer name={}", name);
		Customer customer = repository.findByName(name);
		log.info("findCustomer result customer={}", customer);
		return customer;
	}
}
