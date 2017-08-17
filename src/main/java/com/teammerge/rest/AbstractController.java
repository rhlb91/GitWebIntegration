package com.teammerge.rest;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;

import com.teammerge.services.BranchService;
import com.teammerge.services.CommitService;
import com.teammerge.services.CompanyService;
import com.teammerge.services.DashBoardService;
import com.teammerge.services.RepoCredentialService;
import com.teammerge.services.RepositoryService;
import com.teammerge.services.SchedulerService;

public abstract class AbstractController {
  @Value("${app.debug}")
  private String debug;

  @Resource(name = "repositoryService")
  private RepositoryService repositoryService;

  @Resource(name = "dashBoardService")
  private DashBoardService dashBoardService;

  @Resource(name = "commitService")
  private CommitService commitService;

  @Resource(name = "branchService")
  private BranchService branchService;

  @Resource(name = "scheduleService")
  private SchedulerService scheduleService;

  @Resource(name = "companyService")
  private CompanyService companyService;

  @Resource(name = "repoCredentialService")
  private RepoCredentialService repoCredentialService;

  public RepoCredentialService getRepoCredentialService() {
    return repoCredentialService;
  }

  public void setRepoCredentialService(RepoCredentialService repoCredentialService) {
    this.repoCredentialService = repoCredentialService;
  }

  public SchedulerService getScheduleService() {
    return scheduleService;
  }

  public void setScheduleService(SchedulerService scheduleService) {
    this.scheduleService = scheduleService;
  }

  protected boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  public CompanyService getCompanyService() {
    return companyService;
  }

  public void setCompanyService(CompanyService companyService) {
    this.companyService = companyService;
  }

  public RepositoryService getRepositoryService() {
    return repositoryService;
  }

  public void setRepositoryService(RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  public DashBoardService getDashBoardService() {
    return dashBoardService;
  }

  public void setDashBoardService(DashBoardService dashBoardService) {
    this.dashBoardService = dashBoardService;
  }

  public CommitService getCommitService() {
    return commitService;
  }

  public void setCommitService(CommitService commitService) {
    this.commitService = commitService;
  }

  public BranchService getBranchService() {
    return branchService;
  }

  public void setBranchService(BranchService branchService) {
    this.branchService = branchService;
  }

  protected String convertToFinalOutput(final String output) {
    return "{ \"data\":" + output + "}";
  }

}
