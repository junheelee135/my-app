package com.sp.app.service;

import java.util.List;
import java.util.Map;

import com.sp.app.domain.dto.ReportsDto;

public interface ReportsService {
	public void insertReports(ReportsDto dto) throws Exception;
	
	public int dataCount(Map<String, Object> map);
	public List<ReportsDto> listReports(Map<String, Object> map);

	public int dataGroupCount(Map<String, Object> map);
	public List<ReportsDto> listGroupReports(Map<String, Object> map);
	
	public ReportsDto findById(Long num);
	
	public int dataRelatedCount(Map<String, Object> map);
	public List<ReportsDto> listRelatedReports(Map<String, Object> map);

	public void updateReports(ReportsDto dto) throws Exception;
	public void updateBlockPosts(Map<String, Object> map) throws Exception;
	public void deletePosts(Map<String, Object> map) throws Exception;
	
	public ReportsDto findByPostsId(Map<String, Object> map);
}

