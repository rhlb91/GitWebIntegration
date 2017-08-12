package com.teammerge.cronjob;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.teammerge.model.ScheduleJobModel;
import com.teammerge.services.ScheduleService;

public class QuartzListener implements ServletContextListener {
  Scheduler scheduler = null;

  private ScheduleService scheduleService;

  @Override
  public void contextInitialized(ServletContextEvent servletContext) {
    System.out.println("Context Initialized");

    try {
      scheduleService = ApplicationContextUtils.getBean(ScheduleService.class);
      ScheduleJobModel scheduleJobModel = scheduleService.getSchedule("JobGetCommitDetails");
      String scheduleInterval = scheduleJobModel.getjobscheduleInterval();

      // Setup the Job class and the Job group
      JobKey jobKey = new JobKey("dataInsertionJob", "defaultcronJobGroup");
      JobDetail job = JobBuilder.newJob(DataInsertionJob.class).withIdentity(jobKey).build();

      Trigger trigger =
          TriggerBuilder.newTrigger()
              .withIdentity("dataInsertionJobTrigger", "defaultcronJobGroup").startNow()
              .withSchedule(CronScheduleBuilder.cronSchedule(scheduleInterval)).build();

      // Setup the Job and Trigger with Scheduler & schedule jobs
      scheduler = new StdSchedulerFactory().getScheduler();
      scheduler.start();
      scheduler.scheduleJob(job, trigger);

    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContext) {
    System.out.println("Context Destroyed");
    try {
      scheduler.shutdown();
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

}
