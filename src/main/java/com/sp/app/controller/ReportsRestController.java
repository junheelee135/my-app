package com.sp.app.controller;

import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.sp.app.common.RequestUtils;
import com.sp.app.domain.dto.ReportsDto;
import com.sp.app.domain.dto.SessionInfo;
import com.sp.app.service.ReportsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReportsRestController {
	private final ReportsService service;
	
	@PostMapping("/roports/saved")
	public ResponseEntity<?> handleSaved(ReportsDto dto,
			@SessionAttribute("member") SessionInfo info) {
		
		String state = "true";
		try {
			dto.setReporter_id(info.getMember_id());
			dto.setReport_ip(RequestUtils.getClientIp());
			
			service.insertReports(dto);
			
		} catch (DuplicateKeyException e) {
			state = "liked";
		} catch (Exception e) {
			state = "false";
		}
		
		return ResponseEntity.ok(Map.of(
			"state", state
		));
	}
}
