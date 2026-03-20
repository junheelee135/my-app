package com.sp.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sp.app.domain.dto.ReportsDto;
import com.sp.app.mapper.ReportsMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsServiceImpl implements ReportsService {
	private final ReportsMapper mapper;
	
	@Override
	public void insertReports(ReportsDto dto) throws Exception {
		try {
			mapper.insertReports(dto);
		} catch (Exception e) {
			log.info("insertReports : ", e);
			
			throw e;
		}
	}

	@Override
	public int dataCount(Map<String, Object> map) {
		int result = 0;

		try {
			result = mapper.dataCount(map);
		} catch (Exception e) {
			log.info("dataCount : ", e);
		}

		return result;
	}

	@Override
	public List<ReportsDto> listReports(Map<String, Object> map) {
		List<ReportsDto> list = null;
		
		try {
			list = mapper.listReports(map);
		} catch (Exception e) {
			log.info("listReports : ", e);
		}
		
		return list;
	}

	@Override
	public int dataGroupCount(Map<String, Object> map) {
		int result = 0;

		try {
			result = mapper.dataGroupCount(map);
		} catch (Exception e) {
			log.info("dataGroupCount : ", e);
		}

		return result;
	}

	@Override
	public List<ReportsDto> listGroupReports(Map<String, Object> map) {
		List<ReportsDto> list = null;
		
		try {
			list = mapper.listGroupReports(map);
		} catch (Exception e) {
			log.info("listGroupReports : ", e);
		}
		
		return list;
	}

	@Override
	public ReportsDto findById(Long num) {
		ReportsDto dto = null;
		
		try {
			dto = mapper.findById(num);
		} catch (Exception e) {
			log.info("findById : ", e);
		}
		
		return dto;
	}

	@Override
	public int dataRelatedCount(Map<String, Object> map) {
		int result = 0;

		try {
			result = mapper.dataRelatedCount(map);
		} catch (Exception e) {
			log.info("dataRelatedCount : ", e);
		}

		return result;
	}

	@Override
	public List<ReportsDto> listRelatedReports(Map<String, Object> map) {
		List<ReportsDto> list = null;
		
		try {
			list = mapper.listRelatedReports(map);
		} catch (Exception e) {
			log.info("listRelatedReports : ", e);
		}
		
		return list;
	}

	@Override
	public void updateReports(ReportsDto dto) throws Exception {
		try {
			mapper.updateReports(dto);
		} catch (Exception e) {
			log.info("updateReports : ", e);
		}
	}

	@Override
	public void updateBlockPosts(Map<String, Object> map) throws Exception {
		try {
			mapper.updateBlockPosts(map);
		} catch (Exception e) {
			log.info("updateReports : ", e);
		}
	}

	@Override
	public void deletePosts(Map<String, Object> map) throws Exception {
		try {
			mapper.deletePosts(map);
		} catch (Exception e) {
			log.info("deletePosts : ", e);
		}
	}

	@Override
	public ReportsDto findByPostsId(Map<String, Object> map) {
		ReportsDto dto = null;
		
		try {
			dto = mapper.findByPostsId(map);
		} catch (Exception e) {
			log.info("findByPostsId : ", e);
		}
		
		return dto;
	}
}
