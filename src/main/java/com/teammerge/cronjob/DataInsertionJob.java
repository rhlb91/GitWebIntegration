package com.teammerge.cronjob;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import com.teammerge.utils.CommitCache;
import com.teammerge.utils.CommitLastChangeCache;

public class DataInsertionJob extends AbstractCustomJob implements Job {

  private static final String SCHEDULE_JOB_ID = "JobGetCommitDetails";

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  @Value("${git.commit.timeFormat}")
  private String commitTimeFormat;

  @Value("${app.dateFormat}")
  private String commitDateFormat;

  public DataInsertionJob() {
    super();
  }

  public void execute(JobExecutionContext context) throws JobExecutionException {

    LOG.debug("\nExecuting the DataInsertion Job - " + context.getFireTime());

    fetchAndSaveBranchAndCommitDetails();

    /**
     * To auto update NextFireTime based on ScheduleInterval Time stored in Database.
     **/
    ScheduleJobModel job = scheduleService.getSchedule(SCHEDULE_JOB_ID);
    job.setPreviousFireTime(context.getFireTime());
    job.setNextFireTime(context.getNextFireTime());
    scheduleService.saveSchedule(job);

    LOG.debug("DataInsertion Completed!! Next scheduled time:" + context.getNextFireTime()+"\n");
  }

  public synchronized void fetchAndSaveBranchAndCommitDetails() {

    List<CustomRefModel> customRefModels = repositoryService.getCustomRefModels(true);

    if (CollectionUtils.isNotEmpty(customRefModels)) {
      for (CustomRefModel customRef : customRefModels) {

        Date sinceDate =
            CommitLastChangeCache.instance().getLastChangeDate(customRef.getRepositoryName());
          
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sinceDate);
        calendar.add(Calendar.SECOND, 1);
        sinceDate = calendar.getTime();
        
        List<RepositoryCommit> commits =
            CommitCache.instance().getCommits(customRef.getRepositoryName(),
                customRef.getRepository(), customRef.getRefModel().getName(), sinceDate);

        try {
          saveOrUpdateBranch(customRef, commits);
        } catch (InvalidArgumentsException e) {
          LOG.error("Cannot create new branch model from cronjob !!" + getClass().getSimpleName(),
              e);
        }

        if (CollectionUtils.isNotEmpty(commits)) {
          Date mostRecentCommitDate = new Date(0);
          for (RepositoryCommit commit : commits) {
            try {
              Date commitdate = commit.getCommitDate();

              if (commitdate.after(mostRecentCommitDate)) {
                mostRecentCommitDate = commitdate;
              }
              saveCommit(commit, customRef);
            } catch (InvalidArgumentsException e) {
              LOG.error("Cannot create new commit model from cronjob!!"
                  + getClass().getSimpleName(), e);
            }
          }

          CommitLastChangeCache.instance().updateLastChangeDate(customRef.getRepositoryName(),
              mostRecentCommitDate);
        }
      }
    }
  }
}
