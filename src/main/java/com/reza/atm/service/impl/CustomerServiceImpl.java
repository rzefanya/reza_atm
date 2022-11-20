package com.reza.atm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.reza.atm.model.Customer;
import com.reza.atm.repository.CustomerRepository;
import com.reza.atm.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {
	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private CustomerRepository repository;

	public CustomerServiceImpl(CustomerRepository repository) {
		this.repository = repository;
	}

	@Override
	public Customer register(String name) {
		log.info("register name={}", name);
		Customer customer = new Customer();
		customer.setName(name);
		customer = repository.save(customer);
		log.info("register result customer={}", customer);
		return customer;
	}

	@Override
	public Customer findCustomer(String name) {
		log.info("findCustomer name={}", name);
		Customer customer = repository.findByName(name);
		log.info("findCustomer result customer={}", customer);
		return customer;
	}
}
