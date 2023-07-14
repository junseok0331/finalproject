package com.small.group.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.small.group.dto.BoardCategoryDTO;
import com.small.group.entity.BoardCategory;
import com.small.group.repository.BoardCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardCategoryServiceImpl implements BoardCategoryService {
	private final BoardCategoryRepository boardCategoryRepository;

	/**
	 * ----------------------------------
	 * 			C / R / U / D
	 * ----------------------------------
	 */
	
	/**
	 *	게시글 카테고리 저장하는 함수
	 */
	@Override
	public BoardCategory insertBoardCategory(BoardCategoryDTO boardCategoryData) {
		BoardCategory boardCategory = dtoToEntity(boardCategoryData);
		return boardCategoryRepository.save(boardCategory);
	}

	/**
	 *	게시글 카테고리 한 개 가져오는 함수
	 */
	@Override
	public BoardCategoryDTO readBoardCategory(Integer boardCategoryNo) {
		Optional<BoardCategory> boardCategory = boardCategoryRepository.findById(boardCategoryNo);
		BoardCategoryDTO boardCategoryDTO = null;
		if(boardCategory.isPresent()) {
			boardCategoryDTO = entityToDto(boardCategory.get());
		}
		return boardCategoryDTO;
	}

	/**
	 *	게시글 카테고리 수정하는 함수
	 */
	@Override
	public BoardCategory updateBoardCategory(BoardCategoryDTO boardCategoryData) {
		Optional<BoardCategory> data = boardCategoryRepository.findById(boardCategoryData.getBoardCategoryNo());
		if(data.isPresent()) {
			BoardCategory targetEntity = data.get();
			targetEntity.setBoardCategoryName(boardCategoryData.getBoardCategoryName());
			
			return boardCategoryRepository.save(targetEntity);
		}
		return null;
	}

	/**
	 *	게시글 카테고리 삭제하는 함수
	 */
	@Override
	public Boolean deleteBoardCategory(Integer boardCategoryNo) {
		Optional<BoardCategory> data = boardCategoryRepository.findById(boardCategoryNo);
		if(data.isPresent()) {
			boardCategoryRepository.delete(data.get());
			return true;
		}
		return false;
	}

	/**
	 *	게시글 카테고리 리스트를 가져오는 함수
	 */
	@Override
	public List<BoardCategoryDTO> getBoardCategoryList() {
		List<BoardCategory> boardCategoryList = boardCategoryRepository.findAll();
		List<BoardCategoryDTO> boardCategoryDTOList = boardCategoryList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return boardCategoryDTOList;
	}

	
	
}
