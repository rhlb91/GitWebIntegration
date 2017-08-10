package com.teammerge.strategy.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import com.teammerge.dao.RepoCredentialDao;
import com.teammerge.entity.Company;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.entity.RepoCredentialsKey;
import com.teammerge.model.GitOptions;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.CompanyService;
import com.teammerge.services.GitService;
import com.teammerge.strategy.CloneStrategy;
import com.teammerge.utils.LoggerUtils;
import com.teammerge.utils.StringUtils;

public class UpdateRepoWithPullStrategy implements CloneStrategy {

  private final static Logger LOG = LoggerFactory.getLogger(UpdateRepoWithPullStrategy.class);

  private static final String GIT_FOLDER_NAME = ".git";

  private GitService gitService;

  private CompanyService companyService;

  private RepoCredentialDao repoCredentialDao;

  @Value("${app.debug}")
  private String debug;

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  @Override
  public Repository createOrUpdateRepo(File f, String repositoryName, boolean isRepoExists) {
    if (repositoryName == null) {
      LOG.error("Cannot clone or update repository from path, Reason: RepoName is null");
      return null;
    }

    Repository repo = null;
    long start = System.currentTimeMillis();
    Company company = companyService.getCompanyForProject(repositoryName);
    String remoteRepoPath =
        companyService.getRemoteUrlForCompanyAndProject(company, repositoryName);

    if (StringUtils.isEmpty(remoteRepoPath)) {
      LOG.error("Cannot clone or update repository, Reason: remoteRepoPath is null for repo "
          + repositoryName);
      return null;
    }

    RepoCredentials repoCreds =
        repoCredentialDao.fetchEntity(new RepoCredentialsKey(company.getName(), repositoryName));

    if (repoCreds == null) {
      LOG.error("Cannot clone or update repository, Reason: Credentails not found for companyId: "
          + company.getName() + ", projectId: " + repositoryName);
      return null;
    }

    if (!isRepoExists) {
      LOG.info("Repo does not exits " + repositoryName + ", creating the repository!!");

      GitOptions gitOptions = new GitOptions();
      gitOptions.setURI(remoteRepoPath);
      gitOptions.setDestinationDirectory(f.getAbsolutePath() + "/" + repositoryName);
      gitOptions.setCloneAllBranches(Boolean.TRUE);
      gitOptions.setIncludeSubModule(Boolean.TRUE);
      gitOptions.setBare(Boolean.FALSE);
      gitOptions.setUsername(repoCreds.getUsername());
      gitOptions.setPassword(repoCreds.getPassword());


      try (Git git = gitService.cloneRepository(gitOptions);) {
        repo = git.getRepository();
      } catch (GitAPIException e) {
        LOG.error("Error cloning repository from path " + remoteRepoPath, e);
      }

      return repo;
    } else {

      // check for updates
      repo = loadRepository(f, repositoryName);

      try (Git git = new Git(repo);) {
        PullCommand pc = git.pull();
        PullResult pr = pc.call();
        MergeResult mergeResult = pr.getMergeResult();

        if (isDebugOn()) {
          LOG.debug("Result of repo pull of " + repositoryName + ": "
              + mergeResult.getMergeStatus());
        }
      } catch (GitAPIException e) {
        LOG.error("Error in updating repository " + repositoryName, e);
      }

      // To do delete files except .git dir
      File repoDir = new File(f, repositoryName);
      try {
        File[] files = repoDir.listFiles();
        if (files != null) {
          for (File fe : files) {
            if (fe.isFile()) {
              fe.delete();
            } else {
              if (GIT_FOLDER_NAME.equalsIgnoreCase(fe.getName())) {
                continue;
              }
              FileUtils.deleteDirectory(fe);
            }
          }
        }
      } catch (IOException e) {
        LOG.error("Error occured removing the repsositry folders " + repoDir, e);
      }

      if (isDebugOn()) {
        LOG.debug("Updated repository " + repositoryName + " in "
            + LoggerUtils.getTimeInSecs(start, System.currentTimeMillis()));
      }
      return repo;
    }
  }

  private Repository loadRepository(final File repoDir, final String repoName) {
    File dir = FileKey.resolve(new File(repoDir, repoName), FS.DETECTED);
    if (dir == null)
      return null;

    Repository r = null;
    try {
      FileKey key = FileKey.exact(dir, FS.DETECTED);
      r = RepositoryCache.open(key, true);
    } catch (IOException e) {
      LOG.error("Failed to find " + new File(repoDir, repoName).getAbsolutePath(), e);
    }
    return r;
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
