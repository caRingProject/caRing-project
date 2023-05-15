package com.example.caRing.model.board;

import lombok.Data;

@Data
public class LocationPrice {
	
	private String rent_start;
	private String rent_end;
	private Double searchedLat;
	private Double searchedLng;
	private Long minPrice;
	private Long maxPrice;
	   
}
