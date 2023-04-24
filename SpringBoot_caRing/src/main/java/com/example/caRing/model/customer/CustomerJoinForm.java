package com.example.caRing.model.customer;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CustomerJoinForm {
	@NotBlank
	private String customer_email;
	@Size(min = 4 , max = 20)
	private String customer_password;
	@NotBlank
	private String customer_name;
	@NotBlank
	private String customer_phone;
	@NotBlank
	private String license;
	
	public static Customer toCustomer(CustomerJoinForm customerJoinForm) {
        Customer customer = new Customer();
        customer.setCustomer_password(customerJoinForm.getCustomer_password());
        customer.setCustomer_name(customerJoinForm.getCustomer_name());
        customer.setCustomer_phone(customerJoinForm.getCustomer_phone());
        customer.setLicense(customerJoinForm.getLicense());
        return customer;
	}
}
