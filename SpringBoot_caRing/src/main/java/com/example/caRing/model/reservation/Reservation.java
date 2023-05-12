package com.example.caRing.model.reservation;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Reservation {
	private Long reservation_id;
	private Date reservation_date;
	private String rent_start;
	private String rent_end;
	private String customer_email;
	private Long board_id;
	private Long total_price;
	private String status;
	
	public Reservation(String rent_start, String rent_end) {
		super();
		this.rent_start = rent_start;
		this.rent_end = rent_end;
	}
}
