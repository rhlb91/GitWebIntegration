package com.teammerge.services.impl;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teammerge.manager.IRepositoryManager;
import com.teammerge.manager.RepositoryManager;
import com.teammerge.model.GitOptions;
import com.teammerge.model.RepoParams;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.GitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.LoggerUtils;
import com.teammerge.utils.StringUtils;

@Service("repositoryService")
public class RepositoryServiceImpl implements RepositoryService {
  private final static Logger LOG = LoggerFactory.getLogger(RepositoryServiceImpl.class);

  private static IRepositoryManager repositoryManager = null;

  @Value("${git.repositoriesFolderPlaceholder}")
  private String repositoriesFolderPath;

  @Value("${git.remote.repository.path}")
  private String remoteRepoPath;

  @Value("${git.repository.folderName}")
  private String repoFolderName;

  @Value("${app.debug}")
  private String debug;

  @Autowired
  private RuntimeServiceImpl runtimeService;

  @Resource(name = "gitService")
  private GitService gitService;


  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  public IRepositoryManager getRepositoryManager() {

    if (repositoryManager == null) {
      RepoParams params = new RepoParams();
      params.setRemoteRepoPath(remoteRepoPath);
      params.setRepoFolderName(repoFolderName);
      params.setRuntimeManager(runtimeService.getRuntimeManager());
      params.setUserManager(null);

      repositoryManager = new RepositoryManager(params);
    }
    return repositoryManager;
  }

  public List<RepositoryModel> getRepositoryModels() {
    return getRepositoryManager().getRepositoryModels();
  }

  public Repository getRepository(String repositoryName, boolean updated) {
    long start = System.currentTimeMillis();

    Repository repo = getUpdatedRepository(repositoryName, updated);

    if (repo == null) {
      repo = getRepositoryManager().getRepository(repositoryName);
    }
    if (repo == null) {
      LOG.error("\nCannot Load Repository" + " " + repositoryName);
      return null;
    }

    if (isDebugOn()) {
      LOG.debug("Repository fetched in "
          + LoggerUtils.getTimeInSecs(start, System.currentTimeMillis()));
    }
    return repo;
  }

  /**
   * This function gets the repository url from the configuration file and tries to find if the
   * directory already exists or not. <br>
   * <br>
   * If the repository does not exists in the directory , this function will create the repository
   * from the remote repository defined in the config.properties <br>
   * <br>
   * If the repository exists, this function tries to update the repository by taking a pull of the
   * remote repository
   * 
   * <br>
   * <br>
   * This function takes first argument as repository name to update or create (if not exists), if
   * specified null it will try to update all the repositories mentioned in the config file
   * 
   * @return
   */
  private Repository getUpdatedRepository(String repoName, boolean updateRequired) {
    long start = System.currentTimeMillis();
    Repository repo = null;
    boolean toUpdate = false;

    File repositoriesFolder = getRepositoryManager().getRepositoriesFolder();
    if (!repositoriesFolder.exists() || !repositoriesFolder.isDirectory()) {
      boolean isDirCreated = repositoriesFolder.mkdir();

      if (isDirCreated) {
        toUpdate = true;
      } else {
        LOG.error("Cannot create directory " + repositoriesFolder.getAbsolutePath()
            + ", resolve the issue create and clone dir!! ");
      }
    }

    boolean isRepoExists = isRepoExists(repositoriesFolder, repoName);
    if (toUpdate || updateRequired || !isRepoExists) {
      repo = createOrUpdateRepo(repositoriesFolder, repoName, isRepoExists);
    }

    if (isDebugOn()) {
      LOG.debug("Get updated repository(s) in "
          + LoggerUtils.getTimeInSecs(start, System.currentTimeMillis()));
    }
    return repo;
  }

  boolean isRepoExists(File repoFolder, String repoName) {
    boolean isRepoExists = false;
    if (repoFolder != null && !StringUtils.isEmpty(repoName)) {
      if (repoFolder.list().length > 0) {
        for (String fName : repoFolder.list()) {
          if (repoName.equals(fName)) {
            isRepoExists = true;
            break;
          }
        }
      }
    }
    return isRepoExists;
  }

  private Repository createOrUpdateRepo(File f, String repoName, boolean isRepoExists) {
    String repositoryName = repoName;
    Git git = null;
    long start = 0;
    Repository repo = null;


    if (repoName == null) {
      repositoryName = getRepoNamesFromConfigFile();
    }

    if (isDebugOn()) {
      start = System.currentTimeMillis();
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
      repo = getRepository(repositoryName, false);

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
    if (!StringUtils.isEmpty(remoteRepoPath)) {
      String reponameWithDotGit = remoteRepoPath.substring(remoteRepoPath.lastIndexOf("/") + 1);
      return StringUtils.stripDotGit(reponameWithDotGit);
    }
    return null;
  }

  /**
   * No need to update repository from remote, as it is only printing the list of repositries
   * avalaible in local
   */
  public List<String> getRepositoryList() {
    return getRepositoryManager().getRepositoryList();
  }
}
