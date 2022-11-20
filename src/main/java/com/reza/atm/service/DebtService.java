package com.reza.atm.service;

import com.reza.atm.model.Customer;
import com.reza.atm.model.Debt;

public interface DebtService {
	public Debt createDebt(Customer customer, long amount, Customer target);

	public Debt increaseDebt(Debt debt, long amount);

	public Debt payDebt(Debt debt, long amount);

	public void deleteDebt(Debt debt);
}
