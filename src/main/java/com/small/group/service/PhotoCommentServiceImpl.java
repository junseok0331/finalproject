package com.small.group.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.small.group.dto.CommentDTO;
import com.small.group.dto.PhotoCommentDTO;
import com.small.group.entity.Board;
import com.small.group.entity.Comment;
import com.small.group.entity.Group;
import com.small.group.entity.PhotoComment;
import com.small.group.entity.User;
import com.small.group.repository.GroupRepository;
import com.small.group.repository.PhotoCommentRepository;
import com.small.group.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotoCommentServiceImpl implements PhotoCommentService {

	private final PhotoCommentRepository photoCommentRepository;
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	
	/**
	 *  DTO TO ENTITY
	 */
	private PhotoComment dtoToEntity(PhotoCommentDTO dto) {
		Optional<Group> optGroup = groupRepository.findById(dto.getGroupNo());
		Optional<User> optUser = userRepository.findById(dto.getUserNo());
		
		Group group = (optGroup.isPresent()) ? optGroup.get() : null;
		User user = (optUser.isPresent()) ? optUser.get() : null;
		
		PhotoComment entity = PhotoComment.builder()
				.photoCommentContent(dto.getPhotoCommentContent())
				.group(group)
				.user(user)
				.build();
		return entity;
	}
	
	/**
	 *  ENTITY TO DTO
	 */
	private PhotoCommentDTO entityToDto(PhotoComment entity) {
		
		Integer groupNo = entity.getGroup().getGroupNo();
		Integer userNo = entity.getUser().getUserNo();
		String userName = entity.getUser().getName();
		
		PhotoCommentDTO dto = PhotoCommentDTO.builder()
				.photoCommentNo(entity.getPhotoCommentNo())
				.photoCommentContent(entity.getPhotoCommentContent())
				.groupNo(groupNo)
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
	 *	사진 댓글 저장하는 함수
	 */
	@Override
	public PhotoComment insertPhotoComment(PhotoCommentDTO photoCommentData) {
		PhotoComment photoComment = dtoToEntity(photoCommentData);
		return photoCommentRepository.save(photoComment);
	}

	/**
	 *	사진 댓글 한 개 가져오는 함수
	 */
	@Override
	public PhotoCommentDTO readPhotoComment(Integer photoCommentNo) {
		Optional<PhotoComment> photoComment = photoCommentRepository.findById(photoCommentNo);
		PhotoCommentDTO photoCommentDTO = null;
		if(photoComment.isPresent()) {
			photoCommentDTO = entityToDto(photoComment.get());
		}
		return photoCommentDTO;
	}

	/**
	 *	사진 댓글 수정하는 함수
	 */
	@Override
	public PhotoComment updatePhotoComment(PhotoCommentDTO photoCommentData) {
		Optional<PhotoComment> data = photoCommentRepository.findById(photoCommentData.getPhotoCommentNo());
		
		if(data.isPresent()) {
			PhotoComment targetEntity = data.get();
			targetEntity.setPhotoCommentContent(photoCommentData.getPhotoCommentContent());
			
			return photoCommentRepository.save(targetEntity);
		}
		return null;
	}

	/**
	 *	사진 댓글 삭제하는 함수
	 */
	@Override
	public Boolean deletePhotoComment(Integer PhotoCommentNo) {
		Optional<PhotoComment> data = photoCommentRepository.findById(PhotoCommentNo);
		if(data.isPresent()) {
			photoCommentRepository.delete(data.get());
			return true;
		}
		return false;
	}

	/**
	 *	사진 댓글 리스트를 가져오는 함수
	 */
	@Override
	public List<PhotoCommentDTO> getPhotoCommentList() {
		List<PhotoComment> photoCommentList = photoCommentRepository.findAll();
		List<PhotoCommentDTO> photoCommentDTOList = photoCommentList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return photoCommentDTOList;
	}
	
	/**
	 * 한 그룹 사진의 댓글 리스트를 가져오는 메소드
	 */
	@Override
	public List<PhotoCommentDTO> getPhotoCommentList(Integer groupNo) {
		Group group = Group.builder().groupNo(groupNo).build();
		List<PhotoComment> photoCommentList = photoCommentRepository.findByGroup(group);
		List<PhotoCommentDTO> photoCommentDTOList = photoCommentList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return photoCommentDTOList;
	}
	
	
}
