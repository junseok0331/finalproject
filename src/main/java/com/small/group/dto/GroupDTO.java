package com.small.group.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {

	private Integer groupNo;
	
	@NotBlank(message = "그룹명을 입력하세요.")
	@Size(min = 1, max = 20, message = "그룹명은 1 ~ 20자 이내로 작성하세요.")
	private String groupName;
	
	@NotBlank(message = "그룹소개를 입력하세요.")
	@Size(min = 1, max = 200, message = "그룹소개는 1 ~ 200자 이내로 작성하세요.")
	private String groupDescription;
	
	private Integer groupCategoryNo;
	private String groupCategoryName; // 모임 카테고리 이름
	
	private Integer regionNo;
	private String regionName; // 지역 이름
	
	private Integer userNo;
	private String userId;
	private String userName; // 모임장 이름
	
	private LocalDateTime regDate;
	private LocalDateTime modDate;
}
