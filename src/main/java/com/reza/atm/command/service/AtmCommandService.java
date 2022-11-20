package com.reza.atm.command.service;

import org.springframework.stereotype.Service;

import com.reza.atm.exception.AmountBelowMinimumException;
import com.reza.atm.exception.AtmException;
import com.reza.atm.exception.CustomerNotFoundException;
import com.reza.atm.exception.NotEnoughBalanceException;
import com.reza.atm.exception.NotLoggedInException;
import com.reza.atm.exception.TransferYourselfException;
import com.reza.atm.model.Customer;
import com.reza.atm.model.Debt;
import com.reza.atm.response.History;
import com.reza.atm.response.Transaction;
import com.reza.atm.service.BalanceService;
import com.reza.atm.service.CustomerService;
import com.reza.atm.service.DebtService;

@Service
public class AtmCommandService {
	private CustomerService customerService;
	private BalanceService balanceService;
	private DebtService debtService;

	public AtmCommandService(CustomerService customerService, BalanceService balanceService, DebtService debtService) {
		this.customerService = customerService;
		this.balanceService = balanceService;
		this.debtService = debtService;
	}

	public Customer login(String name) {
		Customer customer = customerService.findCustomer(name);

		if (customer == null) {
			customer = customerService.register(name);
			balanceService.register(customer);
			customer = customerService.findCustomer(name);
		}

		return customer;
	}

	public History deposit(Customer customer, long amount) throws AtmException {
		History history = new History();

		if (customer == null) {
			throw new NotLoggedInException();
		} else if (amount < 1) {
			throw new AmountBelowMinimumException();
		} else {
			long amountLeft = amount;

			if (customer.getDebts() != null && !customer.getDebts().isEmpty()) {
				for (Debt debt : customer.getDebts()) {
					long debtAmount = 0;
					if (debt.getAmount() > amountLeft) {
						debtAmount = amountLeft;
					} else {
						debtAmount = debt.getAmount();
					}
					amountLeft -= debtAmount;

					debtService.payDebt(debt, debtAmount);
					balanceService.deposit(debt.getTarget(), debtAmount);

					Transaction transaction = new Transaction();
					transaction.setCustomer(customer);
					transaction.setTarget(debt.getTarget());
					transaction.setType("transfer");
					transaction.setAmount(debtAmount);
					history.addTransaction(transaction);

					if (amountLeft == 0) {
						break;
					}
				}
			}

			if (amountLeft > 0) {
				balanceService.deposit(customer, amountLeft);
			}

			customer = customerService.findCustomer(customer.getName());
			history.setCustomer(customer);
		}

		return history;
	}

	public Customer withdraw(Customer customer, long amount) throws AtmException {
		if (customer == null) {
			throw new NotLoggedInException();
		} else if (amount < 1) {
			throw new AmountBelowMinimumException();
		} else if (customer.getBalance().getBalance() < amount) {
			throw new NotEnoughBalanceException();
		} else {
			balanceService.withdraw(customer, amount);
			customer = customerService.findCustomer(customer.getName());
		}

		return customer;
	}

	public History transfer(Customer customer, String target, long amount) throws AtmException {
		History history = new History();

		if (customer == null) {
			throw new NotLoggedInException();
		} else if (customer.getName().equals(target)) {
			throw new TransferYourselfException();
		} else if (amount < 1) {
			throw new AmountBelowMinimumException();
		} else {
			Customer targetCustomer = customerService.findCustomer(target);

			if (targetCustomer == null) {
				throw new CustomerNotFoundException();
			}

			long amountLeft = amount;
			if (customer.getCredits() != null) {
				for (Debt debt : customer.getCredits()) {
					if (debt.getCustomer().getName().equals(targetCustomer.getName())) {
						long debtAmount = 0;
						if (debt.getAmount() > amountLeft) {
							debtAmount = amountLeft;
						} else {
							debtAmount = debt.getAmount();
						}
						amountLeft -= debtAmount;

						debtService.payDebt(debt, debtAmount);
						break;
					}
				}
			}

			if (amountLeft > 0) {
				long transferAmount = 0;
				long debtAmount = 0;
				if (customer.getBalance().getBalance() < amountLeft) {
					transferAmount = customer.getBalance().getBalance();
					debtAmount = amountLeft - transferAmount;
				} else {
					transferAmount = amountLeft;
				}

				Debt existingDebt = null;
				for (Debt debt : customer.getDebts()) {
					if (debt.getTarget().getName().equals(targetCustomer.getName())) {
						existingDebt = debt;
						break;
					}
				}

				if (existingDebt != null) {
					long payAmount = 0;
					if (existingDebt.getAmount() < transferAmount) {
						payAmount = existingDebt.getAmount();
					} else {
						payAmount = transferAmount;
					}

					debtService.payDebt(existingDebt, payAmount);
				}

				balanceService.transfer(customer, transferAmount, targetCustomer);

				Transaction transaction = new Transaction();
				transaction.setCustomer(customer);
				transaction.setTarget(targetCustomer);
				transaction.setType("transfer");
				transaction.setAmount(transferAmount);
				history.addTransaction(transaction);

				if (debtAmount > 0) {
					if (existingDebt == null) {
						debtService.createDebt(customer, debtAmount, targetCustomer);
					} else {
						debtService.increaseDebt(existingDebt, debtAmount);
					}
				}
			}
			customer = customerService.findCustomer(customer.getName());
			history.setCustomer(customer);
		}

		return history;
	}
}
