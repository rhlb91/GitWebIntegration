package com.teammerge.cronjob;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.teammerge.GitWebException.InvalidArgumentsException;
import com.teammerge.model.CustomRefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.ScheduleJobModel;
import com.teammerge.services.ScheduleService;
import com.teammerge.utils.CommitCache;
import com.teammerge.utils.TimeUtils;
public class DataInsertionJob extends AbstractCustomJob implements Job {

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  @Value("${git.commit.timeFormat}")
  private String commitTimeFormat;

  @Value("${app.dateFormat}")
  private String commitDateFormat;

  public DataInsertionJob() {
    super();
  }
  
  @Resource(name = "scheduleService")
  private ScheduleService scheduleService;
  

  public void execute(JobExecutionContext context) throws JobExecutionException {
	  
    LOG.info("JobGetCommitDetails start: " + context.getFireTime());
  
    fetchAndSaveBranchAndCommitDetails();
    
    //To Store Schedule Job Details
    scheduleService = ApplicationContextUtils.getBean(ScheduleService.class);
    ScheduleJobModel job = new ScheduleJobModel();
	job.setJobId("JobGetCommitDetails");
	job.setPreviousFireTime(context.getFireTime());
	job.setNextFireTime(context.getNextFireTime());
	job.setJobStatus("Completed");
	job.setjobscheduleInterval("0 0/5 * * * ?");
	scheduleService.saveSchedule(job);
    
    LOG.info("JobGetCommitDetails next scheduled time:" + context.getNextFireTime());
  }

  public synchronized void fetchAndSaveBranchAndCommitDetails() {
    Date minimumDate = TimeUtils.getInceptionDate();
    List<CustomRefModel> branchModels = repositoryService.getCustomRefModels(true);
    
    scheduleService = ApplicationContextUtils.getBean(ScheduleService.class);
	ScheduleJobModel scheduleJobModel= scheduleService.getSchedule("JobGetCommitDetails");
	Date previousfiretime = scheduleJobModel.getPreviousFireTime();
    
    if (CollectionUtils.isNotEmpty(branchModels)) {
      for (CustomRefModel branch : branchModels) {
        List<RepositoryCommit> commits =
            CommitCache.instance().getCommits(branch.getRepositoryName(), branch.getRepository(),
                branch.getRefModel().getName(), minimumDate);

        try {
          saveBranch(branch, commits);
        } catch (InvalidArgumentsException e) {
          LOG.error("Cannot create new branch model from cronjob !!" + getClass().getSimpleName(),
              e);
        }

        if (CollectionUtils.isNotEmpty(commits)) {
          for (RepositoryCommit commit : commits) {
            try {
            	 Date commitdate = commit.getCommitDate();
            	 if (commitdate.compareTo(previousfiretime) > 0) {
            	    saveCommit(commit, branch);
                }
              } catch (InvalidArgumentsException e) {
              LOG.error("Cannot create new commit model from cronjob!!"
                  + getClass().getSimpleName(), e);
            }
          }
        }
      }
    }
  }
}
