package com.small.group.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.small.group.dto.ScheduleDTO;
import com.small.group.entity.Schedule;
import com.small.group.entity.Group;
import com.small.group.repository.GroupRepository;
import com.small.group.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
	
	
	private final ScheduleRepository scheduleRepository;
	private final GroupRepository groupRepository;
	
	/**
	 *  DTO TO ENTITY
	 */
	public Schedule dtoToEntity(ScheduleDTO dto) {
		Optional<Group> optGroup = groupRepository.findById(dto.getGroupNo());
		Group group = (optGroup.isPresent()) ? optGroup.get() : null;
		
		Schedule entity = Schedule.builder()
				.scheduleName(dto.getScheduleName())
				.scheduleDate(dto.getScheduleDate())
				.scheduleTime(dto.getScheduleTime())
				.scheduleLocation(dto.getScheduleLocation())
				.group(group)
				.build();
		
		return entity;
	}
	/**
	 *  ENTITY TO DTO
	 */
	public ScheduleDTO entityToDto(Schedule entity) {
		ScheduleDTO dto = ScheduleDTO.builder()
				.scheduleNo(entity.getScheduleNo())
				.scheduleName(entity.getScheduleName())
				.scheduleDate(entity.getScheduleDate())
				.scheduleTime(entity.getScheduleTime())
				.scheduleLocation(entity.getScheduleLocation())
				.GroupNo(entity.getGroup().getGroupNo())
				.build();
		
		return dto;
	}
	
	
	
	/**
	 *	일정 저장하는 함수
	 */
	@Override
	public Schedule insertSchedule(ScheduleDTO scheduleData) {
		Schedule schedule = dtoToEntity(scheduleData);
		return scheduleRepository.save(schedule);
	}

	/**
	 *	일정 한 개 가져오는 함수
	 */
	@Override
	public ScheduleDTO readSchedule(Integer scheduleNo) {
		Optional<Schedule> optSchedule = scheduleRepository.findById(scheduleNo);
		ScheduleDTO schedultDTO = null;
		if(optSchedule.isPresent()) {
			schedultDTO = entityToDto(optSchedule.get());
		}
		return schedultDTO;
	}

	/**
	 *	일정 수정하는 함수
	 */
	@Override
	public Schedule updateSchedule(ScheduleDTO scheduleData) {
		Optional<Schedule> data = scheduleRepository.findById(scheduleData.getScheduleNo());
		if(data.isPresent()) {
			Schedule targetEntity = data.get();
			targetEntity.setScheduleName(scheduleData.getScheduleName());
			targetEntity.setScheduleDate(scheduleData.getScheduleDate());
			targetEntity.setScheduleTime(scheduleData.getScheduleTime());
			targetEntity.setScheduleLocation(scheduleData.getScheduleLocation());
			
			return scheduleRepository.save(targetEntity);
		}
		return null;
	}

	/**
	 *	일정 삭제하는 함수
	 */
	@Override
	public Boolean deleteSchedule(Integer scheduleNo) {
		Optional<Schedule> data = scheduleRepository.findById(scheduleNo);
		if(data.isPresent()) {
			scheduleRepository.delete(data.get());
			return true;
		}
		return false;
	}

	
	/**
	 * 일정 리스트 조회 함수
	 */
	
	@Override
	public List<ScheduleDTO> getScheduleList(){
		List<Schedule> scheduleList = scheduleRepository.findAll();
		
		List<ScheduleDTO> scheduleDTOList = scheduleList.stream()
				.map(entity -> entityToDto(entity))
				.collect(Collectors.toList());
		
		return scheduleDTOList;
		
	}
	
	/*
	 *	그룹번호에 대한 일정 리스트를 가져오는 함수
	 */
	@Override
	public List<ScheduleDTO> getScheduleListByGroupNo(Integer groupNo) {
		Group group = Group.builder().groupNo(groupNo).build();
		List<Schedule> scheduleList = scheduleRepository.getScheduleListByGroup(group);
		List<ScheduleDTO> scheduleDTOList = scheduleList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		
		return scheduleDTOList;
	}
	
	
}
