package com.example.caRing.model.review;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Review {

	private Long review_id;
	private Long reservation_id;
	private String created_time;
	private Long rate;
	private String contents;
	
}
