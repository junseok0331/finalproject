package com.small.group.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.small.group.entity.Group;
import com.small.group.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {
	
	@Query("select p from Photo p where p.group = :group")
	List<Photo> getPhotoListByGroup(@Param("group") Group group);

}
