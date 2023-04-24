package com.example.caRing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.caRing.model.customer.CustomerJoinForm;
import com.example.caRing.model.customer.CustomerLoginForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("customer")
@Controller

class CustomerController {

	@GetMapping("join")
	public String joinForm(Model model) {
		model.addAttribute("joinForm", new CustomerJoinForm());
		return "customer/customer_join";
	}
	@GetMapping("login")
	public String loginForm(Model model) {
		model.addAttribute("loginForm", new CustomerLoginForm());
		return "customer/customer_login";
	}
}
