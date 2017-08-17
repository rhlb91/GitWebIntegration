package com.teammerge.services;


import org.quartz.Job;

import com.teammerge.model.ScheduleJobModel;

public interface SchedulerService {

  ScheduleJobModel getSchedule(String jobId);

  int saveSchedule(ScheduleJobModel scheduleJobModel);

  void register(Class<? extends Job> jobClass, String cronExpression);

  void reschedule(Class<? extends Job> jobClass, String cronExpression);
}
