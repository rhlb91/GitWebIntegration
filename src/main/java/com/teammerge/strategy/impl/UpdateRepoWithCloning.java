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

import com.teammerge.model.GitOptions;
import com.teammerge.services.GitService;
import com.teammerge.strategy.CloneStrategy;
import com.teammerge.utils.LoggerUtils;
import com.teammerge.utils.StringUtils;

public class UpdateRepoWithCloning implements CloneStrategy {

  private final static Logger LOG = LoggerFactory.getLogger(UpdateRepoWithCloning.class);

  private String remoteRepoPath;

  private GitService gitService;

  @Value("${app.debug}")
  private String debug;

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  @Override
  public Repository createOrUpdateRepo(File gitDir, String repoName, boolean isRepoExists) {
    long start = System.currentTimeMillis();
    String repositoryName = repoName;
    Git git = null;
    Repository repo = null;

    if (repoName == null) {
      repositoryName = getRepoNamesFromConfigFile();
    }

    // clone the new repo for the first time - the repo name should be mentioned in the config
    // file
    if (isRepoExists) {
      File repoDir = new File(gitDir, repositoryName);
      try {
        FileUtils.deleteDirectory(repoDir);
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

    try {
      git = gitService.cloneRepository(gitOptions);
      repo = git.getRepository();
    } catch (GitAPIException e) {
      LOG.error("Error cloning repository from path " + remoteRepoPath, e);
    }

    if (isDebugOn()) {
      LOG.debug("Git Repo cloned successufully from " + remoteRepoPath + " to "
          + gitDir.getAbsolutePath() + " in "
          + LoggerUtils.getTimeInSecs(start, System.currentTimeMillis()));
    }
    return repo;
  }

  private String getRepoNamesFromConfigFile() {
    if (!StringUtils.isEmpty(getRemoteRepoPath())) {
      String reponameWithDotGit =
          getRemoteRepoPath().substring(getRemoteRepoPath().lastIndexOf("/") + 1);
      return StringUtils.stripDotGit(reponameWithDotGit);
    }
    return null;
  }

  public String getRemoteRepoPath() {
    return remoteRepoPath;
  }

  @Required
  public void setRemoteRepoPath(String remoteRepoPath) {
    this.remoteRepoPath = remoteRepoPath;
  }

  public GitService getGitService() {
    return gitService;
  }

  @Required
  public void setGitService(GitService gitService) {
    this.gitService = gitService;
  }

}
