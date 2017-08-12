package com.teammerge.cache;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teammerge.utils.StringUtils;
import com.teammerge.utils.TimeUtils;

/**
 * Caches the last commit saved in DB per repository.
 * <p>
 * This cache is mainly used for Data Insertion CronJob
 * </p>
 */
public class CommitLastChangeCache {
  protected static final Logger LOG = LoggerFactory.getLogger(CommitLastChangeCache.class);

  private static final CommitLastChangeCache instance;

  protected final Map<String, Date> cache;

  public static CommitLastChangeCache instance() {
    return instance;
  }

  static {
    instance = new CommitLastChangeCache();
  }

  protected CommitLastChangeCache() {
    cache = new HashMap<>();
  }

  /**
   * Clears the CommitLastChange cache for a specific repository.
   *
   * @param repositoryName
   */
  public void clear(String repositoryName) {
    String repoKey = repositoryName.toLowerCase();
    boolean hadEntries = false;
    synchronized (cache) {
      hadEntries = cache.remove(repoKey) != null;
    }
    if (hadEntries) {
      LOG.info(MessageFormat.format("{0} CommitLastChange cache cleared", repositoryName));
    }
  }

  public Date getLastChangeDate(String repositoryName) {

    if (StringUtils.isEmpty(repositoryName)) {
      return null;
    }
    String repoKey = repositoryName.toLowerCase();

    Date lastCommitDate = null;

    synchronized (cache) {
      lastCommitDate = cache.get(repoKey);
    }

    if (lastCommitDate == null) {
      Date inceptionDate = TimeUtils.getInceptionDate();
      cache.put(repoKey, inceptionDate);
      lastCommitDate = inceptionDate;
    }
    return lastCommitDate;
  }

  public void updateLastChangeDate(String repositoryName, Date lastChange) {
    if (StringUtils.isEmpty(repositoryName)) {
      return;
    }

    String repoKey = repositoryName.toLowerCase();
    Date inceptionDate = TimeUtils.getInceptionDate();

    if (lastChange == null) {
      lastChange = inceptionDate;
    }

    synchronized (cache) {
      cache.put(repoKey, lastChange);
    }
  }
}
