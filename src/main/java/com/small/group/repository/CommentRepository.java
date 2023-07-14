package com.small.group.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.small.group.entity.Board;
import com.small.group.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer>{

	@Query("select c from Comment c where c.board = :board order by c.regDate desc")
	List<Comment> findByBoard(@Param("board") Board board);
	
}
