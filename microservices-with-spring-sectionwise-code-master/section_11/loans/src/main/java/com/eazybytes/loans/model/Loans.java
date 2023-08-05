package com.eazybytes.loans.model;

import java.sql.Date;

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
public class Loans {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "loan_number")
	private int loanNumber;
	
	@Column(name = "customer_id")
	private int customerId;
	
	@Column(name="start_dt")
	private Date startDt;
	
	@Column(name = "loan_type")
	private String loanType;
	
	@Column(name = "total_loan")
	private int totalLoan;
	
	@Column(name = "amount_paid")
	private int amountPaid;
	
	@Column(name = "outstanding_amount")
	private int outstandingAmount;
	
	@Column(name = "create_dt")
	private String createDt;
	
}
