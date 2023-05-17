package com.example.caRing.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.caRing.model.board.Board;
import com.example.caRing.model.board.BoardDTO;
import com.example.caRing.model.board.car.Car;
import com.example.caRing.model.host.Host;
import com.example.caRing.model.reservation.Reservation;
import com.example.caRing.model.reservation.ReservationDTO;
import com.example.caRing.model.review.Review;
import com.example.caRing.repository.BoardMapper;
import com.example.caRing.repository.HostMapper;
import com.example.caRing.repository.ReservationMapper;
import com.example.caRing.repository.ReviewMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("review")
@Controller
public class ReviewController {
	
	@Autowired
	private ReviewMapper reviewMapper;
	
	@Autowired
	private ReservationMapper reservationMapper;
	
	@Autowired
	private BoardMapper boardMapper;
	
	@Autowired
	private HostMapper hostMapper;

	@GetMapping("/write")
	public String reviewWrite(Model model, @RequestParam Long reservation_id) {
		
		Reservation reservation = reservationMapper.findReservationByReservationId(reservation_id);
		
		ReservationDTO reservationDTO = new ReservationDTO();
		
		// 날짜 형식 변경
		String start = reservation.getRent_start();
		String newStart = start.substring(0, 10);
		reservation.setRent_start(newStart);
		String end = reservation.getRent_end();
		String newEnd = end.substring(0, 10);
		reservation.setRent_end(newEnd);
					
		reservationDTO.setReservation(reservation);
		
		Board board = boardMapper.findBoard(reservation.getBoard_id());
		Car car = boardMapper.findCarInfoByCarInfoId(board.getCarInfo_id());
		
		BoardDTO boardDTO = new BoardDTO();
		boardDTO.setBoard(board);
		boardDTO.setCar(car);
		
		reservationDTO.setBoardDTO(boardDTO);
		
		model.addAttribute("reservationDTO", reservationDTO);
		
		Host host = hostMapper.findHost(board.getHost_email());
		
		model.addAttribute("host", host);
		
		return "review/review";
	}
	
	@PostMapping("write")
	public String saveReview(@ModelAttribute Review review) {
		log.info("review: {}", review);
		reviewMapper.saveReview(review);
		reservationMapper.updateStatus(review.getReservation_id());
		return "redirect:/reservation/reservationlist";
	}
}
