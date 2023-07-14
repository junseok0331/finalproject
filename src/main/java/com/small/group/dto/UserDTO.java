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
public class UserDTO {

	private Integer userNo;
	
	@NotBlank(message = "아이디를 입력하세요.")
	private String userId;
	
	@NotBlank(message = "비밀번호를 입력하세요.")
	@Size(min = 2, max = 20, message = "비밀번호는 8 ~ 20자리 이내로 입력하세요.")
	private String password;
	
	@NotBlank(message = "이름(닉네임)을 입력하세요.")
	@Size(min = 1, max = 14, message = "이름(닉네임)은 1 ~ 14자 이내로 입력하세요.")
	private String name;
	
	@NotBlank(message = "전화번호를 입력하세요.")
	private String phone;
	
	private LocalDateTime regDate;
	private LocalDateTime modDate;
}
