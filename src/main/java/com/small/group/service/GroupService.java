package com.small.group.service;

import java.util.List;

import com.small.group.dto.GroupDTO;
import com.small.group.dto.PageRequestDTO;
import com.small.group.dto.PageResultDTO;
import com.small.group.entity.Group;
import com.small.group.entity.GroupCategory;
import com.small.group.entity.Region;
import com.small.group.entity.User;

public interface GroupService {
	
	// 그룹(모임) 페이지
	PageResultDTO<GroupDTO, Group> getgroupList(PageRequestDTO requestDTO);

	// 그룹(모임)폼에 그룹(모임) 목록을 드롭다운 리스트 형태로 보여주기 위한 조회
	List<GroupDTO> getgroupList(GroupDTO groupData);
    GroupDTO readGroup(Integer groupNo);
    Group insertGroup(GroupDTO groupDTO);
    Group updateGroup(GroupDTO groupDTo);
    Boolean deleteGroup(Integer groupNo);
    List<GroupDTO> getGroupList();
    List<GroupDTO> getGroupList(String keyword); // 키워드로 그룹 검색

}
