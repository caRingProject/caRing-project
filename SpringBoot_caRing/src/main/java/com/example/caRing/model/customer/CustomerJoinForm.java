package com.example.caRing.model.customer;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CustomerJoinForm {
	@Email
	private String customer_email;
	@Size(min = 4 , max = 20)
	private String customer_password;
	@NotBlank
	private String customer_name;
	@Size(min = 11, max = 11, message = "\"-\" 제외 11자리 숫자로 입력해주세요.")
	private String customer_phone;
	@Size(min = 12, max = 12, message = "\"-\" 제외 12자리 숫자로 입력해주세요.")
	private String license;
	
	public static Customer toCustomer(CustomerJoinForm customerJoinForm) {
        Customer customer = new Customer();
        customer.setCustomer_email(customerJoinForm.getCustomer_email());
        customer.setCustomer_password(customerJoinForm.getCustomer_password());
        customer.setCustomer_name(customerJoinForm.getCustomer_name());
        customer.setCustomer_phone(customerJoinForm.getCustomer_phone());
        customer.setLicense(customerJoinForm.getLicense());
        return customer;
	}
}
