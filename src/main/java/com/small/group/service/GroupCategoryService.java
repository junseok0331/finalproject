package com.small.group.service;

import java.util.List;

import com.small.group.dto.GroupCategoryDTO;
import com.small.group.entity.GroupCategory;

public interface GroupCategoryService {

	GroupCategory insertGroupCategory(GroupCategoryDTO groupCategoryData);
	GroupCategoryDTO readGroupCategory(Integer groupCategoryNo);
	GroupCategory updateGroupCategory(GroupCategoryDTO groupCategoryData);
    Boolean deleteGroupCategory(Integer groupCategoryNo);
    List<GroupCategoryDTO> getGroupCategoryList();
}
