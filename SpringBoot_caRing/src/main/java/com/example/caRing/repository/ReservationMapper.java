package com.example.caRing.repository;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.caRing.model.reservation.Reservation;

@Mapper
public interface ReservationMapper {
	
	// board_id를 불러오기 
	//void saveBoard_id(Board board_id);
	
	String findRent_start(String rent_start);
	String findRent_end(String rent_end);
	
	// 모든 예약 정보 조회
    List<Reservation> findAllRe();
    
    // 특정 예약 정보 조회
    List<Reservation> findByReId(Long reservation_id);
    
    // 예약 정보 등록
    void saveRe(Reservation reservation);
    String setRe(Long reservation_id);
    
    
    // 예약 날짜
    Date reservationDate(Date reservation_date);
    
    // 예약 정보 삭제
    void deleteById(String reservationId);
    
    // 예약 번호로 게시글 출력
    //Reservation findReservation (Long reservation_id);
    
    // 사진 출력
    //List<AttachedFile> findFileByAttachedFileId(Long carInfo_id);
    
    // 중복 예약 확인
    boolean checkDuplicateReservation(String boardId, String rentStart, String rentEnd);
    
}
	
	//오늘 점심 맥도날드니깐 준비해줘 -윤상-
	


