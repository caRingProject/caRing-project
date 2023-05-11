package com.example.caRing.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.example.caRing.model.board.Board;
import com.example.caRing.model.board.BoardDTO;
import com.example.caRing.model.board.car.AttachedFile;
import com.example.caRing.model.board.car.Car;
import com.example.caRing.model.host.Host;
import com.example.caRing.model.host.HostJoinForm;
import com.example.caRing.model.host.HostLoginForm;
import com.example.caRing.repository.BoardMapper;
import com.example.caRing.repository.HostMapper;
import com.example.caRing.util.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("host")
@Controller

public class HostController {

	private final HostMapper hostMapper;
	private final BoardMapper boardMapper;
	private final FileService fileService;

	@GetMapping("join")
	public String hostJoinForm(Model model) {
		model.addAttribute("hostJoinForm", new HostJoinForm());
		return "host/host_join";
	}

	@GetMapping("login")
	public String hostLoginForm(Model model) {
		model.addAttribute("hostLoginForm", new HostLoginForm());
		return "host/host_login";
	}

	@PostMapping("join")
	public String hostJoin(@Validated @ModelAttribute("hostJoinForm") HostJoinForm hostJoinForm, BindingResult result) {

		if (result.hasErrors()) {
			return "host/host_join";
		}

		// 사용자로부터 입력받은 아이디로 데이터베이스에서 Member 를 검색한다.
		Host host = hostMapper.findHost(hostJoinForm.getHost_email());
		// 사용자 정보가 존재하면
		if (host != null) {
			result.rejectValue("host_email", "emailError", "이미 가입된 아이디 입니다.");
			return "host/host_join";
		}
		// MemberJoinForm 객체를 Member 타입으로 변환하여 데이터베이스에 저장한다.
		hostMapper.saveHost(hostJoinForm.toHost(hostJoinForm));
		// 메인 페이지로 리다이렉트한다.
		return "redirect:/host/login";
	}

	@ResponseBody
	@PostMapping("emailCheck")
	public int customerEmailCheck(@RequestParam String host_email) throws Exception {
		int cnt = hostMapper.hostEmailCheck(host_email);
		return cnt;
	}

	// 로그인 처리
	@PostMapping("login")
	public String hostLoginForm(@Validated @ModelAttribute("hostLoginForm") HostLoginForm hostLoginForm,
			BindingResult result, HttpServletRequest request,
			@RequestParam(defaultValue = "/host/main") String redirectURL) {

		// validation 에 실패하면 member/loginForm 페이지로 돌아간다.
		if (result.hasErrors()) {
			return "host/host_login";
		}

		log.info("requestURL: {}", redirectURL);

		// 사용자가 입력한 이이디에 해당하는 host 정보를 데이터베이스에서 가져온다.
		Host host = hostMapper.findHost(hostLoginForm.getHost_email());
		// host 가 존재하지 않거나 패스워드가 다르면
		if (host == null || !host.getHost_password().equals(hostLoginForm.getHost_password())) {
			// BindingResult 객체에 GlobalError 를 발생시킨다.
			result.reject("loginError", "아이디가 없거나 패스워드가 다릅니다.");
			// member/loginForm.html 페이지로 돌아간다.
			return "host/host_login";
		}

		// Request 객체에서 Session 객체를 꺼내온다.
		HttpSession session = request.getSession();
		// Session 에 'loginMember' 라는 이름으로 Member 객체를 저장한다.
		session.setAttribute("loginHost", host);
		// 메인 페이지로 리다이렉트 한다.
		return "redirect:" + redirectURL;
	}

	@GetMapping("profile")
	public String hostProfile(Model model, @SessionAttribute(value = "loginHost", required = false) Host loginHost) {
		Host host = hostMapper.findHost(loginHost.getHost_email());
		model.addAttribute("host", host);
		return "host/host_profile";
	}

	@GetMapping("main")
	public String hostMain(Model model, @SessionAttribute(value = "loginHost", required = false) Host loginHost) {
		
		Host host = hostMapper.findHost(loginHost.getHost_email());
		model.addAttribute("host", host);
		log.info("host: {}", host);
		List<Car> cars = boardMapper.findCarInfoByEmail(loginHost.getHost_email());
		model.addAttribute("cars", cars);

		List<Board> boards = boardMapper.findBoardsByEmail(loginHost.getHost_email());
		List<BoardDTO> boardDTOs = new ArrayList<>();
		for (Board board : boards) {
			Car car = boardMapper.findCarInfoByCarInfoId(board.getCarInfo_id());
			BoardDTO dto = new BoardDTO();
			dto.setBoard(board);
			dto.setCar(car);
			boardDTOs.add(dto);
		}
		model.addAttribute("boardDTOs", boardDTOs);

		return "host/host_main";
	}

	@PostMapping("update")
	public String updateHost(@ModelAttribute Host host, MultipartFile img) {
		log.info("host: {}", host);
		if (img != null && !img.isEmpty()) {
			AttachedFile saveFile = fileService.saveFile(img);
			String fullPath = "/uploadImg/" + saveFile.getSaved_filename();
			host.setHost_img(fullPath);
		}
		log.info("host: {}", host);
		hostMapper.updateHost(host);
		return "redirect:/host/main";
	}

	@PostMapping("delete")
	public String removeHost(@RequestParam String host_email,
			@SessionAttribute(value = "loginHost", required = false) Host loginHost) {
		log.info("host_email: {}", host_email);
		String str = host_email.replace(",", "");
		log.info("str: {}", str);
		if (!hostMapper.findHost(str).getHost_email().equals(loginHost.getHost_email())) {
			return "redirect:/#";
		}
		hostMapper.removeHost(str);
		return "redirect:/#";
	}

	// 로그아웃
	@GetMapping("logout")
	public String logout(HttpServletRequest request) {
		// 세션
		HttpSession session = request.getSession();
		session.invalidate();
		return "redirect:/host/main";
	}

}
