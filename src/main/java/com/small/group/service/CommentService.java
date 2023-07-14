package com.small.group.service;

import java.util.List;

import com.small.group.dto.CommentDTO;
import com.small.group.entity.Comment;


public interface CommentService {

	Comment insertComment(CommentDTO commentData);
	CommentDTO readComment(Integer commentNo);
	Comment updateComment(CommentDTO commentData);
    Boolean deleteComment(Integer commentNo);
    List<CommentDTO> getCommentList();
	List<CommentDTO> getCommentList(Integer boardNo);
}
