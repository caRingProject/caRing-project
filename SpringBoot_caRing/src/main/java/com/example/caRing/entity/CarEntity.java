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
public class CarEntity {

	@Id
	private Long carInfo_id;
	
	@Column(name = "brand_id")
	private Long brand_id;
	
	
	@Column(name = "carType_id")
	private Long carType_id;
	

	@Column(name = "seat")
	private String seat;
	
	@Column(name = "fuel_id")
	private Long fuel_id;
	
}
