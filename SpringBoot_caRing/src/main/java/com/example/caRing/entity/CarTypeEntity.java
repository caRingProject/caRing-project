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
public class CarTypeEntity {

	@Id
	private Long carType_id;
	
	@Column(name = "carType_name")
	private String carType_name;
	
}
