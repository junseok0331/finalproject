package com.small.group.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.small.group.entity.Board;
import com.small.group.entity.BoardCategory;
import com.small.group.entity.Group;

public interface BoardRepository extends JpaRepository<Board, Integer>{
	
	
	List<Board> findByGroup(Group group);
	
	@Modifying
	@Query("update Board b set b.boardHit = b.boardHit + 1, b.modDate = :modDate where b.boardNo = :boardNo")
	void countHit(@Param("boardNo") Integer boardNo, @Param("modDate") LocalDateTime modDate);

}
