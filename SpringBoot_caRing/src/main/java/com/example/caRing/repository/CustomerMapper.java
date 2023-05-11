package com.example.caRing.repository;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;


import com.example.caRing.model.customer.Customer;

@Mapper
public interface CustomerMapper {
	 void saveCustomer(Customer customer);
	 Customer findCustomer(String customer_email);
	 int customerEmailCheck(String customer_email);
	 
	 void send(String to);
}
