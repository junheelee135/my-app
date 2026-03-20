package com.sp.app.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.sp.app.domain.entity.Board;

@Mapper
public interface BoardMapper {
	public int dataCount(Map<String, Object> map);
	public List<Board> listBoard(Map<String, Object> map);
	
	public Board findById(Long num);
	public Board findByPrev(Map<String, Object> map);
	public Board findByNext(Map<String, Object> map);
	
	public void insertBoardLike(Map<String, Object> map) throws SQLException;
	public void deleteBoardLike(Map<String, Object> map) throws SQLException;
	public int boardLikeCount(long num);
	public Board hasUserBoardLiked(Map<String, Object> map);
}

