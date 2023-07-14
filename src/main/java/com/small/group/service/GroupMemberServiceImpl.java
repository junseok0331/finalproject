package com.small.group.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.small.group.dto.GroupMemberDTO;
import com.small.group.dto.UserDTO;
import com.small.group.entity.Group;
import com.small.group.entity.GroupMember;
import com.small.group.entity.User;
import com.small.group.repository.GroupMemberRepository;
import com.small.group.repository.GroupRepository;
import com.small.group.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {

	private final GroupMemberRepository groupMemberRepository;
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	private final UserService userService;

	
	/**
	 *  DTO TO ENTITY
	 */
	private GroupMember dtoToEntity(GroupMemberDTO dto) {
		Optional<Group> optGroup = groupRepository.findById(dto.getGroupNo());
		Optional<User> optUser = userRepository.findById(dto.getUserNo());
		
		Group group = (optGroup.isPresent()) ? optGroup.get() : null;
		User user = (optUser.isPresent()) ? optUser.get() : null;
				
		GroupMember entity = GroupMember.builder()
				.group(group)
				.user(user)
				.build();
		return entity;
	}
	
	/**
	 *  ENTITY TO DTO
	 */
	private GroupMemberDTO entityToDto(GroupMember entity) {
		Integer groupNo = entity.getGroup().getGroupNo();
		String groupName = entity.getGroup().getGroupName();
		Integer userNo = entity.getUser().getUserNo();
		String userId = entity.getUser().getUserId();
		
		GroupMemberDTO dto = GroupMemberDTO.builder()
				.groupMemberNo(entity.getGroupMemberNo())
				.groupNo(groupNo)
				.groupName(groupName)
				.userNo(userNo)
				.userId(userId)
				.regDate(entity.getRegDate())
				.build();
		return dto;
	}
	
	/**
	 * ----------------------------------
	 * 			C / R / U / D
	 * ----------------------------------
	 */
	
	/**
	 *	모임 멤버 저장하는 함수
	 */
	@Override
	public GroupMember insertGroupMember(GroupMemberDTO groupMemberData) {
		GroupMember groupMember = dtoToEntity(groupMemberData);
		return groupMemberRepository.save(groupMember);
	}
	
	@Override
	public void insertGroupMember(Group group, User user) {
		GroupMember groupMemberEntity = GroupMember.builder()
									.group(group)
									.user(user).build();
		
		GroupMemberDTO groupMemberDTO = entityToDto(groupMemberEntity);
		insertGroupMember(groupMemberDTO);
	}
	
	/**
	 *	모임 멤버 한 개 가져오는 함수
	 */
	@Override
	public GroupMemberDTO readGroupMember(Integer groupMemberNo) {
		Optional<GroupMember> groupMember = groupMemberRepository.findById(groupMemberNo);
		GroupMemberDTO groupMemberDTO = null;
		if(groupMember.isPresent()) {
			groupMemberDTO = entityToDto(groupMember.get());
		}
		return groupMemberDTO;
	}

	/**
	 *	모임 멤버 수정하는 함수(수정할 필요 없이 추가, 삭제만 기능)
	 */
//	@Override
//	public GroupMember updateGroupMember(GroupMemberDTO groupMemberData) {
//		Optional<GroupMember> data = groupMemberRepository.findById(groupMemberData.getGroupMemberNo());
//		if(data.isPresent()) {
//			GroupMember targetEntity = data.get();
//			
//			Optional<Group> optGroup = groupRepository.findById(groupMemberData.getGroupNo());
//			Group group = (optGroup.isPresent()) ? optGroup.get() : null;
//			
//			Optional<User> optUser = userRepository.findById(groupMemberData.getUserNo());
//			User user = (optUser.isPresent()) ? optUser.get() : null;
//			
//			targetEntity.setGroup(group);
//			targetEntity.setUser(user);
//			
//			return groupMemberRepository.save(targetEntity);
//		}
//		return null;
//	}

	/**
	 *	모임 멤버 삭제하는 함수
	 */
	@Override
	public Boolean deleteGroupMember(Integer groupMemberNo) {
		Optional<GroupMember> data = groupMemberRepository.findById(groupMemberNo);
		if(data.isPresent()) {
			groupMemberRepository.delete(data.get());
			return true;
		}
		return false;
	}

	/**
	 *	모임 멤버 리스트를 가져오는 함수
	 */
	@Override
	public List<GroupMemberDTO> getGroupMemberList() {
		List<GroupMember> groupMemberList = groupMemberRepository.findAll();
		List<GroupMemberDTO> groupMemberDTOList = groupMemberList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return groupMemberDTOList;
	}

	@Override
	public List<GroupMemberDTO> getGroupMemberListByUser(User user) {
		List<GroupMember> groupMemberList = groupMemberRepository.getGroupMemberByUser(user);
		List<GroupMemberDTO> groupMemberDTOList = groupMemberList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return groupMemberDTOList;
	}

	@Override
	public Integer isMemberOfGroup(Integer userNo, Integer groupNo) {
	    Integer result = groupMemberRepository.isMemberOfGroup(userNo, groupNo);
	    return result;
	}
	
	@Override
	public Integer getGroupNoByMemberRegistrationTable(Integer userNo, Integer groupNo) {
		Integer result = groupMemberRepository.getGroupNoByMemberRegistrationTable(userNo, groupNo);
		return result;
	}

	@Override
	public List<GroupMemberDTO> findByGroupNo(Integer groupNo) {
		List<GroupMember> groupMemberList = groupMemberRepository.findByGroupNo(groupNo);
		List<GroupMemberDTO> groupMemberDTOList = groupMemberList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return groupMemberDTOList;
	}
	
	/*
	 *	GroupMember 테이블에서 groupNo에 해당하는 리스트를 조회하고
	 *	오름차순으로 정렬하되 userNo에 해당하는 행이 맨 앞으로 오도록 정렬.
	 *	그리고 해당 UserNo을 순차 조회하여 UserList를 결과 값으로 반환.
	 */
	@Override
	public List<UserDTO> getGroupMemberListByGroupNoAndReaderFirst(Integer groupNo, Integer userNo) {
		// Entity List
		List<GroupMember> entityList = groupMemberRepository.findAll();
		// DTO List By GroupNo
		List<GroupMemberDTO> groupMemberDTOList = entityList
				.stream().filter(groupMember -> groupMember.getGroup().getGroupNo() == groupNo).map(entity -> entityToDto(entity)).collect(Collectors.toList());
		// DTO List Sorted GroupMemberNo
		List<GroupMemberDTO> sortedGroupMemberDTOList = groupMemberDTOList
				.stream().sorted(Comparator.comparingInt(GroupMemberDTO::getGroupMemberNo))
				.collect(Collectors.toList());
		
		// DTO List Sorted UserNo First
		sortedGroupMemberDTOList = sortedGroupMemberDTOList
				.stream().sorted(Comparator.comparingInt(member -> member.getUserNo() == userNo ? 0 : 1))
				.collect(Collectors.toList());
		
		List<UserDTO> userDTOList = new ArrayList<>(); 
		for(GroupMemberDTO groupMember : sortedGroupMemberDTOList) {
			UserDTO user = userService.readUser(groupMember.getUserNo());
			userDTOList.add(user);
		}
		
		return userDTOList;
	}
	

}
