package com.reza.atm.command.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reza.atm.command.service.impl.AtmCommandServiceImpl;
import com.reza.atm.exception.AtmException;
import com.reza.atm.model.Balance;
import com.reza.atm.model.Customer;
import com.reza.atm.model.Debt;
import com.reza.atm.response.History;
import com.reza.atm.service.BalanceService;
import com.reza.atm.service.CustomerService;
import com.reza.atm.service.DebtService;

@ExtendWith(MockitoExtension.class)
public class AtmCommandServiceTest {
	@Mock
	private CustomerService customerService;

	@Mock
	private BalanceService balanceService;

	@Mock
	private DebtService debtService;

	@InjectMocks
	private AtmCommandServiceImpl service;

	@Test
	public void loginExistingTest() {
		String name = "Reza";

		Customer customer = new Customer();
		customer.setName(name);

		when(customerService.findCustomer(name)).thenReturn(customer);

		Customer result = service.login("Reza");
		assertEquals(name, result.getName());
	}

	@Test
	public void loginNotExistingTest() {
		String name = "Reza";

		final Customer customer = new Customer();

		when(customerService.findCustomer(name)).thenReturn(null).thenReturn(customer);
		when(customerService.register(name)).then(new Answer<Customer>() {
			@Override
			public Customer answer(InvocationOnMock invocationOnMock) throws Throwable {
				customer.setName(name);
				return customer;
			}
		});
		when(balanceService.register(customer)).then(new Answer<Balance>() {
			@Override
			public Balance answer(InvocationOnMock invocationOnMock) throws Throwable {
				Balance balance = new Balance();
				balance.setBalance(0);
				customer.setBalance(balance);
				return balance;
			}
		});

		Customer result = service.login(name);
		assertEquals(name, result.getName());
		assertNotNull(result.getBalance());
	}

	// customer got $100 debt and $0 balance
	// target got $0 balance
	@Test
	public void depositTest() throws AtmException, JsonProcessingException {
		String name = "Reza";

		Balance balance = new Balance();
		balance.setBalance(0);

		final Customer customer = new Customer();
		customer.setName(name);
		customer.setBalance(balance);

		Balance targetBalance = new Balance();
		targetBalance.setBalance(0);

		Customer target = new Customer();
		target.setBalance(targetBalance);

		Debt debt = new Debt();
		debt.setAmount(100);
		debt.setCustomer(customer);
		debt.setTarget(target);
		List<Debt> debts = new ArrayList<>();
		debts.add(debt);
		customer.setDebts(debts);

		when(customerService.findCustomer(customer.getName())).thenReturn(customer);
		when(debtService.payDebt(any(), anyLong())).then(new Answer<Debt>() {
			@Override
			public Debt answer(InvocationOnMock invocationOnMock) throws Throwable {
				Debt debt = invocationOnMock.getArgument(0, Debt.class);
				long amount = invocationOnMock.getArgument(1, Long.class);
				debt.setAmount(debt.getAmount() - amount);
				return debt;
			}
		});
		when(balanceService.deposit(any(), anyLong())).then(new Answer<Balance>() {
			@Override
			public Balance answer(InvocationOnMock invocationOnMock) throws Throwable {
				Balance balance = invocationOnMock.getArgument(0, Customer.class).getBalance();
				balance.setBalance(balance.getBalance() + invocationOnMock.getArgument(1, Long.class));
				return balance;
			}
		});

		// customer deposit $10
		// transfer $10 to target
		// customer got $90 debt left and $0 balance
		// target got $10 balance
		History result = service.deposit(customer, 10);
		assertEquals(0, result.getCustomer().getBalance().getBalance());
		assertEquals(90, result.getCustomer().getDebts().get(0).getAmount());
		assertEquals(10, result.getTransactions().get(0).getAmount());
		assertEquals(10, result.getTransactions().get(0).getTarget().getBalance().getBalance());

		// customer deposit $250
		// transfer $90 to target
		// customer got $0 debt left and $160 balance
		// target got $100 balance
		result = service.deposit(customer, 250);
		assertEquals(160, result.getCustomer().getBalance().getBalance());
		assertEquals(0, result.getCustomer().getDebts().get(0).getAmount());
		assertEquals(90, result.getTransactions().get(0).getAmount());
		assertEquals(100, result.getTransactions().get(0).getTarget().getBalance().getBalance());

		// customer deposit $300
		// customer got $460 balance
		result = service.deposit(customer, 300);
		assertEquals(460, result.getCustomer().getBalance().getBalance());
		assertEquals(0, result.getCustomer().getDebts().get(0).getAmount());
	}

	@Test
	public void withdrawTest() throws AtmException {
		String name = "Reza";

		Balance balance = new Balance();
		balance.setBalance(100);

		final Customer customer = new Customer();
		customer.setName(name);
		customer.setBalance(balance);

		when(customerService.findCustomer(customer.getName())).thenReturn(customer);
		when(balanceService.withdraw(any(), anyLong())).then(new Answer<Balance>() {
			@Override
			public Balance answer(InvocationOnMock invocationOnMock) throws Throwable {
				Balance balance = customer.getBalance();
				balance.setBalance(balance.getBalance() - invocationOnMock.getArgument(1, Long.class));
				return balance;
			}
		});

		Customer result = service.withdraw(customer, 100);
		assertEquals(0, result.getBalance().getBalance());
	}

	// customer got no debt
	// customer got $200 credit from creditTarget
	// customer got $100 balance
	// target got $0 balance
	// creditTarget got $0 balance
	@Test
	public void transferTest() throws AtmException, JsonProcessingException {
		String name = "Reza";

		Balance balance = new Balance();
		balance.setBalance(100);

		final Customer customer = new Customer();
		customer.setName(name);
		customer.setBalance(balance);

		Balance targetBalance = new Balance();
		targetBalance.setBalance(0);

		Customer target = new Customer();
		target.setName("target");
		target.setBalance(targetBalance);

		List<Debt> debts = new ArrayList<>();
		customer.setDebts(debts);

		Balance creditBalance = new Balance();
		creditBalance.setBalance(0);

		Customer creditTarget = new Customer();
		creditTarget.setName("creditTarget");
		creditTarget.setBalance(creditBalance);

		Debt debt = new Debt();
		debt.setAmount(200);
		debt.setCustomer(creditTarget);
		debt.setTarget(customer);
		List<Debt> credits = new ArrayList<>();
		credits.add(debt);
		customer.setCredits(credits);

		when(customerService.findCustomer(any())).then(new Answer<Customer>() {
			@Override
			public Customer answer(InvocationOnMock invocationOnMock) throws Throwable {
				String name = invocationOnMock.getArgument(0, String.class);

				if (name.equals("Reza")) {
					return customer;
				} else if (name.equals("target")) {
					return target;
				} else if (name.equals("creditTarget")) {
					return creditTarget;
				}
				return null;
			}
		});
		when(debtService.payDebt(any(), anyLong())).then(new Answer<Debt>() {
			@Override
			public Debt answer(InvocationOnMock invocationOnMock) throws Throwable {
				Debt debt = invocationOnMock.getArgument(0, Debt.class);
				long amount = invocationOnMock.getArgument(1, Long.class);
				debt.setAmount(debt.getAmount() - amount);
				return debt;
			}
		});
		when(debtService.createDebt(any(), anyLong(), any())).then(new Answer<Debt>() {
			@Override
			public Debt answer(InvocationOnMock invocationOnMock) throws Throwable {
				Customer from = invocationOnMock.getArgument(0, Customer.class);
				long amount = invocationOnMock.getArgument(1, Long.class);
				Customer target = invocationOnMock.getArgument(2, Customer.class);

				Debt debt = new Debt();
				debt.setAmount(amount);
				debt.setCustomer(from);
				debt.setTarget(target);
				customer.getDebts().add(debt);

				return debt;
			}
		});
		when(debtService.increaseDebt(any(), anyLong())).then(new Answer<Debt>() {
			@Override
			public Debt answer(InvocationOnMock invocationOnMock) throws Throwable {
				Debt debt = invocationOnMock.getArgument(0, Debt.class);
				long amount = invocationOnMock.getArgument(1, Long.class);
				debt.setAmount(debt.getAmount() + amount);
				return debt;
			}
		});
		when(balanceService.transfer(any(), anyLong(), any())).then(new Answer<Balance>() {
			@Override
			public Balance answer(InvocationOnMock invocationOnMock) throws Throwable {
				Balance balance = invocationOnMock.getArgument(0, Customer.class).getBalance();
				long amount = invocationOnMock.getArgument(1, Long.class);
				Balance targetBalance = invocationOnMock.getArgument(2, Customer.class).getBalance();

				balance.setBalance(balance.getBalance() - amount);
				targetBalance.setBalance(targetBalance.getBalance() + amount);

				return balance;
			}
		});

		// customer wants to transfer $200 to target
		// $100 transferred to target
		// customer got $100 debt to target
		// customer got $0 balance
		// target got $100 balance
		History result = service.transfer(customer, target.getName(), 200);
		assertEquals(100, result.getTransactions().get(0).getAmount());
		assertEquals(target.getName(), result.getTransactions().get(0).getTarget().getName());
		assertEquals(100, result.getCustomer().getDebts().get(0).getAmount());
		assertEquals(target.getName(), result.getCustomer().getDebts().get(0).getTarget().getName());
		assertEquals(0, result.getCustomer().getBalance().getBalance());
		assertEquals(100, result.getCustomer().getDebts().get(0).getTarget().getBalance().getBalance());

		// customer somehow got $200 balance
		// customer transfer $200 to target
		// $200 transferred to target
		// customer got no more debt
		// customer got $0 balance
		// target got $300 balance
		customer.getBalance().setBalance(200);
		result = service.transfer(customer, target.getName(), 200);
		assertEquals(200, result.getTransactions().get(0).getAmount());
		assertEquals(target.getName(), result.getTransactions().get(0).getTarget().getName());
		assertEquals(0, result.getCustomer().getDebts().get(0).getAmount());
		assertEquals(target.getName(), result.getCustomer().getDebts().get(0).getTarget().getName());
		assertEquals(0, result.getCustomer().getBalance().getBalance());
		assertEquals(300, result.getCustomer().getDebts().get(0).getTarget().getBalance().getBalance());

		// customer transfer $200 to target
		// $0 transferred to target
		// customer got $200 more debt
		// customer got $0 balance
		// target got $300 balance
		result = service.transfer(customer, target.getName(), 200);
		assertEquals(0, result.getTransactions().get(0).getAmount());
		assertEquals(target.getName(), result.getTransactions().get(0).getTarget().getName());
		assertEquals(200, result.getCustomer().getDebts().get(0).getAmount());
		assertEquals(target.getName(), result.getCustomer().getDebts().get(0).getTarget().getName());
		assertEquals(0, result.getCustomer().getBalance().getBalance());
		assertEquals(300, result.getCustomer().getDebts().get(0).getTarget().getBalance().getBalance());

		// customer somehow got $200 balance
		// customer transfer $300 to creditTarget
		// $100 transferred to creditTarget
		// customer got no more credit
		// customer got $100 balance
		// creditTarget got $100 balance
		customer.getBalance().setBalance(200);
		result = service.transfer(customer, creditTarget.getName(), 300);
		assertEquals(100, result.getTransactions().get(0).getAmount());
		assertEquals(creditTarget.getName(), result.getTransactions().get(0).getTarget().getName());
		assertEquals(0, result.getCustomer().getCredits().get(0).getAmount());
		assertEquals(creditTarget.getName(), result.getCustomer().getCredits().get(0).getCustomer().getName());
		assertEquals(100, result.getCustomer().getBalance().getBalance());
		assertEquals(100, result.getCustomer().getCredits().get(0).getCustomer().getBalance().getBalance());

	}
}
