package com.teammerge.cronjob;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.teammerge.GitWebException.InvalidArgumentsException;
import com.teammerge.cache.CommitCache;
import com.teammerge.cache.CommitLastChangeCache;
import com.teammerge.cache.CronJobStatusCache;
import com.teammerge.model.CustomRefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.utils.LoggerUtils;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DataInsertionJob extends AbstractCustomJob implements Job {

  private static final Logger LOG = LoggerFactory.getLogger(DataInsertionJob.class);

  private static final String SCHEDULE_JOB_ID = "JobGetCommitDetails";
  private static final String JOB_NUMBER = "jobNumber";

  private int jobNumber;

  @Value("${git.commit.timeFormat}")
  private String commitTimeFormat;

  @Value("${app.dateFormat}")
  private String commitDateFormat;


  Map<String, Date> lastCommitSaveDetailsInDB = new HashMap<>();

  public DataInsertionJob() {
    super();
  }

  /**
   * <p>
   * jobNumber will get inserted by Quartz at run runtime as we have used setter injection for
   * jobNumber.
   * </p>
   */
  public void execute(JobExecutionContext context) {
    try {
      long methodStart = System.currentTimeMillis();
      boolean isError = false;
      JobStatus currentJobStatus = CronJobStatusCache.instance().getJobStatus(SCHEDULE_JOB_ID);

      if (JobStatusEnum.IN_PROGRESS.equals(currentJobStatus.currentStatus)) {
        LOG.debug("Could not run a new job. A job already running!!");
        return;
      }

      LOG.info("\nExecuting the DataInsertion Job #" + jobNumber + " at " + context.getFireTime());

      // before starting this job, changing the status to In_Progress
      updateJobStatusInCache(currentJobStatus, JobStatusEnum.IN_PROGRESS);

      try {
        fetchAndSaveBranchAndCommitDetails();

      } catch (Exception e) {
        updateJobStatusInCache(currentJobStatus, JobStatusEnum.ERROR);
        isError = true;
        LOG.error("Caught exception in " + getClass().getSimpleName(), e);
      }

      // after completing this job, changing the status to completed
      if (!isError)
        updateJobStatusInCache(currentJobStatus, JobStatusEnum.COMPLETED);


      LOG.info("DataInsertion #" + jobNumber + " Completed in "
          + LoggerUtils.getTimeInSecs(methodStart, System.currentTimeMillis())
          + "!! Next scheduled time:" + context.getNextFireTime() + "\n");

      // Incrementing the job execution number, this will get saved with this job detail as we are
      // using @PersistJobDataAfterExecution
      context.getJobDetail().getJobDataMap().put(JOB_NUMBER, jobNumber + 1);
    } catch (Exception e) {
      LOG.error("Caught Exception in execute() in " + getClass().getSimpleName(), e);
    }
  }

  public synchronized void fetchAndSaveBranchAndCommitDetails() {
    List<CustomRefModel> customRefModels = repositoryService.getCustomRefModels(true);

    if (CollectionUtils.isNotEmpty(customRefModels)) {
      for (CustomRefModel customRef : customRefModels) {

        Date sinceDate = getLastChangeDate(getUniqueName(customRef));


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
              LOG.error(
                  "Cannot create new commit model from cronjob!!" + getClass().getSimpleName(), e);
            }
          }

          updateLastCommitDateInCache(getUniqueName(customRef), mostRecentCommitDate);
          updateLastCommitInDB(getUniqueName(customRef), mostRecentCommitDate);
        }
      }
    }
  }

  private void updateLastCommitDateInCache(String uniqueName, Date mostRecentCommitDate) {
    CommitLastChangeCache.instance().updateLastChangeDate(uniqueName, mostRecentCommitDate);
  }

  private void updateLastCommitInDB(String uniqueName, Date mostRecentCommitDate) {
    Date commitDateInDB = lastCommitSaveDetailsInDB.get(uniqueName);
    if (commitDateInDB == null || commitDateInDB.before(mostRecentCommitDate)) {
      branchService.updateLastCommitDateAddedInBranch(uniqueName, mostRecentCommitDate);
      lastCommitSaveDetailsInDB.put(uniqueName, mostRecentCommitDate);
      updateLastCommitDateInCache(uniqueName, mostRecentCommitDate);
    }
  }

  private Date getLastChangeDate(String uniqueName) {
    Date lastChange = null;

    if (jobNumber == 1) {
      lastChange = branchService.getLastCommitDateAddedInBranch(uniqueName);
      lastCommitSaveDetailsInDB.put(uniqueName, lastChange);
      updateLastCommitDateInCache(uniqueName, lastChange);
    }

    if (lastChange == null) {
      lastChange = CommitLastChangeCache.instance().getLastChangeDate(uniqueName);

      // this means there is no entry in the DB for this branch yet, so create with inception date
      // updateLastCommitInDB(uniqueName, lastChange);
    }
    return lastChange;
  }



  private void updateJobStatusInCache(JobStatus currentJobStatus, JobStatusEnum jobStatus) {
    currentJobStatus.currentStatus = jobStatus;
    CronJobStatusCache.instance().updateJobStatus(SCHEDULE_JOB_ID, currentJobStatus);
  }

  public void setJobNumber(int jobNumber) {
    this.jobNumber = jobNumber;
  }

}
