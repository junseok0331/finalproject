package com.small.group.controller;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.small.group.dto.BoardCategoryDTO;
import com.small.group.dto.BoardDTO;
import com.small.group.dto.CommentDTO;
import com.small.group.dto.GroupDTO;
import com.small.group.dto.GroupMemberDTO;
import com.small.group.dto.UserDTO;
import com.small.group.entity.Board;
import com.small.group.entity.Comment;
import com.small.group.entity.User;
import com.small.group.service.BoardCategoryService;
import com.small.group.service.BoardService;
import com.small.group.service.CommentService;
import com.small.group.service.GroupMemberService;
import com.small.group.service.GroupService;
import com.small.group.service.UserService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/board")
@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

	private final BoardService boardService;
	private final BoardCategoryService boardCategoryService;
	private final GroupService groupService;
	private final UserService userService;
	private final GroupMemberService groupMemberService;
	private final CommentService commentService;
	
	// 게시글 조회
	@GetMapping("/boardList")
	public String getBoardList(Model model,
							@RequestParam("groupNo") Integer groupNo,
							@ModelAttribute("boardDTO") BoardDTO boardDTO,
							HttpSession session) {
		User user = (User) session.getAttribute("user");
	    if (user == null) {
	    	return "redirect:/user/login";
	    }
		List<BoardDTO> boardList = boardService.getBoardListByGroupNo(groupNo);
		boardList.sort(Comparator.comparing(BoardDTO::getBoardNo).reversed());
		
		GroupDTO groupDto = groupService.readGroup(groupNo);
		
//		// 내가 가입한 모임만 게시글 작성 가능
//		// 가입한 그룹번호, 해당 그룹번호 일치하면 true 반환
//		// boardList에서 true확인되면 게시글생성 버튼 활성화
//		List<GroupMemberDTO> groupMemberList = groupMemberService.getGroupMemberListByUser(user);
//		for (GroupMemberDTO groupMemberDTO : groupMemberList) {
//			if (groupDto.getGroupNo() == groupMemberDTO.getGroupNo()) {
//				model.addAttribute("groupMemberList", true);
//			}
//		}
		
		// 로그인 된 회원 정보가 모임의 회원 정보가 같을 경우에 '게시글 생성' 버튼의 유무를 전달함.
		Integer isMemberIncludedGroup = groupMemberService.isMemberOfGroup(user.getUserNo(), groupNo);
	    if (isMemberIncludedGroup > 0) {
	        model.addAttribute("boardCreateButton", true);
	    } else {
	    	model.addAttribute("boardCreateButton", false);
	    }
		
		model.addAttribute("group", groupDto);
		model.addAttribute("boardList", boardList);
		model.addAttribute("groupNo", groupNo);
		
		return "board/boardList";
	}
	
	// 게시판 한개 조회
	@GetMapping("/boardRead")
	public String readBoard(@RequestParam Integer boardNo, 
							Model model, 
							HttpSession session) {
	       
		User user = (User) session.getAttribute("user");
	    if (user == null) {
	    	return "redirect:/user/login";
	    }
	    
	    BoardDTO dto = boardService.readBoard(boardNo);
	    Integer groupNo = dto.getGroupNo();
	    
	    Integer isMemberIncludedGroup = groupMemberService.isMemberOfGroup(user.getUserNo(), groupNo);	
		if (isMemberIncludedGroup < 1) {
			return "redirect:/group/groupMain?groupNo=" + groupNo;
		}
	    
	    dto = boardService.readBoard(boardNo);
	    
	    LocalDateTime recentModDate = dto.getModDate();
	      
	    // 게시물 조회수 증가
	    boardService.updateBoardHit(boardNo, recentModDate);
	    
	    dto = boardService.readBoard(boardNo);
	    
	    log.info("boardRead - get groupNo : " + groupNo);
	    
	    GroupDTO groupDTO = groupService.readGroup(groupNo);
	    
	    // 댓글
	    List<CommentDTO> commentList = commentService.getCommentList(boardNo);
	    commentList.sort(Comparator.comparing(CommentDTO::getCommentNo).reversed());
	    
	    /*
		 * 현재 접속한 회원이 해당 게시글의 작성자인지 결과 값을 반환
		 * 작성자이면 게시글 수정, 삭제 버튼이 보이도록 설정
		 */
	    boolean isBoardWriter = false;
		if (dto.getUserNo() == user.getUserNo()) {
			isBoardWriter = true;
		} 
		model.addAttribute("showButton", isBoardWriter);
	    
	    model.addAttribute("commentList", commentList);
	    model.addAttribute("board", dto);
	    model.addAttribute("group", groupDTO);
	    model.addAttribute("groupNo", groupNo);
	    
	    return "board/boardRead";
	}
	
	// 게시물 등록 폼
	@GetMapping("/boardRegister")
	public String registerBoard(@ModelAttribute("boardDTO") BoardDTO boardDTO,
						@ModelAttribute("boardCategoryDTO") BoardCategoryDTO boardCategoryDTO,
						@ModelAttribute("groupDTO") GroupDTO groupDTO,
						@ModelAttribute("userDTO") UserDTO userDTO,
						BindingResult bindingResult,
						HttpSession session,
						Model model) {
		
		log.info("@@@@@@@@@@@boardRegister Form");
		
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		}
		
		/*
		 * 23.07.10 이정민
		 * 현재 접속한 회원이 해당 모임의 생성 회원인지 결과 값 반환
		 * 접속한 회원이 모임원이 아니면 모임 메인화면으로 이동
		 */
		Integer groupNo = groupDTO.getGroupNo();
		log.info("test - groupNo, userNo : " + groupNo + " , " + user.getUserNo());
		Integer isMemberIncludedGroup = groupMemberService.isMemberOfGroup(user.getUserNo(), groupNo);	
		if (isMemberIncludedGroup < 1) {
			return "redirect:/group/groupMain?groupNo=" + groupNo;
		}

		GroupDTO dto = groupService.readGroup(groupDTO.getGroupNo());
		
		model.addAttribute("board", new Board());
		
		List<BoardCategoryDTO> boardCategoryList = boardCategoryService.getBoardCategoryList();
		model.addAttribute("boardCategoryList", boardCategoryList);
		model.addAttribute("groupNo", groupNo);
		model.addAttribute("group", dto);
		
		return "board/boardRegister";
	}
	
	// 게시판 등록
	@PostMapping("/boardRegister")
	public String registerBoard(@ModelAttribute("boardDTO") @Valid BoardDTO boardDTO,
							BindingResult bindingResult,
							Model model,
							HttpSession session) {
		System.out.println("test: " + boardDTO.toString());
		log.info("register process group.toString: " + boardDTO.toString());
		
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		}
		
		// 검증시 오류 있으면
		if(bindingResult.hasErrors()) {

			// Log field errors
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();
			
			for (FieldError error : fieldErrors) {
				log.error(error.getField() + " " + error.getDefaultMessage());
			}
			
			List<BoardCategoryDTO> boardCategoryList = boardCategoryService.getBoardCategoryList();
			model.addAttribute("boardCategoryList", boardCategoryList);
			
			model.addAttribute("board", boardDTO);
			log.info("boardRegister");
			
			return "board/boardRegister";
		}
		Integer groupNo = boardDTO.getGroupNo();
		
		// 검증 오류 없으면
		boardService.insertBoard(boardDTO);
		return "redirect:/board/boardList?groupNo=" + groupNo;
	}
	
	// 게시판 수정 폼
	@GetMapping("/boardModify")
	public String updateBoardForm(@RequestParam Integer boardNo,
								@ModelAttribute("boardDTO") BoardDTO boardDTO,
								BindingResult bindingResult,
								Model model,
								HttpSession session) {
		log.info("boardModify Form");
		
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		}
		
		BoardDTO dto = boardService.readBoard(boardNo);
		
		List<BoardCategoryDTO> boardCategoryList = boardCategoryService.getBoardCategoryList();
		
		/*
		 * 현재 접속한 회원이 해당 게시글의 작성자인지 결과 값을 반환
		 * 접속한 회원이 작성자가 아니면 게시판 페이지로 이동
		 */
		Integer groupNo = dto.getGroupNo();
		boolean isWriter = false;
		if (dto.getUserNo() == user.getUserNo()) {
			isWriter = true;
		}
		if(!isWriter) {
			return "redirect:/board/boardList?groupNo=" + groupNo;
		}
		
		model.addAttribute("board", dto);
		model.addAttribute("boardCategoryList", boardCategoryList);
		
		return "board/boardModify";
	}
	
	@PostMapping("/boardModify")
	public String updateBoard(@ModelAttribute @Valid BoardDTO dto,
							BindingResult bindingResult,
							Model model,
							HttpSession session) {
		log.info("boardModify - post dto: " + dto.toString());
		System.out.println("boardTitle: " + dto.getBoardTitle());
		System.out.println("boardContent: " + dto.getBoardContent());
		
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		}
		// 검증시 오류 있으면
		if (bindingResult.hasErrors()) {
			// Log field errors
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();
			for (FieldError error : fieldErrors) {
				log.error(error.getField() + " " + error.getDefaultMessage());
			}
			dto = boardService.readBoard(dto.getBoardNo());

			model.addAttribute("board", dto);

			List<BoardCategoryDTO> boardCategoryList = boardCategoryService.getBoardCategoryList();
			model.addAttribute("boardCategoryList", boardCategoryList);

			return "board/boardModify";
		}
		boardService.updateBoard(dto);
		return "redirect:/board/boardRead?boardNo=" + dto.getBoardNo();
	}
	
	// 게시글 삭제 GetMapping 수정 07-09 유성태
	@GetMapping("/boardDelete/{boardNo}")
	public String deleteBoard(@PathVariable Integer boardNo, @RequestParam Integer groupNo, HttpSession session, Model model) {
		
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		}
		
		/*
		 * 현재 접속한 회원이 해당 게시글의 작성자인지 결과 값을 반환
		 * 접속한 회원이 작성자가 아니면 게시판 페이지로 이동
		 */
		BoardDTO dto = boardService.readBoard(boardNo);
		boolean isWriter = false;
		if (dto.getUserNo() == user.getUserNo()) {
			isWriter = true;
		}
		if(!isWriter) {
			return "redirect:/board/boardList?groupNo=" + groupNo;
		}
		
		// 게시글에 있는 댓글 삭제
	    List<CommentDTO> commentList = commentService.getCommentList(boardNo);
	    for (CommentDTO comment : commentList) {
	        commentService.deleteComment(comment.getCommentNo());
	    }
		
		boolean deleted = boardService.deleteBoard(boardNo);
		return "redirect:/board/boardList?groupNo=" + groupNo; // 삭제항목으로 수정 07-09 유성태
	}

	// 게시글 삭제 수정
	@PostMapping("/boardDelete/{boardNo}")
	public String deleteBoard(@PathVariable("boardNo") Integer boardNo, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		}
		BoardDTO boardDTO = boardService.readBoard(boardNo);
		Integer groupNo = boardDTO.getGroupNo();
		boolean deleted = boardService.deleteBoard(boardNo);

		return "redirect:/board/boardList?groupNo=" + groupNo;
	}
	
	@PostMapping("/commentDelete")
	@ResponseBody
	public String deleteComment(Model model,
								@RequestBody Integer commentNo,
								HttpSession session) {
		
		CommentDTO commentDTO = CommentDTO.builder()
								.commentNo(commentNo)
								.build();
		
		boolean deleted = commentService.deleteComment(commentNo);
		
		String result = "";
		
		if (deleted) {
			result = "success";
		} else {
			result = "fail";
		}
		
		return result;
	}
	
	/*
	 * 댓글 저장  
	 */
	@PostMapping("/commentInsert")
	@ResponseBody
	public String insertComment (
								@RequestBody CommentData formData,
								HttpSession session,
								Model model) {
		
		Integer boardNo = formData.getBoardNo();
    	Integer userNo = formData.getUserNo();
    	String commentContent = formData.getCommentContent(); 
		
    	
    	CommentDTO commentDTO = CommentDTO.builder()
    							.commentContent(commentContent)
    							.boardNo(boardNo)
    							.userNo(userNo)
    							.build();
		
		Comment comment = commentService.insertComment(commentDTO);
		
		String result = "";
		
		if(comment != null) {
			result = "success";
		} else {
			result = "fail";
		}
		
		return result;
	}
}

@Data
class CommentData {
	
	private Integer boardNo;
	private Integer userNo;
	private String commentContent;

}