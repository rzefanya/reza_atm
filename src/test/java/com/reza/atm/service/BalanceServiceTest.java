package com.reza.atm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reza.atm.model.Balance;
import com.reza.atm.model.Customer;
import com.reza.atm.repository.BalanceRepository;
import com.reza.atm.service.impl.BalanceServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

	@Mock
	private BalanceRepository repository;

	@InjectMocks
	private BalanceServiceImpl service;

	@Test
	public void registerTest() {
		Customer customer = new Customer();
		customer.setName("Reza");
		customer.setId(1);

		when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0, Balance.class));

		Balance result = service.register(customer);
		assertEquals(0, result.getBalance());
		assertEquals(customer, result.getCustomer());
	}

	@Test
	public void depositTest() {
		Customer customer = new Customer();
		customer.setName("Reza");
		customer.setId(1);

		Balance balance = new Balance();
		balance.setCustomer(customer);
		balance.setBalance(100);

		when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0, Balance.class));
		when(repository.findByCustomer(customer)).thenReturn(balance);

		Balance result = service.deposit(customer, 100);
		assertEquals(200, result.getBalance());
	}

	@Test
	public void withdrawTest() {
		Customer customer = new Customer();
		customer.setName("Reza");
		customer.setId(1);

		Balance balance = new Balance();
		balance.setCustomer(customer);
		balance.setBalance(100);

		when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0, Balance.class));
		when(repository.findByCustomer(customer)).thenReturn(balance);

		Balance result = service.withdraw(customer, 100);
		assertEquals(0, result.getBalance());
	}

	@Test
	public void transferTest() {
		Customer customer = new Customer();
		customer.setName("Reza");
		customer.setId(1);

		Balance balance = new Balance();
		balance.setCustomer(customer);
		balance.setBalance(100);

		Customer target = new Customer();
		target.setName("target");
		target.setId(2);

		Balance targetBalance = new Balance();
		targetBalance.setCustomer(target);
		targetBalance.setBalance(1000);

		when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0, Balance.class));
		when(repository.findByCustomer(customer)).thenReturn(balance);
		when(repository.findByCustomer(target)).thenReturn(targetBalance);

		Balance result = service.transfer(customer, 100, target);
		assertEquals(0, result.getBalance());
		assertEquals(1100, targetBalance.getBalance());
	}
}
