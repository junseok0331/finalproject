package com.small.group.service;

import java.util.List;

import javax.validation.Valid;

import com.small.group.dto.UserDTO;
import com.small.group.entity.User;

public interface UserService {

	User insertUser(UserDTO userDTO);
	User login(UserDTO userDTO);
	/**
	 *	회원 한 명 가져오는 함수
	 */
	UserDTO readUser(Integer userNo);
	/**
	 *	회원 수정하는 함수
	 *	수정 가능한 속성(이름, 비밀번호, 휴대폰번호)
	 */
	User updateUser(UserDTO userData);
	/**
	 *	회원 삭제하는 함수
	 */
	Boolean deleteUser(Integer userNo);
	/**
	 *	회원 리스트를 가져오는 함수
	 */
	List<UserDTO> getUserList();
	
	User register(UserDTO userDTO);
	boolean idExist(String userId);
	//boolean loginCheck(UserDTO userDTO);
	List<UserDTO> getUserByNo(Integer userNo);
}
