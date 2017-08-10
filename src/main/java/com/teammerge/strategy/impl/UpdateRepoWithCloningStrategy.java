package com.teammerge.strategy.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import com.teammerge.dao.CompanyDao;
import com.teammerge.dao.RepoCredentialDao;
import com.teammerge.entity.Company;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.entity.RepoCredentialsKey;
import com.teammerge.model.GitOptions;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.CompanyService;
import com.teammerge.services.GitService;
import com.teammerge.strategy.CloneStrategy;
import com.teammerge.utils.StringUtils;

public class UpdateRepoWithCloningStrategy implements CloneStrategy {

  private final static Logger LOG = LoggerFactory.getLogger(UpdateRepoWithCloningStrategy.class);

  private GitService gitService;

  private CompanyService companyService;

  private RepoCredentialDao repoCredentialDao;

  @Value("${app.debug}")
  private String debug;

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  @Override
  public Repository createOrUpdateRepo(File gitDir, String repoName, boolean isRepoExists) {
    if (repoName == null) {
      LOG.error("Error cloning/upadting repository from path, Reason: RepoName is null");
      return null;
    }

    String repositoryName = repoName;
    Git git = null;
    Repository repo = null;

    Company company = companyService.getCompanyForProject(repositoryName);

    String remoteRepoPath = companyService.getRemoteUrlForProject(repoName);

    if (StringUtils.isEmpty(remoteRepoPath)) {
      LOG.error(
          "Cannot clone or update repository, Reason: remoteRepoPath is null for repo " + repoName);
      return null;
    }

    RepoCredentials repoCreds =
        repoCredentialDao.fetchEntity(new RepoCredentialsKey(company.getName(), repositoryName));

    if (repoCreds == null) {
      LOG.error("Cannot clone or update repository, Reason: Credentails not found for companyId: "
          + company.getName() + ", projectId: " + repositoryName);
      return null;
    }

    // If repository exists, delete the old repository
    if (isRepoExists) {
      File repoDir = new File(gitDir, repositoryName);
      try {
        FileUtils.deleteDirectory(repoDir);
        LOG.info("Deleted the old repo " + repoDir);
      } catch (IOException e) {
        LOG.error("Unable to delete Repo dir-" + repoDir, e);
      }
    } else {
      LOG.info("Repo does not exits " + repositoryName + ", creating the repository!!");
    }

    GitOptions gitOptions = new GitOptions();
    gitOptions.setURI(remoteRepoPath);
    gitOptions.setDestinationDirectory(gitDir.getAbsolutePath() + "/" + repositoryName);
    gitOptions.setCloneAllBranches(Boolean.TRUE);
    gitOptions.setIncludeSubModule(Boolean.TRUE);
    gitOptions.setBare(Boolean.FALSE);
    gitOptions.setUsername(repoCreds.getUsername());
    gitOptions.setPassword(repoCreds.getPassword());

    try {
      git = gitService.cloneRepository(gitOptions);
      repo = git.getRepository();
    } catch (GitAPIException e) {
      LOG.error("Error cloning repository from path " + remoteRepoPath, e);
    }
    return repo;
  }

  @Required
  public void setGitService(GitService gitService) {
    this.gitService = gitService;
  }

  @Required
  public void setCompanyService(CompanyService companyService) {
    this.companyService = companyService;
  }

  @Required
  public void setRepoCredentialDao(RepoCredentialDao repoCredentialDao) {
    this.repoCredentialDao = repoCredentialDao;
  }
}
