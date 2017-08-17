package com.teammerge.cronjob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teammerge.GitWebException.InvalidArgumentsException;
import com.teammerge.cache.CommitCache;
import com.teammerge.cache.CommitLastChangeCache;
import com.teammerge.model.CustomRefModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.TimeUtils;

public class MannualDataInsertionCronJob extends AbstractCustomJob {

  private static final Logger LOG = LoggerFactory.getLogger(MannualDataInsertionCronJob.class);

  @Resource(name = "repositoryService")
  private RepositoryService repositoryService;

  public MannualDataInsertionCronJob() {
    super();
  }

  /**
   * this method is responsible for saving branch details in DB.
   * 
   * @return the list of failed branches that are not saved in DB
   */
  public synchronized List<RefModel> runJobSavingForBranchDetails() {

    List<RefModel> failedBranches = new ArrayList<>();

    Date minimumDate = TimeUtils.getInceptionDate();
    List<CustomRefModel> branchModels = repositoryService.getCustomRefModels(true);

    if (CollectionUtils.isEmpty(branchModels)) {
      LOG.warn("No branches found after date:" + minimumDate);
      return null;
    }

    for (CustomRefModel customRefModel : branchModels) {
      // TODO check if individual customRefModel is valid or not, probably
      // create a validator

      List<RepositoryCommit> commits =
          CommitCache.instance().getCommits(customRefModel.getRepositoryName(),
              customRefModel.getRepository(), customRefModel.getRefModel().getName(), minimumDate);

      try {
        saveOrUpdateBranch(customRefModel, commits);
      } catch (InvalidArgumentsException e) {
        failedBranches.add(customRefModel.getRefModel());
        LOG.error("Cannot create new branch model for repo " + customRefModel.getRepositoryName()
            + ", branch " + customRefModel.getRefModel().getName() + " from cronjob "
            + getClass().getSimpleName(), e);
      }
    }
    return failedBranches;
  }

  /**
   * this method is responsible for saving commit details in DB.
   * 
   * @return the list of failed commits that are not saved in DB
   */
  public synchronized List<RepositoryCommit> runJobSavingForCommitDetails() {
    List<RepositoryCommit> failedCommits = new ArrayList<>();

    Date minimumDate = TimeUtils.getInceptionDate();
    List<CustomRefModel> branchModels = repositoryService.getCustomRefModels(true);

    if (CollectionUtils.isEmpty(branchModels)) {
      LOG.warn("No commits found after date:" + minimumDate);
      return null;
    }

    for (CustomRefModel customRefModel : branchModels) {
      List<RepositoryCommit> commits =
          CommitCache.instance().getCommits(customRefModel.getRepositoryName(),
              customRefModel.getRepository(), customRefModel.getRefModel().getName(), minimumDate);

      if (CollectionUtils.isNotEmpty(commits)) {
        for (RepositoryCommit commit : commits) {
          try {
            saveCommit(commit, customRefModel);
          } catch (InvalidArgumentsException e) {
            failedCommits.add(commit);
            LOG.error(
                "Cannot create new commit model for repo " + customRefModel.getRepositoryName()
                    + ", branch " + customRefModel.getRefModel().getName() + ", commit name: "
                    + commit.getName() + " from cronjob " + getClass().getSimpleName(), e);
          }
        }
      }
    }
    return failedCommits;
  }

  /**
   * <p>
   * this method is responsible for saving branches and commit details in DB.
   * </p>
   * 
   * <pThis method returns the List of type Object, thus the caller method should type cast the
   * respective object in particular classes before use. </p>
   * 
   * @return list of failed entries not saved in DB
   */
  public synchronized List<Object> runSaveAllDetails() {
    List<Object> failedEntries = new ArrayList<>();

    Date minimumDate = TimeUtils.getInceptionDate();
    List<CustomRefModel> branchModels = repositoryService.getCustomRefModels(true);

    if (CollectionUtils.isEmpty(branchModels)) {
      LOG.warn("No commits found after date:" + minimumDate);
      return null;
    }

    for (CustomRefModel customRefModel : branchModels) {
      List<RepositoryCommit> commits =
          CommitCache.instance().getCommits(customRefModel.getRepositoryName(),
              customRefModel.getRepository(), customRefModel.getRefModel().getName(), minimumDate);

      try {
        saveOrUpdateBranch(customRefModel, commits);
      } catch (InvalidArgumentsException e) {
        failedEntries.add(customRefModel.getRefModel());
        LOG.error("Cannot create new branch model from cronjob !!" + getClass().getSimpleName(), e);
      }

      if (CollectionUtils.isNotEmpty(commits)) {
        for (RepositoryCommit commit : commits) {
          try {
            saveCommit(commit, customRefModel);
          } catch (InvalidArgumentsException e) {
            failedEntries.add(commit);
            LOG.error("Cannot create new commit model from cronjob!!" + getClass().getSimpleName(),
                e);
          }
        }
      }
    }
    return failedEntries;
  }

  public synchronized void fetchAndSaveBranchAndCommitDetailsOnline(String repoName, String branchId) {
    List<CustomRefModel> customRefModels = repositoryService.getCustomRefModels(true);

    if (CollectionUtils.isNotEmpty(customRefModels)) {
      for (CustomRefModel customRef : customRefModels) {

        String branchName = customRef.getRefModel().getName();
        boolean branchValidation = branchName.contains(branchId);

        if (repoName.equalsIgnoreCase(customRef.getRepositoryName()) && (branchValidation == true)) {

          Date sinceDate =
              CommitLastChangeCache.instance().getLastChangeDate(getUniqueName(customRef));

          List<RepositoryCommit> commits =
              CommitCache.instance().getCommits(customRef.getRepositoryName(),
                  customRef.getRepository(), customRef.getRefModel().getName(), sinceDate);
          try {
            saveOrUpdateBranch(customRef, commits);
          } catch (InvalidArgumentsException e) {
            LOG.error(
                "Cannot create new branch model from cronjob !!" + getClass().getSimpleName(), e);
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
            updateLastCommitInDB(getUniqueName(customRef), mostRecentCommitDate);
          }
        }
      }
    }
  }

  private void updateLastCommitInDB(String uniqueName, Date mostRecentCommitDate) {
    Date commitDateInDB = CommitLastChangeCache.instance().getLastChangeDate(uniqueName);
    if (commitDateInDB == null || commitDateInDB.before(mostRecentCommitDate)) {
      branchService.updateLastCommitDateAddedInBranch(uniqueName, mostRecentCommitDate);
      updateLastCommitDateInCache(uniqueName, mostRecentCommitDate);
    }
  }

  private void updateLastCommitDateInCache(String uniqueName, Date mostRecentCommitDate) {
    CommitLastChangeCache.instance().updateLastChangeDate(uniqueName, mostRecentCommitDate);
  }
}
