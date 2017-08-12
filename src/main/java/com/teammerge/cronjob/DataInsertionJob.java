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
import com.teammerge.cache.CommitCache;
import com.teammerge.cache.CommitLastChangeCache;
import com.teammerge.cache.CronJobStatusCache;
import com.teammerge.model.CustomRefModel;
import com.teammerge.model.RepositoryCommit;

public class DataInsertionJob extends AbstractCustomJob implements Job {

  private static final Logger LOG = LoggerFactory.getLogger(DataInsertionJob.class);
  private static final String SCHEDULE_JOB_ID = "JobGetCommitDetails";

  @Value("${git.commit.timeFormat}")
  private String commitTimeFormat;

  @Value("${app.dateFormat}")
  private String commitDateFormat;

  public DataInsertionJob() {
    super();
  }

  public void execute(JobExecutionContext context) throws JobExecutionException {
    boolean isError = false;
    JobStatus currentJobStatus = CronJobStatusCache.instance().getJobStatus(SCHEDULE_JOB_ID);

    if (JobStatusEnum.IN_PROGRESS.equals(currentJobStatus.currentStatus)) {
      LOG.info("Could not run a new job. A job already running!!");
      return;
    }

    LOG.debug("\nExecuting the DataInsertion Job - " + context.getFireTime());
    currentJobStatus.currentStatus = JobStatusEnum.IN_PROGRESS;
    CronJobStatusCache.instance().updateJobStatus(SCHEDULE_JOB_ID, currentJobStatus);

    try {
      fetchAndSaveBranchAndCommitDetails();

    } catch (Exception e) {
      currentJobStatus.currentStatus = JobStatusEnum.ERROR;
      isError = true;
      LOG.error("Caught error in" + getClass().getSimpleName(), e);
    }

    LOG.debug("DataInsertion Completed!! Next scheduled time:" + context.getNextFireTime() + "\n");

    if (!isError)
      currentJobStatus.currentStatus = JobStatusEnum.COMPLETED;

    CronJobStatusCache.instance().updateJobStatus(SCHEDULE_JOB_ID, currentJobStatus);
  }

  public synchronized void fetchAndSaveBranchAndCommitDetails() {
    List<CustomRefModel> customRefModels = repositoryService.getCustomRefModels(true);

    if (CollectionUtils.isNotEmpty(customRefModels)) {
      for (CustomRefModel customRef : customRefModels) {

        Date sinceDate =
            CommitLastChangeCache.instance().getLastChangeDate(getUniqueName(customRef));

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

          CommitLastChangeCache.instance().updateLastChangeDate(getUniqueName(customRef),
              mostRecentCommitDate);
        }
      }
    }
  }

  private String getUniqueName(CustomRefModel customRef) {
    return customRef.getRepositoryName() + "_" + customRef.getRefModel().getName();
  }
}
