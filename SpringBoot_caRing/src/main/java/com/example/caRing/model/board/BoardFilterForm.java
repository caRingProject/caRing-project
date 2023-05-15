package com.example.caRing.model.board;

import java.util.List;

import lombok.Data;

@Data
public class BoardFilterForm {

	private List<Number> brand_id;
	private List<Number> fuel_id;
	private List<String> seat;
	private List<Number> carType_id;
}
