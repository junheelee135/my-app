package com.sp.app.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.sp.app.common.MyUtil;
import com.sp.app.common.PaginateUtil;
import com.sp.app.common.RequestUtils;
import com.sp.app.common.StorageService;
import com.sp.app.domain.dto.SessionInfo;
import com.sp.app.domain.entity.Board;
import com.sp.app.exception.StorageException;
import com.sp.app.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
  - isEmpty()
    : JDK 6. 문자열이 없는 경우 true 
  - isBlank()
    : JDK 11 이상. 문자열이 없거나 공백으로만 이루어진 경우 true
  - @SessionAttribute
    : 세션값 조회(세션에 저장된 값 가져오기)
*/

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bbs/*")
public class BoardController {
	private final BoardService service;
	private final PaginateUtil paginateUtil;
	// private final @Qualifier("paginateUtil") PaginateUtil paginateUtil;
	private final StorageService storageService;
	private final MyUtil myUtil;
	
	@Value("${file.upload-root}/bbs")
	private String uploadPath;

	@GetMapping("list")
	public String list(@RequestParam(name = "page", defaultValue = "1") int current_page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			Model model) throws Exception {
		
		try {
			int size = 10;
			int dataCount = 0;
			int totalPage = 0;

			kwd = myUtil.decodeUrl(kwd);

			// 전체 페이지 수
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("schType", schType);
			map.put("kwd", kwd);
			
			dataCount = service.dataCount(map);
			if (dataCount != 0) {
				totalPage = paginateUtil.pageCount(dataCount, size);
			}
			
			// 다른 사람이 자료를 삭제하여 전체 페이지수가 변화 된 경우
			current_page = Math.min(current_page, totalPage);

			// 리스트에 출력할 데이터를 가져오기
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;

			map.put("offset", offset);
			map.put("size", size);

			// 글 리스트
			List<Board> list = service.listBoard(map);
			
			model.addAttribute("list", list);
			model.addAttribute("dataCount", dataCount);
			model.addAttribute("size", size);
			model.addAttribute("totalPage", totalPage);
			model.addAttribute("page", current_page);
			
			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);
			
		} catch (Exception e) {
			log.info("list : ", e);
		}

		return "bbs/list";
	}

	@GetMapping("write")
	public String writeForm(Model model) throws Exception {

		model.addAttribute("mode", "write");

		return "bbs/write";
	}

	@PostMapping("write")
	public String writeSubmit(Board dto, 
			@SessionAttribute("member") SessionInfo info) throws Exception {

		try {
			dto.setMember_id(info.getMember_id());
			
			service.insertBoard(dto, uploadPath);
			
		} catch (Exception e) {
			log.info("writeSubmit : ", e);
		}

		return "redirect:/bbs/list";
	}

	@GetMapping("article/{num}")
	public String article(@PathVariable(name = "num") long num,
			@RequestParam(name = "page") String page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			@SessionAttribute("member") SessionInfo info,
			Model model) throws Exception {
		
		String query = "page=" + page;
		try {
			kwd = myUtil.decodeUrl(kwd);
			if (! kwd.isBlank()) {  // if(kwd.length() != 0) {
				query += "&schType=" + schType + 
						"&kwd=" + myUtil.encodeUrl(kwd);
			}
			
			service.updateHitCount(num);

			// 해당 레코드 가져 오기
			Board dto = Objects.requireNonNull(service.findById(num));
			
			dto.setName(myUtil.nameMasking(dto.getName()));
			
			// dto.setContent(myUtil.htmlSymbols(dto.getContent())); // 에디터로 처리
			dto.setContent(myUtil.sanitize(dto.getContent())); // XSS 방지을 위한 새니타이즈 메소드(위험한 속성, 스크립트 제거)
			
			// 이전 글, 다음 글
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("schType", schType);
			map.put("kwd", kwd);
			map.put("num", num);
			
			Board prevDto = service.findByPrev(map);
			Board nextDto = service.findByNext(map);

			// 게시글 좋아요 여부
			map.put("member_id", info.getMember_id());
			boolean isUserLiked = service.isUserBoardLiked(map);
			
			model.addAttribute("dto", dto);
			model.addAttribute("prevDto", prevDto);
			model.addAttribute("nextDto", nextDto);

			model.addAttribute("isUserLiked", isUserLiked);
			
			model.addAttribute("page", page);
			model.addAttribute("query", query);
			
			return "bbs/article";
			
		} catch (NullPointerException e) {
			log.info("article : ", e);
		} catch (Exception e) {
			log.info("article : ", e);
		}
		
		return "redirect:/bbs/list?" + query;
	}

	@GetMapping("update/{num}")
	public String updateForm(@PathVariable(name = "num") long num,
			@RequestParam(name = "page") String page,
			@SessionAttribute("member") SessionInfo info,
			Model model) throws Exception {

		try {
			Board dto = Objects.requireNonNull(service.findById(num));
			
			if (dto.getMember_id() != info.getMember_id()) {
				return "redirect:/bbs/list?page=" + page;
			}
			
			model.addAttribute("dto", dto);
			model.addAttribute("mode", "update");
			model.addAttribute("page", page);
			
			return "bbs/write";
			
		} catch (NullPointerException e) {
		} catch (Exception e) {
			log.info("updateForm : ", e);
		}
		
		return "redirect:/bbs/list?page=" + page;
	}

	@PostMapping("update")
	public String updateSubmit(Board dto,
			@RequestParam(name = "page") String page) throws Exception {

		try {
			service.updateBoard(dto, uploadPath);
		} catch (Exception e) {
			log.info("updateSubmit : ", e);
		}

		return "redirect:/bbs/list?page=" + page;
	}

	@GetMapping("deleteFile/{num}")
	public String deleteFile(@PathVariable(name = "num") long num,
			@RequestParam(name = "page") String page,
			@SessionAttribute("member") SessionInfo info) throws Exception {
		
		try {
			Board dto = Objects.requireNonNull(service.findById(num));
			
			if (dto.getMember_id() != info.getMember_id()) {
				return "redirect:/bbs/list?page=" + page;
			}
			
			if (dto.getSaveFilename() != null) {
				storageService.deleteFile(uploadPath, dto.getSaveFilename());
				
				dto.setSaveFilename("");
				dto.setOriginalFilename("");
				service.updateBoard(dto, uploadPath); // DB 테이블의 파일명 변경(삭제)
			}
			
			return "redirect:/bbs/update/" + num + "?page=" + page;
			
		} catch (NullPointerException e) {
		} catch (Exception e) {
			log.info("deleteFile : ", e);
		}
		
		return "redirect:/bbs/list?page=" + page;
	}

	@GetMapping("delete/{num}")
	public String delete(@PathVariable(name = "num") long num,
			@RequestParam(name = "page") String page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd,
			@SessionAttribute("member") SessionInfo info) throws Exception {
		
		String query = "page=" + page;
		try {
			kwd = myUtil.decodeUrl(kwd);
			if (! kwd.isBlank()) {
				query += "&schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
			}
			
			service.deleteBoard(num, uploadPath, info.getMember_id(), info.getUserLevel());
			
		} catch (Exception e) {
			log.info("delete : ", e);
		}

		return "redirect:/bbs/list?" + query;
	}
	
	/*
	  - ResponseEntity
	  	: 스프링에서 HTTP 응답을 나타내는 클래스
	    : 클라이언트에게 응답을 보낼 때, 상태 코드, 응답 헤더, 응답 바디를 명시적으로 설정
	*/
	@GetMapping("download/{num}")
	public ResponseEntity<?> download(
			@PathVariable("num") long num) throws Exception {
		
		try {
			Board dto = Objects.requireNonNull(service.findById(num));

			return storageService.downloadFile(uploadPath, dto.getSaveFilename(), dto.getOriginalFilename());
			
		} catch (NullPointerException | StorageException e) {
			log.info("download : ", e);
		} catch (Exception e) {
			log.info("download : ", e);
		}
		
		String redirectUrl = RequestUtils.getContextPath() + "/error/downloadFailed";
		return ResponseEntity
				.status(HttpStatus.FOUND)  // 302 상태 코드(리다이렉트)
				.location(URI.create(redirectUrl))  // Location 헤더에 리다이렉트할 URL 설정
				.build();
	}
	
	// 게시글 좋아요 추가 : AJAX-JSON
	// @ResponseBody : ResponseEntity<> 를 반환하면 생략 가능
	@PostMapping("like/{num}")
	public ResponseEntity<?> insertBoardLike(
			@PathVariable(name = "num") long num,
			@SessionAttribute("member") SessionInfo info) {
		
		try {
			int boardLikeCount = 0;
			
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("num", num);
			paramMap.put("member_id", info.getMember_id());
			
			service.insertBoardLike(paramMap);
			boardLikeCount = service.boardLikeCount(num);
			
			return ResponseEntity.ok(Map.of(
				"boardLikeCount", boardLikeCount
			));			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping("like/{num}")
	public ResponseEntity<?> deleteBoardLike(@PathVariable(name = "num") long num,
			@SessionAttribute("member") SessionInfo info) {
		try {
			int boardLikeCount = 0;
			
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("num", num);
			paramMap.put("member_id", info.getMember_id());
			
			service.deleteBoardLike(paramMap);
			
			boardLikeCount = service.boardLikeCount(num);
			
			return ResponseEntity.ok(Map.of(
				"boardLikeCount", boardLikeCount
			));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
