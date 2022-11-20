package com.reza.atm.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.reza.atm.model.Customer;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Transaction {
	@JsonIgnore
	private Customer customer;
	private Customer target;
	private String type;
	private long amount;
}
