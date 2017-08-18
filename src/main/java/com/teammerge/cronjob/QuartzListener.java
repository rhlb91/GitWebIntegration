package com.teammerge.cronjob;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teammerge.services.SchedulerService;

public class QuartzListener implements ServletContextListener {
  private final static Logger LOG = LoggerFactory.getLogger(QuartzListener.class);

  private SchedulerService schedulerService;

  @Override
  public void contextInitialized(ServletContextEvent servletContext) {
    System.out.println("Context Initialized");

    schedulerService = ApplicationContextUtils.getBean(SchedulerService.class);
    schedulerService.register(DataInsertionJob.class, null);
    schedulerService.startSchedule(DataInsertionJob.class);
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContext) {
    schedulerService.destroyScheduler();
  }

}
