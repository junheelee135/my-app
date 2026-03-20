package com.sp.app.service;

import java.util.List;
import java.util.Map;

import com.sp.app.domain.dto.MemberDto;

public interface MemberService {
	public MemberDto loginMember(Map<String, Object> map);
	public MemberDto loginSnsMember(Map<String, Object> map);
	
	public void insertMember(MemberDto dto, String uploadPath) throws Exception;
	public void insertSnsMember(MemberDto dto) throws Exception;

	public void updateLastLogin(Long member_id) throws Exception;
	public void updateMember(MemberDto dto, String uploadPath) throws Exception;
	
	public MemberDto findById(Long member_id);
	public MemberDto findById(String login_id);
	
	public void deleteMember(Map<String, Object> map, String uploadPath) throws Exception;
	public void deleteProfilePhoto(Map<String, Object> map, String uploadPath) throws Exception;
	
	public void generatePwd(MemberDto dto) throws Exception;
	
	public List<MemberDto> listFindMember(Map<String, Object> map);	
}
