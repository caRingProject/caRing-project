package com.example.caRing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.caRing.model.customer.Customer;
import com.example.caRing.model.customer.CustomerJoinForm;
import com.example.caRing.model.customer.CustomerLoginForm;
import com.example.caRing.repository.CustomerMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("customer")
@Controller

class CustomerController {
	
	private final CustomerMapper customerMapper;
	
	@GetMapping("join")
	public String customerJoinForm(Model model) {
		model.addAttribute("customerJoinForm", new CustomerJoinForm());
		return "customer/customer_join";
	}
	@GetMapping("login")
	public String customerLoginForm(Model model) {
		model.addAttribute("customerLoginForm", new CustomerLoginForm());
		return "customer/customer_login";
	}
	
	@PostMapping("join")
    public String customerJoin(@Validated @ModelAttribute("customerJoinForm") CustomerJoinForm customerJoinForm,
                       BindingResult result) {

        if (result.hasErrors()) {
            return "customer/customer_join";
        }
        
        // 사용자로부터 입력받은 아이디로 데이터베이스에서 customer 를 검색한다.
        Customer customer = customerMapper.findCustomer(customerJoinForm.getCustomer_email());
        
        if (customer != null) {
        	result.reject("duplicate ID", "이미 가입된 아이디 입니다.");
            return "customer/customer_join";
        }
        
        // MemberJoinForm 객체를 Member 타입으로 변환하여 데이터베이스에 저장한다.
        customerMapper.saveCustomer(customerJoinForm.toCustomer(customerJoinForm));
        
        // 메인 페이지로 리다이렉트한다.
        return "redirect:/#";
    }
}
