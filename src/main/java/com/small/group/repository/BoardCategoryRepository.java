package com.small.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.small.group.entity.BoardCategory;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Integer> {

}
