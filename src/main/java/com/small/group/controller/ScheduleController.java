package com.small.group.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.small.group.dto.ScheduleDTO;
import com.small.group.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/schedule")
@Controller
@RequiredArgsConstructor
public class ScheduleController {
	
	private final ScheduleService scheduleService;
	
	/*
	 *	그룹 번호에 해당하는 일정 리스트를 가져오는 함수
	 */
	@GetMapping("/scheduleList")
	@ResponseBody
	public List<ScheduleDTO> getScheduleList(Model model, @RequestParam("groupNo") Integer groupNo) {
		List<ScheduleDTO> scheduleDTOList = null;
		scheduleDTOList = scheduleService.getScheduleListByGroupNo(groupNo);
		
		return scheduleDTOList;
	}
	
	/*
	 *	일정 등록하기
	 */
	@PostMapping("/insertSchedule")
	public String insertSchedule(@RequestParam("groupNo") Integer groupNo,
								 @ModelAttribute ScheduleDTO scheduleDTO) {
		scheduleService.insertSchedule(scheduleDTO);
		return "redirect:/group/groupMain?groupNo=" + groupNo;
	}
	
	/*
	 *	일정 삭제하기
	 */
	@GetMapping("/deleteSchedule")
	public String deleteSchedule(@RequestParam("groupNo") Integer groupNo, 
							     @RequestParam("scheduleNo") Integer scheduleNo) {
		scheduleService.deleteSchedule(scheduleNo);
		return "redirect:/group/groupMain?groupNo=" + groupNo;
	}

	
}
