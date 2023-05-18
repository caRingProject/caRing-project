package com.example.caRing.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.example.caRing.model.board.Board;
import com.example.caRing.model.board.BoardDTO;
import com.example.caRing.model.board.BoardFilterForm;
import com.example.caRing.model.board.BoardSearchForm;
import com.example.caRing.model.board.BoardWriteForm;
import com.example.caRing.model.board.Dates;
import com.example.caRing.model.board.Location;
import com.example.caRing.model.board.LocationPrice;
import com.example.caRing.model.board.car.AttachedFile;
import com.example.caRing.model.board.car.Brand;
import com.example.caRing.model.board.car.Car;
import com.example.caRing.model.board.car.CarType;
import com.example.caRing.model.board.car.Feature;
import com.example.caRing.model.board.car.Fuel;
import com.example.caRing.model.board.car.OptionList;
import com.example.caRing.model.host.Host;
import com.example.caRing.model.reservation.Reservation;
import com.example.caRing.model.reservation.ReservationDTO;
import com.example.caRing.repository.BoardMapper;
import com.example.caRing.repository.HostMapper;
import com.example.caRing.repository.ReservationMapper;
import com.example.caRing.repository.ReviewMapper;
import com.example.caRing.util.FileService;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("board")
@Controller
public class BoardControlller {

	@Autowired
	private BoardMapper boardMapper;
	@Autowired
	private HostMapper hostMapper;
	@Autowired
	private FileService fileService;
	@Autowired
	private ReviewMapper reviewMapper;
	@Autowired
	private ReservationMapper reservationMapper;

	@Value("${file.upload.path}")
	private String uploadPath;

	// 차량 등록 페이지 이동
	@GetMapping("car_registration")
	public String car_registration(Model model,
			@SessionAttribute(value = "loginHost", required = false) Host loginHost) {

		Host host = hostMapper.findHost(loginHost.getHost_email());
		model.addAttribute("host", host);
		// 브랜드 출력
		List<Brand> brands = boardMapper.findBrand();
//		log.info("brands: {}", brands);
		model.addAttribute("brands", brands);
		// 차종 출력
		List<CarType> carTypes = boardMapper.findCarType();
		model.addAttribute("carTypes", carTypes);
		// 유종 출력
		List<Fuel> fuels = boardMapper.findFuel();
		model.addAttribute("fuels", fuels);
		// 특징 출력
		List<Feature> features = boardMapper.findFeature();
		model.addAttribute("features", features);

		return "board/car_registration";
	}

	// 차량 등록
	@Transactional(readOnly = true)
	@PostMapping("car_registration")
	public String carSave(Model model, @ModelAttribute Car car, @ModelAttribute AttachedFile attachedFile,
			@SessionAttribute(name = "loginHost", required = true) Host loginHost,
			@RequestParam List<MultipartFile> carUpload, OptionList optionList) {
		Host host = hostMapper.findHost(loginHost.getHost_email());
		model.addAttribute("host", host);

		car.setHost_email(loginHost.getHost_email());
		boardMapper.saveCar(car);

		for (int i = 0; i < carUpload.size(); i++) {
			AttachedFile saveFile = fileService.saveFile(carUpload.get(i));
			saveFile.setCarInfo_id(car.getCarInfo_id());

			boardMapper.saveFile(saveFile);
			if (i == 0) {
				String fullPath = "/uploadImg/" + saveFile.getSaved_filename();
//				log.info("fullPath: {}", fullPath);
				car.setThumbnail(fullPath);
//				log.info("car: {}", car);
			}
		}
		boardMapper.updateCar(car);

		optionList.setCarInfo_id(car.getCarInfo_id());
		boardMapper.saveOption(optionList);

		return "redirect:/host/main";
	}

	// 게시글 등록 페이지 이동
	@GetMapping("write")
	public String boardWriteForm(Model model, @SessionAttribute(value = "loginHost", required = false) Host loginHost) {
		Host host = hostMapper.findHost(loginHost.getHost_email());
		model.addAttribute("host", host);

		model.addAttribute("boardWriteForm", new BoardWriteForm());
		String host_email = loginHost.getHost_email();

		List<Car> carInfo = boardMapper.findCarInfoByEmail(host_email);
		model.addAttribute("carInfo", carInfo);

		return "board/board_write";
	}

	// 게시글 등록

	@PostMapping("write")
	public String boardWrite(@ModelAttribute("carInfo") Car car, BindingResult result,
			@SessionAttribute(value = "loginHost", required = false) Host loginHost,
			@Validated @ModelAttribute("boardWriteForm") BoardWriteForm boardWriteForm, @RequestParam Long carlist,
			@RequestParam Double lat, @RequestParam Double lng) {

		if (loginHost == null) {
			return "redirect:/host/host_login";
		}

		if (result.hasErrors()) {
			return "board/board_write";
		}

		Board board = BoardWriteForm.toBoard(boardWriteForm);
		board.setLat(lat);
		board.setLng(lng);
		board.setHost_email(loginHost.getHost_email());
		board.setCarInfo_id(carlist);

		String title = boardMapper.setTitle(carlist);
		board.setTitle(title);

		boardMapper.saveBoard(board);

		return "redirect:/host/main";
	}

//	@GetMapping("list")
//	public String boardList(Model model, @ModelAttribute BoardSearchForm boardSearchForm,
//			@ModelAttribute Location location) {
//		model.addAttribute("boardSearchForm", boardSearchForm);
////		log.info("location: {}", location);
//		List<Board> lists = boardMapper.findLocation(location);
////		log.info("lists: {}", lists);
//		List<BoardDTO> boardDTOs = new ArrayList<>();
//		for (Board list : lists) {
//			Car car = boardMapper.findCarInfoByCarInfoId(list.getCarInfo_id());
//			BoardDTO dto = new BoardDTO();
//			dto.setBoard(list);
//			dto.setCar(car);
//			boardDTOs.add(dto);
//		}
//		// 브랜드 출력
//		List<Brand> brands = boardMapper.findBrand();
////				log.info("brands: {}", brands);
//		model.addAttribute("brands", brands);
//		// 차종 출력
//		List<CarType> carTypes = boardMapper.findCarType();
//		model.addAttribute("carTypes", carTypes);
//		// 유종 출력
//		List<Fuel> fuels = boardMapper.findFuel();
//		model.addAttribute("fuels", fuels);
//		// 특징 출력
//		List<Feature> features = boardMapper.findFeature();
//		model.addAttribute("features", features);
//		model.addAttribute("boardDTOs", boardDTOs);
//		return "board/board_list";
//	}

	@GetMapping("read")
	public String boardRead(@RequestParam Long board_id, Model model, @RequestParam String rent_start, @RequestParam String rent_end) {
		// 게시글 출력
		Board board = boardMapper.findBoard(board_id);
//		log.info("board: {}", board);
		String start = board.getRent_start();
		String newStart = start.substring(0, 10);
		board.setRent_start(newStart);
		String end = board.getRent_end();
		String newEnd = end.substring(0, 10);
		board.setRent_end(newEnd);
		model.addAttribute("board", board);

		// 호스트 출력
		Host host = hostMapper.findHost(board.getHost_email());
		model.addAttribute("host", host);

		// 차 출력
		Car car = boardMapper.findCarInfoByCarInfoId(board.getCarInfo_id());
		model.addAttribute("car", car);
//		log.info("car: {}", car);
		CarType carType = boardMapper.findCarTypeById(car.getCarType_id());
		model.addAttribute("carType", carType);
		Fuel fuel = boardMapper.findFuelById(car.getFuel_id());
		model.addAttribute("fuel", fuel);

		Long carInfo_id = board.getCarInfo_id();

		// 옵션 리스트 출력
		String optionValue = boardMapper.findOptionListById(carInfo_id);
		String[] optionList = optionValue.split(",");
//		System.out.println(Arrays.toString(optionList));
		List<String> option = new ArrayList<>();
		for (int i = 0; i < optionList.length; i++) {
			option.add(optionList[i]);
		}
//		log.info("option: {}", option);
		model.addAttribute("option", option);

		// 사진 출력
		List<AttachedFile> attachedFiles = boardMapper.findFileByAttachedFileId(carInfo_id);
//		log.info("attachedFiles: {}", attachedFiles);
		List<String> paths = new ArrayList<>();
		String fullPath = null;

		for (AttachedFile image : attachedFiles) {
			fullPath = "/uploadImg/" + image.getSaved_filename();
//			log.info("fullPath: {}", fullPath);
			paths.add(fullPath);
		}
		model.addAttribute("paths", paths);
//		log.info("paths: {}", paths);

		BoardDTO boardDTO = new BoardDTO();
		boardDTO.setBoard(board);
		boardDTO.setCar(car);
		ReservationDTO reservationDTO = new ReservationDTO();
		Reservation reservation = new Reservation();
		reservationDTO.setBoardDTO(boardDTO);
		reservationDTO.setReservation(reservation);
		model.addAttribute("reservationDTO", reservationDTO);
		
		List<Reservation> reservationChecks = reservationMapper.findReservationByBoardId(board_id);
		model.addAttribute("reservationChecks", reservationChecks);

		Long rate = reviewMapper.findRateByBoardId(board_id);
		model.addAttribute("rate", rate);

		return "board/board_read";
	}

/////////////////////////////////////////////////////////////////////// yoon

	@GetMapping("list/priceasc")
	public String boardListAsc(Model model, @RequestParam("searchedLat") double searchedLat,
			@RequestParam("searchedLng") double searchedLng, @RequestParam("rent_start") String rent_Start,
			@RequestParam("rent_end") String rent_End, @RequestParam("location") String location,
			@ModelAttribute BoardSearchForm boardSearchForm) {
//		log.info("bsf: {}", searchedLat);

		boardSearchForm.setLocation(location);
		boardSearchForm.setRent_end(rent_End);
		boardSearchForm.setRent_start(rent_Start);
		boardSearchForm.setSearchedLat(searchedLat);
		boardSearchForm.setSearchedLng(searchedLng);
		Location location1 = new Location();
		location1.setSearchedLat(searchedLat);
		location1.setSearchedLng(searchedLng);
		location1.setRent_start(rent_Start);
		location1.setRent_end(rent_End);
//		System.out.println(location1);
		List<Board> lists = boardMapper.findLocationAsc(location1);

//		log.info("lists: {}", lists);

		List<BoardDTO> boardDTOs = new ArrayList<>();
		for (Board list : lists) {
			Car car = boardMapper.findCarInfoByCarInfoId(list.getCarInfo_id());
			BoardDTO dto = new BoardDTO();
			dto.setBoard(list);
			dto.setCar(car);
			boardDTOs.add(dto);
		}

		List<Brand> brands = boardMapper.findBrand();
		model.addAttribute("brands", brands);
		List<CarType> carTypes = boardMapper.findCarType();
		model.addAttribute("carTypes", carTypes);
		List<Fuel> fuels = boardMapper.findFuel();
		model.addAttribute("fuels", fuels);
		List<Feature> features = boardMapper.findFeature();
		model.addAttribute("features", features);
		model.addAttribute("boardDTOs", boardDTOs);
		return "board/board_list";
	}

	@GetMapping("list/pricedesc")
	public String boardListDesc(Model model, @RequestParam("searchedLat") double searchedLat,
			@RequestParam("searchedLng") double searchedLng, @RequestParam("rent_start") String rent_Start,
			@RequestParam("rent_end") String rent_End, @RequestParam("location") String location,
			@ModelAttribute BoardSearchForm boardSearchForm) {

//		log.info("bsf: {}", searchedLat);

		boardSearchForm.setLocation(location);
		boardSearchForm.setRent_end(rent_End);
		boardSearchForm.setRent_start(rent_Start);
		boardSearchForm.setSearchedLat(searchedLat);
		boardSearchForm.setSearchedLng(searchedLng);
		Location location1 = new Location();
		location1.setSearchedLat(searchedLat);
		location1.setSearchedLng(searchedLng);
		location1.setRent_start(rent_Start);
		location1.setRent_end(rent_End);
//		System.out.println(location1);
		List<Board> lists = boardMapper.findLocationDesc(location1);

//		log.info("lists: {}", lists);

		List<BoardDTO> boardDTOs = new ArrayList<>();
		for (Board list : lists) {
			Car car = boardMapper.findCarInfoByCarInfoId(list.getCarInfo_id());
			BoardDTO dto = new BoardDTO();
			dto.setBoard(list);
			dto.setCar(car);
			boardDTOs.add(dto);
		}

		List<Brand> brands = boardMapper.findBrand();
		model.addAttribute("brands", brands);
		List<CarType> carTypes = boardMapper.findCarType();
		model.addAttribute("carTypes", carTypes);
		List<Fuel> fuels = boardMapper.findFuel();
		model.addAttribute("fuels", fuels);
		List<Feature> features = boardMapper.findFeature();
		model.addAttribute("features", features);
		model.addAttribute("boardDTOs", boardDTOs);
		return "board/board_list";

	}

	@GetMapping("list/distance")
	public String boardListDistance(Model model, @RequestParam("searchedLat") double searchedLat,
			@RequestParam("searchedLng") double searchedLng, @RequestParam("rent_start") String rent_Start,
			@RequestParam("rent_end") String rent_End, @RequestParam("location") String location,
			@ModelAttribute BoardSearchForm boardSearchForm) {

//		log.info("bsf: {}", searchedLat);

		boardSearchForm.setLocation(location);
		boardSearchForm.setRent_end(rent_End);
		boardSearchForm.setRent_start(rent_Start);
		boardSearchForm.setSearchedLat(searchedLat);
		boardSearchForm.setSearchedLng(searchedLng);
		Location location1 = new Location();
		location1.setSearchedLat(searchedLat);
		location1.setSearchedLng(searchedLng);
		location1.setRent_start(rent_Start);
		location1.setRent_end(rent_End);
//		System.out.println(location1);
		List<Board> lists = boardMapper.findLocationdistance(location1);

//		log.info("lists: {}", lists);

		List<BoardDTO> boardDTOs = new ArrayList<>();
		for (Board list : lists) {
			Car car = boardMapper.findCarInfoByCarInfoId(list.getCarInfo_id());
			BoardDTO dto = new BoardDTO();
			dto.setBoard(list);
			dto.setCar(car);
			boardDTOs.add(dto);
		}

		List<Brand> brands = boardMapper.findBrand();
		model.addAttribute("brands", brands);
		List<CarType> carTypes = boardMapper.findCarType();
		model.addAttribute("carTypes", carTypes);
		List<Fuel> fuels = boardMapper.findFuel();
		model.addAttribute("fuels", fuels);
		List<Feature> features = boardMapper.findFeature();
		model.addAttribute("features", features);
		model.addAttribute("boardDTOs", boardDTOs);
		return "board/board_list";

	}

	@GetMapping("list/pricerange")
	public String boardListDistance(Model model, @RequestParam("searchedLat") double searchedLat,
			@RequestParam("searchedLng") double searchedLng, @RequestParam("rent_start") String rent_Start,
			@RequestParam("rent_end") String rent_End, @RequestParam("location") String location,
			@ModelAttribute BoardSearchForm boardSearchForm, @RequestParam("minPrice") Long minPrice,
			@RequestParam("maxPrice") Long maxPrice) {

//		log.info("bsf: {}", searchedLat);

		boardSearchForm.setLocation(location);
		boardSearchForm.setRent_end(rent_End);
		boardSearchForm.setRent_start(rent_Start);
		boardSearchForm.setSearchedLat(searchedLat);
		boardSearchForm.setSearchedLng(searchedLng);
		LocationPrice locationPrice = new LocationPrice();
		locationPrice.setSearchedLat(searchedLat);
		locationPrice.setSearchedLng(searchedLng);
		locationPrice.setRent_start(rent_Start);
		locationPrice.setRent_end(rent_End);
		locationPrice.setMaxPrice(maxPrice);
		locationPrice.setMinPrice(minPrice);
		List<Board> lists = boardMapper.findLocationdPrice(locationPrice);

//		log.info("lists: {}", lists);

		List<BoardDTO> boardDTOs = new ArrayList<>();
		for (Board list : lists) {
			Car car = boardMapper.findCarInfoByCarInfoId(list.getCarInfo_id());
			BoardDTO dto = new BoardDTO();
			dto.setBoard(list);
			dto.setCar(car);
			boardDTOs.add(dto);
		}

		List<Brand> brands = boardMapper.findBrand();
		model.addAttribute("brands", brands);
		List<CarType> carTypes = boardMapper.findCarType();
		model.addAttribute("carTypes", carTypes);
		List<Fuel> fuels = boardMapper.findFuel();
		model.addAttribute("fuels", fuels);
		List<Feature> features = boardMapper.findFeature();
		model.addAttribute("features", features);
		model.addAttribute("boardDTOs", boardDTOs);
		return "board/board_list";

	}

	@GetMapping("list/filter")
	public String test(@RequestParam(name = "brand_id", required = false) List<Number> brand_id,
			@RequestParam("location") String location,
			@RequestParam(name = "carType_id", required = false) List<Number> carType_id,
			@RequestParam("searchedLat") Double searchedLat,
			@RequestParam(name = "seat", required = false) List<String> seat,
			@RequestParam("searchedLng") Double searchedLng,
			@RequestParam(name = "fuel_id", required = false) List<Number> fuel_id,
			@RequestParam("rent_start") String rent_Start,
			@RequestParam(name = "option_value", required = false) String option_value,
			@RequestParam("rent_end") String rent_End, @ModelAttribute BoardSearchForm boardSearchForm, Model model) {
		log.info("option : {}", option_value);
		boardSearchForm.setLocation(location);
		boardSearchForm.setRent_end(rent_End);
		boardSearchForm.setRent_start(rent_Start);
		boardSearchForm.setSearchedLat(searchedLat);
		boardSearchForm.setSearchedLng(searchedLng);
		Location location1 = new Location();
		location1.setSearchedLat(searchedLat);
		location1.setSearchedLng(searchedLng);
		location1.setRent_start(rent_Start);
		location1.setRent_end(rent_End);

		BoardFilterForm boardFilterForm = new BoardFilterForm();
		boardFilterForm.setBrand_id(brand_id);
		boardFilterForm.setCarType_id(carType_id);
		boardFilterForm.setFuel_id(fuel_id);
		boardFilterForm.setSeat(seat);

		List<Car> cars = boardMapper.findCarInfoByBoardFilterForm(boardFilterForm);

		List<Board> lists = boardMapper.findLocation(location1);

		List<BoardDTO> boardDTOs = new ArrayList<>();

		for (Board list : lists) {
			for (Car car : cars) {
				if (list.getCarInfo_id().equals(car.getCarInfo_id())) {

					if (option_value == null) {
						BoardDTO dto = new BoardDTO();
						dto.setBoard(list);
						dto.setCar(car);
						boardDTOs.add(dto);
					}

					if (option_value != null) {
						String[] optionValues = option_value.split(",");
						System.out.println(Arrays.toString(optionValues));
						String optionList = boardMapper.findOptionListById(car.getCarInfo_id());
						String[] optionLists = optionList.split(",");
						System.out.println(Arrays.toString(optionLists));
						for (String optionlist : optionLists) {
							for (String optionvalues : optionValues) {
								if (optionlist.equals(optionvalues)) {
									BoardDTO dto = new BoardDTO();
									System.out.println(list);
									System.out.println(car);
									dto.setBoard(list);
									dto.setCar(car);
									boardDTOs.add(dto);
								}
							}
						}
					}
				}
			}
		}
		List<Brand> brands = boardMapper.findBrand();
		model.addAttribute("brands", brands);
		List<CarType> carTypes = boardMapper.findCarType();
		model.addAttribute("carTypes", carTypes);
		List<Fuel> fuels = boardMapper.findFuel();
		model.addAttribute("fuels", fuels);
		List<Feature> features = boardMapper.findFeature();
		model.addAttribute("features", features);
		model.addAttribute("boardDTOs", boardDTOs);

		return "board/board_list";
	}

/////////////////////////////////////////////////////////////////////////////// 병휘

	// 게시글 삭제
	@PostMapping("deleteByBoardId")
	public String deleteByBoardId(@SessionAttribute(value = "loginHost", required = false) Host loginHost,
			@RequestParam Long board_id) {

		// board_id 에 해당하는 게시글을 가져온다.
		Board board = boardMapper.findBoard(board_id);
		// 게시글이 존재하지 않거나 작성자와 로그인 사용자의 아이디가 다르면 리스트로 리다이렉트 한다.
		if (board == null || !board.getHost_email().equals(loginHost.getHost_email())) {
			log.info("삭제 권한 없음");
			return "redirect:/host/main";
		}

		// 게시글에 해당하는 예약정보를 가져온다.
		List<Reservation> reservations = reservationMapper.findReservationByBoardId(board_id);

		if (reservations.size() == 0) {
			boardMapper.deleteByBoardId(board_id);
		}

		// 예약정보가 존재하고, 해당 board_id가 Reservation_id와 같으면 게시글을 삭제할 수 없다.
		for (Reservation reservation : reservations) {
			if (reservation.getStatus() == 2) {
				log.info("게시글에 해당하는 예약이 존재");
				return "redirect:/host/main?remove=true";
			}
		}

		// 게시글을 삭제한다.
		boardMapper.deleteByBoardId(board_id);
		// board/list 로 리다이렉트 한다.
		return "redirect:/host/main";
	}

	// 차 삭제
	@PostMapping("deleteByCarId")
	public String deleteByCarId(@SessionAttribute(value = "loginHost", required = false) Host loginHost,
			@RequestParam Long carInfo_id) {
		log.info("id : {}", carInfo_id);
		// board_id 에 해당하는 게시글을 가져온다.
		List<Board> boards = boardMapper.findBoardsByCarInfoId(carInfo_id);

		for (Board board : boards) {
			List<Reservation> reservations = reservationMapper.findReservationByBoardId(board.getBoard_id());

			if (reservations.size() == 0) {
				boardMapper.deleteByCarId(carInfo_id);
			}

			// 예약정보가 존재하고, 해당 board_id가 Reservation_id와 같으면 게시글을 삭제할 수 없다.
//			for (Reservation reservation : reservations) {
//				if (reservation.getStatus() == 1 || reservation.getStatus() == 2) {
//					log.info("게시글에 해당하는 예약이 존재");
//					return "redirect:/host/main?msg=reservation_exist";
//				}
//			}
			
			for (Reservation reservation : reservations) {
				if (reservation.getStatus() == 2) {
					log.info("게시글에 해당하는 예약이 존재");
					return "redirect:/host/main?remove=true";
				}
			}
		}

		// 게시글을 삭제한다.
		boardMapper.deleteByCarId(carInfo_id);
		// board/list 로 리다이렉트 한다.
		return "redirect:/host/main";
	}
	
	
	@GetMapping("list")
	public String boardListEdit(Model model, @ModelAttribute BoardSearchForm boardSearchForm,
			@ModelAttribute Location location) {
		model.addAttribute("boardSearchForm", boardSearchForm);
//		log.info("location: {}", location);
		List<Board> lists = boardMapper.findLocation(location);
//		log.info("lists: {}", lists);
		
		Dates dates = new Dates();
		dates.setRent_start(location.getRent_start());
		dates.setRent_end(location.getRent_end());
		
		List<Long> boardIds = boardMapper.findBoardIdByPeriod(dates);
		
		System.out.println("hi" + boardIds);
			
		List<BoardDTO> boardDTOs = new ArrayList<>();
		for(Long boardId : boardIds) {
			for (Board list : lists) {
				if (list.getBoard_id() == boardId) {
					lists.remove(list);
				}
			}
		}
		
		for(Board list : lists) {
			Car car = boardMapper.findCarInfoByCarInfoId(list.getCarInfo_id());
			BoardDTO dto = new BoardDTO();
			dto.setBoard(list);
			dto.setCar(car);
			boardDTOs.add(dto);
			
		}
		

		// 브랜드 출력
		List<Brand> brands = boardMapper.findBrand();
//				log.info("brands: {}", brands);
		model.addAttribute("brands", brands);
		// 차종 출력
		List<CarType> carTypes = boardMapper.findCarType();
		model.addAttribute("carTypes", carTypes);
		// 유종 출력
		List<Fuel> fuels = boardMapper.findFuel();
		model.addAttribute("fuels", fuels);
		// 특징 출력
		List<Feature> features = boardMapper.findFeature();
		model.addAttribute("features", features);
		model.addAttribute("boardDTOs", boardDTOs);
		return "board/board_list";
	}

}
