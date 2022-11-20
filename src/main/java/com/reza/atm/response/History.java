package com.reza.atm.response;

import java.util.ArrayList;
import java.util.List;

import com.reza.atm.model.Customer;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class History {
	private Customer customer;
	private List<Transaction> transactions;

	public void addTransaction(Transaction transaction) {
		if (transactions == null) {
			transactions = new ArrayList<>();
		}

		transactions.add(transaction);
	}
}
