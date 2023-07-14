package com.small.group.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.Commit;

import com.small.group.dto.ChatDTO;
import com.small.group.entity.Board;
import com.small.group.entity.BoardCategory;
import com.small.group.entity.Group;
import com.small.group.entity.User;
import com.small.group.repository.BoardCategoryRepository;
import com.small.group.repository.BoardRepository;
import com.small.group.repository.GroupRepository;
import com.small.group.repository.UserRepository;


@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ServiceTest {

	@Autowired
	private BoardCategoryService boardCategoryService; // 게시글 카테고리
	@Autowired
	private BoardCategoryRepository boardCategoryRepository; // 게시글 카테고리 DAO
	@Autowired
	
	private GroupCategoryService groupCategoryService; // 모임 카테고리
	
	@Autowired
	private BoardService boardService; // 게시글
	@Autowired
	private BoardRepository boardRepository; // 게시글 DAO
	
	@Autowired
	private GroupService groupService; // 모임
	@Autowired
	private GroupRepository groupRepository; // 모임DAO
	
	@Autowired
	private UserService userService; // 회원
	@Autowired
	private UserRepository userRepository; // 회원 DAO
	
	@Autowired
	private RegionService regionService; // 지역
	
	@Autowired
	private ChatService chatService;
	
	//@Test
	public void test() {
		List<ChatDTO> chatList = chatService.getChatListByGroupNo(1);
		for(ChatDTO dto : chatList) {
			System.out.println("채팅내용: " + dto.getChatContent());
		}
	}
}
