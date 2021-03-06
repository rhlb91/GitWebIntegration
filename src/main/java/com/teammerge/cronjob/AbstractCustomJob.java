package com.teammerge.cronjob;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.teammerge.GitWebException.InvalidArgumentsException;
import com.teammerge.cache.CommitLastChangeCache;
import com.teammerge.entity.BranchModel;
import com.teammerge.entity.CommitModel;
import com.teammerge.model.CustomRefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.populator.BranchPopulator;
import com.teammerge.populator.CommitPopulator;
import com.teammerge.services.BranchService;
import com.teammerge.services.CommitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.services.SchedulerService;

public abstract class AbstractCustomJob {

  private static CommitPopulator commitPopulator;

  private static BranchPopulator branchPopulator;

  private static CommitService commitService;

  protected static BranchService branchService;

  protected static RepositoryService repositoryService;

  protected static SchedulerService schedulerService;

  public AbstractCustomJob() {

  }

  protected void saveCommit(RepositoryCommit commit, CustomRefModel branch)
      throws InvalidArgumentsException {
    CommitModel newCommit = new CommitModel();
    commitPopulator.populate(commit, branch, newCommit);
    commitService.saveOrUpdateCommitInSeparateSession(newCommit);
  }

  protected void saveOrUpdateBranch(CustomRefModel branch, List<RepositoryCommit> commits)
      throws InvalidArgumentsException {
    BranchModel branchModel = branchService.getBranchDetails(branch.getRefModel().getName());

    if (branchModel == null) {
      branchModel = new BranchModel(branch.getRefModel().getName(), branch.getRepositoryName());
    }
    branchPopulator.populate(branch, branchModel.getNumOfCommits() + commits.size(), branchModel);
    branchService.saveOrUpdateBranchInSeparateSession(branchModel);
  }
  
  protected String getUniqueName(CustomRefModel customRef) {
    return customRef.getRepositoryName() + "_" + customRef.getRefModel().getName();
  }
  
  public static class JobStatus {
    public JobStatusEnum currentStatus;

    public JobStatus() {
      this.currentStatus = JobStatusEnum.NOT_STARTED;
    }
  }

  public enum JobStatusEnum {
    NOT_STARTED, IN_PROGRESS, COMPLETED, ERROR;

    public static JobStatusEnum forName(String name) {
      for (JobStatusEnum type : values()) {
        if (type.name().equalsIgnoreCase(name)) {
          return type;
        }
      }
      return NOT_STARTED;
    }

    @Override
    public String toString() {
      return name().toLowerCase();
    }
  }

  protected void printJobStatus(JobStatus jobStatus) {
    System.out.println(jobStatus.currentStatus);
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

  @Required
  public void setSchedulerService(SchedulerService schedulerService1) {
    schedulerService = schedulerService1;
  }

}
