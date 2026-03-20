package com.sp.app.domain.dto;

import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReplyDto {
	private String target; // 댓글 테이블
	private String targetLike; // 좋아요 테이블
	private long replyNum;
	private long num;
	private Long member_id;
	private String name;
	private String profile_photo;
	private String content;
	private String reg_date;
	private long parentNum;
	private int showReply;
	private int block;
	private String ipAddr; 
	
	private int answerCount;
	private int likeCount;
	private int disLikeCount;
	
	@Value("-1")
	private int userReplyLiked; // 리플 좋아요/싫어요 유무(-1:하지않음, 1:좋아요, 0:싫어요)
	
	private boolean hasOwner;
	private boolean hasAdmin;	
}
