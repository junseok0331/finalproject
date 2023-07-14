package com.small.group.repository;

import java.util.Optional;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.Commit;

import com.jayway.jsonpath.Option;
import com.small.group.entity.Board;
import com.small.group.entity.BoardCategory;
import com.small.group.entity.Chat;
import com.small.group.entity.Comment;
import com.small.group.entity.Group;
import com.small.group.entity.GroupCategory;
import com.small.group.entity.GroupMember;
import com.small.group.entity.Region;
import com.small.group.entity.User;
import com.small.group.repository.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DummyDataFiles {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RegionRepository regionRepository;
	
	@Autowired
	private BoardCategoryRepository boardCategoryRepository;
	
	@Autowired
	private GroupCategoryRepository groupCategoryRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private ChatRepository chatRepository;
	
	@Autowired
	private GroupMemberRepository groupMemberRepository;
	
	
	/**
	 *	회원 데이터 입력
	 */
	@Test
	@Commit
	@Order(1)
	public void userDataInsert() {
		IntStream.rangeClosed(1, 9).forEach(i -> {
			User user = User.builder()
					.name("홍길동" + i)
					.userId("user0" + i)
					.password("123456")
					.phone("010-1111-2222")
					.build();
			userRepository.save(user);
		});
	}
	
	
	/**
	 *	지역 데이터 입력
	 */
	@Test
	@Commit
	@Order(2)
	public void regionDataInsert() {
		String regionList[] = {"수원시", "성남시","고양시","용인시","부천시","안산시","안양시","남양주시","화성시","평택시","의정부시","시흥시","파주시","김포시","광명시","군포시","이천시","양주시",
							   "오산시","구리시","안성시","포천시","의왕시","여주시","동두천시","과천시","광주시","여주시","파주시"};
		for(String regionStr : regionList) {
			Region region = Region.builder()
					.regionName(regionStr)
					.build();
			regionRepository.save(region);
		}
	}
	
	/**
	 *	게시글 카테고리 데이터 입력
	 */
	@Test
	@Commit
	@Order(3)
	public void boardCategoryDataInsert() {
		String boardCategoryList[] = {"공지사항", "가입인사", "자유글", "정모후기", "관심사공유"};
		for(String categoryName : boardCategoryList) {
			BoardCategory boardCategory = BoardCategory.builder()
					.boardCategoryName(categoryName)
					.build();
			boardCategoryRepository.save(boardCategory);
		}
	}
	
	/**
	 *	모임 카테고리 데이터 입력
	 */
	@Test
	@Commit
	@Order(4)
	public void groupCategoryDataInsert() {
		String groupCategoryList[] = {"문화/예술", "운동", "맛집", "여행", "자기계발", "친목", "소개팅", "자유주제"};
		for(String categoryName : groupCategoryList) {
			GroupCategory groupCategory = GroupCategory.builder()
					.groupCategoryName(categoryName)
					.build();
			
			groupCategoryRepository.save(groupCategory);
		}
		
	}
	
	/**
	 *	모임 데이터 입력
	 */
	@Test
	@Commit
	@Order(5)
	public void groupDataInsert() {
		Optional<GroupCategory> optGroupCategory = groupCategoryRepository.findById(1);
		Optional<Region> optRegion = regionRepository.findById(1);
		Optional<User> optUser = userRepository.findById(1);
		GroupCategory groupCategory = (optGroupCategory.isPresent()) ? optGroupCategory.get() : null;
		Region region = (optRegion.isPresent()) ? optRegion.get() : null;
		User user = (optUser.isPresent()) ? optUser.get() : null;
		
		System.out.println("테스트:::::");
		System.out.println(groupCategory.getGroupCategoryName());
		System.out.println(region.getRegionName());
		System.out.println(user.getName());
		
		IntStream.rangeClosed(1, 10).forEach(i -> {
			Group group = Group.builder()
					.groupName("모임" + i)
					.groupDescription("모임설명" + i)
					.groupCategory(groupCategory)
					.region(region)
					.user(user).
					build();
			
			groupRepository.save(group);
		});
	}
	
	
	/**
	 *	게시글 데이터 입력
	 */
	@Test
	@Commit
	@Order(6)
	public void boardDataInsert() {
		Optional<BoardCategory> optBoardCategory = boardCategoryRepository.findById(3); // 자유글
		Optional<Group> optGroup = groupRepository.findById(1);
		Optional<User> optUser = userRepository.findById(1);
		BoardCategory boardCategory = (optBoardCategory.isPresent()) ? optBoardCategory.get() : null;
		Group group = (optGroup.isPresent()) ? optGroup.get() : null;
		User user = (optUser.isPresent()) ? optUser.get() : null;
		
		IntStream.rangeClosed(1, 30).forEach(i -> {
			Board board = Board.builder()
					.boardTitle("게시판제목" + i)
					.boardContent("게시판 내용" + i)
					.boardCategory(boardCategory)
					.group(group)
					.user(user)
					.build();
			
			boardRepository.save(board);
		});
	}
	
	/**
	 *	댓글 데이터 입력
	 */
	@Test
	@Commit
	@Order(7)
	public void commentDataInsert() {
		IntStream.rangeClosed(1, 10).forEach(i -> {
			Optional<Board> optBoard = boardRepository.findById(1);
			Optional<User> optUser = userRepository.findById(1);
			Board board = (optBoard.isPresent()) ? optBoard.get() : null;
			User user = (optUser.isPresent()) ? optUser.get() : null;
			
			Comment comment = Comment.builder()
					.commentContent("댓글" + i)
					.board(board)
					.user(user)
					.build();
			
			commentRepository.save(comment);
		});
	}
	
	/**
	 *	채팅 데이터 입력
	 */
	@Test
	@Commit
	@Order(8)
	public void chatDataInsert() {
		Optional<Group> optGroup = groupRepository.findById(1);
		Optional<User> optUser = userRepository.findById(1);
		Group group = (optGroup.isPresent()) ? optGroup.get() : null;
		User user = (optUser.isPresent()) ? optUser.get() : null;
		
		IntStream.rangeClosed(1, 10).forEach(i -> {
			Chat chat = Chat.builder()
					.chatContent("채팅" + i)
					.group(group)
					.user(user)
					.build();
			
			chatRepository.save(chat);
		});
	}
	
	/**
	 *	모임 멤버 데이터 입력
	 */
	@Test
	@Commit
	@Order(9)
	public void groupMemberDataInsert() {
		Optional<User> optUser = userRepository.findById(1);
		User user = optUser.get();
		
		for(int i=1; i<=10; i++) {
			Optional<Group> optGroup = groupRepository.findById(i);
			Group group = optGroup.get();
			GroupMember groupMember = GroupMember.builder()
					.group(group)
					.user(user)
					.build();
			
			groupMemberRepository.save(groupMember);
		}
	}
	

	
}
