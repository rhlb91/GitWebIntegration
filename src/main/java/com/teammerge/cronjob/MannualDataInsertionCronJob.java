package com.teammerge.cronjob;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teammerge.GitWebException.InvalidArgumentsException;
import com.teammerge.model.CustomRefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.CommitCache;
import com.teammerge.utils.TimeUtils;

public class MannualDataInsertionCronJob extends AbstractCustomJob {

  private static final Logger LOG = LoggerFactory.getLogger(MannualDataInsertionCronJob.class);

  @Resource(name = "repositoryService")
  private RepositoryService repositoryService;

  public MannualDataInsertionCronJob() {
    super();
  }

  public synchronized void runJobSavingForBranchDetails() {
    System.out.println("Enter in runJobSavingForBranchDetails");
    Date minimumDate = TimeUtils.getInceptionDate();
    List<CustomRefModel> branchModels = repositoryService.getCustomRefModels(true);

    if (CollectionUtils.isEmpty(branchModels)) {
      LOG.warn("No branches found after date:" + minimumDate);
      return;
    }

    for (CustomRefModel customRefModel : branchModels) {
      // TODO check if individual customRefModel is valid or not, probably create a validator

      List<RepositoryCommit> commits =
          CommitCache.instance().getCommits(customRefModel.getRepositoryName(),
              customRefModel.getRepository(), customRefModel.getRefModel().getName(), minimumDate);

      try {
        saveBranch(customRefModel, commits);
      } catch (InvalidArgumentsException e) {
        LOG.error("Cannot create new branch model from cronjob !!" + getClass().getSimpleName(), e);
      }
    }

  }

  public synchronized void runJobSavingForCommitDetails() {
    System.out.println("Enter in runJobSavingForCommitDetails");

    Date minimumDate = TimeUtils.getInceptionDate();
    List<CustomRefModel> branchModels = repositoryService.getCustomRefModels(true);

    if (CollectionUtils.isEmpty(branchModels)) {
      LOG.warn("No commits found after date:" + minimumDate);
      return;
    }

    for (CustomRefModel temp : branchModels) {
      List<RepositoryCommit> commits = CommitCache.instance().getCommits(temp.getRepositoryName(),
          temp.getRepository(), temp.getRefModel().getName(), minimumDate);


      if (CollectionUtils.isNotEmpty(commits)) {
        for (RepositoryCommit commit : commits) {
          try {
            saveCommit(commit, temp);
          } catch (InvalidArgumentsException e) {
            LOG.error("Cannot create new commit model from cronjob!!" + getClass().getSimpleName(),
                e);
          }
        }
      }
    }
  }

  public synchronized void runJobSavingForRepository() {
    System.out.println("Enter in runJobSavingForRepository");
    List<RepositoryModel> repositoryModels = repositoryService.getRepositoryModels();

    if (CollectionUtils.isEmpty(repositoryModels)) {
      LOG.warn("No Repository found!!");
      return;
    }

    for (RepositoryModel model : repositoryModels) {
      repositoryService.saveRepository(model);
    }
  }
}


