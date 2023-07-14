package com.small.group.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.Commit;

import com.small.group.dto.UserDTO;
import com.small.group.entity.User;

@SpringBootTest
@Transactional
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceTest {

	@Autowired
	private UserService userService;

	
//	@Test
//	@Commit
	public void test() {
		UserDTO user = UserDTO.builder()
				.userId("user01")
				.password("123456")
				.name("홍길동")
				.phone("010-1234-5678")
				.build();
		User result = userService.insertUser(user);
		assertNotNull(result);
	}
	
	//@Test
	public void loginTest() {
		UserDTO user = UserDTO.builder()
				.userId("서해번쩍")
				.password("1234")
				.build();
		
		if(userService.login(user) != null){
			System.out.println("회원이 존재합니다.");
		} else {
			System.out.println("가입되지 않은 아이디입니다.");
		}
	}
}
