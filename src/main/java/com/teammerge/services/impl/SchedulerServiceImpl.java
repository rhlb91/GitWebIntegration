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

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  private Scheduler scheduler;

  @Autowired
  private BaseDao<ScheduleJobModel> baseDao;


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

  @PostConstruct
  void init() {
    try {
      scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.start();
    } catch (Exception e) {
      // handle exception
    }
  }

  @PreDestroy
  void destroy() {
    try {
      scheduler.shutdown();
    } catch (Exception e) {
      // handle exception
    }
  }

  @Override
  public void register(Class<? extends Job> jobClass, String cronExpression) {
    // TODO Auto-generated method stub
    try {
     // String name = jobClass.getSimpleName();
      CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule( cronExpression );
      JobKey jobKey = new JobKey("dataInsertionJob", "defaultcronJobGroup");
      JobDetail jobDetail = JobBuilder.newJob( jobClass ).withIdentity( jobKey ).build();
      CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("dataInsertionJobTrigger", "defaultcronJobGroup" ).withSchedule( cronScheduleBuilder ).build();

      scheduler.scheduleJob( jobDetail, cronTrigger );
  } catch ( Exception e ) {
      // handle exception
  }

  }

  @Override
  public void reschedule(Class<? extends Job> jobClass, String cronExpression) {
    // TODO Auto-generated method stub
    try {
      String name = jobClass.getSimpleName();
      CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule( cronExpression );

      CronTrigger newCronTrigger = TriggerBuilder.newTrigger().withIdentity( name ).withSchedule( cronScheduleBuilder ).build();

      scheduler.rescheduleJob( TriggerKey.triggerKey( name ), newCronTrigger );
  } catch ( Exception e ) {
      // handle exception
  }
  }
}
