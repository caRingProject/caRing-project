package com.example.caRing.model.customer;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CustomerJoinForm {
	@Size(min = 4 , max = 20)
	private String customer_id;
	@Size(min = 4 , max = 20)
	private String customer_password;
	@NotBlank
	private String customer_name;
	@NotBlank
	private String customer_address;
	@NotBlank
	private String customer_phone;
	@NotBlank
	private String license;
	@NotBlank
	private String email;
	@Past
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birth;
	private Long grade_id; 
	
	public static Customer toCustomer(CustomerJoinForm customerJoinForm) {
        Customer customer = new Customer();
        customer.setCustomer_id(customerJoinForm.getCustomer_id());
        customer.setCustomer_password(customerJoinForm.getCustomer_password());
        customer.setCustomer_name(customerJoinForm.getCustomer_name());
        customer.setCustomer_address(customerJoinForm.getCustomer_address());
        customer.setBirth(customerJoinForm.getBirth());
        customer.setEmail(customerJoinForm.getEmail());
        customer.setCustomer_phone(customerJoinForm.getCustomer_phone());
        customer.setLicense(customerJoinForm.getLicense());
        return customer;
	}
}
