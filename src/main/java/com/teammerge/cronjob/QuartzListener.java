package com.teammerge.cronjob;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;




@Component
public class QuartzListener implements ServletContextListener {
        Scheduler scheduler = null;

        @Override
        public void contextInitialized(ServletContextEvent servletContext) {
                System.out.println("Context Initialized");
                
                try {
                
                     // Setup the Job class and the Job group
                        JobDetail job = newJob(JobGetCommitDetails.class).withIdentity("Job", "Group").build();

                        // Create a Trigger that fires every 2 minutes.
                        Trigger trigger = newTrigger()
                        .withIdentity("Job", "Group")
                         .startNow()
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * * * ?"))
                        .build();

                        // Setup the Job and Trigger with Scheduler & schedule jobs
                        scheduler = new StdSchedulerFactory().getScheduler();
                        scheduler.start();
                        scheduler.scheduleJob(job, trigger);
                        
                       
       
               }
                catch (SchedulerException e) {
                        e.printStackTrace();
                }
        }

        @Override
        public void contextDestroyed(ServletContextEvent servletContext) {
                System.out.println("Context Destroyed");
                try 
                {
                        scheduler.shutdown();
                } 
                catch (SchedulerException e) 
                {
                        e.printStackTrace();
                }
        }
               
        public  void refresh() {
                System.out.println("Enter in refresh job");
                try 
                {
                        scheduler.shutdown();
                        
                     // Setup the Job class and the Job group
                        JobDetail job = newJob(JobGetCommitDetails.class).withIdentity("Job", "Group").build();

                        // Create a Trigger that fires every 2 minutes.
                        Trigger trigger = newTrigger()
                        .withIdentity("Job", "Group")
                         .startNow()
                        .withSchedule(CronScheduleBuilder.cronSchedule("0/120 * * * * ?"))
                        .build();

                        // Setup the Job and Trigger with Scheduler & schedule jobs
                        scheduler = new StdSchedulerFactory().getScheduler();
                        scheduler.start();
                        scheduler.scheduleJob(job, trigger);     
                        
                } 
                catch (SchedulerException e) 
                {
                        e.printStackTrace();
                }
        }
        
        
}
