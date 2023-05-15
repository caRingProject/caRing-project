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
import com.example.caRing.model.board.BoardSearchForm;
import com.example.caRing.model.board.BoardWriteForm;
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
import com.example.caRing.util.FileService;

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
	
	@Value("${file.upload.path}")
	private String uploadPath; 

	// 차량 등록 페이지 이동
	@GetMapping("car_registration")
	public String car_registration(Model model) {
		// 브랜드 출력
		List<Brand> brands = boardMapper.findBrand();
		// log.info("brands: {}", brands);
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
	public String carSave(@ModelAttribute Car car, @ModelAttribute AttachedFile attachedFile,
			@SessionAttribute(name = "loginHost", required = true) Host loginHost,
			@RequestParam List<MultipartFile> carUpload, OptionList optionList) {
		
		
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
		model.addAttribute("boardWriteForm", new BoardWriteForm());
		String host_email = loginHost.getHost_email();
		
		List<Car> carInfo = boardMapper.findCarInfoByEmail(host_email);
		model.addAttribute("carInfo", carInfo);
		
		return "board/board_write";
	}

	// 게시글 등록
	
	@PostMapping("write")
	public String boardWrite(@ModelAttribute("carInfo") Car car, BindingResult result, @SessionAttribute(value = "loginHost", required = false) Host loginHost,
			 @Validated @ModelAttribute("boardWriteForm") BoardWriteForm boardWriteForm,
			 @RequestParam Long carlist, @RequestParam Double lat, @RequestParam Double lng
			 ) {
		
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

	@GetMapping("list")
	public String boardList(Model model, @ModelAttribute BoardSearchForm boardSearchForm) {
		
		Location location = new Location();
		location.setSearchedLat(boardSearchForm.getSearchedLat());
		location.setSearchedLng(boardSearchForm.getSearchedLng());
		location.setRent_start(boardSearchForm.getRent_start());
		location.setRent_end(boardSearchForm.getRent_end());
		System.out.println(location);
		System.out.println(boardSearchForm);
		List<Board> lists = boardMapper.findLocation(location);
		
		log.info("lists: {}", lists);
		
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
	
	@GetMapping("read")
	public String boardRead(@RequestParam Long board_id, Model model) {
		// 게시글 출력
		Board board = boardMapper.findBoard(board_id);
//		log.info("board: {}", board);
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
		System.out.println(Arrays.toString(optionList));
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
			log.info("fullPath: {}", fullPath);
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
		
		return "board/board_read";
	}
	
	
///////////////////////////////////////////////////////////////////////
	
	
	@GetMapping("list/priceasc")
	public String boardListAsc(Model model, @RequestParam("searchedLat") double searchedLat, 
							@RequestParam("searchedLng") double searchedLng, 
							@RequestParam("rent_start") String rent_Start, 
							@RequestParam("rent_end") String rent_End,
							@RequestParam("location") String location,
							@ModelAttribute BoardSearchForm boardSearchForm) {
		log.info("bsf: {}", searchedLat);
		
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
		System.out.println(location1);
		List<Board> lists = boardMapper.findLocationAsc(location1);
		
		log.info("lists: {}", lists);
		
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
		
		log.info("bsf: {}", searchedLat);

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
		System.out.println(location1);
		List<Board> lists = boardMapper.findLocationDesc(location1);

		log.info("lists: {}", lists);

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
		
		log.info("bsf: {}", searchedLat);

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
		System.out.println(location1);
		List<Board> lists = boardMapper.findLocationdistance(location1);

		log.info("lists: {}", lists);

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
			@ModelAttribute BoardSearchForm boardSearchForm, @RequestParam("minPrice") Long minPrice, @RequestParam("maxPrice") Long maxPrice) {
		
		log.info("bsf: {}", searchedLat);

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

		log.info("lists: {}", lists);

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

}
