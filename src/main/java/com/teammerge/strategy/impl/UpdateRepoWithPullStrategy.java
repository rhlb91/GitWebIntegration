package com.teammerge.strategy.impl;

import java.io.File;
import java.io.IOException;

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

import com.teammerge.model.GitOptions;
import com.teammerge.services.GitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.strategy.CloneStrategy;
import com.teammerge.utils.LoggerUtils;
import com.teammerge.utils.StringUtils;

public class UpdateRepoWithPullStrategy implements CloneStrategy {

  private final static Logger LOG = LoggerFactory.getLogger(UpdateRepoWithPullStrategy.class);

  private String remoteRepoPath;

  private GitService gitService;

  @Value("${app.debug}")
  private String debug;

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  @Override
  public Repository createOrUpdateRepo(File f, String repoName, boolean isRepoExists) {
    if (repoName == null) {
      LOG.error("Error cloning/upadting repository from path " + remoteRepoPath
          + ", Reason: RepoName is null");
      return null;
    }

    Git git = null;
    Repository repo = null;
    String repositoryName = repoName;
    long start = System.currentTimeMillis();

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
      } catch (GitAPIException e) {
        LOG.error("Error cloning repository from path " + remoteRepoPath, e);
      }
      return repo;
    } else {
      // check for updates
      repo = loadRepository(f, repositoryName);

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

//  private String getRepoNamesFromConfigFile() {
//    if (!StringUtils.isEmpty(getRemoteRepoPath())) {
//      String reponameWithDotGit =
//          getRemoteRepoPath().substring(getRemoteRepoPath().lastIndexOf("/") + 1);
//      return StringUtils.stripDotGit(reponameWithDotGit);
//    }
//    return null;
//  }

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
