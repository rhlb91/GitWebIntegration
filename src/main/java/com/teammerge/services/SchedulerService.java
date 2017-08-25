package com.teammerge.services;


import org.quartz.Job;

import com.teammerge.model.ScheduleJobModel;

public interface SchedulerService {

  ScheduleJobModel getSchedule(String jobId);

  int saveSchedule(ScheduleJobModel scheduleJobModel);

  /**
   * registers the cron job instance
   * <p>
   * if the cronExpression parameter is provided null, the application tries to find the value from
   * DB.
   * </p>
   * <p>
   * If the cronExpression value is not provided and not available in DB, the default cron
   * expression will set to <code> 0 0 0 0 0 0 0 </code>
   * </p>
   * 
   * @param jobClass
   * @param cronExpression
   */
  void register(Class<? extends Job> jobClass, String cronExpression);

  void startSchedule(Class<? extends Job> jobClass);

  void reschedule(Class<? extends Job> jobClass, String cronExpression);

  void destroyScheduler();

  void pauseJob(String jobName);

  void resumeJob(String jobName, String jobGroup);
}
