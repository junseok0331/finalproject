package com.small.group.service;

import java.time.LocalDateTime;
import java.util.List;

import com.small.group.dto.BoardDTO;
import com.small.group.dto.PageRequestDTO;
import com.small.group.dto.PageResultDTO;
import com.small.group.entity.Board;


public interface BoardService {

	List<BoardDTO> getBoardList();
	Board insertBoard(BoardDTO boardData);
	BoardDTO readBoard(Integer boardNo);
	Board updateBoard(BoardDTO boardData);
    Boolean deleteBoard(Integer boardNo);
    
    // 게시판 페이지
    public PageResultDTO<BoardDTO, Board> getBoardList(PageRequestDTO requestDTO);
	List<BoardDTO> getBoardListByGroupNo(Integer groupNo);
	void updateBoardHit(Integer boardNo, LocalDateTime recentModDate);
	Integer isWriterOfBoard(Integer userNo, Integer boardNo);
}
