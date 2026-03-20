package com.sp.app.service;

import java.util.List;
import java.util.Map;

import com.sp.app.domain.dto.ReplyDto;

public interface ReplyService {
	public void insertReply(ReplyDto dto) throws Exception;
	public List<ReplyDto> listReply(Map<String, Object> map);
	public int replyCount(Map<String, Object> map);
	public void deleteReply(Map<String, Object> map) throws Exception;
	
	public List<ReplyDto> listReplyAnswer(Map<String, Object> map);
	public int replyAnswerCount(Map<String, Object> map);
	
	public void insertReplyLike(Map<String, Object> map) throws Exception;
	public Map<String, Object> replyLikeCount(Map<String, Object> map);
	public Integer hasUserReplyLiked(Map<String, Object> map);
	
	public void updateReplyShowHide(Map<String, Object> map) throws Exception;
}

