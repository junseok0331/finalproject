package com.small.group.service;

import java.util.List;

import com.small.group.dto.PhotoDTO;
import com.small.group.entity.Photo;


public interface PhotoService {

	Photo insertPhoto(PhotoDTO photoData);
	PhotoDTO readPhoto(Integer photoNo);
	Photo updatePhoto(PhotoDTO photoData);
    Boolean deletePhoto(Integer photoNo);
    List<PhotoDTO> getPhotoList();
    
    Photo dtoToEntity(PhotoDTO dto);
    PhotoDTO entityToDto(Photo entity);
    /*
     * 그룹 번호, 사용자 번호로 구분하여 사진첩 리스트를 가져오는 함수
     */
    List<PhotoDTO> getPhotoListByGroupNo(Integer groupNo);
}
