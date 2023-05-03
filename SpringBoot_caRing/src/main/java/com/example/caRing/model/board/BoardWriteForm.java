package com.example.caRing.model.board;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class BoardWriteForm {
	
	private Long carInfo_id;
	private LocalDateTime rent_start;
	private LocalDateTime rent_end;
	private String board_contents;
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