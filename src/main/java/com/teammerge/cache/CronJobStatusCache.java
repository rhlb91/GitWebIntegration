package com.teammerge.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teammerge.cronjob.AbstractCustomJob.JobStatus;
import com.teammerge.utils.StringUtils;


public class CronJobStatusCache {
  protected static final Logger LOG = LoggerFactory.getLogger(CronJobStatusCache.class);

  private static final CronJobStatusCache instance;

  protected final ObjectCache<JobStatus> cache;

  public static CronJobStatusCache instance() {
    return instance;
  }

  static {
    instance = new CronJobStatusCache();
  }

  private CronJobStatusCache() {
    cache = new ObjectCache<>();
  }

  // /**
  // * Clears the CommitLastChange cache for a specific repository.
  // *
  // * @param objectKey
  // */
  // public void clear(String objectKey) {
  // String repoKey = objectKey.toLowerCase();
  // boolean hadEntries = false;
  // synchronized (cache) {
  // hadEntries = cache.remove(repoKey) != null;
  // }
  // if (hadEntries) {
  // LOG.info(MessageFormat.format("{0} CommitLastChange cache cleared", objectKey));
  // }
  // }
  //
  public JobStatus getJobStatus(String objectKey) {

    if (StringUtils.isEmpty(objectKey)) {
      return null;
    }
    String repoKey = objectKey.toLowerCase();

    JobStatus status = null;
    synchronized (cache) {
      status = cache.getObject(repoKey);
    }
    if (status == null) {
      status = new JobStatus();
      cache.updateObject(repoKey, status);
    }
    return status;
  }

  public void updateJobStatus(String jobName, JobStatus status) {
    if (StringUtils.isEmpty(jobName)) {
      return;
    }

    String objectKey = jobName.toLowerCase();

    if (status == null) {
      status = new JobStatus();
    }

    synchronized (cache) {
      cache.updateObject(objectKey, status);
    }
  }

}
