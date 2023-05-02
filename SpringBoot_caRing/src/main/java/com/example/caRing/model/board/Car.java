package com.example.caRing.model.board;

import lombok.Data;

@Data
public class Car {

	private Long carinfo_id;
	private String car_type;
	private String car_name;
	private Long car_year;
	private Long seat;
	private String fuel;
	private String feature;
	private Long brand_id;
	
}
