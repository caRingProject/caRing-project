package com.example.caRing.model.board;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Board {
   private Long board_id;
   private String host_email;
   private String title;
   private String board_contents;
   private LocalDateTime created_time;
   private Long price;
   private String area;
   private Long carInfo_id;
   private LocalDateTime rent_start;
   private LocalDateTime rent_end;
}