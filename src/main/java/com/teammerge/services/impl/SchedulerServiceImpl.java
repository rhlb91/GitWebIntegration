package com.teammerge.services.impl;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.dao.BaseDao;
import com.teammerge.model.ScheduleJobModel;
import com.teammerge.services.SchedulerService;

@Service("scheduleService")
public class SchedulerServiceImpl implements SchedulerService {

  private static final Logger LOG = LoggerFactory.getLogger(SchedulerServiceImpl.class);

  private final String JOB_NAME = "dataInsertionJob";

  private final String CRONJOB_GROUP = "defaultCronJobGroup";

  private final String CRONJOB_TRIGGER = "dataInsertionJobTrigger";
  private final String DEFAULT_CRON_EXPRESSION = "0 0 0 0 0 0 0";

  private static final String JOB_NUMBER = "jobNumber";

  private Scheduler scheduler;

  @Autowired
  private BaseDao<ScheduleJobModel> baseDao;

  @PostConstruct
  void init() {
    try {
      scheduler = StdSchedulerFactory.getDefaultScheduler();
    } catch (Exception e) {
      LOG.error("Error initializing cron job scheduler!!", e);
    }
  }

  @PreDestroy
  @Override
  public void destroyScheduler() {
    try {
      scheduler.shutdown();
      LOG.info("Destroyed scheduler with job name " + JOB_NAME);
    } catch (Exception e) {
      LOG.error("Error shutting down cron job scheduler!!", e);
    }
  }

  @Override
  public void register(Class<? extends Job> jobClass, String cronExpression) {
    try {
      if (cronExpression == null) {
        ScheduleJobModel scheduleJobModel = getSchedule(JOB_NAME);
        cronExpression = scheduleJobModel.getjobscheduleInterval();
      }

      if (cronExpression == null) {
        LOG.error("No cron expression found for " + JOB_NAME
            + " in DB. Insert a valid cron expression in DB to run this cronjob!! ");
        cronExpression = DEFAULT_CRON_EXPRESSION;
      }

      CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
      JobKey jobKey = new JobKey(JOB_NAME, CRONJOB_GROUP);
      JobDetail jobDetail =
          JobBuilder.newJob(jobClass).withIdentity(jobKey).usingJobData(JOB_NUMBER, 1).build();

      CronTrigger cronTrigger =
          TriggerBuilder.newTrigger().withIdentity(CRONJOB_TRIGGER, CRONJOB_GROUP)
              .withSchedule(cronScheduleBuilder).build();

      scheduler.scheduleJob(jobDetail, cronTrigger);
    } catch (Exception e) {
      LOG.error("Error registring cron job scheduler!!", e);
    }

  }

  @Override
  public void reschedule(Class<? extends Job> jobClass, String cronExpression) {
    try {
      String name = jobClass.getSimpleName();
      if (!name.equals(JOB_NAME)) {
        LOG.warn("Not rescheduling job. Invalid Job class specified!!");
        return;
      }

      CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

      CronTrigger newCronTrigger =
          TriggerBuilder.newTrigger().withIdentity(CRONJOB_TRIGGER, CRONJOB_GROUP)
              .withSchedule(cronScheduleBuilder).build();

      scheduler
          .rescheduleJob(TriggerKey.triggerKey(CRONJOB_TRIGGER, CRONJOB_GROUP), newCronTrigger);

      // updating the scheduler in DB
      updateScheduleInDB(cronExpression);

    } catch (Exception e) {
      LOG.error("Error rescheduling cron job scheduler!!", e);
    }
  }

  @Override
  public void startSchedule(Class<? extends Job> jobClass) {
    try {
      String name = jobClass.getSimpleName();
      if (!name.equals(JOB_NAME)) {
        LOG.warn("Cannot start scheduler job. Invalid Job class specified!!");
        return;
      }

      if (scheduler != null) {
        scheduler.start();
      }
    } catch (SchedulerException e) {
      LOG.error("Error starting cron job scheduler!!", e);
    }
  }

  /**
   * updating the cron expression in DB so that the next time when server starts, updated value will
   * be get
   * 
   * @param cronExpression
   */
  private void updateScheduleInDB(String cronExpression) {
    ScheduleJobModel scheduleJobModel = getSchedule(JOB_NAME);
    if (scheduleJobModel != null) {
      scheduleJobModel.setjobscheduleInterval(cronExpression);
      saveSchedule(scheduleJobModel);
    }
  }

  @Override
  public ScheduleJobModel getSchedule(String jobId) {
    ScheduleJobModel scheduleJobModel = baseDao.fetchEntity(jobId);
    return scheduleJobModel;
  }

  @Autowired
  public void setBaseDao(BaseDao<ScheduleJobModel> baseDao) {
    baseDao.setClazz(ScheduleJobModel.class);
    this.baseDao = baseDao;
  }

  public int saveSchedule(ScheduleJobModel scheduleJobModel) {
    baseDao.saveEntity(scheduleJobModel);
    return 0;
  }

}
