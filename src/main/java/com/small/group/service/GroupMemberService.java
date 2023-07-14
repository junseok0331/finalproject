package com.small.group.service;

import java.util.List;

import com.small.group.dto.GroupMemberDTO;
import com.small.group.dto.UserDTO;
import com.small.group.entity.Group;
import com.small.group.entity.GroupMember;
import com.small.group.entity.User;


public interface GroupMemberService {
	
	GroupMember insertGroupMember(GroupMemberDTO groupMemberData);
	GroupMemberDTO readGroupMember(Integer groupMemberNo);
//	GroupMember updateGroupMember(GroupMemberDTO groupMemberData);
    Boolean deleteGroupMember(Integer groupMemberNo);
    List<GroupMemberDTO> getGroupMemberList();
	List<GroupMemberDTO> getGroupMemberListByUser(User user);
	Integer isMemberOfGroup(Integer userNo, Integer groupNo);
	void insertGroupMember(Group group, User user);
	Integer getGroupNoByMemberRegistrationTable(Integer userNo, Integer groupNo);
	List<GroupMemberDTO> findByGroupNo(Integer groupNo);
	List<UserDTO> getGroupMemberListByGroupNoAndReaderFirst(Integer groupNo, Integer userNo);
}
