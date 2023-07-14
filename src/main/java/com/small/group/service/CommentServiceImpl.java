package com.small.group.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.small.group.dto.CommentDTO;
import com.small.group.entity.Board;
import com.small.group.entity.Comment;
import com.small.group.entity.User;
import com.small.group.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;
	
	/**
	 *  DTO TO ENTITY
	 */
	private Comment dtoToEntity(CommentDTO dto) {
		Optional<Board> optBoard = boardRepository.findById(dto.getBoardNo());
		Optional<User> optUser = userRepository.findById(dto.getUserNo());
		
		Board board = (optBoard.isPresent()) ? optBoard.get() : null;
		User user = (optUser.isPresent()) ? optUser.get() : null;
		
		Comment entity = Comment.builder()
				.commentContent(dto.getCommentContent())
				.board(board)
				.user(user)
				.build();
		return entity;
	}
	
	/**
	 *  ENTITY TO DTO
	 */
	private CommentDTO entityToDto(Comment entity) {
		
		Integer boardNo = entity.getBoard().getBoardNo();
		Integer userNo = entity.getUser().getUserNo();
		String userName = entity.getUser().getName();
		
		CommentDTO dto = CommentDTO.builder()
				.commentNo(entity.getCommentNo())
				.commentContent(entity.getCommentContent())
				.boardNo(boardNo)
				.userNo(userNo)
				.userName(userName)
				.regDate(entity.getRegDate())
				.modDate(entity.getModDate())
				.build();
		return dto;
	}
	
	/**
	 * ----------------------------------
	 * 			C / R / U / D
	 * ----------------------------------
	 */
	
	/**
	 *	댓글 저장하는 함수
	 */
	@Override
	public Comment insertComment(CommentDTO commentData) {
		Comment comment = dtoToEntity(commentData);
		return commentRepository.save(comment);
	}

	/**
	 *	댓글 한 개 가져오는 함수
	 */
	@Override
	public CommentDTO readComment(Integer commentNo) {
		Optional<Comment> comment = commentRepository.findById(commentNo);
		CommentDTO commentDTO = null;
		if(comment.isPresent()) {
			commentDTO = entityToDto(comment.get());
		}
		return commentDTO;
	}

	/**
	 *	댓글 수정하는 함수
	 */
	@Override
	public Comment updateComment(CommentDTO commentData) {
		Optional<Comment> data = commentRepository.findById(commentData.getCommentNo());
		
		if(data.isPresent()) {
			Comment targetEntity = data.get();
			targetEntity.setCommentContent(commentData.getCommentContent());
			
			return commentRepository.save(targetEntity);
		}
		return null;
	}

	/**
	 *	댓글 삭제하는 함수
	 */
	@Override
	public Boolean deleteComment(Integer commentNo) {
		Optional<Comment> data = commentRepository.findById(commentNo);
		if(data.isPresent()) {
			commentRepository.delete(data.get());
			return true;
		}
		return false;
	}

	/**
	 *	댓글 리스트를 가져오는 함수
	 */
	@Override
	public List<CommentDTO> getCommentList() {
		List<Comment> commentList = commentRepository.findAll();
		List<CommentDTO> commentDTOList = commentList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return commentDTOList;
	}
	
	/**
	 * 한 게시글의 댓글 리스트를 가져오는 메소드
	 */
	@Override
	public List<CommentDTO> getCommentList(Integer boardNo) {
		Board board = Board.builder().boardNo(boardNo).build();
		List<Comment> commentList = commentRepository.findByBoard(board);
		List<CommentDTO> commentDTOList = commentList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return commentDTOList;
	}
	
	
}
