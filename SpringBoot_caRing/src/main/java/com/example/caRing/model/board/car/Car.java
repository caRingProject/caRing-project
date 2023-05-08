package com.example.caRing.model.board.car;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Car {

	private Long carInfo_id;
	private Long brand_id;
	private Long carType_id;
	private String car_name;
	private Long car_year;
	private String seat;
	private Long fuel_id;
	private String host_email;
	private String thumbnail;
	
	public Car(Long brand_id, Long carType_id, String car_name, Long car_year, String seat, Long fuel_id, String thumbnail) {
		this.brand_id = brand_id;
		this.carType_id = carType_id;
		this.car_name = car_name;
		this.car_year = car_year;
		this.seat = seat;
		this.fuel_id = fuel_id;
		this.thumbnail = thumbnail;
	}
		
}
