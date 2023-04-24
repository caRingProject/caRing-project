package com.example.caRing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.caRing.model.customer.CustomerJoinForm;
import com.example.caRing.model.customer.CustomerLoginForm;
import com.example.caRing.model.host.HostJoinForm;
import com.example.caRing.model.host.HostLoginForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("host")
@Controller

public class HostController {
	
	@GetMapping("join")
	public String joinForm(Model model) {
		model.addAttribute("joinForm", new HostJoinForm());
		return "host/host_join";
	}
	@GetMapping("login")
	public String loginForm(Model model) {
		model.addAttribute("loginForm", new HostLoginForm());
		return "host/host_login";
	}
	
	
	
	
}


