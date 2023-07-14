package com.small.group.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.small.group.dto.GroupCategoryDTO;
import com.small.group.entity.GroupCategory;
import com.small.group.repository.*;

import lombok.RequiredArgsConstructor;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupCategoryServiceImpl implements GroupCategoryService {

	private final GroupCategoryRepository groupCategoryRepository;
	
	/**
	 *  DTO TO ENTITY
	 */
	private GroupCategory dtoToEntity(GroupCategoryDTO dto) {
		GroupCategory entity = GroupCategory.builder()
				.groupCategoryName(dto.getGroupCategoryName())
				.build();
		return entity;
	}
	
	/**
	 *  ENTITY TO DTO
	 */
	private GroupCategoryDTO entityToDto(GroupCategory entity) {
		GroupCategoryDTO dto = GroupCategoryDTO.builder()
				.groupCategoryNo(entity.getGroupCategoryNo())
				.groupCategoryName(entity.getGroupCategoryName())
				.build();
		return dto;
	}

	/**
	 * ----------------------------------
	 * 			C / R / U / D
	 * ----------------------------------
	 */
	
	/**
	 *	모임 카테고리 저장하는 함수
	 */
	@Override
	public GroupCategory insertGroupCategory(GroupCategoryDTO categoryData) {
		GroupCategory groupCategory = dtoToEntity(categoryData);
		return groupCategoryRepository.save(groupCategory);
	}

	/**
	 *	모임 카테고리 한 개 가져오는 함수
	 */
	@Override
	public GroupCategoryDTO readGroupCategory(Integer groupCategoryNo) {
		Optional<GroupCategory> groupCategory = groupCategoryRepository.findById(groupCategoryNo);
		GroupCategoryDTO groupCategoryDTO = null;
		if(groupCategory.isPresent()) {
			groupCategoryDTO = entityToDto(groupCategory.get());
		}
		return groupCategoryDTO;
	}

	/**
	 *	모임 카테고리 수정하는 함수
	 */
	@Override
	public GroupCategory updateGroupCategory(GroupCategoryDTO groupCategoryData) {
		Optional<GroupCategory> data = groupCategoryRepository.findById(groupCategoryData.getGroupCategoryNo());
		if(data.isPresent()) {
			GroupCategory targetEntity = data.get();
			targetEntity.setGroupCategoryName(groupCategoryData.getGroupCategoryName());
			
			return groupCategoryRepository.save(targetEntity);
		}
		return null;
	}

	/**
	 *	모임 카테고리 삭제하는 함수
	 */
	@Override
	public Boolean deleteGroupCategory(Integer groupCategoryNo) {
		Optional<GroupCategory> data = groupCategoryRepository.findById(groupCategoryNo);
		if(data.isPresent()) {
			groupCategoryRepository.delete(data.get());
			return true;
		}
		return false;
	}

	/**
	 *	모임 카테고리 리스트를 가져오는 함수
	 */
	@Override
	public List<GroupCategoryDTO> getGroupCategoryList() {
        List<GroupCategory> groupCategoryList = groupCategoryRepository.findAll();
        List<GroupCategoryDTO> groupCategoryDTOList = groupCategoryList
        		.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
        return groupCategoryDTOList;
	}

}
