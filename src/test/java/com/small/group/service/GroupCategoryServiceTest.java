package com.small.group.service;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.xml.bind.SchemaOutputResolver;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.Commit;

import com.small.group.dto.GroupCategoryDTO;
import com.small.group.entity.GroupCategory;
import com.small.group.repository.GroupCategoryRepository;

@SpringBootTest
@Transactional
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GroupCategoryServiceTest {

	@Autowired
	private GroupCategoryService groupCategoryService;
	
	@PersistenceContext
	private EntityManager em;
	
	//@Test
	//@Commit
	public void test() {
		
		// GroupCategoryDTO 객체 생성
		GroupCategoryDTO groupCategory = GroupCategoryDTO.builder()
				.groupCategoryNo(1)
				.groupCategoryName("모임카테고리1")
				.build();
		
		// CREATE TEST
		groupCategoryService.insertGroupCategory(groupCategory);
		
		// READ TEST
		groupCategory = groupCategoryService.readGroupCategory(1);
		System.out.println("가져온 카테고리 이름: " + groupCategory.getGroupCategoryName());
		
		// UPDATE TEST
		groupCategory.setGroupCategoryName("모임카테고리(변경)");
		groupCategoryService.updateGroupCategory(groupCategory);
		
		// DELETE TEST
		if(groupCategoryService.deleteGroupCategory(3)) {
			System.out.println("삭제 성공");
		}
		
		// SELECT TEST
		List<GroupCategoryDTO> groupCategoryDTOList = groupCategoryService.getGroupCategoryList();
		for(GroupCategoryDTO dto : groupCategoryDTOList) {
			System.out.println("모임 카테고리 이름 : " + dto.getGroupCategoryName());
		}
	}
}
