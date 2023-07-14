package com.small.group.service;

import java.util.List;

import com.small.group.dto.BoardCategoryDTO;
import com.small.group.entity.BoardCategory;

public interface BoardCategoryService {
	BoardCategory insertBoardCategory(BoardCategoryDTO boardCategoryData);
	BoardCategoryDTO readBoardCategory(Integer boardCategoryNo);
	BoardCategory updateBoardCategory(BoardCategoryDTO boardCategoryData);
    Boolean deleteBoardCategory(Integer boardCategoryNo);
    List<BoardCategoryDTO> getBoardCategoryList();
    
    /**
	 *  DTO TO ENTITY
	 */
	default BoardCategory dtoToEntity(BoardCategoryDTO dto) {
		BoardCategory entity = BoardCategory.builder()
				.boardCategoryName(dto.getBoardCategoryName())
				.build();
		return entity;
	}
	/**
	 *  ENTITY TO DTO
	 */
	default BoardCategoryDTO entityToDto(BoardCategory entity) {
		BoardCategoryDTO dto = BoardCategoryDTO.builder()
				.boardCategoryNo(entity.getBoardCategoryNo())
				.boardCategoryName(entity.getBoardCategoryName())
				.build();
		return dto;
	}
	
	
}
