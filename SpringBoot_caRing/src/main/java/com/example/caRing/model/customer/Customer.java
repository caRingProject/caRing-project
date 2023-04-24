package com.example.caRing.model.customer;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Customer {

	private String customer_id;
	private String customer_password;
	private String customer_name;
	private String customer_address;
	private String customer_phone;
	private String license;
	private String email;
	private LocalDate birth;
	private Long grade_id; 
}
 