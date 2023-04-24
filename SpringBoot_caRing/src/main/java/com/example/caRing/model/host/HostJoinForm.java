package com.example.caRing.model.host;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.caRing.model.customer.Customer;
import com.example.caRing.model.customer.CustomerJoinForm;

import lombok.Data;

@Data
public class HostJoinForm {
	
	@NotBlank
	private String host_email;
	@Size(min = 4 , max = 20)
	private String host_password;
	@NotBlank
	private String host_name;
	@NotBlank
	private String host_phone;
	
	public static Host toHost(HostJoinForm hostJoinForm) {
        Host host = new Host();
        host.setHost_password(hostJoinForm.getHost_password());
        host.setHost_name(hostJoinForm.getHost_name());
        host.setHost_phone(hostJoinForm.getHost_phone());
        return host;
	}
}
