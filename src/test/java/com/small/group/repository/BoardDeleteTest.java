package com.small.group.repository;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import com.small.group.entity.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BoardDeleteTest {

	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	
//	@Test
//	@Commit
	public void boardDeleteTest() {
		// 요구사항 : 게시물을 삭제 했을 때 관련된 모든 데이터(댓글) 삭제
		Board board = boardRepository.findById(1).get(); // 게시물 1번 선택
		boardRepository.delete(board);
	}
	
	//@Test
	//@Commit
	public void userDeleteTest() {
		// 요구사항 : 회원을 삭제 했을 때 관련된 모든 데이터(모임, 게시글, 댓글 등) 삭제
		User user = userRepository.findById(1).get();
		userRepository.delete(user);
		
	}
	
}
