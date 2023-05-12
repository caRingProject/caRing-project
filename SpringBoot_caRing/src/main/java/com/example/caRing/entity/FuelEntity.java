package com.example.caRing.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class FuelEntity {

	@Id
	private Long fuel_id;
	
	@Column(name = "fuel_name")
	private String fuel_name;
}
