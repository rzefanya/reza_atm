package com.reza.atm.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.reza.atm.command.service.AtmCommandService;
import com.reza.atm.exception.AlreadyLoginException;
import com.reza.atm.exception.AtmException;
import com.reza.atm.exception.NotLoggedInException;
import com.reza.atm.model.Customer;
import com.reza.atm.model.Debt;
import com.reza.atm.response.History;
import com.reza.atm.response.Transaction;

@ShellComponent
public class AtmCommand {
	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private AtmCommandService service;
	private Customer customer;

	public AtmCommand(AtmCommandService service) {
		this.service = service;
	}

	@ShellMethod
	public String login(@ShellOption String name) {
		try {
			if (customer != null) {
				throw new AlreadyLoginException();
			}

			customer = service.login(name);

			StringBuffer sb = new StringBuffer();
			sb.append(String.format("Hello, %s!\n", customer.getName()));
			sb = this.createResponse(sb, customer);
			return sb.toString();
		} catch (Exception e) {
			return handleError(e);
		}
	}

	@ShellMethod
	public String deposit(@ShellOption long amount) {
		try {
			History history = service.deposit(customer, amount);
			customer = history.getCustomer();

			StringBuffer sb = new StringBuffer();
			sb = this.createResponse(sb, history);
			return sb.toString();
		} catch (Exception e) {
			return handleError(e);
		}
	}

	@ShellMethod
	public String withdraw(@ShellOption long amount) {
		try {
			customer = service.withdraw(customer, amount);
			StringBuffer sb = new StringBuffer();
			sb = this.createResponse(sb, customer);
			return sb.toString();
		} catch (Exception e) {
			return handleError(e);
		}
	}

	@ShellMethod
	public String transfer(@ShellOption String target, @ShellOption long amount) {
		try {
			History history = service.transfer(customer, target, amount);
			customer = history.getCustomer();

			StringBuffer sb = new StringBuffer();
			sb = this.createResponse(sb, history);
			return sb.toString();
		} catch (Exception e) {
			return handleError(e);
		}
	}

	@ShellMethod
	public String logout() {
		try {
			if (customer == null) {
				throw new NotLoggedInException();
			}

			StringBuffer sb = new StringBuffer();
			sb.append(String.format("Goodbye, %s!", customer.getName()));

			customer = null;

			return sb.toString();
		} catch (Exception e) {
			return handleError(e);
		}
	}

	private StringBuffer createResponse(StringBuffer sb, History history) {
		if (history.getTransactions() != null) {
			for (Transaction transaction : history.getTransactions()) {
				sb.append(String.format("Transferred $%s to %s\n", transaction.getAmount(),
						transaction.getTarget().getName()));
			}
		}

		sb = this.createResponse(sb, history.getCustomer());
		return sb;
	}

	private StringBuffer createResponse(StringBuffer sb, Customer customer) {
		sb.append(String.format("Your balance is $%s", customer.getBalance().getBalance()));

		if (customer.getDebts() != null && !customer.getDebts().isEmpty()) {
			for (Debt debt : customer.getDebts()) {
				if (debt.getAmount() > 0) {
					sb.append(String.format("\nOwed $%s to %s", debt.getAmount(), debt.getTarget().getName()));
				}
			}
		}

		if (customer.getCredits() != null && !customer.getCredits().isEmpty()) {
			for (Debt debt : customer.getCredits()) {
				if (debt.getAmount() > 0) {
					sb.append(String.format("\nOwed $%s from %s", debt.getAmount(), debt.getCustomer().getName()));
				}
			}
		}

		return sb;
	}

	private String handleError(Exception e) {
		if (e instanceof AtmException) {
			return e.getMessage();
		}

		log.error(e.getMessage(), e);
		return "Unknown error";
	}
}
