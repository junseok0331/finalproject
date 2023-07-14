package com.small.group.service;

import java.util.List;

import com.small.group.dto.ScheduleDTO;
import com.small.group.entity.Schedule;


public interface ScheduleService {

	Schedule insertSchedule(ScheduleDTO scheduleData);
	ScheduleDTO readSchedule(Integer scheduleNo);
	Schedule updateSchedule(ScheduleDTO scheduleData);
    Boolean deleteSchedule(Integer scheduleNo);
    List<ScheduleDTO> getScheduleList();
    List<ScheduleDTO> getScheduleListByGroupNo(Integer groupNo);
    
    
}
