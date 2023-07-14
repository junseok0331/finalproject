package com.small.group.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.small.group.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Integer> {
	
	List<Group> findByGroupDescriptionContainingIgnoreCaseOrGroupNameContainingIgnoreCase(String keyword, String keyword2);

	//@Query("select g from tbl_group g where g.groupCategoryNo = :groupCategoryNo")
	//List<Group> findByCategoryNo(@Param("groupCategoryNo") Integer groupCategoryNo);

}
