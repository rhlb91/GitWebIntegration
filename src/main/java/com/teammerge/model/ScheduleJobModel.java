package com.teammerge.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "schedule_job_details")
public class ScheduleJobModel implements Serializable {

  private static final long serialVersionUID = 1100490355473736524L;

  @Id
  @Column(name = "job_id")
  private String jobId;

  @Column(name = "job_status")
  private String jobstatus;

  @Column(name = "job_scheduleInterval")
  private String jobscheduleInterval;

  @Column(name = "PreviousFireTime")
  private Date PreviousFireTime;

  @Column(name = "NextFireTime")
  private Date NextFireTime;

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("job_id:- " + getJobId());
    str.append("job_id:- " + getJobId());
    str.append(" previous_fire_time:- " + getPreviousFireTime());
    str.append(" next_fire_time:- " + getNextFireTime());
    return str.toString();
  }

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  public String getjobscheduleInterval() {
    return jobscheduleInterval;
  }

  public void setjobscheduleInterval(String jobscheduleInterval) {
    this.jobscheduleInterval = jobscheduleInterval;
  }

  public String getJobStatus() {
    return jobstatus;
  }

  public void setJobStatus(String jobstatus) {
    this.jobstatus = jobstatus;
  }

  public Date getPreviousFireTime() {
    return PreviousFireTime;
  }

  public void setPreviousFireTime(Date PreviousFireTime) {
    this.PreviousFireTime = PreviousFireTime;
  }

  public Date getNextFireTime() {
    return NextFireTime;
  }

  public void setNextFireTime(Date NextFireTime) {
    this.NextFireTime = NextFireTime;
  }
}
