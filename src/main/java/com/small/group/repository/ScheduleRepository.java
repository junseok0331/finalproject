package com.small.group.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.small.group.entity.Group;
import com.small.group.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer>{

	@Query("select s from tbl_schedule s where s.group = :group")
	List<Schedule> getScheduleListByGroup(@Param("group") Group group);
}
