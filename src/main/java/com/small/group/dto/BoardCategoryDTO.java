package com.small.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardCategoryDTO {

	private Integer boardCategoryNo;
	private String boardCategoryName;
	
}
