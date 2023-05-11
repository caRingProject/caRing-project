package com.example.caRing.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.caRing.model.customer.Customer;
import com.example.caRing.model.customer.CustomerJoinForm;
import com.example.caRing.model.customer.CustomerLoginForm;
import com.example.caRing.repository.CustomerMapper;
import com.example.caRing.util.MailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("customer")
@Controller
class CustomerController {
	
	private final CustomerMapper customerMapper;
	private final MailService mailService;
	
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
	
	@Transactional(readOnly = true)
	@PostMapping("join")
    public String customerJoin(@Validated @ModelAttribute("customerJoinForm") CustomerJoinForm customerJoinForm,
                       BindingResult result) {

        if (result.hasErrors()) {
            return "customer/customer_join";
        }
        
        // 사용자로부터 입력받은 아이디로 데이터베이스에서 customer 를 검색한다.
        Customer customer = customerMapper.findCustomer(customerJoinForm.getCustomer_email());
        
        if (customer != null) {
        	result.rejectValue("customer_email", "emailError", "이미 가입된 아이디 입니다.");
            return "customer/customer_join";
        }
        
        if (customerJoinForm.getCustomer_phone().contains("-")) {
        	result.rejectValue("customer_phone", "phoneError", "\"-\"를 포함하지 않고 입력해주세요.");
            return "customer/customer_join";
        }
        
        if (customerJoinForm.getLicense().contains("-")) {
        	result.rejectValue("license", "licenseError", "\"-\"를 포함하지 않고 입력해주세요.");
            return "customer/customer_join";
        }
        
        	
        // MemberJoinForm 객체를 Member 타입으로 변환하여 데이터베이스에 저장한다.
        customerMapper.saveCustomer(customerJoinForm.toCustomer(customerJoinForm));
        
        // 메인 페이지로 리다이렉트한다.
        return "redirect:/customer/login";
    }
	
	@ResponseBody
	@PostMapping("emailCheck")
	public int customerEmailCheck(@RequestParam String customer_email) throws Exception {
		int cnt = customerMapper.customerEmailCheck(customer_email);
		return cnt; 
	}
	
	// 로그인 처리
    @PostMapping("login")
    public String login(@Validated @ModelAttribute("customerLoginForm") CustomerLoginForm customerLoginForm,
                        BindingResult result,
                        HttpServletRequest request,
                        @RequestParam(defaultValue = "/") String redirectURL) {
        // validation 에 실패하면 member/loginForm 페이지로 돌아간다.
        if (result.hasErrors()) {
            return "customer/customer_login";
        }
        // 사용자가 입력한 이이디에 해당하는 Member 정보를 데이터베이스에서 가져온다.
        Customer customer = customerMapper.findCustomer(customerLoginForm.getCustomer_email());
        // Member 가 존재하지 않거나 패스워드가 다르면
        if (customer == null || !customer.getCustomer_password().equals(customerLoginForm.getCustomer_password())) {
            // BindingResult 객체에 GlobalError 를 발생시킨다.
            result.reject("loginError", "아이디가 없거나 패스워드가 다릅니다.");
            // member/loginForm.html 페이지로 돌아간다.
            return "customer/customer_login";
        }

        // Request 객체에서 Session 객체를 꺼내온다.
        HttpSession session = request.getSession();
        // Session 에 'loginMember' 라는 이름으로 Member 객체를 저장한다.
        session.setAttribute("loginCustomer", customer);
        // 메인 페이지로 리다이렉트 한다.
        return "redirect:" + redirectURL;
    }
    
    // 로그아웃
 	@GetMapping("logout")
 	public String logout(HttpServletRequest request) {
 		// 세션
 		HttpSession session = request.getSession();
 		session.invalidate();
 		return "redirect:/";
 	}
}
