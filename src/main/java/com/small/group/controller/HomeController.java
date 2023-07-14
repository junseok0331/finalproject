package com.small.group.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aspectj.weaver.patterns.HasMemberTypePatternForPerThisMatching;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.small.group.dto.GroupCategoryDTO;
import com.small.group.dto.GroupDTO;
import com.small.group.dto.PageRequestDTO;
import com.small.group.dto.PageResultDTO;
import com.small.group.dto.RegionDTO;
import com.small.group.dto.UserDTO;
import com.small.group.entity.Group;
import com.small.group.entity.User;
import com.small.group.service.ChatService;
import com.small.group.service.GroupCategoryService;
import com.small.group.service.GroupMemberService;
import com.small.group.service.GroupService;
import com.small.group.service.RegionService;
import com.small.group.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
	
	private final GroupService groupService;
	private final GroupCategoryService groupCategoryService;
	private final GroupMemberService groupMemberService;
	private final ChatService chatService;
	private final RegionService regionService;
	private final UserService userService;

	@GetMapping("/")
	public String home(Model model,
				@ModelAttribute("groupCategoryDTO") GroupCategoryDTO groupCategoryDTO,
				@ModelAttribute("regionDTO") RegionDTO regionDTO,
				PageRequestDTO pageRequestDTO,
				HttpSession session) {
		// 세션에서 사용자 정보 가져오기
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	    	
	    }
	    
		List<GroupCategoryDTO> groupCategoryList = groupCategoryService.getGroupCategoryList();
    	model.addAttribute("groupCategoryList", groupCategoryList);
    	List<RegionDTO> regionList = regionService.getRegionList();
    	model.addAttribute("regionList", regionList);
    	
    	// 그룹 리스트 조회 기능 구현
	    // groupService를 사용하여 그룹 리스트를 조회하고 model에 결과를 담아서 리턴
	    PageResultDTO<GroupDTO, Group> result = groupService.getgroupList(pageRequestDTO);

	    // 모델에 데이터를 추가하여 View로 전달
	    model.addAttribute("result", result);
		
    	return "home";
	}
}
