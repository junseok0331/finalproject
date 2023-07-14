package com.small.group.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.small.group.dto.GroupDTO;
import com.small.group.dto.PageRequestDTO;
import com.small.group.dto.PageResultDTO;
import com.small.group.entity.Group;
import com.small.group.entity.GroupCategory;
import com.small.group.entity.Region;
import com.small.group.entity.User;
import com.small.group.repository.*;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
	
	// 의존성주입 : @RequiredArgsConstructor
	private final GroupRepository groupRepository;
	private final GroupCategoryRepository groupCategoryRepository;
	private final UserRepository userRepository;
	private final RegionRepository regionRepository;


    /**
	 *  DTO TO ENTITY
	 */
    private Group dtoToEntity(GroupDTO dto) {
    	Optional<GroupCategory> optGroupCategory = groupCategoryRepository.findById(dto.getGroupCategoryNo());
		GroupCategory groupCategory = (optGroupCategory.isPresent()) ? optGroupCategory.get() : null;
		
		Optional<User> optUser = userRepository.findById(dto.getUserNo());
		User user = (optUser.isPresent()) ? optUser.get() : null;
		
		Optional<Region> optRegion  = regionRepository.findById(dto.getRegionNo());
		Region region = (optRegion.isPresent()) ? optRegion.get() : null;
		
		
    	Group entity = Group.builder()
							.groupNo(dto.getGroupNo())
							.groupName(dto.getGroupName())
							.groupDescription(dto.getGroupDescription())
							.groupCategory(groupCategory)
							.region(region)
							.user(user)
							.build();
		return entity;
	}

	/**
	 *  ENTITY TO DTO
	 */
    private GroupDTO entityToDto(Group entity) {

		GroupDTO dto = GroupDTO.builder()
							.groupNo(entity.getGroupNo())
							.groupName(entity.getGroupName())
							.groupDescription(entity.getGroupDescription())
							.groupCategoryNo(entity.getGroupCategory().getGroupCategoryNo())
							.groupCategoryName(entity.getGroupCategory().getGroupCategoryName())
							.regionNo(entity.getRegion().getRegionNo())
							.regionName(entity.getRegion().getRegionName())
							.userNo(entity.getUser().getUserNo())
							.userId(entity.getUser().getUserId())
							.userName(entity.getUser().getName())
							.regDate(entity.getRegDate())
							.modDate(entity.getModDate())
							.build();
		return dto;
	}
    
    
	/**
	 * ----------------------------------
	 * 			C / R / U / D
	 * ----------------------------------
	 */
    
    /**
	 *	그룹(모임) 목록 조회[페이징]
	 */
    @Override
    public PageResultDTO<GroupDTO, Group> getgroupList(PageRequestDTO requestDTO) {

        Pageable pageable = requestDTO.getPageable(Sort.by("groupNo").descending());
        
        Page<Group> result = groupRepository.findAll(pageable);
        
        Function<Group, GroupDTO> fn = (entity -> entityToDto(entity)); // java.util.Function
        
        return new PageResultDTO<>(result, fn );
    }
    
	/**
	 *	모임 리스트를 가져오는 함수 1
	 */
    @Override
    public List<GroupDTO> getgroupList(GroupDTO groupData) {
        List<Group> groupList  = groupRepository.findAll();

        List<GroupDTO> groupDTOList  = groupList .stream()
                .map(entity -> entityToDto(entity))
                .collect(Collectors.toList());

        return groupDTOList;
    }
    
    /**
	 *	모임 리스트를 가져오는 함수
	 */
	@Override
	public List<GroupDTO> getGroupList() {
		List<Group> groupList = groupRepository.findAll();
		List<GroupDTO> groupDTOList = groupList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return groupDTOList;
	}
    
	/**
	 *	모임 저장하는 함수
	 */
	@Override
	public Group insertGroup(GroupDTO groupData) {
		Group group = dtoToEntity(groupData);
		return groupRepository.save(group);
	}
    
	/**
	 *	모임 한 개 가져오는 함수
	 */
	@Override
	public GroupDTO readGroup(Integer groupNo) {
		Optional<Group> group = groupRepository.findById(groupNo);
		GroupDTO groupDTO = null;
		if(group.isPresent()) {
			groupDTO = entityToDto(group.get());
		}
		return group.isPresent() ? entityToDto(group.get()): null;
	}
    
    /**
	 *	모임 수정하는 함수
	 */
	@Override
	public Group updateGroup(GroupDTO groupData) {
		Optional<Group> data = groupRepository.findById(groupData.getGroupNo());
		if(data.isPresent()) {
			Group targetEntity = data.get();
			targetEntity.setGroupName(groupData.getGroupName());
			targetEntity.setGroupDescription(groupData.getGroupDescription());
			
			Region region = regionRepository.findById(groupData.getRegionNo())
					.orElseThrow(() -> new RuntimeException("지역 정보가 존재하지 않습니다."));
			
			GroupCategory groupCategory = groupCategoryRepository.findById(groupData.getGroupCategoryNo())
					.orElseThrow(() -> new RuntimeException("그룹카테고리 정보가 존재하지 않습니다."));
			
			User user = userRepository.findById(groupData.getUserNo())
					.orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));;
			
			targetEntity.setRegion(region);
			targetEntity.setGroupCategory(groupCategory);
			targetEntity.setUser(user);
			return groupRepository.save(targetEntity);
		}
		return null;
	}
	  
	/**
	 *	모임 멤버 삭제하는 함수
	 */
	@Override
	public Boolean deleteGroup(Integer groupNo) {
		Optional<Group> data = groupRepository.findById(groupNo);
		if(data.isPresent()) {
			groupRepository.delete(data.get());
			return true;
		}
		return false;
	}

	/**
     *	 검색어(keyword)를 사용하여 지역이름, 모임이름, 모임설명 검색
     */
	@Override
	public List<GroupDTO> getGroupList(String keyword) {
		List<GroupDTO> groupList = getGroupList();
		List<GroupDTO> searchGroupList = groupList
											.stream()
											.filter(dto -> getSearchList(dto, keyword))
											.collect(Collectors.toList());
		
		return searchGroupList;
		}
	/**
	 *	keyword 값으로 검색하여 지역이름, 모임이름, 모임설명에 포함이 되는지 확인한 후 
	 *	GroupDTO 객체를 반환하는 함수
	 */
	private boolean getSearchList(GroupDTO dto, String keyword) {
		boolean checkGroupName = dto.getGroupName().contains(keyword);
		boolean checkGroupDescription = dto.getGroupDescription().contains(keyword);
		boolean checkRegionName = dto.getRegionName().contains(keyword);
		
		if (checkGroupName || checkGroupDescription || checkRegionName) {
			return true;
		}
		
		return false;
	}
	
}
