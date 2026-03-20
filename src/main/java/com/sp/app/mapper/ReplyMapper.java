package com.sp.app.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.sp.app.domain.dto.ReplyDto;

@Mapper
public interface ReplyMapper {
	public void insertReply(ReplyDto dto) throws SQLException;
	public int replyCount(Map<String, Object> map);
	public List<ReplyDto> listReply(Map<String, Object> map);
	public void deleteReply(Map<String, Object> map) throws SQLException;
	
	public List<ReplyDto> listReplyAnswer(Map<String, Object> map);
	public int replyAnswerCount(Map<String, Object> map);
	
	public void insertReplyLike(Map<String, Object> map) throws SQLException;
	public Map<String, Object> replyLikeCount(Map<String, Object> map);
	public Optional<Integer> hasUserReplyLiked(Map<String, Object> map);
	
	public void updateReplyShowHide(Map<String, Object> map) throws SQLException;	
}

