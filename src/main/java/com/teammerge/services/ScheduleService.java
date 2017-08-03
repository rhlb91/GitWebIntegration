package com.teammerge.services;


import com.teammerge.model.ScheduleJobModel;

public interface ScheduleService {

	ScheduleJobModel getSchedule(String jobId);

	int saveSchedule(ScheduleJobModel scheduleJobModel);
}