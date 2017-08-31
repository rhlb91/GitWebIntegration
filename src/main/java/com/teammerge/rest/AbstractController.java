package com.teammerge.rest;

import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

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

  @Resource(name = "schedulerService")
  private SchedulerService schedulerService;

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

  public SchedulerService getSchedulerService() {
    return schedulerService;
  }

  public void setSchedulerService(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
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

  protected Response createResponse(int status, Map<String, Object> result) {
    return Response.status(status).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

}
