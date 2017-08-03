package com.teammerge.cronjob;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

import com.teammerge.model.ScheduleJobModel;
import com.teammerge.services.ScheduleService;

@Component
public class QuartzListener implements ServletContextListener {
  Scheduler scheduler = null;

  @Resource(name = "scheduleService")
  private ScheduleService scheduleService;
    
  @Override
  public void contextInitialized(ServletContextEvent servletContext) {
    System.out.println("Context Initialized");
    
    try {
    	scheduleService = ApplicationContextUtils.getBean(ScheduleService.class);
    	ScheduleJobModel scheduleJobModel= scheduleService.getSchedule("JobGetCommitDetails");
        String scheduleInterval = scheduleJobModel.getjobscheduleInterval();
    	    	
      // Setup the Job class and the Job group
      JobDetail job = newJob(DataInsertionJob.class).withIdentity("Job", "Group").build();

      // Create a Trigger that fires every 5 minutes.
      Trigger trigger =
          newTrigger().withIdentity("Job", "Group").startNow()
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
