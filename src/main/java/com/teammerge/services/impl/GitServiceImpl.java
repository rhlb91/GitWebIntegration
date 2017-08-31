package com.teammerge.services.impl;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.Constants.CloneStatus;
import com.teammerge.dao.BaseDao;
import com.teammerge.model.CreateBranchOptions;
import com.teammerge.model.GitOptions;
import com.teammerge.model.RepoCloneStatusModel;
import com.teammerge.services.GitService;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.LoggerUtils;
import com.teammerge.utils.StringUtils;

@Service("gitService")
public class GitServiceImpl implements GitService {
  private final Logger LOG = LoggerFactory.getLogger(getClass());

  BaseDao<RepoCloneStatusModel> repoCloneStatusDao;

  @Value("${app.debug}")
  private String debug;

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  @Override
  public Git cloneRepository(GitOptions options) throws InvalidRemoteException, TransportException,
      GitAPIException {

    // create a thread for every new cloning
    GitServiceRunnable runnable = new GitServiceRunnable(options);
    Thread t1 = new Thread(runnable);
    t1.setName("CloningThread-" + options.getRepositoryName());
    t1.start();

    return null;
  }

  private boolean isCredentialsProvided(String username, String password) {
    return !StringUtils.isEmpty(username) && !StringUtils.isEmpty(password);
  }

  public Ref createBranch(CreateBranchOptions branchOptions) throws Exception {
    Ref ref = null;
    String startingPoint = branchOptions.getStartingPoint();
    try (Git git = new Git(branchOptions.getRepo())) {

      if (StringUtils.isEmpty(startingPoint)) {
        startingPoint = JGitUtils.getHEADRef(branchOptions.getRepo());
      }
      ref =
          git.branchCreate().setName(branchOptions.getBranchName()).setStartPoint(startingPoint)
              .call();
      if (ref == null) {
        LOG.debug("Trying to create a branch with starting point as master");
        ref =
            git.branchCreate().setName(branchOptions.getBranchName()).setStartPoint("master")
                .call();
      }
      if (ref == null) {
        LOG.debug("Trying to create a branch with starting point as origin/master");
        ref =
            git.branchCreate().setName(branchOptions.getBranchName())
                .setStartPoint("origin/master").call();
      }

      if (ref != null) {
        // push the newly created branch to remote origin
        PushCommand pushCommand = git.push();
        pushCommand.setRemote(branchOptions.getRemoteURL());
        pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(branchOptions
            .getUserName(), branchOptions.getPassword()));
        pushCommand.add(ref);
        pushCommand.call();
      }
    }

    return ref;
  }

  protected class GitServiceRunnable implements Runnable {
    GitOptions options;

    public GitServiceRunnable(GitOptions options) {
      this.options = options;
    }

    @Override
    public void run() {
      long start = System.currentTimeMillis();

      LOG.info("Cloning repository " + options.getURI() + ", in a thread "
          + Thread.currentThread().getName());

      if (checkIfAlreadyRunning(options.getRepositoryName())) {
        LOG.info("Cloning already running fro this repo!! New clone request Skipped.");
        return;
      }

      updateRepoCloneStatus(options.getRepositoryName(), Constants.CloneStatus.IN_PROGRESS);

      Git git = null;
      CloneCommand cmd = Git.cloneRepository();
      cmd.setCloneAllBranches(options.isCloneAllBranches());
      cmd.setCloneSubmodules(options.isIncludeSubModule());
      cmd.setURI(options.getURI());
      cmd.setBare(options.isBare());
      cmd.setNoCheckout(Boolean.TRUE);
      File destinationFolder = new File(options.getDestinationDirectory());
      cmd.setDirectory(destinationFolder);

      if (isCredentialsProvided(options.getUsername(), options.getPassword())) {
        cmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(options.getUsername(),
            options.getPassword()));
      }

      try {
        git = cmd.call();

        if (git != null && git.getRepository() != null) {
          updateRepoCloneStatus(options.getRepositoryName(), Constants.CloneStatus.COMPLETED);
        }
        LOG.info("Repo " + options.getURI() + " cloned to " + options.getDestinationDirectory()
            + " in " + LoggerUtils.getTimeInSecs(start, System.currentTimeMillis())
            + " with thread:" + Thread.currentThread().getName());

      } catch (GitAPIException e) {
        updateRepoCloneStatus(options.getRepositoryName(), Constants.CloneStatus.FAILURE);
        LOG.error(
            "Error from Thread - " + Thread.currentThread().getName() + " !!" + e.getMessage(), e);
      }
    }
  }

  private void updateRepoCloneStatus(String repositoryName, CloneStatus cloneStatus) {
    RepoCloneStatusModel repoCloneStatus = repoCloneStatusDao.fetchEntity(repositoryName);
    if (repoCloneStatus == null) {
      repoCloneStatus = new RepoCloneStatusModel(repositoryName);
    }
    repoCloneStatus.setCloneStatus(cloneStatus.name());
    repoCloneStatusDao.saveOrUpdateEntity(repoCloneStatus);

  }

  public boolean checkIfAlreadyRunning(String repositoryName) {
    RepoCloneStatusModel repoCloneStatus = repoCloneStatusDao.fetchEntity(repositoryName);
    return CloneStatus.IN_PROGRESS.equals(CloneStatus.forName(repoCloneStatus.getCloneStatus()));
  }

  @Autowired
  public void setRepoCloneStatusDao(BaseDao<RepoCloneStatusModel> baseDao) {
    baseDao.setClazz(RepoCloneStatusModel.class);
    this.repoCloneStatusDao = baseDao;
  }
}
