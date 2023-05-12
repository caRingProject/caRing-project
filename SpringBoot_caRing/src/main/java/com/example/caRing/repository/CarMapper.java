package com.example.caRing.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.caRing.entity.CarEntity;

public interface CarMapper extends JpaSpecificationExecutor<CarEntity> {

	List<CarEntity> findAll(Specification<CarEntity> spec);
	
}
