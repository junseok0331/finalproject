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
public class ChatDTO {

	private Integer chatNo;
	
	@NotBlank(message = "채팅 내용을 입력하세요")
	@Size(min = 1, max = 50, message = "내용은 최대 50글자수만 작성이 가능합니다.")
	private String chatContent;
	
	@NotBlank(message = "모임에 대한 지정이 비어 있습니다.")
	private Integer groupNo;
	
	private Integer userNo;
	
	@NotBlank(message = "작성자 이름이 비어 있습니다.")
	private String userName; // 회원 이름
	
	
	private LocalDateTime regDate;
	private LocalDateTime modDate;
}
