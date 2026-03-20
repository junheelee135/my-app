package com.sp.app.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportsDto {
	private long num;
	private Long target_num;
	private String target;
	private Long reporter_id;
	private String content_type;
	private String content_title;
	private String reason_code;
	private String reason_detail;
	private String report_date;
	private String report_ip;
	private int report_status;
	private String action_taken;
	private Long processor_id;
	private String processed_date;
	
	private int reportsCount;
	private String reporter_name;
	private String processor_name;

	// 게시글 정보
	private String writer_id;
	private String writer;
	private String subject;
	private String content;
	private String imageFilename;
	private int block;
	
	private String mode;
}
