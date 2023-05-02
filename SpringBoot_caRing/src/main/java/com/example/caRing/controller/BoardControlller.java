package com.example.caRing.controller;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.example.caRing.model.board.AttachedFile;
import com.example.caRing.model.board.Brand;
import com.example.caRing.model.board.Car;
import com.example.caRing.model.host.Host;
import com.example.caRing.model.host.HostLoginForm;
import com.example.caRing.repository.BoardMapper;
import com.example.caRing.repository.HostMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("board")
@Controller
public class BoardControlller {
	
	@Autowired
	private BoardMapper boardMapper;

	@GetMapping("car_registration")
	public String car_registration(Model model) {
		List<Brand> brands = boardMapper.findBrand();
//		log.info("brands: {}", brands);
		model.addAttribute("brands", brands);
		return "board/car_registration";
	}
	
//	@PostMapping("car_registration")
//	public String carSave(AttachedFile attachedFile, Car car, @ModelAttribute("hostLoginForm") HostLoginForm hostLoginForm,
//			MultipartFile imgFile) throws Exception {
//		
//		String oriImgName = imgFile.getOriginalFilename();
//		String imgName = "";
//		
//		String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/files/";
//		
//		UUID uuid = UUID.randomUUID();
//		
//		String savedFileName = uuid + "_" + oriImgName;
//		
//		imgName = savedFileName;
//		
//		File saveFile = new File(projectPath, imgName);
//		
//		imgFile.transferTo(saveFile);
////		
////		attachedFile.setImgName(imgName);
////		attachedFile.setImgPath("/files/" + imgName);
//		
//		
//		return null;
//	}
	
	@GetMapping("write")
	public String boardWrite(Model model) {
		return "board/board_write";
	}
}
