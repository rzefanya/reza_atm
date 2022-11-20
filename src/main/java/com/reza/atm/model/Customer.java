package com.reza.atm.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;

	@ToString.Exclude
	@OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
	private Balance balance;

	@ToString.Exclude
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "customer", cascade = CascadeType.ALL)
	private List<Debt> debts;

	@ToString.Exclude
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "target", cascade = CascadeType.ALL)
	private List<Debt> credits;
}
