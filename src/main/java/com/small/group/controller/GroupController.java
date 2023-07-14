package com.small.group.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
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

import com.small.group.dto.BoardDTO;
import com.small.group.dto.ChatDTO;
import com.small.group.dto.CommentDTO;
import com.small.group.dto.GroupCategoryDTO;
import com.small.group.dto.GroupDTO;
import com.small.group.dto.GroupMemberDTO;
import com.small.group.dto.PageRequestDTO;
import com.small.group.dto.PageResultDTO;
import com.small.group.dto.RegionDTO;
import com.small.group.dto.ScheduleDTO;
import com.small.group.dto.UserDTO;
import com.small.group.entity.Group;
import com.small.group.entity.User;
import com.small.group.repository.GroupMemberRepository;
import com.small.group.repository.GroupRepository;
import com.small.group.service.BoardCategoryService;
import com.small.group.service.BoardService;
import com.small.group.service.ChatService;
import com.small.group.service.CommentService;
import com.small.group.service.GroupCategoryService;
import com.small.group.service.GroupMemberService;
import com.small.group.service.GroupService;
import com.small.group.service.RegionService;
import com.small.group.service.ScheduleService;
import com.small.group.service.UserService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/group")
@Controller
@RequiredArgsConstructor
@Slf4j
public class GroupController {
	
	private final GroupService groupService;
	private final GroupCategoryService groupCategoryService;
	private final GroupMemberService groupMemberService;
	private final ChatService chatService;
	private final RegionService regionService;
	private final UserService userService;
	private final GroupRepository groupRepository;
	private final BoardCategoryService boardCategoryService;
	private final CommentService commentService;
	private final BoardService boardService;
	private final ScheduleService scheduleService;
	private final GroupMemberRepository groupMemberRepository;
	
	@GetMapping("/groupList")
	public String getGroupList(PageRequestDTO pageRequestDTO,
	                           @RequestParam(required = false) Integer groupCategoryNo,
	                           @RequestParam(required = false) String searchKeyword,
	                           Model model, HttpSession session) {
	    System.out.println("!@@@@@@@ groupCategoryNo : " + groupCategoryNo);
	    
	    
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	    	return "redirect:/user/login";
	    }
	    
	    if (searchKeyword != null) {
	    	List<GroupDTO> groupList = groupService.getGroupList(searchKeyword);
	    	groupList.sort(Comparator.comparing(GroupDTO::getGroupNo).reversed());
			Map<Integer, Integer> memberCountMap = new HashMap<>();
			for (GroupDTO groupDTO : groupList) {
				Integer countMember = groupMemberRepository.countGroupMember(groupDTO.getGroupNo());
				memberCountMap.put(groupDTO.getGroupNo(), countMember);
			}
			
			model.addAttribute("memberCountMap", memberCountMap);
	    	
	    	model.addAttribute("groupList", groupList);
	    	model.addAttribute("searchKeyword", searchKeyword);
	    	return "group/groupSearch";
	    }
	    
	    GroupCategoryDTO groupCategoryDTO = null;
	    
	    if (groupCategoryNo != null) {
	    	pageRequestDTO.setGroupCategoryNo(groupCategoryNo);
	    	groupCategoryDTO = groupCategoryService.readGroupCategory(groupCategoryNo);
	    } else {
	        // groupCategoryNo가 없을 경우 전체 그룹을 가져오도록 설정
	        pageRequestDTO.setGroupCategoryNo(null);
	    }

	    PageResultDTO<GroupDTO, Group> result = groupService.getgroupList(pageRequestDTO);
	    
	    List<GroupDTO> filteredGroups;
	    
		if (groupCategoryNo != null) {
			filteredGroups = result.getDtoList().stream()
				.filter(group -> group.getGroupCategoryNo() == groupCategoryNo)
				.collect(Collectors.toList());
		} else {
			filteredGroups = result.getDtoList();
		}
		
		filteredGroups.sort(Comparator.comparing(GroupDTO::getGroupNo).reversed());
		
		log.info("groupCategory dto.toString() : " + groupCategoryDTO);
		
		Map<Integer, Integer> memberCountMap = new HashMap<>();
		for (GroupDTO groupDTO : filteredGroups) {
			Integer countMember = groupMemberRepository.countGroupMember(groupDTO.getGroupNo());
			memberCountMap.put(groupDTO.getGroupNo(), countMember);
		}
		
		model.addAttribute("memberCountMap", memberCountMap);
	    model.addAttribute("result", result);
	    model.addAttribute("filteredGroups", filteredGroups);
	    model.addAttribute("groupCategoryNo", groupCategoryNo);
	    model.addAttribute("groupCategory", groupCategoryDTO);
	    
	    return "group/groupList";
	}
	
//	@GetMapping("/groupList/{groupCategoryNo}")
//	public String getGroupListByGroupCategoryNo(Integer groupCategoryNo, Model model) {
//		List<GroupDTO> groupList = groupService.groupListByGroupCategoryNo(groupCategoryNo);
//    	
//        model.addAttribute("group", groupList);
//		return "group/groupList";
//	}

    // 그룹 등록폼(Get요청)
    @GetMapping("/groupInsert")
    public String insertGroup(Model model, 
    		@ModelAttribute("groupDTO") GroupDTO groupDTO,
    		@ModelAttribute("groupCategoryDTO") GroupCategoryDTO groupCategoryDTO,
    		@ModelAttribute("regionDTO") RegionDTO regionDTO,
    		BindingResult bindingResult,
            PageRequestDTO pageRequestDTO,
            HttpSession session
            ) throws Exception {
    	log.info("groupInsertForm");
    	
    	User user = (User) session.getAttribute("user");
	    if (user == null) {
	    	return "redirect:/user/login";
	    }
    	
    	List<GroupCategoryDTO> groupCategoryList = groupCategoryService.getGroupCategoryList();
    	model.addAttribute("groupCategoryList", groupCategoryList);
    	List<RegionDTO> regionList = regionService.getRegionList();
    	model.addAttribute("regionList", regionList);
    	
    	return "group/groupInsert";
    }

    /*
     * 그룹(모임) 등록처리(Post 요청)
     *  - GroupDTO 파라미터 
     *    : 등록 정보를 모두 담아주는 커맨트 객체
     *    : 등록하다 오류나면 입력정보를 그대로 갖고 다시 등록폼으로 가서
     *      입력했던 정보 그대로 세팅할 때 커맨드 객체가 필요함. 
     * @Valid : 뒤에 오는 커맨드 객체에 설정된 조건에 합당한지 검증
     *  - 검증후 문제가 있으면 다시 입력폼으로 이동     
     * BindingResult 
	 *  - BindingResult 객체는 검증 결과에 대한 결과 정보들을 담고 있습니다
	 *  - 검증 객체 바로 뒤어 오도록 한다.
	 * RedirectAttributes
	 *  - VO 입력값 전송
	 *  - 오류값 객체 전송  
     */
    @PostMapping("/groupInsert")
    public String insertGroup(@ModelAttribute("groupDTO") @Valid GroupDTO group, 
							BindingResult bindingResult, 
							Model model,
							HttpSession session) {
    	
    	System.out.println("test : " + group.toString());
    	log.info("register process group.toString : "+ group.toString());
    	
    	User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		}
        // 검증시 오류 있으면
        if (bindingResult.hasErrors()) {
        	
            // Log field errors
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError error : fieldErrors) {
                log.error( error.getField() + " "+ error.getDefaultMessage());
            }
            List<GroupCategoryDTO> groupCategoryList = groupCategoryService.getGroupCategoryList();
        	model.addAttribute("groupCategoryList", groupCategoryList);
        	
        	List<RegionDTO> regionList = regionService.getRegionList();
        	model.addAttribute("regionList", regionList);
            
        	model.addAttribute("group", group);
            log.info("groupInsert");
            
            return "group/groupInsert";
        }
        
        // 검증 오류 없음
        Group groupEntity = groupService.insertGroup(group);
        Integer groupNo = groupEntity.getGroupNo();
		
		groupMemberService.insertGroupMember(groupEntity, user);
        
        return "redirect:/group/groupMain?groupNo=" + groupNo;
    }

	@GetMapping("/groupMain")
	public String readGroup(@RequestParam Integer groupNo, Model model, @ModelAttribute("groupDTO") GroupDTO groupDTO,
			HttpSession session) {

		// 세션에서 사용자 정보 가져오기
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		}

		// 세션에 사용자 정보(userNo) 저장할 변수
		Integer userNo = 0;
		// 사용자가 가입가능한 DB인지 확인하는 변수
		Integer isMemberResult = 0;

		// 사용자의 정보가 null이 아니라면 가입 가능한지 쿼리로 확인
		if (user != null) {

			userNo = user.getUserNo(); // userNo를 user.getUserNo()로 설정

			isMemberResult = groupMemberService.isMemberOfGroup(userNo, groupNo);

			// 가입된 모임이 없다면 0
			if (isMemberResult == 0) {
				userNo = user.getUserNo();
			} else {
				// 가입된 모임이 있는 경우 userNo를 0으로 설정
				userNo = 0;

			}
		}

		System.out.println("유저번호 : " + userNo);
		System.out.println("그룹번호 : " + groupNo);
		System.out.println("가입된 그룹인지 : " + isMemberResult);

		GroupDTO dto = groupService.readGroup(groupNo);
		
		// groupMain에 회원리스트 출력 (23.07.10 안지우)
		List<GroupMemberDTO> groupMemberDTO = groupMemberService.getGroupMemberList();
		List<UserDTO> userDTO = userService.getUserList();
		
		List<String> userIds = new ArrayList<>(); // userId 값을 담을 배열
		
		for (GroupMemberDTO memberDTO : groupMemberDTO) {
		    if (memberDTO.getGroupNo() == groupNo) {
		        for (UserDTO userdto : userDTO) {
		            if (userdto.getUserNo() == memberDTO.getUserNo()) {
		                userIds.add(userdto.getUserId()); // userId 값을 배열에 추가
		            }
		        }
		    }
		}

		model.addAttribute("userIds", userIds); // userIds 배열을 model에 추가

		// 로그인된 사용자와 그룹장이 같으면 수정버튼 유무
		if (user.getUserNo() == dto.getUserNo()) {
			// 수정하기 버튼 활성화
			model.addAttribute("modifyButtonActive", true);
		} else {
			// 수정하기 버튼 비활성화
			model.addAttribute("modifyButtonActive", false);
		}

		if (isMemberResult > 0) {
			model.addAttribute("deleteJoin", true);
		} else {
			model.addAttribute("deleteJoin", false);
		}

		boolean isUserBelongsToGroup = false;
		if (user != null) {
			Integer result = groupMemberService.isMemberOfGroup(user.getUserNo(), groupNo); // 회원이 그룹에 속해있는지 여부
			if (result > 0) {
				isUserBelongsToGroup = true;
			} // 그룹에 속해 있음을 true로 변경
		}
		
		// 현재 접속한 회원이 해당 모임의 생성 회원인지 결과 값을 반환
		boolean isMemberGroupLeader = false;
		if(dto.getUserNo() == user.getUserNo()) {
			isMemberGroupLeader = true;
		}
		model.addAttribute("isMemberGroupLeader", isMemberGroupLeader);
		
		// 해당 그룹의 배경 이미지 파일이 존재하는지 여부에 따라서 model 객체에 값을 전달
		boolean mainImageExists = mainImageFileExists(session, groupNo);
		model.addAttribute("mainImageExists", mainImageExists);
		
		Integer readerUserNo = groupService.readGroup(groupNo).getUserNo(); 
		model.addAttribute("readerUserNo", readerUserNo);
		model.addAttribute("sessionUserNo", user.getUserNo());
		//--------------------------------------------------------
		
		model.addAttribute("groupNo", groupNo);
		model.addAttribute("userExists", isUserBelongsToGroup);
  
		dto.setGroupDescription(strReplace(dto.getGroupDescription())); // description 공백, 개행 문자를 HTML 태그로 변경
		model.addAttribute("group", dto);
		model.addAttribute("userNo", userNo);

		return "/group/groupMain";
	}
    
	// 회원 그룹 가입하기
	@GetMapping("/join")
	public String joinGroup(@RequestParam("groupNo") Integer groupNo, Model model, HttpSession session) {
		Integer userNo = 0;
		// 세션에서 사용자 정보 가져오기
		User user = (User) session.getAttribute("user");
		if (user != null) {
			userNo = user.getUserNo();
		}

		GroupMemberDTO groupMemberData = new GroupMemberDTO();
		groupMemberData.setGroupNo(groupNo);
		groupMemberData.setUserNo(userNo);
		groupMemberService.insertGroupMember(groupMemberData);
		return "redirect:/group/groupMain?groupNo=" + groupNo;
	}
    
	// 가입한 그룹 탈퇴하기
	@GetMapping("/deleteJoin")
    public String deleteGroupMember(@RequestParam("groupNo") Integer groupNo, 
    								Model model, HttpSession session) {
    	
    	Integer userNo = 0;
    	Integer groupMemberNo = 0;
    	
    	// 세션에서 사용자 정보 가져오기
        User user = (User) session.getAttribute("user");
        if(user != null) {
        	userNo= user.getUserNo();
        }
        
        groupMemberNo = groupMemberService.getGroupNoByMemberRegistrationTable(userNo, groupNo);
        groupMemberService.deleteGroupMember(groupMemberNo);
        
        return "redirect:/group/groupMain?groupNo=" + groupNo;
    }

    /*
     *  그룹 수정 처리[값 검증]
     *   - @ModelAttribute @Valid GroupDTO dto : 화면의 입력데이터 저장(커맨드 객체)
     *    : 화면의 값을 커맨드 객체의 속성으로 바인딩
     *    : 입력값 문제 있을 때 다시 수정폼으로 이동해서 해당값을 그대로 세팅할 때 사용
     *   - @Valid : 커맨드 객체의 값이 설정한 조건에 맞는지 검증
     *   - BindingResult bindingResult : 검증한 결과 저장 객체
     *    : 안에는 GroupDTO 객체를 통해서 오류 값이 보관되어 있어서 다시 돌아간
     *      타임리프 페이지에서 오류 메시지 사용함.
     */

    /*
     *  그룹 수정 처리[값 검증]
     *   - @ModelAttribute @Valid GroupDTO dto : 화면의 입력데이터 저장(커맨드 객체)
     *    : 화면의 값을 커맨드 객체의 속성으로 바인딩
     *    : 입력값 문제 있을 때 다시 수정폼으로 이동해서 해당값을 그대로 세팅할 때 사용
     *   - @Valid : 커맨드 객체의 값이 설정한 조건에 맞는지 검증
     *   - BindingResult bindingResult : 검증한 결과 저장 객체
     *    : 안에는 GroupDTO 객체를 통해서 오류 값이 보관되어 있어서 다시 돌아간
     *      타임리프 페이지에서 오류 메시지 사용함.
     */
    @GetMapping("/groupModify")
    public String updateGroupForm(@RequestParam Integer groupNo,
    		@ModelAttribute("groupDTO") GroupDTO groupDTO,
    		BindingResult bindingResult,
    		Model model,
    		HttpSession session) {
    	log.info("groupModifyForm");
    	
    	User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		}
		
		/*
		 * 23.07.10 이정민
		 * 현재 접속한 회원이 해당 모임의 생성 회원인지 결과 값을 반환
		 * 접속한 회원이 모임장이 아니면 모임메인화면으로 이동
		 */
		boolean isMemberGroupLeader = false;
		if(groupDTO.getUserNo() == user.getUserNo()) {
			isMemberGroupLeader = true;
		}
		if (isMemberGroupLeader = false) {
			return "redirect:/group/groupMain?groupNo=" + groupNo;
		}
		/*************************************************************/
        
		Integer userNo = user.getUserNo();
        model.addAttribute("userNo", userNo);
        
    	GroupDTO dto = groupService.readGroup(groupNo);
    	
    	System.out.println("dto.toString : " + dto.toString());
    	model.addAttribute("group", dto);
    	
    	List<GroupCategoryDTO> groupCategoryList = groupCategoryService.getGroupCategoryList();
    	model.addAttribute("groupCategoryList", groupCategoryList);
    	List<RegionDTO> regionList = regionService.getRegionList();
    	model.addAttribute("regionList", regionList);
    	List<GroupMemberDTO> userList = groupMemberService.findByGroupNo(groupNo);
    	model.addAttribute("userList", userList);
    	
    	return "group/groupModify";
    }
    
	@PostMapping("/groupModify")
	public String updateGroup(@ModelAttribute @Valid GroupDTO dto, 
								BindingResult bindingResult, 
								Model model,
								HttpSession session) {

		log.info("updateGroup - post dto : " + dto.toString());
		
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
			GroupDTO groupDTO = groupService.readGroup(dto.getGroupNo());
			
			model.addAttribute("group", dto);
			
			List<GroupCategoryDTO> groupCategoryList = groupCategoryService.getGroupCategoryList();
	    	model.addAttribute("groupCategoryList", groupCategoryList);
	    	List<RegionDTO> regionList = regionService.getRegionList();
	    	model.addAttribute("regionList", regionList);
	    	List<UserDTO> userList = userService.getUserList();
	    	model.addAttribute("userList", userList);
	    	
	    	return "group/groupModify";
		}

		groupService.updateGroup(dto);
		
		Integer groupNo = dto.getGroupNo();
		
		return "redirect:/group/groupMain?groupNo=" + groupNo;
	}
    
    @GetMapping("/groupDelete/{groupNo}")
    public String deleteGroup(@PathVariable("groupNo") Integer groupNo, HttpSession session) {
    	
    	User user = (User) session.getAttribute("user");
		
    	if (user == null) {
			return "redirect:/user/login";
		}
    	
    	
    	/*
		 * 23.07.10 이정민
		 * 현재 접속한 회원이 해당 모임의 생성 회원인지 결과 값을 반환
		 * 접속한 회원이 모임장이 아니면 모임메인화면으로 이동
		 */
    	GroupDTO groupDTO = groupService.readGroup(groupNo);
		boolean isMemberGroupLeader = false;
		if(groupDTO.getUserNo() == user.getUserNo()) {
			isMemberGroupLeader = true;
		}
		if (!isMemberGroupLeader) {
			return "redirect:/group/groupMain?groupNo=" + groupNo;
		}
		/*************************************************************/
    	
		/**
		 * 그룹 삭제 23.07.12 이정민 
		 * 댓글 - 게시글 - 일정 - 채팅 - 가입회원 - 이미지- 모임 삭제 순서
		 */
		// 1. 삭제하고자 하는 모임에 작성된 모든 게시글 조회 -> 조회한 게시글에 있는 모든 댓글 조회 -> 조회한 모든 댓글 삭제 -> 조회한 모든 게시글 삭제
		List<BoardDTO> boardList = boardService.getBoardListByGroupNo(groupNo);
		for (BoardDTO boards : boardList) {
			Integer boardNo = boards.getBoardNo();
			List<CommentDTO> commentList = commentService.getCommentList(boardNo);
			for (CommentDTO comments : commentList) {
				Integer commentNo = comments.getCommentNo();
				boolean commentDeleted = commentService.deleteComment(commentNo);
			}
			boolean boardDeleted = boardService.deleteBoard(boardNo);
		}
		
		// 2. 삭제하고자 하는 모임에 등록된 모든 스케쥴 조회 -> 조회한 모든 스케쥴 삭제
		List<ScheduleDTO> scheduleList = scheduleService.getScheduleListByGroupNo(groupNo);
		for (ScheduleDTO schedules : scheduleList) {
			Integer scheduleNo = schedules.getScheduleNo();
			boolean scheduleDeleted = scheduleService.deleteSchedule(scheduleNo);
		}
		
		// 3. 삭제하고자 하는 모임에 등록된 모든 채팅 조회 -> 조회한 모든 채팅 삭제
		List<ChatDTO> chatList = chatService.getChatListByGroupNo(groupNo);
		for (ChatDTO chats : chatList) {
			Integer chatNo = chats.getChatNo();
			boolean chatDeleted = chatService.deleteChat(chatNo);
		}
		
		// 4. 삭제하고자 하는 모임에 가입한 모든 회원 조회 -> 조회한 모든 회원 강퇴
		List<GroupMemberDTO> groupMemberList = groupMemberService.findByGroupNo(groupNo);
		for (GroupMemberDTO groupMembers : groupMemberList) {
			Integer groupMemberNo = groupMembers.getGroupMemberNo();
			boolean groupMemberDeleted = groupMemberService.deleteGroupMember(groupMemberNo);
		}
		
		// 5. 삭제하고자 하는 모임에 등록된 이미지 조회 -> 조회한 이미지 삭제
		String fileName = "main_image_group_" + groupNo + ".jpg"; // 이미지 파일명 예시, 확장자에 맞게 변경
        String imagePath = "src/main/resources/static/image/groupMain/" + fileName;
        String deleteFilePath = imagePath.substring(0, imagePath.lastIndexOf(".") + 1);
        String[] allowedExtension = {"jpg", "jpeg", "png", "gif"};
        for (String extension : allowedExtension) {
        	File file = new File(deleteFilePath + extension);
        	if (file.exists()) {
                boolean isImageDeleted = file.delete();
                if (isImageDeleted) {
                	file.delete();
                	log.info("모임 이미지가 삭제되었습니다.");
                } else {
                    log.info("모임 이미지 삭제에 실패했습니다.");
                }
            } else {
                log.info("삭제할 모임 이미지가 존재하지 않습니다.");
            }
		}
		
		// 6. 삭제하고자 하는 모임에 등록된 모든 데이터가 삭제되면 모임 삭제 
    	boolean groupDeleted = groupService.deleteGroup(groupNo);
        
    	return "redirect:/group/groupList";
    }
    
    /*
    @PostMapping("/search")
    public String searchGroup(GroupSearchDTO searchDTO, Model model) {
        // 그룹 검색 기능 구현
        // searchDTO를 사용하여 그룹을 검색하고 model에 결과를 담아서 리턴
        // 예시: List<GroupDTO> searchResult = groupService.searchGroup(searchDTO);
        // model.addAttribute("searchResult", searchResult);
        return "group/search";
    }
    */
    
    /*
     *	[ 채팅 관련 컨트롤러 맵핑 ]
     *	작성자 : 이민혁 
     */
    
    /*
     *	처음 채팅방에 입장 했을 때 보여지는 채팅 목록
     */
    @GetMapping("/chatList")
    public String chatList(Model model, @RequestParam("groupNo") Integer groupNo,
    					@ModelAttribute GroupDTO groupDTO, HttpSession session) {
    	
    	User user = (User) session.getAttribute("user");
		
    	if (user == null) {
			return "redirect:/user/login";
		}
    	
    	List<ChatDTO> chatList = chatService.getChatListByGroupNo(groupNo);
    	chatList = chatList.stream().map(chat -> strReplace(chat)).collect(Collectors.toList());
    	
    	GroupDTO dto = groupService.readGroup(groupNo);

		log.info("test - groupNo, userNo : " + groupNo + " , " + user.getUserNo());
		Integer isMemberIncludedGroup = groupMemberService.isMemberOfGroup(user.getUserNo(), groupNo);	
		if (isMemberIncludedGroup < 1) {
			return "redirect:/group/groupMain?groupNo=" + groupNo;
		}
    	
    	model.addAttribute("chatList", chatList);
    	model.addAttribute("groupNo", groupNo);
    	model.addAttribute("group", dto);
    	return "chat/chatList";
    }
    
 
    
    /*
     *	채팅 입력 후 Ajax 요청으로 데이터를 저장하고 리스트를 모델에 저장하는 함수
     */
    @PostMapping("/chatList")
    @ResponseBody
    public ResponseEntity<List<ChatDTO>> chatList(Model model, @RequestBody FormData formData) {
    	// FormData 타입으로 채팅 입력과 관련된 데이터 전달 받음
    	Integer groupNo = formData.getGroupNo();
    	Integer userNo = formData.getUserNo();
    	String inputText = formData.getInputText();
    	
    	// 채팅 데이터 입력
    	ChatDTO chatDTO = ChatDTO.builder()
    			.chatContent(inputText)
    			.groupNo(groupNo)
    			.userNo(userNo)
    			.build();
    	chatService.insertChat(chatDTO);
    	
    	// 채팅 목록을 조회해서 MODEL 객체에 전달
    	List<ChatDTO> chatList = chatService.getChatListByGroupNo(groupNo);
    	// 문자열의 공백과 개행문자를 HTML 태그의 형태로 변환
    	chatList = chatList.stream().map(chat -> strReplace(chat)).collect(Collectors.toList());
    	
    	model.addAttribute("chatList", chatList);

    	return ResponseEntity.ok(chatList);
    }
    
    /*
     *  문자열의 공백과 개행문자를 HTML 태그의 형태로 변경하는 함수
     *  /chatList 맵핑으로 채팅 목록을 가져올 때 사용
     */
    private ChatDTO strReplace(ChatDTO chat) {
    	String replaceStr = chat.getChatContent().replace(" ", "&nbsp;");
    	replaceStr = replaceStr.replace("\n", "<br/>");
    	chat.setChatContent(replaceStr);
    	return chat;
    }
    
    /*
     * 문자열의 공백과 라인개행 문자를 HTML의 태그로 수정
     */
    private String strReplace(String str) {
       return str.replace(" ", "&nbsp;").replace("\n", "<br/>");
    }
    
    /*
     *	groupMain.html에서 ajax요청으로 해당 모임원의 목록을 조회하여 
     *	결과 값을 전달하는 함수
     */
    @GetMapping("/memberList")
    @ResponseBody
    public List<UserDTO> getMemberList(@RequestParam Integer groupNo, @RequestParam Integer userNo) {
    	List<UserDTO> userList = groupMemberService.getGroupMemberListByGroupNoAndReaderFirst(groupNo, userNo);
    	return userList;
    }
    
    // 메인 이미지가 존재하는지 확인하고 결과 값을 반환하는 함수
    private boolean mainImageFileExists(HttpSession session, Integer groupNo) {
    	String[] allowedExtensions = {"jpg", "jpeg", "png", "gif"};
    	String fileName = "main_image_group_" + groupNo + ".";
		String path = session.getServletContext().getRealPath("/");
		
		Integer indexOfMain = path.indexOf("\\src\\main") + "\\src\\main".length();
		String realPath = path.substring(0, indexOfMain).replace("\\", "/") + "/resources/static/image/groupMain/";
		String fileAbPath = realPath + fileName;
		for(String extension : allowedExtensions) {
			File f = new File(fileAbPath + extension);
			if(f.exists()) {
				if(f.isFile()) {
					return true;
				}
			}
		}
		return false;
    	
    }
    
}

/**
 * 
 * @author 505
 * 채팅 관련 폼 데이터를 받아줄 클래스 선언
 */
@Data
class FormData {
	
	private Integer groupNo;
	private Integer userNo;
	private String inputText;

}

