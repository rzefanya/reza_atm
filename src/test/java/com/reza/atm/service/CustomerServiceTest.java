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
import com.reza.atm.repository.CustomerRepository;
import com.reza.atm.service.impl.CustomerServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

	@Mock
	private CustomerRepository repository;

	@InjectMocks
	private CustomerServiceImpl service;

	@Test
	public void registerTest() {
		String name = "Reza";

		when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0, Customer.class));

		Customer result = service.register(name);
		assertEquals(name, result.getName());
	}

	@Test
	public void findCustomerTest() {
		String name = "Reza";

		Customer customer = new Customer();
		customer.setName(name);

		when(repository.findByName(name)).thenReturn(customer);

		Customer result = service.findCustomer(name);
		assertEquals(name, result.getName());
	}
}
