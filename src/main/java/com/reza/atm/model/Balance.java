package com.reza.atm.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString
public class Balance {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@JsonIgnore
	@ToString.Exclude
	@OneToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	private long balance;
}
