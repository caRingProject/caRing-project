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
public class BrandEntity {

	@Id
	private Long brand_id;
	
	@Column(name = "brand_name")
	private String brand_name;
	
}
