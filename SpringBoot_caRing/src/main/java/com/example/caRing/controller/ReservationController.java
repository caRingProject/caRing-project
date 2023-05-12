package com.example.caRing.controller;

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
import com.example.caRing.model.reservation.Reservation;
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
    
//    // 모든 예약 목록 조회
//    @GetMapping("/list")
//    public String getReservationList(Model model) {
//        List<Reservation> reservationList = reservationMapper.findAllRe();
//        model.addAttribute("reservationList", reservationList);
//        return "reservation/list"; // 마이페이지의 list로 넣어야 할듯? 
//    }

//    // 예약 상세 조회
//    @GetMapping("/{reservation_Id}")
//    public String getReservation(@PathVariable("reservation_id") Long reservation_id, Model model) {
//    	
//        Reservation reservation = reservationMapper.getfindByReId(reservation_id);
//        model.addAttribute("reservation", reservation);
//        return "reservation/detail"; // 이것도 ?
//    }
    
    
    // 이걸로 board(게시판)에 있던 캘린더를 불러오게 해야함!! 
    
    // 예약 등록 페이지 이동
    @GetMapping("reservation")
    public String createReservationPage(Model model, @SessionAttribute(value = "loginCustomer", required = false) Customer loginCustomer,
    									@RequestParam String rent_start, @RequestParam String rent_end, @RequestParam Long board_id,
    									@RequestParam Long carInfo_id, @RequestParam Long price) {
 
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
			return "redirect:/customer/customer_login";
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
		mailService.sendMail(loginCustomer.getCustomer_email());
		
        return "redirect:/reservation/reservationCheck";	// chat !!! 
    }
        
    @GetMapping("/reservationCheck")
    public String reservationCheck (Model model) {
    	return "reservation/reservationCheck";
    }
        
     // reservation_date 를 숫자로 써서 
    	// 며칠 쓴건지 계산 하는건? board_id => price => Date reservation_date => total_price 이런 느낌으로 
    	
    	
		/*
		 * // Board 객체를 데이터베이스에서 가져온다고 가정 Board board = boardMapper.findBoard(board_id);
		 * 
		 * // rent_start와 rent_end 사이의 일 수 계산 long numOfDays =
		 * calculateDaysBetweenDates(rent_start, rent_end);
		 * 
		 * // 하루당 가격 계산 long Price = board.getPrice();
		 * 
		 * // 총 가격 계산 long totalPrice = Price * numOfDays;
		 * 
		 * 
		 * log.info("numOfDays: {}", numOfDays); log.info("days: {}", days);
		 * log.info("totalPrice: {}", totalPrice);
		 * 
		 * model.addAttribute("board", board); model.addAttribute("days", days);
		 * model.addAttribute("totalPrice", totalPrice);
		 * 
		 * 
		 * 
		 */	    
    
    // 이거 맞나?
    // 예약 삭제		이건 나중에 경로같은거 다 바꿔야 할듯 
    @DeleteMapping("/reservation_id/delete")
    public String deleteReservation(@PathVariable("reservation_id") String reservation_id) {
        reservationMapper.deleteById(reservation_id);
        return "redirect:/reservation/list";
    }
    
    
}
