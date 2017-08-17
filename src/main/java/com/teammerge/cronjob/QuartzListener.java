package com.teammerge.cronjob;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teammerge.services.SchedulerService;

public class QuartzListener implements ServletContextListener {
  private final static Logger LOG = LoggerFactory.getLogger(QuartzListener.class);

  private SchedulerService scheduleService;

  @Override
  public void contextInitialized(ServletContextEvent servletContext) {
    System.out.println("Context Initialized");

    scheduleService = ApplicationContextUtils.getBean(SchedulerService.class);
    scheduleService.register(DataInsertionJob.class, null);
    scheduleService.startSchedule(DataInsertionJob.class);
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContext) {
    scheduleService.destroyScheduler();
  }

}
