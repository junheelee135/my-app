package com.sp.app.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.sp.app.common.PaginateUtil;
import com.sp.app.domain.dto.ReplyDto;
import com.sp.app.domain.dto.SessionInfo;
import com.sp.app.service.ReplyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/posts/*")
public class ReplyRestController {
	private final ReplyService service;
	private final PaginateUtil paginateUtil;

	// 댓글 리스트 : JSON
	@GetMapping("{target}/{num}")
	public ResponseEntity<?> listReply(
			@PathVariable(name = "target") String target,
			@PathVariable(name = "num") long num, 
			@RequestParam(name = "pageNo", defaultValue = "1") int current_page,
			@SessionAttribute("member") SessionInfo info) throws Exception {

		try {
			int size = 5;
			int total_page = 0;
			int dataCount = 0;

			Map<String, Object> map = new HashMap<>();
			map.put("target", target);
			map.put("targetLike", target + "Like");
			
			map.put("num", num);
			
			map.put("userLevel", info.getUserLevel());
			map.put("member_id", info.getMember_id());
			
			dataCount = service.replyCount(map);
			total_page = paginateUtil.pageCount(dataCount, size);
			current_page = Math.min(current_page, total_page);

			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;

			map.put("offset", offset);
			map.put("size", size);
			
			List<ReplyDto> listReply = service.listReply(map);
			for(ReplyDto dto : listReply) {
				if(dto.getMember_id() == info.getMember_id()) {
					dto.setHasOwner(true);
				} else if(info.getUserLevel() >= 51) {
					dto.setHasAdmin(true);
				}
			}
			
			// AJAX 용 페이징
			String paging = paginateUtil.pagingMethod(current_page, total_page, "loadContent");

			return ResponseEntity.ok(Map.of(
				"listReply", listReply, 
				"pageNo", current_page,
				"totalCount", dataCount,
				"totalPage", total_page,
				"paging", paging
			));
		} catch (Exception e) {
			log.info("listReply : ", e);
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 에러 코드
		}
	}

	// 댓글 및 댓글의 답글 등록 : JSON
	@PostMapping("{target}/insert")
	public ResponseEntity<?> saveReply(
			@PathVariable(name = "target") String target,
			ReplyDto dto,
			@SessionAttribute("member") SessionInfo info) {
		try {
			dto.setTarget(target);
			
			dto.setMember_id(info.getMember_id());
			service.insertReply(dto);
			
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 댓글 및 댓글의 답글 삭제 : JSON
	// DELETE 매핑은 json으로 넘어온 데이터를 @RequestBody 로 받지 않는다.
	@DeleteMapping("{target}/{replyNum}")
	public ResponseEntity<?> deleteReply(
			@PathVariable(name = "target") String target,
			@PathVariable(name = "replyNum") long replyNum,
			@RequestParam Map<String, Object> paramMap) {
		try {
			paramMap.put("target", target);
			paramMap.put("replyNum", replyNum);
			
			service.deleteReply(paramMap);
			
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 댓글의 답글 리스트 : JSON
	@GetMapping("{target}/answer")
	public ResponseEntity<?> listReplyAnswer(
			@PathVariable(name = "target") String target,
			@RequestParam Map<String, Object> paramMap,
			@SessionAttribute("member") SessionInfo info) throws Exception {
		
		try {
			paramMap.put("target", target);
			paramMap.put("userLevel", info.getUserLevel());
			paramMap.put("member_id", info.getMember_id());
			
			// 댓글별 답글 개수
			int answerCount = service.replyAnswerCount(paramMap);
			
			List<ReplyDto> listAnswers = service.listReplyAnswer(paramMap);
			for(ReplyDto dto : listAnswers) {
				if(dto.getMember_id() == info.getMember_id()) {
					dto.setHasOwner(true);
				} else if(info.getUserLevel() >= 51) {
					dto.setHasAdmin(true);
				}
			}

			return ResponseEntity.ok(Map.of(
				"answerCount", answerCount, 
				"listAnswers", listAnswers
			));		
		} catch (Exception e) {
			log.info("listReplyAnswer : ", e);
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 댓글의 좋아요/싫어요 추가 : JSON
	@PostMapping("{target}/like")
	public ResponseEntity<?> saveReplyLike(
			@PathVariable(name = "target") String target,
			@RequestParam Map<String, Object> paramMap,
			@SessionAttribute("member") SessionInfo info) {
		
		String state = "true";
		int likeCount = 0;
		int disLikeCount = 0;
		try {
			paramMap.put("targetLike", target + "Like");
			paramMap.put("member_id", info.getMember_id());
			service.insertReplyLike(paramMap);
			
			Map<String, Object> countMap = service.replyLikeCount(paramMap);
			// 마이바티스의 resultType이 map인 경우 int는 BigDecimal로 넘어옴
			likeCount = ((BigDecimal)countMap.get("LIKECOUNT")).intValue();
			disLikeCount = ((BigDecimal)countMap.get("DISLIKECOUNT")).intValue();
		} catch (DuplicateKeyException e) {
			state = "liked";
		} catch (Exception e) {
			state = "false";
		}

		return ResponseEntity.ok(Map.of(
			"likeCount", likeCount, 
			"disLikeCount", disLikeCount,
			"state", state
		));
	}
	
	// 댓글 숨김/표시 : JSON
	@PostMapping("{target}/replyShowHide")
	public ResponseEntity<?> replyShowHide(
			@PathVariable(name = "target") String target,
			@RequestParam Map<String, Object> paramMap,
			@SessionAttribute("member") SessionInfo info) {
		try {
			paramMap.put("target", target);
			paramMap.put("member_id", info.getMember_id());
			
			service.updateReplyShowHide(paramMap);
			
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
