package com.small.group.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.small.group.dto.PhotoDTO;
import com.small.group.entity.Photo;
import com.small.group.entity.Group;
import com.small.group.entity.User;
import com.small.group.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

	private final PhotoRepository photoRepository;
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	
	/**
	 *  DTO TO ENTITY
	 */
	public Photo dtoToEntity(PhotoDTO dto) {
		Optional<Group> optGroup = groupRepository.findById(dto.getGroupNo());
		Optional<User> optUser = userRepository.findById(dto.getUserNo());
		
		Group group = (optGroup.isPresent()) ? optGroup.get() : null;
		User user = (optUser.isPresent()) ? optUser.get() : null;
		
		
		Photo entity = Photo.builder()
				.photoTitle(dto.getPhotoTitle())
				.photoContent(dto.getPhotoContent())
				.group(group)
				.user(user)
				.imageUrl(dto.getImageUrl())
				.thumbnailUrl(dto.getThumbnailUrl())
				.build();
		return entity;
	}
	
	/**
	 *  ENTITY TO DTO
	 */
	public PhotoDTO entityToDto(Photo entity) {
		Integer groupNo = entity.getGroup().getGroupNo();
		Integer userNo = entity.getUser().getUserNo();
		String userName = entity.getUser().getName();
		
		PhotoDTO dto = PhotoDTO.builder()
				.photoNo(entity.getPhotoNo())
				.photoTitle(entity.getPhotoTitle())
				.photoContent(entity.getPhotoContent())
				.groupNo(groupNo)
				.userNo(userNo)
				.userName(userName)
				.imageUrl(entity.getImageUrl()) 
				.thumbnailUrl(entity.getThumbnailUrl())
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
	 *	사진 저장하는 함수
	 */
	@Override
	public Photo insertPhoto(PhotoDTO photoData) {
		Photo photo = dtoToEntity(photoData);
	    return photoRepository.save(photo);
	}

	/**
	 *	사진첩리스트 중 한 개 가져오는 함수
	 */
	@Override
	public PhotoDTO readPhoto(Integer photoNo) {
		Optional<Photo> Photo = photoRepository.findById(photoNo);
		PhotoDTO PhotoDTO = null;
		if(Photo.isPresent()) {
			PhotoDTO = entityToDto(Photo.get());
		}
		return PhotoDTO;
	}

	/**
	 *	사진리스트 중 한 개 수정하는 함수
	 */
	@Override
	public Photo updatePhoto(PhotoDTO photoData) {
		Optional<Photo> data = photoRepository.findById(photoData.getPhotoNo());
		if(data.isPresent()) {
			Photo targetEntity = data.get();
			targetEntity.setPhotoContent(photoData.getPhotoContent());
			
			return photoRepository.save(targetEntity);
		}
		return null;
	}

	/**
	 *	사진 삭제하는 함수
	 */
	@Override
	public Boolean deletePhoto(Integer photoNo) {
		Optional<Photo> data = photoRepository.findById(photoNo);
		if(data.isPresent()) {
			photoRepository.delete(data.get());
			return true;
		}
		return false;
	}

	/**
	 *	사진 리스트를 가져오는 함수
	 */
	@Override
	public List<PhotoDTO> getPhotoList() {
		List<Photo> PhotoList = photoRepository.findAll();
		List<PhotoDTO> PhotoDTOList = PhotoList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return PhotoDTOList;
	}

	/**
	 *	그룹 번호로 구분하여 사진 리스트를 가져오는 함수 07.12 10:55 수정
	 
	@Override
	public List<PhotoDTO> getPhotoListByGroupNo(Integer groupNo, Integer userNo) {
		Group group = Group.builder().groupNo(groupNo).build(); // 모임 번호만 입력된 모임 객체 생성
		List<Photo> PhotoList = photoRepository.getPhotoListByGroup(group); // 모임 번호를 기준으로 쿼리 검색함
		List<PhotoDTO> PhotoDTOList = PhotoList.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return PhotoDTOList;
	}
	*/
	
	@Override
	public List<PhotoDTO> getPhotoListByGroupNo(Integer groupNo) {
	    Group group = Group.builder().groupNo(groupNo).build();
	    List<Photo> photoList = photoRepository.getPhotoListByGroup(group);
	    List<PhotoDTO> photoDTOList = photoList.stream()
	    		.map(entity -> entityToDto(entity)).collect(Collectors.toList());
	    return photoDTOList;
	}
	
}
