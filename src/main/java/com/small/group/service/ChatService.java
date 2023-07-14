package com.small.group.service;

import java.util.List;

import com.small.group.dto.ChatDTO;
import com.small.group.entity.Chat;


public interface ChatService {

	Chat insertChat(ChatDTO chatData);
	ChatDTO readChat(Integer chatNo);
	Chat updateChat(ChatDTO chatData);
    Boolean deleteChat(Integer chatNo);
    List<ChatDTO> getChatList();
    
    Chat dtoToEntity(ChatDTO dto);
    ChatDTO entityToDto(Chat entity);
    /*
     * 그룹 번호로 구분하여 채팅 리스트를 가져오는 함수
     */
    List<ChatDTO> getChatListByGroupNo(Integer groupNo);
}
