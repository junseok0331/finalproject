package com.small.group.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.small.group.dto.ChatDTO;
import com.small.group.entity.Chat;
import com.small.group.entity.Group;
import com.small.group.entity.User;
import com.small.group.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

	private final ChatRepository chatRepository;
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	
	/**
	 *  DTO TO ENTITY
	 */
	public Chat dtoToEntity(ChatDTO dto) {
		Optional<Group> optGroup = groupRepository.findById(dto.getGroupNo());
		Optional<User> optUser = userRepository.findById(dto.getUserNo());
		
		Group group = (optGroup.isPresent()) ? optGroup.get() : null;
		User user = (optUser.isPresent()) ? optUser.get() : null;
		
		
		Chat entity = Chat.builder()
				.chatContent(dto.getChatContent())
				.group(group)
				.user(user)
				.build();
		return entity;
	}
	
	/**
	 *  ENTITY TO DTO
	 */
	public ChatDTO entityToDto(Chat entity) {
		Integer groupNo = entity.getGroup().getGroupNo();
		Integer userNo = entity.getUser().getUserNo();
		String userName = entity.getUser().getName();
		
		ChatDTO dto = ChatDTO.builder()
				.chatNo(entity.getChatNo())
				.chatContent(entity.getChatContent())
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
	 *	채팅 저장하는 함수
	 */
	@Override
	public Chat insertChat(ChatDTO chatData) {
		Chat chat = dtoToEntity(chatData);
		return chatRepository.save(chat);
	}

	/**
	 *	채팅 한 개 가져오는 함수
	 */
	@Override
	public ChatDTO readChat(Integer chatNo) {
		Optional<Chat> chat = chatRepository.findById(chatNo);
		ChatDTO chatDTO = null;
		if(chat.isPresent()) {
			chatDTO = entityToDto(chat.get());
		}
		return chatDTO;
	}

	/**
	 *	채팅 수정하는 함수
	 */
	@Override
	public Chat updateChat(ChatDTO chatData) {
		Optional<Chat> data = chatRepository.findById(chatData.getChatNo());
		if(data.isPresent()) {
			Chat targetEntity = data.get();
			targetEntity.setChatContent(chatData.getChatContent());
			
			return chatRepository.save(targetEntity);
		}
		return null;
	}

	/**
	 *	채팅 삭제하는 함수
	 */
	@Override
	public Boolean deleteChat(Integer chatNo) {
		Optional<Chat> data = chatRepository.findById(chatNo);
		if(data.isPresent()) {
			chatRepository.delete(data.get());
			return true;
		}
		return false;
	}

	/**
	 *	채팅 리스트를 가져오는 함수
	 */
	@Override
	public List<ChatDTO> getChatList() {
		List<Chat> chatList = chatRepository.findAll();
		List<ChatDTO> chatDTOList = chatList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return chatDTOList;
	}

	/**
	 *	그룹 번호로 구분하여 채팅 리스트를 가져오는 함수
	 */
	@Override
	public List<ChatDTO> getChatListByGroupNo(Integer groupNo) {
		Group group = Group.builder().groupNo(groupNo).build(); // 모임 번호만 입력된 모임 객체 생성
		List<Chat> chatList = chatRepository.getChatListByGroup(group); // 모임 번호를 기준으로 쿼리 검색함
		List<ChatDTO> chatDTOList = chatList.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return chatDTOList;
	}
	
	
	
	
}
