package com.example.caRing.model.customer;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Customer {

	private String customer_email;
	private String customer_password;
	private String customer_name;
	private String customer_phone;
	private String license;
	private String customer_img;
}
 