package com.sp.app.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member2")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member2 {
	@Id
	@Column(name = "member_id")
	private Long memberid;
	
	private String name;
	private String birth;
	private String profile_photo;
	private String tel;
	private String zip;
	private String addr1;
	private String addr2;
	private String email;
	private int receive_email;

	@OneToOne
	@JoinColumn(name = "member_id", insertable = false, updatable = false)
	private Member1 member1;
}
