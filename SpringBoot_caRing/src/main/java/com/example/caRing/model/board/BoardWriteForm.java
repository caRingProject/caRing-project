package com.example.caRing.model.board;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class BoardWriteForm {
    @NotBlank
    private String title;
    @NotBlank
    private String contents;
    private MultipartFile attachedFile;

    public static Board toBoard(BoardWriteForm boardWriteForm) {
        Board board = new Board();
        board.setTitle(boardWriteForm.getTitle());
        board.setBoard_contents(boardWriteForm.getContents());
        return board;
    }
}