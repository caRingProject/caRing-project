package com.example.caRing.controller;

import java.io.File;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

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
	private FileService fileService;

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
	@PostMapping("car_registration")
	public String carSave(@ModelAttribute Car car, @ModelAttribute AttachedFile attachedFile, 
			@SessionAttribute(name = "loginHost", required = true) Host loginHost,
			@RequestParam List<MultipartFile> carUpload, OptionList optionList) {
		log.info("loginHost: {}", loginHost);
		log.info("carUpload: {}", carUpload);
		log.info("car: {}", car);
		log.info("optionList: {}", optionList);
		car.setHost_email(loginHost.getHost_email());
		boardMapper.saveCar(car);
		
		optionList.setCarInfo_id(car.getCarInfo_id());
		log.info("car.getCarInfo_id(): {}", car.getCarInfo_id());
		boardMapper.saveOption(optionList);
		
		for (MultipartFile file: carUpload) {
			AttachedFile saveFile = fileService.saveFile(file);
			log.info("saveFile.getOriginal_filename: {}", saveFile.getOriginal_filename());
			log.info("saveFile.getFile_size: {}", saveFile.getFile_size());
			log.info("saveFile.getFile_size: {}", saveFile.getOriginal_filename());
			log.info("saveFile.getFile_size: {}", saveFile.getSaved_filename());
			saveFile.setCarInfo_id(car.getCarInfo_id());
			saveFile.setCarInfo_id(car.getCarInfo_id());
			boardMapper.saveFile(saveFile);
		}
		
		return "board/car_registration";
	}

	// 게시글 등록
	@GetMapping("write")
	public String boardWrite(Model model) {
		return "board/board_write";
	}
}
