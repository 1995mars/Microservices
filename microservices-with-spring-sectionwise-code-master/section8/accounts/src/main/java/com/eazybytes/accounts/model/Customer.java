package com.eazybytes.accounts.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "customer_id")
	private int customerId;
	private String name;
	private String email;
	@Column(name = "mobile_number")
	private String mobileNumber;
	@Column(name = "create_dt")
	private LocalDate createDt;

}
