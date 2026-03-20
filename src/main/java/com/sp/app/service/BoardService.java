package com.sp.app.service;

import java.util.List;
import java.util.Map;

import com.sp.app.domain.entity.Board;

public interface BoardService {
	public void insertBoard(Board dto, String uploadPath) throws Exception;
	public void updateBoard(Board dto, String uploadPath) throws Exception;
	public void deleteBoard(long num, String uploadPath, Long member_id, int userLevel) throws Exception;
	public void updateHitCount(long num) throws Exception;
	
	public List<Board> listBoard(Map<String, Object> map);
	public int dataCount(Map<String, Object> map);
	public Board findById(long num);
	public Board findByPrev(Map<String, Object> map);
	public Board findByNext(Map<String, Object> map);
	
	public void insertBoardLike(Map<String, Object> map) throws Exception;
	public void deleteBoardLike(Map<String, Object> map) throws Exception;
	public int boardLikeCount(long num);
	public boolean isUserBoardLiked(Map<String, Object> map);
	
	public boolean deleteUploadFile(String uploadPath, String filename);
}
