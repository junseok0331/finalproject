package com.small.group.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.small.group.dto.BoardDTO;
import com.small.group.dto.PageRequestDTO;
import com.small.group.dto.PageResultDTO;
import com.small.group.entity.Board;
import com.small.group.entity.BoardCategory;
import com.small.group.entity.Group;
import com.small.group.entity.User;
import com.small.group.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
	
	private final BoardRepository boardRepository;
	private final BoardCategoryRepository boardCategoryRepository;
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	
	/**
	 *  DTO TO ENTITY
	 */
	private Board dtoToEntity(BoardDTO dto) {
		Optional<BoardCategory> optBoardCategory = boardCategoryRepository.findById(dto.getBoardCategoryNo());
		Optional<Group> optGroup = groupRepository.findById(dto.getGroupNo());
		Optional<User> optUser = userRepository.findById(dto.getUserNo());
		
		BoardCategory boardCategory = (optBoardCategory.isPresent()) ? optBoardCategory.get() : null;
		Group group = (optGroup.isPresent()) ? optGroup.get() : null;
		User user = (optUser.isPresent()) ? optUser.get() : null;
		
		Board entity = Board.builder()
				.boardTitle(dto.getBoardTitle())
				.boardContent(dto.getBoardContent())
				.boardHit(dto.getBoardHit())
				.boardCategory(boardCategory)
				.group(group)
				.user(user)
				.build();
		
		return entity;
	}
	
	/**
	 *  ENTITY TO DTO
	 */
	private BoardDTO entityToDto(Board entity) {
		Integer boardCategoryNo = entity.getBoardCategory().getBoardCategoryNo();
		String boardCategoryName = entity.getBoardCategory().getBoardCategoryName();
		Integer groupNo = entity.getGroup().getGroupNo();
		Integer userNo = entity.getUser().getUserNo();
		String userName = entity.getUser().getName();
		
		BoardDTO dto = BoardDTO.builder()
				.boardNo(entity.getBoardNo())
				.boardTitle(entity.getBoardTitle())
				.boardContent(entity.getBoardContent())
				.boardHit(entity.getBoardHit())
				.boardCategoryNo(entity.getBoardCategory().getBoardCategoryNo())
				.boardCategoryName(entity.getBoardCategory().getBoardCategoryName())
				.groupNo(entity.getGroup().getGroupNo())
				.userNo(entity.getUser().getUserNo())
				.userName(entity.getUser().getName())
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
	 *	게시글 저장하는 함수
	 */
	@Override
	public Board insertBoard(BoardDTO boardData) {
		Board board = dtoToEntity(boardData);
		return boardRepository.save(board);
	}

	/**
	 *	게시글 한 개 가져오는 함수
	 */
	@Override
	public BoardDTO readBoard(Integer boardNo) {
		Optional<Board> board = boardRepository.findById(boardNo);
		BoardDTO boardDTO = null;
		if(board.isPresent()) {
			boardDTO = entityToDto(board.get());
		}
		return boardDTO;
	}

	/**
	 *	게시글 수정하는 함수
	 */
	@Override
	public Board updateBoard(BoardDTO boardData) {
		Optional<Board> data = boardRepository.findById(boardData.getBoardNo());
		if(data.isPresent()) {
			Board targetEntity = data.get();
			targetEntity.setBoardTitle(boardData.getBoardTitle());
			targetEntity.setBoardContent(boardData.getBoardContent());
			
			BoardCategory boardCategory = boardCategoryRepository.findById(boardData.getBoardCategoryNo())
					.orElseThrow(() -> new RuntimeException("카테고리 정보가 존재하지 않습니다."));
			
			targetEntity.setBoardCategory(boardCategory);
			
			return boardRepository.save(targetEntity);
		}
		return null;
	}

	/**
	 *	게시글 삭제하는 함수
	 */
	@Override
	public Boolean deleteBoard(Integer boardNo) {
		Optional<Board> data = boardRepository.findById(boardNo);
		if(data.isPresent()) {
			boardRepository.delete(data.get());
			return true;
		}
		return false;
	}

	/**
	 *	게시글 목록조회 페이징
	 */
	@Override
	public PageResultDTO<BoardDTO, Board> getBoardList(PageRequestDTO requestDTO){
		Pageable pageable = requestDTO.getPageable(Sort.by("boardNo").descending());
		Page<Board> result = boardRepository.findAll(pageable);
		Function<Board, BoardDTO> fn = (entity -> entityToDto(entity));
		return new PageResultDTO<>(result, fn);
	}
	
	/**
	 * 게시글 리스트 조회 함수
	 */
	
	@Override
	public List<BoardDTO> getBoardList(){
		List<Board> boardList = boardRepository.findAll();
		
		List<BoardDTO> boardDTOList = boardList.stream()
				.map(entity -> entityToDto(entity))
				.collect(Collectors.toList());
		
		return boardDTOList;
		
	}

	@Override
	public List<BoardDTO> getBoardListByGroupNo(Integer groupNo) {
		Group group = Group.builder().groupNo(groupNo).build();
		List<Board> boardList = boardRepository.findByGroup(group);
		List<BoardDTO> boardDTOList = boardList.stream()
				.map(entity -> entityToDto(entity))
				.collect(Collectors.toList());
		return boardDTOList;
	}
	
	/*
	 * 게시물 조회수 - 유성태
	 */
	@Override
	@Transactional
	public void updateBoardHit(Integer boardNo, LocalDateTime recentModDate) {
	    boardRepository.countHit(boardNo, recentModDate);
	 }

	@Override
	public Integer isWriterOfBoard(Integer userNo, Integer boardNo) {
		// TODO Auto-generated method stub
		return null;
	}
		
}
