package com.small.group.service;

import java.util.List;

import com.small.group.dto.PhotoCommentDTO;
import com.small.group.entity.PhotoComment;


public interface PhotoCommentService {

	PhotoComment insertPhotoComment(PhotoCommentDTO photoCommentData);
	PhotoCommentDTO readPhotoComment(Integer photoCommentNo);
	PhotoComment updatePhotoComment(PhotoCommentDTO photoCommentData);
    Boolean deletePhotoComment(Integer photoCommentNo);
    List<PhotoCommentDTO> getPhotoCommentList();
	List<PhotoCommentDTO> getPhotoCommentList(Integer groupNo);
}
