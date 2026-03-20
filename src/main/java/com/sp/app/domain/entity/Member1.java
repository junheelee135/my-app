package com.sp.app.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 인자가 없는 생성자를 생성하되 다른 패키지에 소속된 클래스는 접근을 불허
@AllArgsConstructor
@Builder
public class Member1 {
	@Id
	@Column(name = "member_id")
	@SequenceGenerator(name="S_MY_SEQ", sequenceName = "member_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "S_MY_SEQ")
	private Long memberid;
		
	@Column(name = "login_id", unique = true, length = 100, nullable = true)
	private String loginid;
	
	@Column(length = 100, nullable = true)
	private String password;
	
	@Column(name = "sns_provider", length = 50, nullable = true)
	private String snsprovider;
	
	@Column(name = "sns_id", length = 100, nullable = true)
	private String snsid;
	
	@Column(name="userlevel", columnDefinition = "NUMBER(3) DEFAULT 1", nullable = false, insertable = false)
	private int userlevel;
	
	@Column(columnDefinition = "NUMBER(1) DEFAULT 1", insertable = false)
	private int enabled;
	
	@Column(name = "created_at", columnDefinition = "DATE DEFAULT SYSDATE", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdat;

	@Column(name = "update_at", columnDefinition = "DATE DEFAULT SYSDATE", nullable = false, insertable = false)
	private LocalDateTime updateat;
	
	@Column(name = "last_login", nullable = true)
	private LocalDateTime lastlogin;
	
	@Column(name = "failure_cnt", columnDefinition = "NUMBER(2) DEFAULT 0", insertable = false)
	private int failure_cnt;	
}
