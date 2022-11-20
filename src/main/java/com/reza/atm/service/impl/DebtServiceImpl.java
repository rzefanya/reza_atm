package com.reza.atm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.reza.atm.model.Customer;
import com.reza.atm.model.Debt;
import com.reza.atm.repository.DebtRepository;
import com.reza.atm.service.DebtService;

@Service
public class DebtServiceImpl implements DebtService {
	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private DebtRepository repository;

	public DebtServiceImpl(DebtRepository repository) {
		this.repository = repository;
	}

	@Override
	public Debt createDebt(Customer customer, long amount, Customer target) {
		log.info("createDebt customer={} amount={} target={}", customer, amount, target);
		Debt debt = new Debt();
		debt.setAmount(amount);
		debt.setCustomer(customer);
		debt.setTarget(target);
		debt = repository.save(debt);
		log.info("createDebt result debt={}", debt);
		return debt;
	}

	@Override
	public Debt increaseDebt(Debt debt, long amount) {
		log.info("increaseDebt debt={} amount={}", debt, amount);
		long totalAmount = debt.getAmount() + amount;
		debt.setAmount(totalAmount);
		debt = repository.save(debt);
		log.info("increaseDebt result debt={}", debt);
		return debt;
	}

	@Override
	public Debt payDebt(Debt debt, long amount) {
		log.info("payDebt debt={} amount={}", debt, amount);
		long totalAmount = debt.getAmount() - amount;
		debt.setAmount(totalAmount);
		debt = repository.save(debt);
		log.info("payDebt result debt={}", debt);
		return debt;
	}

	@Override
	public void deleteDebt(Debt debt) {
		log.info("deleteDebt debt={}", debt);
		repository.delete(debt);
		log.info("deleteDebt result");
	}
}
