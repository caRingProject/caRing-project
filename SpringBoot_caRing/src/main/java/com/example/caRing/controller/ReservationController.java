package com.example.caRing.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.caRing.model.board.Board;
import com.example.caRing.model.board.BoardDTO;
import com.example.caRing.model.board.car.Car;
import com.example.caRing.model.customer.Customer;
import com.example.caRing.model.host.Host;
import com.example.caRing.model.reservation.Reservation;
import com.example.caRing.model.reservation.ReservationDTO;
import com.example.caRing.repository.BoardMapper;
import com.example.caRing.repository.ReservationMapper;
import com.example.caRing.util.MailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("reservation")
@Controller
public class ReservationController {

	@Autowired
	private ReservationMapper reservationMapper;

	@Autowired
	private BoardMapper boardMapper;

	@Autowired
	private MailService mailService;

	// 예약 등록 페이지 이동
	@GetMapping("reservation")
	public String createReservationPage(Model model,
			@SessionAttribute(value = "loginCustomer", required = false) Customer loginCustomer,
			@RequestParam String rent_start, @RequestParam String rent_end, @RequestParam Long board_id,
			@RequestParam Long carInfo_id, @RequestParam Long price) {

		// Customer 로그인 확인용
		if (loginCustomer == null) {
			return "redirect:/customer/login";
		}

		Board board = boardMapper.findBoard(board_id);
		Car car = boardMapper.findCarInfoByCarInfoId(carInfo_id);
		BoardDTO boardDTO = new BoardDTO();
		boardDTO.setBoard(board);
		boardDTO.setCar(car);
		model.addAttribute("boardDTO", boardDTO);
		Reservation reservation = new Reservation();
		reservation.setRent_start(rent_start);
		reservation.setRent_end(rent_end);
		model.addAttribute("reservation", reservation);

		return "reservation/reservation";
	}

	// 예약 등록
	@PostMapping("/reservation")
	public String createReservation(Model model, @ModelAttribute Reservation reservation, @RequestParam Long totalPrice,
			@SessionAttribute(value = "loginCustomer", required = false) Customer loginCustomer) {

		// Customer 로그인 확인용
		if (loginCustomer == null) {
			return "redirect:/customer/login";
		}

//		if (result.hasErrors()) {
//			return "board/board_id";
//		}

//		// 중복 예약 확인
//	    if (reservationMapper.checkDuplicateReservation(reservation.getBoard_id(), rent_start, rent_end)) {
//	        throw new IllegalArgumentException("중복된 예약이 있습니다.");
//	    }

		// 출력용
//		log.info("rent_start: {}", rent_start);
//		log.info("rent_end: {}", rent_end);
//		Reservation reservation = new Reservation();
//		reservation.setRent_start(rent_start);
//		reservation.setRent_end(rent_end);
		log.info("reservation: {}", reservation);
		reservation.setTotal_price(totalPrice);
		reservation.setCustomer_email(loginCustomer.getCustomer_email());
		log.info("reservation: {}", reservation);

		reservationMapper.saveRe(reservation);
		mailService.reservationRequestMail(loginCustomer.getCustomer_email());

		return "redirect:/reservation/reservationCheck"; // chat !!!
	}

	@GetMapping("/reservationCheck")
	public String reservationCheck(Model model) {
		return "reservation/reservationCheck";
	}

	// 유저 예약 조회
	@GetMapping("/reservationlist")
	public String reservationcustomer(Model model,
			@SessionAttribute(value = "loginCustomer", required = false) Customer loginCustomer) {
		List<Reservation> reservations = reservationMapper
				.findReservationByCustomerEmail(loginCustomer.getCustomer_email());
		log.info("reservations: {}", reservations);
		model.addAttribute("reservations", reservations);
		// 예약 번호, 예약 날짜, 보드 타이틀, 일일 금액, 총 금액
		List<ReservationDTO> reservationDTOs = new ArrayList<>();
		for (Reservation reservation : reservations) {
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
			reservationDTOs.add(reservationDTO);
		}
		log.info("reservationDTOs: {}", reservationDTOs);
		model.addAttribute("reservationDTOs", reservationDTOs);
		return "reservation/reservationList";
	}

	// 게시글 삭제
	@PostMapping("deleteByReId")
	public String deleteByReId(@SessionAttribute(value = "loginHost", required = false) Host loginHost,
			@RequestParam Long reservation_id) {

		// 예약을 삭제한다.
		reservationMapper.deleteByReId(reservation_id);
		// board/list 로 리다이렉트 한다.
		return "redirect:/host/main";
	}

}
