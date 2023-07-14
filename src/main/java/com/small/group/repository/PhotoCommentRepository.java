package com.small.group.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.small.group.entity.Group;
import com.small.group.entity.PhotoComment;

public interface PhotoCommentRepository extends JpaRepository<PhotoComment, Integer>{

	@Query("select c from PhotoComment c where c.group = :group order by c.regDate desc")
	List<PhotoComment> findByGroup(@Param("group") Group group);
	
}
