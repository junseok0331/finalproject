package com.small.group.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {

	private Integer scheduleNo;
	
	@NotBlank(message = "제목을 입력해주세요")
	private String scheduleName;
	
	@NotBlank(message = "날짜를 설정해주세요.")
	private String scheduleDate;
	
	@NotBlank(message = "시간을 설정해주세요.")
	private String scheduleTime;
	
	@NotBlank(message = "모임이 시작되는 위치를 입력해주세요")
	private String scheduleLocation;
	
	private Integer GroupNo;
	
	private LocalDateTime regDate;
	private LocalDateTime modDate;
}
