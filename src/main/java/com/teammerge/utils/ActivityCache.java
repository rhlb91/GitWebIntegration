package com.teammerge.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teammerge.model.ActivityModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.ActivityModel;

public class ActivityCache {
  private static final ActivityCache instance;
  protected static final Logger logger = LoggerFactory.getLogger(ActivityCache.class);

  private static Map<String, ObjectCache<List<ActivityModel>>> cache;

  protected int cacheDays = -1;

  public static ActivityCache instance() {
    return instance;
  }

  static {
    instance = new ActivityCache();
  }

  protected ActivityCache() {
    cache = new HashMap<>();
  }

  /**
   * Returns the cutoff date for the cache. Commits after this date are cached. Commits before this
   * date are not cached.
   *
   * @return
   */
  public Date getCutoffDate() {
    final Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(System.currentTimeMillis());
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.add(Calendar.DATE, -1 * cacheDays);
    return cal.getTime();
  }

  /**
   * Sets the number of days to cache.
   *
   * @param days
   */
  public synchronized void setCacheDays(int days) {
    this.cacheDays = days;
    clear();
  }

  /**
   * Clears the entire commit cache.
   *
   */
  public void clear() {
    synchronized (cache) {
      cache.clear();
    }
  }

  /**
   * Clears the commit cache for a specific repository.
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
      logger.info(MessageFormat.format("{0} commit cache cleared", repositoryName));
    }
  }

  /**
   * Clears the commit cache for a specific branch of a specific repository.
   *
   * @param repositoryName
   * @param branch
   */
  public void clear(String repositoryName, String branch) {
    String repoKey = repositoryName.toLowerCase();
    boolean hadEntries = false;
    synchronized (cache) {
      ObjectCache<List<ActivityModel>> repoCache = cache.get(repoKey);
      if (repoCache != null) {
        List<ActivityModel> commits = repoCache.remove(branch.toLowerCase());
        hadEntries = !ArrayUtils.isEmpty(commits);
      }
    }
    if (hadEntries) {
      logger.info(MessageFormat.format("{0}:{1} commit cache cleared", repositoryName, branch));
    }
  }

  /**
   * Get all commits for the specified repository:branch that are in the cache.
   *
   * @param repositoryName
   * @param repository
   * @param branch
   * @return a list of commits
   */
  public List<ActivityModel> getCommits(String repositoryName, Repository repository,
      String branch) {
    return getCommits(repositoryName, repository, branch, getCutoffDate());
  }

  /**
   * Get all commits for the specified repository:branch since a specific date. These commits may be
   * retrieved from the cache if the sinceDate is after the cacheCutoffDate.
   *
   * @param repositoryName
   * @param repository
   * @param branch
   * @param sinceDate
   * @return a list of commits
   */
  public List<ActivityModel> getCommits(String repositoryName, Repository repository,
      String branch, Date sinceDate) {
    long start = System.nanoTime();
    Date cacheCutoffDate = getCutoffDate();
    List<ActivityModel> list;
    if (cacheDays > 0 && (sinceDate.getTime() >= cacheCutoffDate.getTime())) {
      // request fits within the cache window
      String repoKey = repositoryName.toLowerCase();
      String branchKey = branch.toLowerCase();

      RevCommit tip = JGitUtils.getCommit(repository, branch);
      Date tipDate = JGitUtils.getCommitDate(tip);

      ObjectCache<List<ActivityModel>> repoCache;
      synchronized (cache) {
        repoCache = cache.get(repoKey);
        if (repoCache == null) {
          repoCache = new ObjectCache<>();
          cache.put(repoKey, repoCache);
        }
      }
      synchronized (repoCache) {
        List<ActivityModel> activity;
        if (!repoCache.hasCurrent(branchKey, tipDate)) {
          activity = repoCache.getObject(branchKey);
          if (ArrayUtils.isEmpty(activity)) {
            // we don't have any cached commits for this branch, reload
            activity = get(repositoryName, repository, branch, cacheCutoffDate);
            repoCache.updateObject(branchKey, tipDate, activity);
            logger.debug(MessageFormat.format(
                "parsed {0} commits from {1}:{2} since {3,date,yyyy-MM-dd} in {4} msecs",
                activity.size(), repositoryName, branch, cacheCutoffDate,
                LoggerUtils.getTimeInSecs(start, System.currentTimeMillis())));
          } else {
            // incrementally update cache since the last cached commit
          //  ObjectId sinceCommit = activity.get(0).getCommits().get(0).Id();
            List<ActivityModel> incremental =null;
                //get(repositoryName, repository, branch, sinceCommit);
            logger.info(MessageFormat.format(
                "incrementally added {0} commits to cache for {1}:{2} in {3} msecs",
                incremental.size(), repositoryName, branch,
                LoggerUtils.getTimeInSecs(start, System.currentTimeMillis())));
            incremental.addAll(activity);
            repoCache.updateObject(branchKey, tipDate, incremental);
            activity = incremental;
          }
        } else {
          // cache is current
          activity = repoCache.getObject(branchKey);
          // evict older commits outside the cache window
          activity = reduce(activity, cacheCutoffDate);
          // update cache
          repoCache.updateObject(branchKey, tipDate, activity);
        }

        if (sinceDate.equals(cacheCutoffDate)) {
          // Mustn't hand out the cached list; that's not thread-safe
          list = new ArrayList<>(activity);
        } else {
          // reduce the commits to those since the specified date
          list = reduce(activity, sinceDate);
        }
      }
      logger.debug(MessageFormat.format(
          "retrieved {0} commits from cache of {1}:{2} since {3,date,yyyy-MM-dd} in {4} msecs",
          list.size(), repositoryName, branch, sinceDate,
          TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)));
    } else {
      // not caching or request outside cache window
      list = get(repositoryName, repository, branch, sinceDate);
      logger.debug(MessageFormat.format(
          "parsed {0} commits from {1}:{2} since {3,date,yyyy-MM-dd} in {4} msecs", list.size(),
          repositoryName, branch, sinceDate,
          LoggerUtils.getTimeInSecs(start, System.currentTimeMillis())));
    }
    return list;
  }

  /**
   * Returns a list of commits for the specified repository branch.
   *
   * @param repositoryName
   * @param repository
   * @param branch
   * @param sinceDate
   * @return a list of commits
   */
  protected List<ActivityModel> get(String repositoryName, Repository repository, String branch,
      Date sinceDate) {
    Map<ObjectId, List<RefModel>> allRefs = JGitUtils.getAllRefs(repository, false);
    List<RevCommit> revLog = JGitUtils.getRevLog(repository, branch, sinceDate);
    List<ActivityModel> commits = new ArrayList<ActivityModel>(revLog.size());
//    for (RevCommit commit : revLog) {
//      ActivityModel commitModel = new ActivityModel(repositoryName, branch, commit);
//      List<RefModel> commitRefs = allRefs.get(commitModel.getId());
//      commitModel.setRefs(commitRefs);
//      commits.add(commitModel);
//    }
    return commits;
  }

  /**
   * Returns a list of commits for the specified repository branch since the specified commit.
   *
   * @param repositoryName
   * @param repository
   * @param branch
   * @param sinceCommit
   * @return a list of commits
   */
  protected List<ActivityModel> get(String repositoryName, Repository repository, String branch,
      ObjectId sinceCommit) {
    Map<ObjectId, List<RefModel>> allRefs = JGitUtils.getAllRefs(repository, false);
    List<RevCommit> revLog = JGitUtils.getRevLog(repository, sinceCommit.getName(), branch);
    List<ActivityModel> commits = new ArrayList<ActivityModel>(revLog.size());
    for (RevCommit commit : revLog) {
//      ActivityModel commitModel = new ActivityModel(repositoryName, branch, commit);
//      List<RefModel> commitRefs = allRefs.get(commitModel.getId());
//      commitModel.setRefs(commitRefs);
//      commits.add(commitModel);
    }
    return commits;
  }

  /**
   * Reduces the list of commits to those since the specified date.
   *
   * @param commits
   * @param sinceDate
   * @return a list of commits
   */
  protected List<ActivityModel> reduce(List<ActivityModel> commits, Date sinceDate) {
    List<ActivityModel> filtered = new ArrayList<ActivityModel>(commits.size());
    for (ActivityModel commit : commits) {
//      if (commit.getCommitDate().compareTo(sinceDate) >= 0) {
//        filtered.add(commit);
//      }
    }
    return filtered;
  }

}
