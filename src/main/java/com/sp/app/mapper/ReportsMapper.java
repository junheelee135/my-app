package com.sp.app.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.sp.app.domain.dto.ReportsDto;

@Mapper
public interface ReportsMapper {
	public void insertReports(ReportsDto dto) throws SQLException;
	
	public int dataCount(Map<String, Object> map);
	public List<ReportsDto> listReports(Map<String, Object> map);

	public int dataGroupCount(Map<String, Object> map);
	public List<ReportsDto> listGroupReports(Map<String, Object> map);
	
	public ReportsDto findById(Long num);
	
	public int dataRelatedCount(Map<String, Object> map);
	public List<ReportsDto> listRelatedReports(Map<String, Object> map);

	public void updateReports(ReportsDto dto) throws SQLException;
	public void updateBlockPosts(Map<String, Object> ma) throws SQLException;
	public void deletePosts(Map<String, Object> map) throws SQLException;
	
	public ReportsDto findByPostsId(Map<String, Object> map);
}

