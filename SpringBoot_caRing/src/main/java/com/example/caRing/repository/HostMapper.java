package com.example.caRing.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.caRing.model.host.Host;

@Mapper
public interface HostMapper {
	void saveHost(Host host);
	Host findHost(String host_email);
	int hostEmailCheck(String host_email);
}


