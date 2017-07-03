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
import org.eclipse.jgit.transport.FetchResult;
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
import com.teammerge.utils.StringUtils;

@Service("repositoryService")
public class RepositoryServiceImpl implements RepositoryService {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static IRepositoryManager repositoryManager = null;

  @Value("${git.repositoriesFolderPlaceholder}")
  private String repositoriesFolderPath;

  @Value("${git.remote.repository.path}")
  private String remoteRepoPath;

  @Value("${git.repository.folderName}")
  private String repoFolderName;

  @Autowired
  private RuntimeServiceImpl runtimeService;

  @Resource(name = "gitService")
  private GitService gitService;

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

  public Repository getRepository(String repositoryName) {
    Repository r = getRepositoryManager().getRepository(repositoryName);
    if (r == null) {
      System.out.println("\n\nCannot Load Repository" + " " + repositoryName);
      return null;
    }
    return r;
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
   * @return
   */
  private File createOrUpdateRepoIfRequired() {
    // check if repository folder exists
    // if repofolder exists : check if the reposiories inside in up to date or not
    // if repo is upto date : good
    // else update the repo
    // else : clone the repo

    File repositoriesFolder = getRepositoryManager().getRepositoriesFolder();
    if (repositoriesFolder.exists() && repositoriesFolder.isDirectory()) {
      createOrUpdateRepo(repositoriesFolder);
    } else {
      boolean isDirCreated = repositoriesFolder.mkdir();
      if (isDirCreated) {
        createOrUpdateRepo(repositoriesFolder);
      } else {
        logger.error("Cannot create directory " + repositoriesFolder.getAbsolutePath()
            + ", resolve the issue create and clone dir!! ");
      }
    }
    return repositoriesFolder;
  }

  private void createOrUpdateRepo(File f) {
    String repositoryNameFromConfigFile = getRepoNamesFromConfigFile();
    boolean isRepoExists = false;
    if (f.list().length > 0) {
      for (String fName : f.list()) {
        if (repositoryNameFromConfigFile.equals(fName)) {
          isRepoExists = true;
        }
      }
    }

    // clone the new repo for the first time - the repo name should be mentioned in the config
    // file
    if (!isRepoExists) {
      System.out.println("Repo doesnot exits " + repositoryNameFromConfigFile+", creating the repository!!");

      GitOptions gitOptions = new GitOptions();
      gitOptions.setURI(remoteRepoPath);
      gitOptions.setDestinationDirectory(f.getAbsolutePath()+ "/" +repositoryNameFromConfigFile);
      gitOptions.setCloneAllBranches(Boolean.TRUE);
      gitOptions.setIncludeSubModule(Boolean.TRUE);

      try {
        Git git = gitService.cloneRepository(gitOptions);

        logger.info("Git Repo cloned successufully from " + remoteRepoPath + " to "
            + f.getAbsolutePath());
      } catch (GitAPIException e) {
        logger.error("Error cloning repository from path " + remoteRepoPath, e);
      }
    } else {
      // check for updates
      Repository r = getRepository(repositoryNameFromConfigFile);

      Git g = new Git(r);
      PullCommand pc = g.pull();
      try {
        PullResult pr = pc.call();
        FetchResult fetchResult = pr.getFetchResult();
        MergeResult mergeResult = pr.getMergeResult();
        System.out.println("Result of pull of repo " + repositoryNameFromConfigFile + ": "
            + mergeResult.getMergeStatus());
      } catch (GitAPIException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  private String getRepoNamesFromConfigFile() {
    if (!StringUtils.isEmpty(remoteRepoPath)) {
      String reponameWithDotGit = remoteRepoPath.substring(remoteRepoPath.lastIndexOf("/") + 1);
      return StringUtils.stripDotGit(reponameWithDotGit);
    }
    return null;
  }

  public List<String> getRepositoryList() {
    createOrUpdateRepoIfRequired();
    return getRepositoryManager().getRepositoryList();
  }
}
