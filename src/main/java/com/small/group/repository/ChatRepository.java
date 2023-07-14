package com.small.group.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.small.group.entity.Chat;
import com.small.group.entity.Group;

public interface ChatRepository extends JpaRepository<Chat, Integer>{

	/*
	 * 모임(Group)으로 구분하여 채팅 리스트를 가져오는 쿼리
	 * @param group
	 * @return
	 */
	@Query("select c from Chat c where group = :group")
	public List<Chat> getChatListByGroup(@Param("group") Group group);
}
