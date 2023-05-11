package com.example.caRing.model.board;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

@Data
public class Board {
   private Long board_id;
   private String host_email;
   private String title;
   private String board_contents;
   private LocalDateTime created_time;
   private Long price;
   private Double lat;
   private Double lng;
   private Long carInfo_id;
   private String rent_start;
   private String rent_end;
}