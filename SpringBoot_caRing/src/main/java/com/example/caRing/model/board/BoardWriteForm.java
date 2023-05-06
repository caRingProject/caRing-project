package com.example.caRing.model.board;

import java.time.LocalDateTime;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class BoardWriteForm {
	
	private Long carInfo_id;
	
	private String rent_start;
	private String rent_end;
	@NotBlank
	private String board_contents;
	@NotBlank
	private String area;
	private Long price;

	public static Board toBoard(BoardWriteForm boardWriteForm) {
		Board board = new Board();
		board.setCarInfo_id(boardWriteForm.getCarInfo_id());
		board.setRent_start(boardWriteForm.getRent_start());
		board.setRent_end(boardWriteForm.getRent_end());
		board.setBoard_contents(boardWriteForm.getBoard_contents());
		board.setArea(boardWriteForm.getArea());
		board.setPrice(boardWriteForm.getPrice());
		return board;

	}
}