package com.reza.atm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.reza.atm.model.Balance;
import com.reza.atm.model.Customer;
import com.reza.atm.repository.BalanceRepository;
import com.reza.atm.service.BalanceService;

@Service
public class BalanceServiceImpl implements BalanceService {
	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private BalanceRepository repository;

	public BalanceServiceImpl(BalanceRepository repository) {
		this.repository = repository;
	}

	@Override
	public Balance register(Customer customer) {
		log.info("register customer={}", customer);
		Balance balance = new Balance();
		balance.setCustomer(customer);
		customer.setBalance(balance);
		balance = repository.save(balance);
		log.info("register result balance={}", balance);
		return balance;
	}

	@Override
	public Balance deposit(Customer customer, long amount) {
		log.info("deposit customer={} amount={}", customer, amount);
		Balance balance = repository.findByCustomer(customer);
		long totalBalance = balance.getBalance() + amount;
		balance.setBalance(totalBalance);
		balance = repository.save(balance);
		log.info("deposit result balance={}", balance);
		return balance;
	}

	@Override
	public Balance withdraw(Customer customer, long amount) {
		log.info("withdraw customer={} amount={}", customer, amount);
		Balance balance = repository.findByCustomer(customer);
		long totalBalance = balance.getBalance() - amount;
		balance.setBalance(totalBalance);
		balance = repository.save(balance);
		log.info("deposit result balance={}", balance);
		return balance;
	}

	@Override
	public Balance transfer(Customer customer, long amount, Customer target) {
		log.info("transfer customer={} amount={} target={}", customer, amount, target);
		Balance customerBalance = repository.findByCustomer(customer);
		Balance targetBalance = repository.findByCustomer(target);

		long totalBalance = customerBalance.getBalance() - amount;
		long totalTargetBalance = targetBalance.getBalance() + amount;

		customerBalance.setBalance(totalBalance);
		targetBalance.setBalance(totalTargetBalance);

		targetBalance = repository.save(targetBalance);
		customerBalance = repository.save(customerBalance);
		log.info("transfer result customerBalance={} targetBalance={}", customerBalance, targetBalance);
		return customerBalance;
	}
}
