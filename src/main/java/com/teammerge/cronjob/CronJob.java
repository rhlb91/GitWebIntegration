package com.teammerge.cronjob;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.teammerge.model.BranchModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.CustomRefModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.TimeUtils;
import com.teammerge.services.BranchService;
import com.teammerge.services.CommitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.JGitUtils;

@Component
public class CronJob {

  private static final Logger LOG = LoggerFactory.getLogger(CronJob.class);

  @Resource(name = "repositoryService")
  private RepositoryService repositoryService;

  @Resource(name = "branchService")
  private BranchService branchService;

  @Resource(name = "commitService")
  private CommitService commitService;


  public synchronized void runJobSavingForBranchDetails() {
    System.out.println("Enter in runJobSavingForBranchDetails");
    List<CustomRefModel> branchModels = repositoryService.getCustomRefModels();

    if (CollectionUtils.isEmpty(branchModels)) {
      return;
    }

    for (CustomRefModel customRefModel : branchModels) {

      // TODO check if individual customRefModel is valid or not, probably create a validator

      DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      Date lastmodifieddate = customRefModel.getRefModel().getDate();
      String lastmodified_date = df.format(lastmodifieddate);
      List<RevCommit> commits =
          JGitUtils.getRevLog(customRefModel.getRepository(),
              customRefModel.getRefModel().displayName, TimeUtils.getInceptionDate());

      BranchModel Bmodel = new BranchModel();
      Bmodel.setLastModifiedDate(lastmodified_date);
      Bmodel.setNumOfCommits(commits.size());
      Bmodel.setNumOfPull(1);
      Bmodel.setRepositoryId(customRefModel.getRepositoryName());
      Bmodel.setBranchId(customRefModel.getRefModel().getName());

      branchService.saveBranch(Bmodel);
    }

  }

  public synchronized void runJobSavingForCommitDetails() {
    System.out.println("Enter in runJobSavingForCommitDetails");

    repositoryService = ApplicationContextUtils.getBean(RepositoryService.class);
    commitService = ApplicationContextUtils.getBean(CommitService.class);

    List<CustomRefModel> branchModels = repositoryService.getCustomRefModels();

    if (CollectionUtils.isEmpty(branchModels)) {
      return;
    }

    for (CustomRefModel temp : branchModels) {
      List<RevCommit> commits =
          JGitUtils.getRevLog(temp.getRepository(), temp.getRefModel().displayName,
              TimeUtils.getInceptionDate());

      if (commits != null) {

        for (RevCommit commit : commits) {

          CommitModel model = new CommitModel();

          model.setCommitId(commit.getName());
          model.setCommitAuthor(commit.getAuthorIdent());
          model.setBranchName(temp.getRefModel().displayName);
          model.setShortMessage(commit.getShortMessage());
          model.setTrimmedMessage(commit.getShortMessage());
          model.setCommitDate(JGitUtils.getCommitDate(commit));
          model.setCommitHash("rtttt");
          model.setCommitTimeFormatted("1:00 AM");
          model.setIsMergeCommit(true);
          model.setRepositoryName(temp.getRepositoryName());

          commitService.saveCommit(model);
        }

      }

    }

  }

}
