package com.sp.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sp.app.common.MyUtil;
import com.sp.app.domain.dto.ReplyDto;
import com.sp.app.mapper.ReplyMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyServiceImpl implements ReplyService {
	private final ReplyMapper mapper;
	private final MyUtil myUtil;

	@Override
	public void insertReply(ReplyDto dto) throws Exception {
		try {
			mapper.insertReply(dto);
		} catch (Exception e) {
			log.info("insertReply : ", e);
			throw e;
		}
	}

	@Override
	public List<ReplyDto> listReply(Map<String, Object> map) {
		List<ReplyDto> list = null;
		
		try {
			list = mapper.listReply(map);
			for(ReplyDto dto : list) {
				dto.setName(myUtil.nameMasking(dto.getName()));
				dto.setContent(myUtil.htmlSymbols(dto.getContent()));
				
				map.put("replyNum", dto.getReplyNum());
				dto.setUserReplyLiked(hasUserReplyLiked(map));
			}			
		} catch (Exception e) {
			log.info("listReply : ", e);
		}
		
		return list;
	}

	@Override
	public int replyCount(Map<String, Object> map) {
		int result = 0;
		
		try {
			result = mapper.replyCount(map);
		} catch (Exception e) {
			log.info("listReply : ", e);
		}
		
		return result;
	}

	@Override
	public void deleteReply(Map<String, Object> map) throws Exception {
		try {
			mapper.deleteReply(map);
		} catch (Exception e) {
			log.info("deleteReply : ", e);
			throw e;
		}
	}

	@Override
	public List<ReplyDto> listReplyAnswer(Map<String, Object> map) {
		List<ReplyDto> list = null;
		
		try {
			list = mapper.listReplyAnswer(map);
			for(ReplyDto dto : list) {
				dto.setContent(myUtil.htmlSymbols(dto.getContent()));
				dto.setName(myUtil.nameMasking(dto.getName()));
			}
		} catch (Exception e) {
			log.info("listReplyAnswer : ", e);
		}
		
		return list;
	}

	@Override
	public int replyAnswerCount(Map<String, Object> map) {
		int result = 0;
		
		try {
			result = mapper.replyAnswerCount(map);
		} catch (Exception e) {
			log.info("replyAnswerCount : ", e);
		}
		
		return result;
	}

	@Override
	public void insertReplyLike(Map<String, Object> map) throws Exception {
		try {
			mapper.insertReplyLike(map);
		} catch (Exception e) {
			log.info("insertReplyLike : ", e);
			throw e;
		}
	}

	@Override
	public Map<String, Object> replyLikeCount(Map<String, Object> map) {
		Map<String, Object> countMap = null;
		
		try {
			countMap = mapper.replyLikeCount(map);
		} catch (Exception e) {
			log.info("replyLikeCount : ", e);
		}
		
		return countMap;
	}
	
	@Override
	public Integer hasUserReplyLiked(Map<String, Object> map) {
		int result = -1;
		
		// -1:공감여부를 하지않은상태, 0:비공감, 1:공감
		try {
			result = mapper.hasUserReplyLiked(map).orElse(-1);
		} catch (Exception e) {
			log.info("userReplyLiked : ", e);
		}
		
		return result;
	}

	@Override
	public void updateReplyShowHide(Map<String, Object> map) throws Exception {
		try {
			mapper.updateReplyShowHide(map);
		} catch (Exception e) {
			log.info("updateReplyShowHide : ", e);
			throw e;
		}
	}
}
