package com.sp.app.domain.entity;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bbs")
public class Board {
	@Id
	@Column(name = "num", columnDefinition = "NUMBER")
	@SequenceGenerator(name="S_MY_SEQ", sequenceName = "bbs_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "S_MY_SEQ")
	private long num;
	
	@Column(name="member_id", nullable = false, updatable = false)
	private Long member_id;
	
	@Column(length = 250, nullable = false)
	private String subject;
	
	@Column(columnDefinition = "CLOB", nullable = false)
	private String content;
	
	@Column(name = "reg_date", nullable = false, columnDefinition = "DATE DEFAULT SYSDATE", 
			updatable = false)
	private LocalDateTime reg_date;
	
	@Column(name = "hitcount", nullable = false, columnDefinition = "NUMBER DEFAULT 0",
			insertable = false) // INSERT 제외
	private int hitCount;
	
	@Column(name="savefilename", nullable = true, length = 500)
	private String saveFilename;
	
	@Column(name="originalfilename", nullable = true, length = 500)
	private String originalFilename;

	@Column(name = "block", columnDefinition = "NUMBER(1) DEFAULT 0", 
			nullable = false, insertable = false)
	private int block;
	
	@Transient // 테이블 컬럼에서 제외
	private MultipartFile selectFile; // <input type='file' name='selectFile' ..
	@Transient
	private String name;
	@Transient
	private int replyCount;
	@Transient
	private int boardLikeCount;
	
	@PrePersist // INSERT 전에 호출한다. 
	public void prePersist() {
		this.reg_date = this.reg_date == null ? 
				LocalDateTime.now() : this.reg_date;
	}
}
