package com.teammerge.cronjob;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.teammerge.GitWebException.InvalidArgumentsException;
import com.teammerge.model.BranchModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.CustomRefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.populator.BranchPopulator;
import com.teammerge.populator.CommitPopulator;
import com.teammerge.services.BranchService;
import com.teammerge.services.CommitService;
import com.teammerge.services.RepositoryService;

public abstract class AbstractCustomJob {

  private static CommitPopulator commitPopulator;

  private static BranchPopulator branchPopulator;

  private static CommitService commitService;

  private static BranchService branchService;

  protected static RepositoryService repositoryService;
  
  public AbstractCustomJob() {
    // repositoryService = ApplicationContextUtils.getBean(RepositoryService.class);
    /*
     * commitPopulator = ApplicationContextUtils.getBean(CommitPopulator.class); branchPopulator =
     * ApplicationContextUtils.getBean(BranchPopulator.class); branchService =
     * ApplicationContextUtils.getBean(BranchService.class); commitService =
     * ApplicationContextUtils.getBean(CommitService.class);
     */
  }

  protected void saveCommit(RepositoryCommit commit, CustomRefModel branch)
      throws InvalidArgumentsException {
    CommitModel newCommit = new CommitModel();
    commitPopulator.populate(commit, branch, newCommit);
    commitService.saveCommit(newCommit);
  }

  protected void saveBranch(CustomRefModel branch, List<RepositoryCommit> commits)
      throws InvalidArgumentsException {
    BranchModel newbranch = new BranchModel();
    branchPopulator.populate(branch, commits.size(), newbranch);
    branchService.saveBranch(newbranch);
  }

  @Required
  public void setCommitService(CommitService commitService1) {
    commitService = commitService1;
  }

  @Required
  public void setBranchService(BranchService branchService1) {
    branchService = branchService1;
  }

  @Required
  public void setRepositoryService(RepositoryService repositoryService1) {
    repositoryService = repositoryService1;
  }

  @Required
  public void setCommitPopulator(CommitPopulator commitPopulator1) {
    commitPopulator = commitPopulator1;
  }

  @Required
  public void setBranchPopulator(BranchPopulator branchPopulator1) {
    branchPopulator = branchPopulator1;
  }

}
