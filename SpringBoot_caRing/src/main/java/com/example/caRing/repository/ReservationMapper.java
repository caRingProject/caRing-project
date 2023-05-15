package com.example.caRing.repository;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.caRing.model.reservation.Reservation;

@Mapper
public interface ReservationMapper {

	// 예약 정보 등록
	void saveRe(Reservation reservation);

	// 예약 정보 삭제
	void deleteById(String reservationId);

	// 예약 번호로 게시글 출력
	Reservation findReservationByReservationId (Long reservation_id);
	
	// 호스트 예약 조회
	List<Reservation> findReservationByHostEmail(String host_email);

	// 유저 예약 조회
	List<Reservation> findReservationByCustomerEmail(String customer_email);
	
	// 상태 업데이트
	void updateStatus(Long reservation_id);
	
	List<Reservation> findReservationByBoardId(Long board_id);
	
	// 예약 정보 삭제
    void deleteByReId(Long reservation_id);
	
}
