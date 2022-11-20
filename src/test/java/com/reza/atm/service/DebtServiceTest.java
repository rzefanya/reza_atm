package com.reza.atm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reza.atm.model.Customer;
import com.reza.atm.model.Debt;
import com.reza.atm.repository.DebtRepository;
import com.reza.atm.service.impl.DebtServiceImpl;

@ExtendWith(MockitoExtension.class)
public class DebtServiceTest {

	@Mock
	private DebtRepository repository;

	@InjectMocks
	private DebtServiceImpl service;

	@Test
	public void createDebtTest() {
		Customer customer = new Customer();
		customer.setName("Reza");

		Customer target = new Customer();
		target.setName("target");

		when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0, Debt.class));

		Debt result = service.createDebt(customer, 100, target);
		assertEquals(customer, result.getCustomer());
		assertEquals(100, result.getAmount());
		assertEquals(target, result.getTarget());
	}

	@Test
	public void increaseDebtTest() {
		Debt debt = new Debt();
		debt.setAmount(100);

		when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0, Debt.class));

		Debt result = service.increaseDebt(debt, 100);
		assertEquals(200, result.getAmount());
	}
	
	@Test
	public void payDebtTest() {
		Debt debt = new Debt();
		debt.setAmount(100);

		when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0, Debt.class));

		Debt result = service.payDebt(debt, 100);
		assertEquals(0, result.getAmount());
	}
}
