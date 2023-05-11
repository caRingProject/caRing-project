package com.example.caRing.model.reservation;

import com.example.caRing.model.board.BoardDTO;

import lombok.Data;

@Data
public class ReservationDTO {
	
	private Reservation reservation;
	private BoardDTO boardDTO;

}
