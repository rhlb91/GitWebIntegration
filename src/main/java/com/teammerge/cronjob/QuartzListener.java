package com.teammerge.cronjob;

import java.io.*;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QuartzListener implements ServletContextListener {
	Scheduler scheduler = null;

	@Value("${git.commit.timeFormat}")
	private String commitTimeFormat;

	@Value("${JobScheduleTime}")
	private String JobScheduleTime;

	@Override
	public void contextInitialized(ServletContextEvent servletContext) {
		System.out.println("Context Initialized");

		
		Properties prop = new Properties();
    	InputStream input = null;

    	try {

    		input = new FileInputStream("/home/reflex/Documents/config.properties");
    		prop.load(input);

    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} finally {
    		if (input != null) {
    			try {
    				input.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
                        
    	String JobScheduleTime = prop.getProperty("JobScheduleTime");
	
		try {

			// Setup the Job class and the Job group
			JobDetail job = newJob(JobGetCommitDetails.class).withIdentity("Job", "Group").build();

			// Create a Trigger that fires every 5 minutes.
			Trigger trigger = newTrigger().withIdentity("Job", "Group").startNow()
					.withSchedule(CronScheduleBuilder.cronSchedule(JobScheduleTime)).build();

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
