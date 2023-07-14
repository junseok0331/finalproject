package com.small.group.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoCommentDTO {

	private Integer photoCommentNo;
	
	@NotBlank(message = "내용을 입력하세요.")
	@Size(min = 1, max = 100, message = "댓글은 1 ~ 100자 이내로 작성하세요.")
	private String photoCommentContent;
	
	private Integer groupNo;
	
	private Integer userNo;
	private String userName; // 회원 이름
	
	private LocalDateTime regDate;
	private LocalDateTime modDate;
}
