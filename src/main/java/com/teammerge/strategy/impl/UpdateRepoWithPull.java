package com.teammerge.strategy.impl;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
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

public class UpdateRepoWithPull implements CloneStrategy {

  private final static Logger LOG = LoggerFactory.getLogger(UpdateRepoWithPull.class);

  private String remoteRepoPath;

  private GitService gitService;

  @Value("${app.debug}")
  private String debug;

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  @Override
  public Repository createOrUpdateRepo(File f, String repoName, boolean isRepoExists) {
    String repositoryName = repoName;
    Git git = null;
    long start = 0;
    Repository repo = null;


    if (repoName == null) {
      repositoryName = getRepoNamesFromConfigFile();
    }

    // clone the new repo for the first time - the repo name should be mentioned in the config
    // file
    if (!isRepoExists) {
      LOG.info("Repo does not exits " + repositoryName + ", creating the repository!!");

      GitOptions gitOptions = new GitOptions();
      gitOptions.setURI(remoteRepoPath);
      gitOptions.setDestinationDirectory(f.getAbsolutePath() + "/" + repositoryName);
      gitOptions.setCloneAllBranches(Boolean.TRUE);
      gitOptions.setIncludeSubModule(Boolean.TRUE);
      gitOptions.setBare(Boolean.FALSE);

      try {
        git = gitService.cloneRepository(gitOptions);
        repo = git.getRepository();

        LOG.info("Git Repo cloned successufully from " + remoteRepoPath + " to "
            + f.getAbsolutePath());
      } catch (GitAPIException e) {
        LOG.error("Error cloning repository from path " + remoteRepoPath, e);
      }

      if (isDebugOn()) {
        LOG.debug("Created new repository " + f.getAbsolutePath() + " in "
            + LoggerUtils.getTimeInSecs(start, System.currentTimeMillis()));
      }
      return repo;
    } else {
      // check for updates
      // TODO see how loadRepository() will work here
      repo = null;// loadRepository(repositoryName, false);

      git = new Git(repo);
      PullCommand pc = git.pull();
      try {
        PullResult pr = pc.call();
        MergeResult mergeResult = pr.getMergeResult();

        if (isDebugOn()) {
          LOG.debug("Result of repo pull of " + repositoryName + ": "
              + mergeResult.getMergeStatus());
        }
      } catch (GitAPIException e) {
        LOG.error("Error in updating repository " + repositoryName, e);
      } finally {
        git.close();
      }
      if (isDebugOn()) {
        LOG.debug("Updated repository " + repositoryName + " in "
            + LoggerUtils.getTimeInSecs(start, System.currentTimeMillis()));
      }
      return repo;
    }
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
