package com.example.caRing.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.caRing.model.host.Host;

@Mapper
public interface HostMapper {
	void saveHost(Host host);
	Host findHost(String host_email);
	int hostEmailCheck(String host_email);
	void updateHost(Host host);
	void removeHost(String host_email);
	
	// 호스트 예약 수락
	void updateStatus2(Long reservation_id);
	
	// 호스트 예약 거절
	void updateStatus5(Long reservation_id);
}


