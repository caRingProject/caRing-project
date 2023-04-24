package com.example.caRing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
//        // 이메일 주소에 '@' 문자가 포함되어 있는지 확인한다.
//        if (!joinForm.getEmail().contains("@")) {
//            // BindingResult 객체에 GlobalError 를 추가한다.
//            result.reject("emailError", "이메일 형식이 잘못되었습니다.");
//            // member/joinForm.html 페이지를 리턴한다.
//            return "member/joinForm";
//        }
//        // 사용자로부터 입력받은 아이디로 데이터베이스에서 Member 를 검색한다.
//        Member member = memberMapper.findMember(joinForm.getMember_id());
//        // 사용자 정보가 존재하면
//        if (member != null) {
//            log.info("이미 가입된 아이디 입니다.");
//            // BindingResult 객체에 GlobalError 를 추가한다.
//            result.reject("duplicate ID", "이미 가입된 아이디 입니다.");
//            // member/joinForm.html 페이지를 리턴한다.
//            return "member/joinForm";
//        }
        // MemberJoinForm 객체를 Member 타입으로 변환하여 데이터베이스에 저장한다.
        customerMapper.saveCustomer(customerJoinForm.toCustomer(customerJoinForm));
        // 메인 페이지로 리다이렉트한다.
        return "redirect:/";
    }
}
