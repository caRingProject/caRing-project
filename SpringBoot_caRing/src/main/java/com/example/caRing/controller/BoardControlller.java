package com.example.caRing.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.example.caRing.model.board.Board;
import com.example.caRing.model.board.BoardDTO;
import com.example.caRing.model.board.BoardWriteForm;
import com.example.caRing.model.board.car.AttachedFile;
import com.example.caRing.model.board.car.Brand;
import com.example.caRing.model.board.car.Car;
import com.example.caRing.model.board.car.CarType;
import com.example.caRing.model.board.car.Feature;
import com.example.caRing.model.board.car.Fuel;
import com.example.caRing.model.board.car.OptionList;
import com.example.caRing.model.host.Host;
import com.example.caRing.model.host.HostLoginForm;
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
				log.info("fullPath: {}", fullPath);
				car.setThumbnail(fullPath);
				log.info("car: {}", car);
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
	public String boardList(Model model) {
		List<Board> boards = boardMapper.findAllBoards();
		List<BoardDTO> boardDTOs = new ArrayList<>();
		for (Board board : boards) {
			Car car = boardMapper.findCarInfoByCarInfoId(board.getCarInfo_id());
			BoardDTO dto = new BoardDTO();
			dto.setBoard(board);
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
		log.info("car: {}", car);
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
		log.info("paths: {}", paths);
		
		return "board/board_read";
	}
	
}
