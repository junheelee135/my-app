package com.sp.app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sp.app.common.RequestUtils;
import com.sp.app.domain.dto.MemberDto;
import com.sp.app.domain.dto.SessionInfo;
import com.sp.app.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/member/*")
public class MemberController {
	private final MemberService service;
	
	@Value("${file.upload-root}/member")
	private String uploadPath;
	
	// @RequestMapping(value = "login", method = RequestMethod.GET)
	@GetMapping("login")
	public String loginForm() {
		return "member/login";
	}

	// @RequestMapping(value = "login", method = RequestMethod.POST)
	@PostMapping("login")
	public String loginSubmit(@RequestParam Map<String, Object> paramMap,
			Model model,
			HttpSession session) {
		
		MemberDto dto = service.loginMember(paramMap);
		if (dto == null) {
			model.addAttribute("message", "아이디 또는 패스워드가 일치하지 않습니다.");
			return "member/login";
		}

		// 세션에 로그인 정보 저장
		SessionInfo info = SessionInfo.builder()
				.member_id(dto.getMember_id())
				.login_id(dto.getLogin_id())
				.name(dto.getName())
				.email(dto.getEmail())
				.userLevel(dto.getUserLevel())
				.avatar(dto.getProfile_photo())
				.login_type("local")
				.build();
		
		// 세션 유지시간
		session.setMaxInactiveInterval(30 * 60); // 30분. 기본:30분
		
		// 세션에 로그인 정보 저장
		session.setAttribute("member", info);
		
		// 로그인 이전 URI로 이동
		String uri = (String) session.getAttribute("preLoginURI");
		session.removeAttribute("preLoginURI");
		if (uri == null) {
			uri = "redirect:/";
		} else {
			uri = "redirect:" + uri;
		}

		return uri;
	}

	@GetMapping("logout")
	public String logout(HttpSession session) {
		// 세션에 저장된 정보 지우기
		session.removeAttribute("member");

		// 세션에 저장된 모든 정보 지우고, 세션초기화
		session.invalidate();

		return "redirect:/";
	}

	@GetMapping("account")
	public String memberForm(Model model) {
		model.addAttribute("mode", "account");
		return "member/member";
	}

	@PostMapping("account")
	public String memberSubmit(MemberDto dto,
			final RedirectAttributes rAttr,
			Model model) {

		try {
			dto.setIpAddr(RequestUtils.getClientIp());
			
			service.insertMember(dto, uploadPath);
			
			StringBuilder sb = new StringBuilder();
			sb.append(dto.getName() + "님의 회원 가입이 정상적으로 처리되었습니다.<br>");
			sb.append("메인화면으로 이동하여 로그인 하시기 바랍니다.<br>");

			// 리다이렉트된 페이지에 값 넘기기
			rAttr.addFlashAttribute("message", sb.toString());
			rAttr.addFlashAttribute("title", "회원 가입");

			return "redirect:/member/complete";
			
		} catch (DuplicateKeyException e) {
			// 기본키 중복에 의한 제약 조건 위반
			model.addAttribute("mode", "account");
			model.addAttribute("message", "아이디 중복으로 회원가입이 실패했습니다.");
		} catch (DataIntegrityViolationException e) {
			// 데이터형식 오류, 참조키, NOT NULL 등의 제약조건 위반
			model.addAttribute("mode", "account");
			model.addAttribute("message", "제약 조건 위반으로 회원가입이 실패했습니다.");
		} catch (Exception e) {
			model.addAttribute("mode", "account");
			model.addAttribute("message", "회원가입이 실패했습니다.");
		}

		return "member/member";
	}

	@GetMapping("complete")
	public String complete(@ModelAttribute("message") String message) throws Exception {
		if (message == null || message.isBlank()) { // F5를 누른 경우
			return "redirect:/";
		}

		return "member/complete";
	}
	
	@ResponseBody
	@PostMapping("userIdCheck")
	public Map<String, ?> handleUserIdCheck(@RequestParam(name = "login_id") String login_id) throws Exception {
		// ID 중복 검사
		Map<String, Object> model = new HashMap<>();
		
		String p = "false";
		try {
			MemberDto dto = service.findById(login_id);
			if (dto == null) {
				p = "true";
			}
		} catch (Exception e) {
		}
		
		model.put("passed", p);
		
		return model;
	}	
	
	@GetMapping("pwd")
	public String pwdForm(@RequestParam(name = "dropout", required = false) String dropout, 
			Model model) {

		if (dropout == null) {
			model.addAttribute("mode", "update");
		} else {
			model.addAttribute("mode", "dropout");
		}

		return "member/pwd";
	}

	@PostMapping("pwd")
	public String pwdSubmit(@RequestParam(name = "password") String password,
			@RequestParam(name = "mode") String mode, 
			final RedirectAttributes rAttr,
			Model model,
			HttpSession session) {

		try {
			SessionInfo info = (SessionInfo) session.getAttribute("member");
			MemberDto dto = Objects.requireNonNull(service.findById(info.getMember_id()));

			if (! dto.getPassword().equals(password)) {
				model.addAttribute("mode", mode);
				model.addAttribute("message", "패스워드가 일치하지 않습니다.");
				
				return "member/pwd";
			}

			if (mode.equals("dropout")) {
				// 게시판 테이블등 자료 삭제

				// 회원탈퇴 처리
				/*
				  Map<String, Object> map = new HashMap<>();
				  map.put("member_id", info.getMember_id());
				  map.put("filename", info.getAvatar());
				 */

				// 세션 정보 삭제
				session.removeAttribute("member");
				session.invalidate();

				StringBuilder sb = new StringBuilder();
				sb.append(dto.getName() + "님의 회원 탈퇴 처리가 정상적으로 처리되었습니다.<br>");
				sb.append("메인화면으로 이동 하시기 바랍니다.<br>");

				rAttr.addFlashAttribute("title", "회원 탈퇴");
				rAttr.addFlashAttribute("message", sb.toString());

				return "redirect:/member/complete";
			}

			model.addAttribute("dto", dto);
			model.addAttribute("mode", "update");
			
			// 회원정보수정폼
			return "member/member";
			
		} catch (NullPointerException e) {
			session.invalidate();
		} catch (Exception e) {
		}
		
		return "redirect:/";
	}

	@PostMapping("update")
	public String updateSubmit(MemberDto dto,
			final RedirectAttributes rAttr,
			@SessionAttribute("member") SessionInfo info,
			Model model) {

		StringBuilder sb = new StringBuilder();
		try {
			dto.setMember_id(info.getMember_id());
			
			service.updateMember(dto, uploadPath);
			
			// 세션의 profile photo 변경
			info.setAvatar(dto.getProfile_photo());
			
			sb.append(dto.getName() + "님의 회원정보가 정상적으로 변경되었습니다.<br>");
			sb.append("메인화면으로 이동 하시기 바랍니다.<br>");
		} catch (Exception e) {
			sb.append(dto.getName() + "님의 회원정보 변경이 실패했습니다.<br>");
			sb.append("잠시후 다시 변경 하시기 바랍니다.<br>");
		}

		rAttr.addFlashAttribute("title", "회원 정보 수정");
		rAttr.addFlashAttribute("message", sb.toString());
		
		return "redirect:/member/complete";
	}

	// 패스워드 찾기
	@GetMapping("pwdFind")
	public String pwdFindForm(@SessionAttribute(name="member", required = false) SessionInfo info) throws Exception {
		if(info != null) {
			return "redirect:/";
		}
		
		return "member/pwdFind";
	}
	
	@PostMapping("pwdFind")
	public String pwdFindSubmit(@RequestParam(name = "login_id") String login_id,
			final RedirectAttributes rAttr,
			Model model) throws Exception {
		
		try {
			MemberDto dto = service.findById(login_id);
			if(dto == null || dto.getEmail() == null || dto.getUserLevel() == 0 || dto.getEnabled() == 0) {
				model.addAttribute("message", "등록된 아이디가 아닙니다.");
				
				return "member/pwdFind";
			}
			
			service.generatePwd(dto);
			
			StringBuilder sb = new StringBuilder();
			sb.append("회원님의 이메일로 임시패스워드를 전송했습니다.<br>");
			sb.append("로그인 후 패스워드를 변경하시기 바랍니다.<br>");
			
			rAttr.addFlashAttribute("title", "패스워드 찾기");
			rAttr.addFlashAttribute("message", sb.toString());
			
			return "redirect:/member/complete";
			
		} catch (Exception e) {
			model.addAttribute("message", "이메일 전송이 실패했습니다.");
		}
		
		return "member/pwdFind";
	}
	
	@ResponseBody
	@DeleteMapping("deleteProfile")
	public Map<String, ?> deleteProfilePhoto(@RequestParam(name = "profile_photo") String profile_photo,
			@SessionAttribute("member") SessionInfo info) throws Exception {
		// 프로파일 포토 삭제
		Map<String, Object> model = new HashMap<String, Object>();
		
		String state = "false";
		try {
			if(! profile_photo.isBlank()) {
				Map<String, Object> map = new HashMap<>();
				map.put("member_id", info.getMember_id());
				map.put("filename", info.getAvatar());
				// map.put("filename", profile_photo);
				
				service.deleteProfilePhoto(map, uploadPath);
				
				info.setAvatar(null);
				state = "true";
			}
		} catch (Exception e) {
		}
		
		model.put("state", state);
		
		return model;
	}
	
	@GetMapping("noAuthorized")
	public String noAuthorized(Model model) {
		return "member/noAuthorized";
	}
}
