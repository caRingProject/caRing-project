package com.example.caRing.model.host;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.caRing.model.customer.Customer;
import com.example.caRing.model.customer.CustomerJoinForm;

import lombok.Data;

@Data
public class HostJoinForm {
	
	@Email
	private String host_email;
	@Size(min = 4 , max = 20)
	private String host_password;
	@NotBlank
	private String host_name;
	@Size(min = 11, max = 11, message = "\"-\" 제외 11자리 숫자로 입력해주세요.")
	private String host_phone;
	
	public static Host toHost(HostJoinForm hostJoinForm) {
        Host host = new Host();
        host.setHost_email(hostJoinForm.getHost_email());
        host.setHost_password(hostJoinForm.getHost_password());
        host.setHost_name(hostJoinForm.getHost_name());
        host.setHost_phone(hostJoinForm.getHost_phone());
        return host;
	}
}
