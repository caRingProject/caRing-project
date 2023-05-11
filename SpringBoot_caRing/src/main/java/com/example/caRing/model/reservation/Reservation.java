package com.example.caRing.model.reservation;

import java.util.Date;

import lombok.Data;

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
	
}
